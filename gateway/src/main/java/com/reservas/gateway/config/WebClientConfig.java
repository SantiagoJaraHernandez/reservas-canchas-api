package com.reservas.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private final GatewayProperties props;

    public WebClientConfig(GatewayProperties props) {
        this.props = props;
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl(props.getBaseUrl())
                .build();
    }
}