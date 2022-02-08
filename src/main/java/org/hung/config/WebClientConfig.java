package org.hung.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebClientConfig {
	
	@Value("${hkjc.info-api-domain}/rs/info/v1")
	private String infoApiBaseUrl;
	
	@Bean(name="infoapi-webclient")
	public WebClient infoapiWebClient (
		ReactiveClientRegistrationRepository clientRegistrations,
		ReactiveOAuth2AuthorizedClientService clientService
	) {
		ReactiveOAuth2AuthorizedClientManager manager = 
				new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrations,clientService);

	    //ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider =
	    //        ReactiveOAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build();

	    //manager.setAuthorizedClientProvider(authorizedClientProvider);

	    ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
	            new ServerOAuth2AuthorizedClientExchangeFilterFunction(manager);
	    
	    oauth.setDefaultClientRegistrationId("racing-touch");
	    //ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
	    //        new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations, authorizedClients);
	    
	    WebClient webclient = WebClient.builder()
		  .baseUrl(infoApiBaseUrl)
		  .filter(oauth)
		  .build();
	    
	    return webclient;
	}

}
