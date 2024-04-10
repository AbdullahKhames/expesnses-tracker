package name.expenses.features.user.controller;

import jakarta.ejb.EJB;
import jakarta.servlet.http.HttpServlet;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import name.expenses.features.user.service.UserService;

@Path("/user")
public class UserController extends HttpServlet {

    @EJB
    private UserService userService;

    @GET
    public Response getUserInfo() {
        String userInfo = userService.getUserInfo();
        return Response.ok("<h1>" + userInfo + "</h1>").build();
    }
}
