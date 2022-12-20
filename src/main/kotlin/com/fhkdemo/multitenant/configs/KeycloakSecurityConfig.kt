package com.fhkdemo.multitenant.configs

import org.keycloak.adapters.KeycloakConfigResolver
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties
import org.keycloak.adapters.springsecurity.KeycloakConfiguration
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy


@KeycloakConfiguration
@EnableGlobalMethodSecurity(jsr250Enabled = true)
//@Import(KeycloakSpringBootConfigResolver::class)
//@ConditionalOnProperty(name = ["keycloak.enabled"], havingValue = "true", matchIfMissing = true)
class KeycloakSecurityConfig: KeycloakWebSecurityConfigurerAdapter() {
    /**
     * Registers the KeycloakAuthenticationProvider with the authentication manager.
     */
    @Autowired
    @Throws(Exception::class)
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        val authenticationProvider = KeycloakAuthenticationProvider()
        authenticationProvider.setGrantedAuthoritiesMapper(SimpleAuthorityMapper())
        auth.authenticationProvider(authenticationProvider)
    }
    /**
     * Defines the session authentication strategy.
     */
    @Bean
    override fun sessionAuthenticationStrategy(): SessionAuthenticationStrategy {
        return RegisterSessionAuthenticationStrategy(buildSessionRegistry())
    }

    @Bean
    protected fun buildSessionRegistry(): SessionRegistry {
        return SessionRegistryImpl()
    }

    /**
     * Use properties in application.properties instead of keycloak.json
     */

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        super.configure(http)
        http.csrf().disable()
//            .authorizeRequests()
//            .antMatchers("/check").permitAll()
//            .and()
//            .authorizeRequests()
//            .antMatchers("/tenant/*").hasAnyRole("USER", "ADMIN")
//            .and()
//            .authorizeRequests()
//            .antMatchers("/carrier/*").hasAnyRole("MAINTAINER")
//            .and()
//            .authorizeRequests()
//            .anyRequest().authenticated();
            .authorizeRequests()
                .anyRequest().permitAll();
    }

}