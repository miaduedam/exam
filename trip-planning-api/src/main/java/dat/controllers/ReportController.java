package dat.controllers;

import dat.config.HibernateConfig;
import dat.daos.CandidateDAO;
import dat.dtos.CandidateReportDTO;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

public class ReportController {

    private final CandidateDAO candidateDAO;

    public ReportController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.candidateDAO = CandidateDAO.getInstance(emf);
    }
    public void getTopByPopularity(Context ctx) {
        CandidateReportDTO top = candidateDAO.getTopCandidateByPopularity();
        if (top == null) {
            ctx.status(404).json("No candidates with skills found");
        } else {
            ctx.status(200).json(top);
        }
    }
}
