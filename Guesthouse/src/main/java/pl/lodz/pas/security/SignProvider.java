package pl.lodz.pas.security;

import java.text.ParseException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SignProvider {

    @Inject
    @ConfigProperty(name = "pl.lodz.pas.security.jws.secret")
    private String secret;


    public String sign(String payload) throws JOSEException {
        JWSSigner signer = new MACSigner(secret);
        JWSObject jws = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(payload));
        jws.sign(signer);

        return jws.serialize();
    }


    public boolean verify(String ifMatch, String payload) throws JOSEException, ParseException {
        JWSObject jws = JWSObject.parse(ifMatch);
        JWSVerifier jwsVerifier = new MACVerifier(secret);

        if (!jws.verify(jwsVerifier)) {
            return false;
        }

        String signedRequestObject = sign(payload);
        return ifMatch.equals(signedRequestObject);
    }
}
