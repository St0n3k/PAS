package pl.lodz.pas.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.crypto.JwtSigner;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.Set;

@ApplicationScoped
public class JwtProvider {

    private final String SECRET = "iufewnfiuIFBEFI&e#73ht782NFUFE#t";

    //JWT expiration time = 1h
    private final int JWT_EXPIRATION_TIME = 3600000;

    public String generateJWT(String subject, Set<String> roles) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .claim("roles", roles)
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

    }

    public Jws<Claims> parseJWT(String jwt) throws SignatureException {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(jwt);
    }

    public boolean validateToken(String jwt) {
        try {
            parseJWT(jwt);
            return true;
        }  catch (Exception e) {
            return false;
        }
    }

    public String getToken(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "");
        }
        return null;
    }


}
