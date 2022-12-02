package com.fhkdemo.multitenant;

import com.fhkdemo.multitenant.configs.PathBasedConfigResolver;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringKeycloakMultiTenantApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringKeycloakMultiTenantApplication.class, args);
    }

    /**
     * Required to handle spring boot configurations
     * 
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(PathBasedConfigResolver.class)
    public KeycloakConfigResolver keycloakConfigResolver() {
        return new PathBasedConfigResolver();
    }
}
