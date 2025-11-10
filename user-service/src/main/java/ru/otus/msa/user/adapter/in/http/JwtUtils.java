package ru.otus.msa.user.adapter.in.http;

import java.util.Base64;
import java.util.UUID;
import org.json.JSONObject;

public class JwtUtils {

    public static UUID extractSubFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        String jwtToken = authorizationHeader.substring(7);
        String[] parts = jwtToken.split("\\.");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token");
        }

        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        JSONObject jsonPayload = new JSONObject(payload);

        return UUID.fromString(jsonPayload.getString("sub"));
    }
}
