package pl.lodz.pas.manager;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import pl.lodz.p.it.pas.dto.LoginResponse;
import pl.lodz.pas.exception.user.AuthenticationException;
import pl.lodz.pas.exception.user.InactiveUserException;
import pl.lodz.pas.security.GuesthouseIdentityStore;
import pl.lodz.pas.security.JwtProvider;


@RequestScoped
public class AuthManager {

    @Inject
    GuesthouseIdentityStore guesthouseIdentityStore;

    @Inject
    JwtProvider jwtProvider;

    public LoginResponse login(String username, String password)
            throws AuthenticationException, InactiveUserException {
        CredentialValidationResult result = guesthouseIdentityStore
                .validate(new UsernamePasswordCredential(username, password));

        String jwt = jwtProvider.generateJWT(result.getCallerPrincipal().getName(), result.getCallerGroups());
        return new LoginResponse(jwt);
    }

}
