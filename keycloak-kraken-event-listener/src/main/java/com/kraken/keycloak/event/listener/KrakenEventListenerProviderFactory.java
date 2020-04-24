package com.kraken.keycloak.event.listener;

import org.keycloak.Config;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import static java.lang.System.getenv;
import static java.util.Objects.requireNonNull;


public class KrakenEventListenerProviderFactory implements EventListenerProviderFactory {


    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        final String[] keycloakUrl = requireNonNull(getenv("KRAKEN_SECURITY_URL")).split(",");
        final String[] urls = requireNonNull(getenv("KRAKEN_URLS")).split(",");
        final String clientId = requireNonNull(getenv("KRAKEN_SECURITY_API_ID"));
        final String clientSecret = requireNonNull(getenv("KRAKEN_SECURITY_API_SECRET"));
        final String realm = requireNonNull(getenv("KRAKEN_SECURITY_REALM"));

        final Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakUrl + "/u/auth")
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();

//        final List<WebClient> webClients = new ArrayList<>();
//        for (String url : urls) {
//            webClients.add(WebClient.builder()
//                    .baseUrl(url)
//                    .filter((clientRequest, exchangeFunction) -> {
//                        return exchangeFunction.exchange(ClientRequest.from(clientRequest)
//                                .headers(headers -> headers.setBearerAuth(keycloak.tokenManager().getAccessTokenString()))
//                                .build());
//                    })
//                    .build());
//        }


        return new KrakenEventListenerProvider(/*webClients*/);
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return "kraken_event_listener";
    }
}