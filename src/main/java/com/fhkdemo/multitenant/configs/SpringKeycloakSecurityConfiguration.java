package com.fhkdemo.multitenant.configs;

import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

/**
 * @author Edward P. Legaspi | czetsuya@gmail.com
 */
public class SpringKeycloakSecurityConfiguration {

    @DependsOn("keycloakConfigResolver")
    @KeycloakConfiguration
    @EnableGlobalMethodSecurity(jsr250Enabled = true)
//    @Import(KeycloakSpringBootConfigResolver.class)
    @ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true", matchIfMissing = true)
    public static class KeycloakConfigurationAdapter extends KeycloakWebSecurityConfigurerAdapter {
        /**
         * Registers the KeycloakAuthenticationProvider with the authentication manager.
         */
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
            SimpleAuthorityMapper soa = new SimpleAuthorityMapper();
            keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(soa);
            auth.authenticationProvider(keycloakAuthenticationProvider);
        }

        /**
         * Defines the session authentication strategy.
         */
        @Bean
        @Override
        protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
            return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
        }

        @Bean
        protected SessionRegistry buildSessionRegistry() {
            return new SessionRegistryImpl();
        }

        /**
         * Configuration spécifique à keycloak (ajouts de filtres, etc)
         *
         * @param http
         * @throws Exception
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);
            http
                    .authorizeRequests()
                    .anyRequest().permitAll();
            http.csrf().disable();
        }

        }
}