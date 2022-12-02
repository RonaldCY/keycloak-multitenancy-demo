package com.fhkdemo.multitenant.configs;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.fhkdemo.multitenant.util.Global;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import static org.keycloak.adapters.KeycloakDeploymentBuilder.loadAdapterConfig;

/**
 * @author ronald chan | ncronaldchan@gmail.com
 */
@ConditionalOnProperty(prefix = "keycloak.config", name = "resolver", havingValue = "path")
public class PathBasedConfigResolver implements KeycloakConfigResolver {

    private final ConcurrentHashMap<String, KeycloakDeployment> cache = new ConcurrentHashMap<>();

    private KeycloakDeployment keycloakDeployment;
    @Autowired(
            required = false
    )
    private AdapterConfig adapterConfig;
    @Autowired
    private Global global;

    @Override
    public KeycloakDeployment resolve(HttpFacade.Request request) {

        String path = request.getURI();
        int multitenantIndex = path.indexOf("tenant/");
        String realm = "";

        if (multitenantIndex == -1) {
            realm = "carrier";
//            throw new IllegalStateException("Not able to resolve realm from the request path!");
        } else {
            realm = path.substring(path.indexOf("tenant/")).split("/")[1];
            if (realm.contains("?")) {
                realm = realm.split("\\?")[0];
            }
        }

        global.setRealm(realm);

        if (!cache.containsKey(realm)) {
            File file = new File("realm_json/" + realm + "-keycloak.json");
            try {
                InputStream is = new FileInputStream(file);
                AdapterConfig realmAdapter = loadAdapterConfig(is);
                adapterConfig.setRealm(realmAdapter.getRealm());
                Map<String, Object> credMap = new HashMap();
                credMap.put("secret", realmAdapter.getCredentials().get("secret"));
                adapterConfig.setCredentials(credMap);
                adapterConfig.setPolicyEnforcerConfig(realmAdapter.getPolicyEnforcerConfig());

                KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(this.adapterConfig);
                cache.put(realm, deployment);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return cache.get(realm);
    }

}
