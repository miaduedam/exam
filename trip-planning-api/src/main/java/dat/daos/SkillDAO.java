package dat.daos;

import dat.dtos.SkillDTO;

import dat.entities.Skill;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;


import java.util.List;


public class SkillDAO implements IDAO<SkillDTO, Integer> {

    private static SkillDAO instance;
    private static EntityManagerFactory emf;

    public static SkillDAO getInstance(EntityManagerFactory _emf){
        if (instance == null){
            emf = _emf;
            instance = new SkillDAO();
        }
        return instance;
    }
    @Override
    public SkillDTO read(Integer integer) {
        try(EntityManager em = emf.createEntityManager()){
            Skill skill = em.find(Skill.class, integer);
            return new SkillDTO(skill);
        }
    }

    @Override
    public List<SkillDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<SkillDTO> query = em.createQuery("SELECT s FROM Skill s", SkillDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public SkillDTO create(SkillDTO skillDTO) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            Skill skill = new Skill(skillDTO);
            em.persist(skill);
            em.getTransaction().commit();
            return new SkillDTO(skill);
        }
    }

    @Override
    public SkillDTO update(Integer integer, SkillDTO skillDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Skill skill = em.find(Skill.class, integer);
            skill.setName(skillDTO.getName());
            skill.setDescription(skillDTO.getDescription());
            skill.setCategory(skillDTO.getCategory());
            Skill mergedSkill = em.merge(skill);
            em.getTransaction().commit();
            return mergedSkill != null ? new SkillDTO(mergedSkill) : null;
        }
    }




    @Override
    public void delete(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Skill skill = em.find(Skill.class, integer);
            if (skill != null) {
                em.remove(skill);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            Skill skill = em.find(Skill.class, integer);
            return skill != null;
        }
    }



}
