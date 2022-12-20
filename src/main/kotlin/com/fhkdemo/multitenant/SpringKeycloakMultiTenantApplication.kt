package com.fhkdemo.multitenant

import com.fhkdemo.multitenant.configs.PathBasedConfigResolver
import org.keycloak.adapters.KeycloakConfigResolver
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
//@Import(KeycloakSpringBootConfigResolver::class)
class SpringKeycloakMultiTenantApplication {
	@Bean
	@ConditionalOnMissingBean(PathBasedConfigResolver::class)
	fun keycloakConfigResolver(): KeycloakConfigResolver {
		return PathBasedConfigResolver()
	}
}

fun main(args: Array<String>) {
	runApplication<SpringKeycloakMultiTenantApplication>(*args)
}
//{
//
//	@Bean
//	@ConditionalOnMissingBean(PathBasedConfigResolver::class)
//	fun keycloakConfigResolver(): KeycloakConfigResolver {
//		return PathBasedConfigResolver()
//	}
//
//	companion object {
//		@JvmStatic
//		fun main(args: Array<String>) {
//			SpringApplication.run(SmsgatewaymgntApplication::class.java, *args)
//		}
//	}
//}

