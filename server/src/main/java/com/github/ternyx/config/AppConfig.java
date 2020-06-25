package com.github.ternyx.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * AppConfig
 */
@Configuration
public class AppConfig {

    @Autowired
    YoutubeApiFilter youtubeApiFilter;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public FilterRegistrationBean<YoutubeApiFilter> authFilter() {
        FilterRegistrationBean<YoutubeApiFilter> authFilter = new FilterRegistrationBean<>();

        authFilter.setFilter(youtubeApiFilter);
        authFilter.addUrlPatterns("/api/youtube/*");

        return authFilter;
    }
    
}
