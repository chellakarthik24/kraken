.EXPORT_ALL_VARIABLES:

package:
	mvn package

deploy-dev: package
	cp target/kraken-keycloak-event-listener-jar-with-dependencies.jar ../development/keycloak/deployments/kraken-keycloak-event-listener.jar
