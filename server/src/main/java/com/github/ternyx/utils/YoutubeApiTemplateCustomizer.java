package com.github.ternyx.utils;

import java.io.IOException;
import java.util.List;
import com.github.ternyx.services.UserManager;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * YoutubeApiTemplateCustomizer
 */
@Component
public class YoutubeApiTemplateCustomizer implements RestTemplateCustomizer {

    UserManager userManager;

    public YoutubeApiTemplateCustomizer(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor(){
            @Override
            public ClientHttpResponse intercept(HttpRequest req, byte[] body,
                ClientHttpRequestExecution execution) throws IOException {

                req.getHeaders().addIfAbsent("Authorization", "Bearer " + UserManager.getUserFromContext().getToken().getAccessToken());

                ClientHttpResponse res =  execution.execute(req, body);

                if (res.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    req.getHeaders().replace("Authorization", List.of("Bearer " +userManager.refreshToken()));
                    return execution.execute(req, body);
                }
                return res;
            }
        });
    }
}
