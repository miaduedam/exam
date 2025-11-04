package dat;

import dat.config.HibernateConfig;
import dat.config.Populate;
import dat.controllers.CandidateController;
import dat.controllers.ReportController;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static io.javalin.http.HttpStatus.OK;
import static io.javalin.http.HttpStatus.CREATED;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EndpointTest {

    private static Javalin app;
    private static CandidateController candidateController;
    private static ReportController reportController;

    @BeforeAll
    public static void setup() {
        HibernateConfig.getEntityManagerFactory();
        Populate.populate(HibernateConfig.getEntityManagerFactory());
        candidateController = new CandidateController();
        reportController = new ReportController();

        // Start Javalin
        app = Javalin.create();
        app.get("/candidates", candidateController::readAll);
        app.get("/candidates/{id}", candidateController::read);
        app.post("/candidates", candidateController::create);
        app.put("/candidates/{id}", candidateController::update);
        app.delete("/candidates/{id}", candidateController::delete);
        app.put("/candidates/{candidateId}/skills/{skillId}", candidateController::addSkillToCandidate);

        // Reports endpoint (US-6)
        app.get("/reports/candidates/top-by-popularity", reportController::getTopByPopularity);

        app.start(7001);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7001;
    }

    @AfterAll
    public void tearDown() {
        app.stop();
    }

   //CRUD for candidates
    @Test
    @DisplayName("Create new candidate")
    void testCreateCandidate() {
        String json = """
        {
            "name": "Mia",
            "phone": "12345678",
            "educationBackground": "AP Graduate in Computer Science"
        }
        """;

        given()
                .contentType("application/json")
                .body(json)
                .when()
                .post("/candidates")
                .then()
                .statusCode(CREATED.getCode())
                .body("name", equalTo("Mia"))
                .body("phone", equalTo("12345678"));
    }

    @Test
    @DisplayName("Get all candidates")
    void testGetAllCandidates() {
        given()
                .when()
                .get("/candidates")
                .then()
                .statusCode(OK.getCode())
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    @DisplayName("Get candidate by ID")
    void testGetCandidateById() {
        given()
                .pathParam("id", 1)
                .when()
                .get("/candidates/{id}")
                .then()
                .statusCode(OK.getCode())
                .body("candidateId", equalTo(1))
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Update candidate info")
    void testUpdateCandidate() {
        String json = """
        {
            "name": "Mia Updated",
            "phone": "87654321",
            "educationBackground": "Bachelor in Software Engineering"
        }
        """;

        given()
                .pathParam("id", 1)
                .contentType("application/json")
                .body(json)
                .when()
                .put("/candidates/{id}")
                .then()
                .statusCode(OK.getCode())
                .body("name", equalTo("Mia Updated"));
    }

    @Test
    @DisplayName("Delete candidate")
    void testDeleteCandidate() {
        given()
                .pathParam("id", 2)
                .when()
                .delete("/candidates/{id}")
                .then()
                .statusCode(204);   // <- skift fra 200 til 204

    }


    //Link skill to candidate

    @Test
    @DisplayName("Link skill to candidate")
    void testAddSkillToCandidate() {
        given()
                .pathParam("candidateId", 1)
                .pathParam("skillId", 1)
                .when()
                .put("/candidates/{candidateId}/skills/{skillId}")
                .then()
                .statusCode(OK.getCode())
                .body(containsString("Skill added"));
    }

    //Filter by skill category
    @Test
    @DisplayName("Filter candidates by skill category")
    void testFilterByCategory() {
        given()
                .queryParam("category", "PROG_LANG")
                .when()
                .get("/candidates")
                .then()
                .statusCode(OK.getCode())
                .body("size()", greaterThanOrEqualTo(1))
                .body("[0].skills[0].category", equalTo("PROG_LANG"));
    }

    //Enrichment data from Skill Stats API
    @Test
    @DisplayName("Candidate includes enriched skill data")
    void testGetCandidateWithEnrichedSkills() {
        given()
                .pathParam("id", 1)
                .when()
                .get("/candidates/{id}")
                .then()
                .statusCode(OK.getCode())
                .body("skillEnrichedDTOS.size()", greaterThan(0))
                .body("skillEnrichedDTOS[0].popularityScore", greaterThan(0))
                .body("skillEnrichedDTOS[0].averageSalary", greaterThan(0));
    }

    //Top candidate by popularity
    @Test
    @DisplayName("Get candidate with highest avg popularity")
    void testTopCandidateByPopularity() {
        given()
                .when()
                .get("/reports/candidates/top-by-popularity")
                .then()
                .statusCode(OK.getCode())
                .body("averagePopularityScore", greaterThan(0.0F))
                .body("name", notNullValue());
    }
}
