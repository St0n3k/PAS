package pl.lodz.pas.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.pas.dto.ChangePasswordDTO;
import pl.lodz.p.it.pas.dto.LoginDTO;
import pl.lodz.p.it.pas.dto.LoginResponse;
import pl.lodz.pas.exception.InvalidInputException;
import pl.lodz.pas.exception.user.AuthenticationException;
import pl.lodz.pas.exception.user.InactiveUserException;
import pl.lodz.pas.exception.user.UserNotFoundException;
import pl.lodz.pas.manager.AuthManager;

@RequestScoped
@Path("/")
public class AuthController {

    @Inject
    private AuthManager authManager;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/login")
    public LoginResponse login(@Valid LoginDTO loginDTO)
            throws AuthenticationException, InactiveUserException {
        return authManager.login(loginDTO.getUsername(), loginDTO.getPassword());
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/changePassword")
    public Response changePassword(@Valid ChangePasswordDTO dto)
            throws UserNotFoundException, InvalidInputException {
        authManager.changePassword(dto.getOldPassword(), dto.getNewPassword());
        return Response.ok().build();
    }
}
