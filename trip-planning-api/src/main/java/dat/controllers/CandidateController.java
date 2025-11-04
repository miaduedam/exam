package dat.controllers;

import dat.config.HibernateConfig;
import dat.daos.CandidateDAO;
import dat.dtos.CandidateDTO;
import dat.entities.Candidate;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;


public class CandidateController implements IController<CandidateDTO, Integer> {

    private final CandidateDAO candidateDAO;

    public CandidateController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.candidateDAO = CandidateDAO.getInstance(emf);
    }


    @Override
    public void read(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        CandidateDTO candidateDTO = candidateDAO.read(id);
        if (candidateDTO == null) {
            ctx.status(404).json("Candidate not found");
            return;
        }

        ctx.status(200).json(candidateDTO);
    }



    @Override
    public void readAll(Context ctx) {
        // List of DTOS
        List<CandidateDTO> candidateDTOS = candidateDAO.readAll();
        // response
        ctx.res().setStatus(200);
        ctx.json(candidateDTOS, CandidateDTO.class);
    }

    @Override
    public void create(Context ctx) {
        // request
        CandidateDTO jsonRequest = ctx.bodyAsClass(CandidateDTO.class);
        // DTO
        CandidateDTO tripDTO = candidateDAO.create(jsonRequest);
        // response
        ctx.res().setStatus(201);
        ctx.json(tripDTO, CandidateDTO.class);
    }

    @Override
    public void update(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // dto
        CandidateDTO candidateDTO = candidateDAO.update(id, validateEntity(ctx));
        // response
        ctx.res().setStatus(200);
        ctx.json(candidateDTO, Candidate.class);
    }

    @Override
    public void delete(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        candidateDAO.delete(id);
        // response
        ctx.res().setStatus(204);
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        return candidateDAO.validatePrimaryKey(integer);
    }


    @Override
    public CandidateDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(CandidateDTO.class)
                .check(c -> c.getName() != null && !c.getName().isBlank(),"Candidate name must be provided")
                .check(c -> c.getPhone() != null && c.getPhone().matches("\\d{8}"),"Phone number must be 8 digits")
                .check(c -> c.getEducationBackground() != null && !c.getEducationBackground().isBlank(),"Education background must be provided")
                .get();
    }


    public void addSkillToCandidate(Context ctx) {
        int candidateId = ctx.pathParamAsClass("candidateId", Integer.class).get();
        int skillId = ctx.pathParamAsClass("skillId", Integer.class).get();

        try {
            CandidateDTO updated = candidateDAO.addSkillToCandidate(candidateId, skillId);
            ctx.status(HttpStatus.OK)
                    .json("Skill added to candidate: " + updated.getName());
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.NOT_FOUND)
                    .json("Candidate or Skill not found: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json("Error adding skill to candidate: " + e.getMessage());
        }
    }


    public void getCandidatesByCategory(Context ctx){
        String category = ctx.queryParam("category");

        if (category == null || category.isEmpty()){
            ctx.status(400).json("{\"error\": \"Category parameter is required\"}");
        }

        List<CandidateDTO> filteredTrips = candidateDAO.getCandidatesByCategory(category);
        ctx.status(200).json(filteredTrips);
    }
}
