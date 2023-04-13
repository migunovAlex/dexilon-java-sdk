package com.migal.trading.dexilon.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class DexilonClientConfiguration {

    @Bean
    DexilonHttpClient customerClient(WebClient.Builder builder, @Value("dexilon.api.url") String dexilonApiUrl) {
        return HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(builder.baseUrl(dexilonApiUrl).build()))
                .build()
                .createClient(DexilonHttpClient.class);
    }

    @Bean
    DexilonChainClient dexilonChainClient(WebClient.Builder builder, @Value("dexilon.chain.url") String dexilonChainUrl) {
        return HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(builder.baseUrl(dexilonChainUrl).build()))
                .build()
                .createClient(DexilonChainClient.class);
    }

}
