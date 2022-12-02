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
 * @author Edward P. Legaspi | czetsuya@gmail.com
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
//        if (this.keycloakDeployment != null) {
//            return this.keycloakDeployment;
//        } else {
//            this.keycloakDeployment = KeycloakDeploymentBuilder.build(this.adapterConfig);
//            return this.keycloakDeployment;
//        }
//        List<KeycloakSpringBootProperties.SecurityConstraint> securityConstraints = new ArrayList<>();
//        List<KeycloakSpringBootProperties.SecurityCollection> securityCollections = new ArrayList();
//        KeycloakSpringBootProperties.SecurityCollection securityCollection = new KeycloakSpringBootProperties.SecurityCollection();
//        securityCollection.setPatterns(Arrays.asList("/tenant/branch1/catalog"));
//        securityCollection.setName("catalog management");
//        securityCollections.add(securityCollection);
//        KeycloakSpringBootProperties.SecurityConstraint securityConstraint = new KeycloakSpringBootProperties.SecurityConstraint();
//        securityConstraint.setSecurityCollections(securityCollections);
//        securityConstraint.setAuthRoles(Arrays.asList("ADMIN", "USER"));
//        securityConstraints.add(securityConstraint);
//        keycloakSpringBootProperties.setSecurityConstraints(securityConstraints);
//
//        if (!cache.containsKey(realm)) {
//            File file = new File("realm_json/" + realm + "-keycloak.json");
//            try {
//                InputStream is = new FileInputStream(file);
//                KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(is);
//                cache.put(realm, deployment);
//            } catch (FileNotFoundException e) {
//                throw new RuntimeException(e);
//            }
////            adapterConfig.setRealm("branch1");
////            adapterConfig.setAuthServerUrl("http://host.docker.internal:8080/");
////            adapterConfig.setSslRequired("external");
////            adapterConfig.setResource("web");
////            adapterConfig.setPublicClient(true);
////            adapterConfig.setConfidentialPort(0);
////            Map<String, Object> credMap = new HashMap();
////            credMap.put("secret", "IMIMLUqh7K7e73VuZY8HtVyOm81qSYaf");
////            adapterConfig.setCredentials(credMap);
//
////        // new Policy enforcer instance
////        PolicyEnforcerConfig enforcerConfig = new PolicyEnforcerConfig();
////
////        // list of PathConfig
////        List<PolicyEnforcerConfig.PathConfig> paths = new ArrayList<>();
////
////        // new PathConfig object to be added into paths list
////        PolicyEnforcerConfig.PathConfig pathConfig = new PolicyEnforcerConfig.PathConfig();
////        pathConfig.setPath("/tenant/branch1/catalog");
////
////        List<PolicyEnforcerConfig.MethodConfig> PolicyMethods = new ArrayList<>();
////
////        PolicyEnforcerConfig.MethodConfig mc1 = new PolicyEnforcerConfig.MethodConfig();
////        mc1.setScopes(Arrays.asList("create"));
////        mc1.setMethod("POST");
////        PolicyMethods.add(mc1);
////        PolicyEnforcerConfig.MethodConfig mc2 = new PolicyEnforcerConfig.MethodConfig();
////        mc2.setScopes(Arrays.asList("view"));
////        mc2.setMethod("GET");
////        PolicyMethods.add(mc2);
////
////        pathConfig.setMethods(PolicyMethods);
////        paths.add(pathConfig);
////
////        enforcerConfig.setPaths(paths);
////        adapterConfig.setPolicyEnforcerConfig(enforcerConfig);
//
////            cache.put(realm, KeycloakDeploymentBuilder.build(adapterConfig));
//        }
        return cache.get(realm);
    }

}