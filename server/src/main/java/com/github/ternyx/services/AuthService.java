package com.github.ternyx.services;

import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.ternyx.models.OAuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * AuthService
 */
@Service
public class AuthService {
    private static final String GRANT_TYPE = "authorization_code";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";

    @Value("${youtube.client-id}")
    private String clientId;

    @Value("${youtube.client-secret}")
    private String clientSecret;

    @Value("${youtube.redirect-uri}")
    private String redirectUri;

    @Autowired
    private RestTemplate restTemplate;

    public OAuthToken retrieveToken(String code) {
        Map<String, String> params = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "code", code,
                "grant_type", GRANT_TYPE,
                "redirect_uri", redirectUri);

        return restTemplate.postForObject(TOKEN_URL, params, OAuthToken.class);
    }

    public OAuthToken refreshToken(OAuthToken token) {
        Map<String, String> params = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "grant_type", GRANT_TYPE,
                "refresh_token", token.getRefreshToken());

        ResponseEntity<OAuthToken> res = restTemplate.postForEntity(TOKEN_URL, params, OAuthToken.class);

        return restTemplate.postForObject(TOKEN_URL, params, OAuthToken.class);
    }

    public String getUserId(OAuthToken token) {
        final String url = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/youtube/v3/channels")
            .queryParam("part", "id")
            .queryParam("fields", "items/id")
            .queryParam("mine", "true")
            .toUriString();


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.getAccessToken());

        HttpEntity<String> entity = new HttpEntity<String>("", headers);

        ResponseEntity<JsonNode> res =
                restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class, 1);

        // TODO check for revokes/expires (unlikely)
        return res.getBody().at("/items/0/id").asText();
    }
}
