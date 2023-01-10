package pl.lodz.pas.security;


import java.util.Date;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class JwtProvider {

    @Inject
    @ConfigProperty(name = "pl.lodz.pas.security.jwt.secret")
    private String SECRET;

    //JWT expiration time = 1h
    @Inject
    @ConfigProperty(name = "pl.lodz.pas.security.jwt.expirationTime",
                    defaultValue = "3600000")
    private int JWT_EXPIRATION_TIME;

    public String generateJWT(String subject, String role) {
        return Jwts.builder()
                   .setSubject(subject)
                   .setIssuedAt(new Date())
                   .claim("role", role)
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
        } catch (Exception e) {
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
