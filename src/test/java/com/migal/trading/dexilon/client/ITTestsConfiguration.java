package com.migal.trading.dexilon.client;

import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@ContextConfiguration
public class ITTestsConfiguration {

    @Bean
    public DexilonHttpClient testWebClient() {
        WebClient.Builder builder = WebClient.builder();
        return HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(builder.baseUrl("https://api.staging.dexilon.io/api/v1/").build()))
                .build()
                .createClient(DexilonHttpClient.class);
    }

}
