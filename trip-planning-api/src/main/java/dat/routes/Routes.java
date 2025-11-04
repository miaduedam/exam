package dat.routes;

import dat.security.routes.SecurityRoutes;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final CandidateRoutes candidateRoutes = new CandidateRoutes();
    private final ReportRoutes reportRoutes = new ReportRoutes();
    private final SecurityRoutes securityRoutes = new SecurityRoutes();

    public EndpointGroup getRoutes() {
        return () -> {
            get("/", ctx -> ctx.result("API is running!"));
            path("/candidates", candidateRoutes.getRoutes());
            path("reports", reportRoutes.getRoutes());
            path("/auth", securityRoutes.getSecurityRoutes());
        };
    }
}
