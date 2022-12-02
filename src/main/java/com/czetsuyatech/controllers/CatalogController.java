package com.czetsuyatech.controllers;

import java.security.Principal;

import com.czetsuyatech.configs.PathBasedConfigResolver;
import com.czetsuyatech.util.Global;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * @author Edward P. Legaspi | czetsuya@gmail.com
 */

@RestController
public class CatalogController {

    @Autowired
    private Global global;

    @GetMapping("check")
    public String check() {
        return "Checked!";
    }

    @GetMapping("/tenant/{realm}/catalog")
    public String listCatalog() {
        return "At " + global.getRealm() + "\n" + getUserInfo();
    }

    @PostMapping("/tenant/{realm}/catalog")
    @ResponseStatus(HttpStatus.CREATED)
    public String createCatalog() {
        return "creating catalog at " + global.getRealm();
    }

    @DeleteMapping("/carrier/catalog")
    public String deleteCatalog() {
        return "deleting catalog by carrier";
    }


//    @GetMapping("/tenant/branch2/catalog")
//    public String listCatalogBranch2() {
//        return getUserInfo();
//    }

    @SuppressWarnings("unchecked")
    private String getUserInfo() {

        KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        final Principal principal = (Principal) authentication.getPrincipal();

        String tokenInfo = null;
        if (principal instanceof KeycloakPrincipal) {

            KeycloakPrincipal<KeycloakSecurityContext> kPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) principal;
            KeycloakSecurityContext ksc = kPrincipal.getKeycloakSecurityContext();
            IDToken token = ksc.getIdToken();
            AccessToken accessToken = kPrincipal.getKeycloakSecurityContext().getToken();
            tokenInfo = accessToken.getSubject();

            // this value is the one use to call another service as bearer token
            // Authorization : Bearer kcs.getTokenString()
            // use this link to read the token https://jwt.io
            System.out.println(ksc.getTokenString());
            System.out.println(accessToken.getGivenName());
            System.out.println(accessToken.getFamilyName());
        }

        return "userInfo " + tokenInfo;
    }
}
