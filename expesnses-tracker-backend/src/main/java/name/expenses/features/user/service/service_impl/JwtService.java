package name.expenses.features.user.service.service_impl;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;


import jakarta.servlet.http.HttpServletRequest;
import name.expenses.features.user.models.Role;
import name.expenses.features.user.models.Type;
import name.expenses.features.user.models.User;
import name.expenses.features.user.models.UserDetails;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class JwtService {

    //    minimum requirement for jwt is 256bit
    private static final String SECRET_KEY = "77217A25432A462D4A614E645267556B58703273357638782F413F4428472B4B";
    public String extractUsername(String token) {
//        subject should be username or email of my user
        return extractClaim(token,Claims::getSubject);
    }
    public  <T> T extractClaim(String token , Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);

    }

    public String generateToken(User userDetails){
        Map<String , Object> map = new HashMap<>();
        map.put("UUID",userDetails.getRefNo());
//        map.put("id", userDetails.getId());
        return generateToken(map , userDetails);
    }
    public String generateRefreshToken(User userDetails){
        Map<String , Object> map = new HashMap<>();
//        map.put("UUID",userDetails.getRef());
        return generateRefreshToken(map, userDetails);
    }
    public String generateVerificationToken(String otpRef, String deviceId, Type type){
        Map<String , Object> map = new HashMap<>();
        map.put("ref",otpRef);
        map.put("type",type.toString());
        map.put("deviceId", deviceId);
        return generateVerificationToken(map);
    }
    public String generateToken(Map<String , Object> extraClaims , User userDetails){
        Date in = new Date();
        int months =((Role) userDetails.getRoles().toArray()[0]).getName().equals("ROLE_ADMIN")? 24 : 6;
        log.info("month : {}",months);
        log.info("roles : {}",userDetails.getRoles());
        LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault()).plusMonths(months);
        Date validTill = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .claim("roles",userDetails.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis())) //means when this Claim was Created
                .setExpiration(validTill) //6 months
                .signWith( getSignInKey(),SignatureAlgorithm.HS256)
                .compact(); //compact will generate and return the token
    }
    public String generateRefreshToken(Map<String , Object> extraClaims , User userDetails){
        Date in = new Date();
        int years = ((Role) userDetails.getRoles().toArray()[0]).getName().equals("ROLE_FAC")? 6 : 1;
        LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault()).plusYears(years);
        Date validTill = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .claim("roles",userDetails.getRoles())
                .setIssuedAt(new Date(System.currentTimeMillis())) //means when this Claim was Created
                .setExpiration(validTill) //1 year
                .signWith(getSignInKey(),SignatureAlgorithm.HS256)
                .compact(); //compact will generate and return the token
    }
    public String generateVerificationToken(Map<String , Object> extraClaims){
        Date in = new Date();
        LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault()).plusMinutes(10);
        Date validTill = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setClaims(extraClaims)
//                .setSubject(userDetails.getUsername())
//                .claim("roles", Set.of("ROLE_VERIFIED"))
                .setIssuedAt(new Date(System.currentTimeMillis())) //means when this Claim was Created
                .setExpiration(validTill) //10 min
                .signWith(getSignInKey(),SignatureAlgorithm.HS256)
                .compact(); //compact will generate and return the token
    }


    //    validate that this token belong to this userDetails
    public boolean isTokenValid(String token , UserDetails userDetails){
        final  String userName = extractUsername(token);
        return  (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token , Claims::getExpiration);
    }

    //    Claims is an extra data send with jwt
    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())//setSigningKey uses to create the signature (secret key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public String extractUUIDFromToken(String token){

        return extractAllClaims(token).get("UUID").toString();
    }
    public String extractUUIDFromRequest(HttpServletRequest request){
        String token;
        token = extractTokenFromRequest(request);

        return extractAllClaims(token).get("UUID").toString();
    }
    public String extractTokenFromRequest(HttpServletRequest request){
        String token;
        try {
            String operator = request.getHeader("Authorization");
            token = operator.substring(7);
        }catch (Exception e){
             throw new IllegalArgumentException("Cannot find Token");
        }

        return token;
    }


}