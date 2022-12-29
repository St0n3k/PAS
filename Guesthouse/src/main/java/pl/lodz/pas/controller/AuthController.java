package pl.lodz.pas.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import pl.lodz.p.it.pas.dto.LoginDTO;
import pl.lodz.p.it.pas.dto.LoginResponse;
import pl.lodz.pas.exception.user.AuthenticationException;
import pl.lodz.pas.exception.user.InactiveUserException;
import pl.lodz.pas.manager.AuthManager;

@RequestScoped
@Path("/")
public class AuthController {

    @Inject
    private AuthManager loginManager;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/login")
    public LoginResponse login(@Valid LoginDTO loginDTO)
            throws AuthenticationException, InactiveUserException {
        return loginManager.login(loginDTO.getUsername(), loginDTO.getPassword());
    }
}
