package dat.daos;

import com.fasterxml.jackson.databind.JsonNode;
import dat.dtos.CandidateDTO;
import dat.dtos.CandidateReportDTO;
import dat.dtos.SkillEnrichedDTO;
import dat.entities.Candidate;
import dat.entities.Skill;
import dat.enums.SkillCategory;
import dat.services.SkillStatsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.io.IOException;
import java.util.List;

public class CandidateDAO implements IDAO<CandidateDTO, Integer> {

    private static CandidateDAO instance;
    private static EntityManagerFactory emf;

    public static CandidateDAO getInstance(EntityManagerFactory _emf){
        if (instance == null){
            emf = _emf;
            instance = new CandidateDAO();
        }
        return instance;
    }

    @Override
    public CandidateDTO read(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Candidate candidate = em.find(Candidate.class, id);
            if (candidate == null) return null;


            List<Skill> skills = candidate.getSkills().stream().toList();
            if (skills.isEmpty()) {
                // Hvis ingen skills, returner kandidat uden enrichment
                return new CandidateDTO(candidate, List.of());
            }

            SkillStatsService service = new SkillStatsService();
            JsonNode apiData = null;
            try {
                apiData = service.getSkillStats(
                        skills.stream().map(Skill::getSlug).toList()
                );
            } catch (IOException e) {
                System.err.println("Could not fetch external skill stats: " + e.getMessage());
            }
            JsonNode finalApiData = apiData;
            List<SkillEnrichedDTO> enriched = skills.stream().map(skill -> {
                SkillEnrichedDTO dto = new SkillEnrichedDTO(skill);

                if (finalApiData != null) {
                    for (JsonNode node : finalApiData) {
                        if (node.get("slug").asText().equalsIgnoreCase(skill.getSlug())) {
                            dto.setPopularityScore(node.get("popularityScore").asInt());
                            dto.setAverageSalary(node.get("averageSalary").asInt());
                        }
                    }
                }
                return dto;
            }).toList();
            return new CandidateDTO(candidate, enriched);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching candidate or skill stats", e);
        }
    }


    @Override
    public List<CandidateDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<CandidateDTO> query = em.createQuery("SELECT c FROM Candidate c", CandidateDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public CandidateDTO create(CandidateDTO candidateDTO) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            Candidate candidate = new Candidate(candidateDTO);
            em.persist(candidate);
            em.getTransaction().commit();
            return new CandidateDTO(candidate);
       }
    }

    @Override
    public CandidateDTO update(Integer integer, CandidateDTO candidateDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Candidate candidate = em.find(Candidate.class, integer);
            candidate.setName(candidateDTO.getName());
            candidate.setPhone(candidateDTO.getPhone());
            candidate.setEducationBackground(candidateDTO.getEducationBackground());
            Candidate mergedCandidate = em.merge(candidate);
            em.getTransaction().commit();
            return mergedCandidate != null ? new CandidateDTO(mergedCandidate) : null;
        }
    }


    @Override
    public void delete(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Candidate candidate = em.find(Candidate.class, integer);
            if (candidate != null) {
                em.remove(candidate);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            Candidate candidate = em.find(Candidate.class, integer);
            return candidate != null;
        }
    }

    public List<CandidateDTO> getCandidatesByCategory(String category) {
        try (EntityManager em = emf.createEntityManager()) {
            // Konverter string til enum
            SkillCategory enumCategory;
            try {
                enumCategory = SkillCategory.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid category: " + category);
            }

            // Hent alle kandidater, med minimum et skill
            TypedQuery<Candidate> query = em.createQuery(
                    "SELECT DISTINCT c FROM Candidate c JOIN c.skills s WHERE s.category = :category",
                    Candidate.class);

            query.setParameter("category", enumCategory);

            return query.getResultList()
                    .stream()
                    .map(CandidateDTO::new)
                    .toList();
        }
    }


    public CandidateDTO addSkillToCandidate(int skillId, int candidateId){
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            Skill skill = em.find(Skill.class, skillId);
            Candidate candidate = em.find(Candidate.class, candidateId);
            if (skill != null && candidate != null){
                candidate.addSkill(skill);
                em.merge(candidate);
                em.getTransaction().commit();
                return new CandidateDTO(candidate);
            }else {
                throw new RuntimeException("Candidate or Skill not found");
            }
        } catch (Exception e){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error adding Skill to Candidate", e);
        } finally {
            em.close();
        }
    }

    public CandidateReportDTO getTopCandidateByPopularity() {
        try (EntityManager em = emf.createEntityManager()) {
            List<Candidate> candidates = em.createQuery("SELECT c FROM Candidate c", Candidate.class).getResultList();

            SkillStatsService service = new SkillStatsService();

            CandidateReportDTO topCandidate = null;
            double highestAvg = 0;

            for (Candidate candidate : candidates) {
                List<Skill> skills = candidate.getSkills().stream().toList();
                if (skills.isEmpty()) continue;

                JsonNode apiData = service.getSkillStats(
                        skills.stream().map(Skill::getSlug).toList()
                );

                double total = 0;
                int count = 0;
                for (Skill skill : skills) {
                    for (JsonNode node : apiData) {
                        if (node.get("slug").asText().equalsIgnoreCase(skill.getSlug())) {
                            total += node.get("popularityScore").asInt();
                            count++;
                        }
                    }
                }
                if (count > 0) {
                    double avg = total / count;

                    if (avg > highestAvg) {
                        highestAvg = avg;
                        topCandidate = new CandidateReportDTO(candidate, avg);
                    }
                }
            }
            return topCandidate;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching top candidate", e);
        }
    }
}
