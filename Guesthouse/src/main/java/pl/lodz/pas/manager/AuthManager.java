package pl.lodz.pas.manager;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import pl.lodz.p.it.pas.dto.LoginResponse;
import pl.lodz.p.it.pas.model.user.Client;
import pl.lodz.p.it.pas.model.user.User;
import pl.lodz.pas.exception.InvalidInputException;
import pl.lodz.pas.exception.user.AuthenticationException;
import pl.lodz.pas.exception.user.InactiveUserException;
import pl.lodz.pas.exception.user.UserNotFoundException;
import pl.lodz.pas.repository.impl.UserRepository;
import pl.lodz.pas.security.GuesthouseIdentityStore;
import pl.lodz.pas.security.JwtProvider;

import java.security.Principal;


@RequestScoped
public class AuthManager {

    @Inject
    GuesthouseIdentityStore guesthouseIdentityStore;

    @Inject
    JwtProvider jwtProvider;

    @Context
    private SecurityContext securityContext;

    @Inject
    private UserRepository userRepository;

    public LoginResponse login(String username, String password)
            throws AuthenticationException, InactiveUserException {
        CredentialValidationResult result = guesthouseIdentityStore
                .validate(new UsernamePasswordCredential(username, password));

        String jwt = jwtProvider.generateJWT(result.getCallerPrincipal().getName(), result.getCallerGroups());
        return new LoginResponse(jwt);
    }

    public void changePassword(String oldPassword, String newPassword) throws UserNotFoundException, InvalidInputException {
        Principal principal = securityContext.getUserPrincipal();
        if (principal instanceof User user) {
            if(user.getPassword().equals(oldPassword)
                    && !user.getPassword().equals(newPassword)){
                user.setPassword(newPassword);
                userRepository.update(user);
                return;
            }
            throw new InvalidInputException();
        }
        throw new UserNotFoundException();
    }

}
