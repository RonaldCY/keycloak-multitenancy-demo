package com.fhkdemo.multitenant

import org.keycloak.adapters.KeycloakConfigResolver
import org.keycloak.adapters.KeycloakDeployment
import org.keycloak.adapters.KeycloakDeploymentBuilder
import org.keycloak.adapters.spi.HttpFacade
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties
import org.keycloak.representations.adapters.config.AdapterConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap

@ConditionalOnProperty(prefix = "keycloak.config", name = ["resolver"], havingValue = "path")
class PathBasedConfigResolver: KeycloakConfigResolver {
    private val cache = ConcurrentHashMap<String, KeycloakDeployment>()

    @Autowired(required = false)
    private val adapterConfig: AdapterConfig? = null

    override fun resolve(request: HttpFacade.Request): KeycloakDeployment? {
        val path = request.uri
//        val multitenantIndex = path.indexOf("tenant/")
        var realm = "internal"
        if (path.indexOf("tenant/") != -1) {
            realm = path.substring(path.indexOf("tenant/")).split("/".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[1]
            if (realm.contains("?")) {
                realm = realm.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            }
        } else if (path.indexOf("carrier/") != -1) {
            realm = "carrier"
        } else if (path.indexOf("internal/") == -1 && path.indexOf("check") == -1) {
            throw IllegalStateException("Not able to resolve realm from the request path!");
        }

        if (!cache.containsKey(realm)) {
            val file = File("realm_json/$realm-keycloak.json")
            try {
                val `is`: InputStream = FileInputStream(file)
                val realmAdapter = KeycloakDeploymentBuilder.loadAdapterConfig(`is`)
                adapterConfig!!.realm = realmAdapter.realm
                val credMap = hashMapOf<String, Any?>()
                credMap["secret"] = realmAdapter.credentials["secret"]
                adapterConfig.credentials = credMap
                adapterConfig.policyEnforcerConfig = realmAdapter.policyEnforcerConfig
                val deployment = KeycloakDeploymentBuilder.build(adapterConfig)
                cache[realm] = deployment
            } catch (e: FileNotFoundException) {
                throw RuntimeException(e)
            }
        }
        return cache[realm]
    }
}