package com.fhkdemo.multitenant.controllers

import org.keycloak.KeycloakPrincipal
import org.keycloak.KeycloakSecurityContext
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
class CatalogController {

    @GetMapping("check")
    fun healthCheck(): String? {
        return "It is healthy"
    }

    @GetMapping("/tenant/{realm}/catalog")
    fun listCatalog(@PathVariable("realm") realm: String): String? {
        return """
            ${"At $realm"}
            ${getUserInfo()}
            """.trimIndent()
    }

    @PostMapping("/tenant/{realm}/catalog")
    @ResponseStatus(HttpStatus.CREATED)
    fun createCatalog(@PathVariable("realm") realm: String): String? {
        return "creating catalog at $realm"
    }

    @DeleteMapping("/carrier/catalog")
    fun deleteCatalog(): String? {
        return "deleting catalog by carrier"
    }

    @GetMapping("/internal/catalog")
    fun anyCatalog(): String? {
        return "Any access from internal"
    }

    private fun getUserInfo(): String? {
        val authentication = SecurityContextHolder.getContext().authentication as KeycloakAuthenticationToken
        val principal = authentication.principal as Principal
        var tokenInfo: String? = null
        if (principal is KeycloakPrincipal<*>) {
            val kPrincipal = principal as KeycloakPrincipal<KeycloakSecurityContext>
            val ksc = kPrincipal.keycloakSecurityContext
            val token = ksc.idToken
            val accessToken = kPrincipal.keycloakSecurityContext.token
            tokenInfo = accessToken.subject

            // this value is the one use to call another service as bearer token
            // Authorization : Bearer kcs.getTokenString()
            // use this link to read the token https://jwt.io
            println(ksc.tokenString)
            println(accessToken.givenName)
            println(accessToken.familyName)
        }
        return "userInfo $tokenInfo"
    }
}