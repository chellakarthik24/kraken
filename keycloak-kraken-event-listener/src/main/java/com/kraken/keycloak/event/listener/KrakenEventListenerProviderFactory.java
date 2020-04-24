package com.kraken.keycloak.event.listener;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import static java.lang.System.getenv;
import static java.lang.System.setOut;
import static java.util.Objects.requireNonNull;


public class KrakenEventListenerProviderFactory implements EventListenerProviderFactory {

    private String keycloakUrl;
    private String[] urls;
    private String clientId;
    private String clientSecret;
    private String realm;

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {

//        final Keycloak keycloak = KeycloakBuilder.builder()
//                .serverUrl(keycloakUrl + "/u/auth")
//                .realm(realm)
//                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
//                .clientId(clientId)
//                .clientSecret(clientSecret)
//                .build();

//        System.out.println(keycloak.tokenManager().getAccessTokenString());
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

        System.out.println(urls);
        System.out.println(keycloakUrl);
        System.out.println(clientId);
        return new KrakenEventListenerProvider(/*webClients*/);
    }

    @Override
    public void init(Config.Scope scope) {
        this.keycloakUrl = requireNonNull(getenv("KRAKEN_SECURITY_URL"));
        this.urls = requireNonNull(getenv("KRAKEN_URLS")).split(",");
        this.clientId = requireNonNull(getenv("KRAKEN_SECURITY_API_ID"));
        this.clientSecret = requireNonNull(getenv("KRAKEN_SECURITY_API_SECRET"));
        this.realm = requireNonNull(getenv("KRAKEN_SECURITY_REALM"));
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