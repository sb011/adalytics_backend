package com.adalytics.adalytics_backend.utils;

import com.adalytics.adalytics_backend.constants.CommonConstants;
import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    public String generateToken(String userId) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withSubject(userId)
                .withIssuer(issuer)
                .withExpiresAt(new Date(System.currentTimeMillis() + CommonConstants.JWT_TOKEN_EXPIRY))
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .acceptExpiresAt(10)
                    .build()
                    .verify(token);
        } catch (JWTVerificationException exception) {
            throw new BadRequestException("Invalid Token !", ErrorCodes.Token_Invalid.getErrorCode());
        }
    }
}
