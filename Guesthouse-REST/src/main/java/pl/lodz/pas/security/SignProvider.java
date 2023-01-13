package pl.lodz.pas.security;

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


import java.text.ParseException;

@ApplicationScoped
public class SignProvider {

    private final String secret = "h78ht723h8hH7fh329fh32732hg32g83j29";


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
