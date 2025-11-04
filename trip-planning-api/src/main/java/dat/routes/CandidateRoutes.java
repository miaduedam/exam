package dat.routes;


import dat.controllers.CandidateController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;

public class CandidateRoutes {

    private final CandidateController candidateController = new CandidateController();

    protected EndpointGroup getRoutes() {

        return () -> {
            get("/", candidateController::readAll, Role.ANYONE);
            get("/{id}", candidateController::read, Role.ANYONE);
            post("/", candidateController::create, Role.ADMIN);
            put("/{id}", candidateController::update, Role.ADMIN);
            delete("/{id}", candidateController::delete, Role.ADMIN);
            put("/{candidateId}/skills/{skillId}", candidateController::addSkillToCandidate, Role.ADMIN);

            get("/?category={category}", candidateController::getCandidatesByCategory);

        };
    }
}

