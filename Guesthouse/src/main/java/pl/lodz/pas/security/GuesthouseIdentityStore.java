package pl.lodz.pas.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import pl.lodz.p.it.pas.model.user.User;
import pl.lodz.pas.exception.user.AuthenticationException;
import pl.lodz.pas.exception.user.InactiveUserException;
import pl.lodz.pas.repository.impl.UserRepository;

import java.util.Collections;
import java.util.Objects;


@ApplicationScoped
public class GuesthouseIdentityStore implements IdentityStore {

    @Inject
    private UserRepository userRepository;

    public CredentialValidationResult validate(UsernamePasswordCredential credential)
            throws InactiveUserException, AuthenticationException {

        User user = userRepository.getUserByUsername(credential.getCaller())
                .orElseThrow(AuthenticationException::new);

        if (!Objects.equals(user.getPassword(), credential.getPasswordAsString())) {
            throw new AuthenticationException();
        }

        if (!user.isActive()) {
            throw new InactiveUserException();
        }
        return new CredentialValidationResult(credential.getCaller(), Collections.singleton(user.getRole()));
    }
}
