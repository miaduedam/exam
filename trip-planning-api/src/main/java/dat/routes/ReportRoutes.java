package dat.routes;


import dat.controllers.ReportController;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.get;

public class ReportRoutes {
    private final ReportController reportController = new ReportController();

    protected EndpointGroup getRoutes() {

        return () -> get("/candidates/top-by-popularity", reportController::getTopByPopularity);
    }
}
