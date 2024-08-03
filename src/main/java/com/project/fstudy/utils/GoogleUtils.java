package com.project.fstudy.utils;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.project.fstudy.exception.GoogleIdTokenUnknownException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class GoogleUtils {
    @Value("${spring.security.oauth2.client.registration.google.clientId}")
    private String CLIENT_ID;
    public Map<String, String> getUserInfo(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            throw new GoogleIdTokenUnknownException("");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", payload.getEmail().split("@")[0].trim());
        userInfo.put("email", payload.getEmail());
        userInfo.put("name", (String) payload.get("name"));
        userInfo.put("image", (String) payload.get("picture"));

        return userInfo;
    }
}
