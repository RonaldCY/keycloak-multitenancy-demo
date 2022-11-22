package com.czetsuyatech.configs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.OIDCHttpFacade;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * @author Edward P. Legaspi | czetsuya@gmail.com
 */
@ConditionalOnProperty(prefix = "keycloak.config", name = "resolver", havingValue = "path")
public class PathBasedConfigResolver implements KeycloakConfigResolver {

//    // env variables from yaml
//    @Value("${keycloak.realm}")
//    private String realm;
//    @Value("${keycloak.auth-server-url}")
//    private String auth_server_url;
//
//    @Value("${keycloak.resource}")
//    private String resource;
//
//    @Value("${keycloak.credentials.secret}")
//    private String secret;

    private final ConcurrentHashMap<String, KeycloakDeployment> cache = new ConcurrentHashMap<>();

    @SuppressWarnings("unused")
    private static AdapterConfig adapterConfig = new AdapterConfig();

    @Override
    public KeycloakDeployment resolve(OIDCHttpFacade.Request request) {

        String path = request.getURI();
        int multitenantIndex = path.indexOf("tenant/");

        if (multitenantIndex == -1) {
            throw new IllegalStateException("Not able to resolve realm from the request path!");
        }

        String realm = path.substring(path.indexOf("tenant/")).split("/")[1];
        if (realm.contains("?")) {
            realm = realm.split("\\?")[0];
        }

        if (!cache.containsKey(realm)) {
            File file = new File("realm_json/" + realm + "-keycloak.json");
            try {
                InputStream is = new FileInputStream(file);
                cache.put(realm, KeycloakDeploymentBuilder.build(is));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return cache.get(realm);
    }

    //delete later
    static void setAdapterConfig(AdapterConfig adapterConfig) {
        PathBasedConfigResolver.adapterConfig = adapterConfig;
    }

}