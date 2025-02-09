package cane.brothers.tgbot.web;

import cane.brothers.tgbot.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class HttpClientConfig {

    @Bean
    Supplier<ProxySelector> proxySupplier(AppProperties properties) {
        return properties.proxy() == null ?
                () -> null :
                () -> new ProxySelector() {
                    @Override
                    public List<Proxy> select(URI uri) {
                        try {
                            URL url = uri.toURL();
                            if (url.getProtocol().startsWith("http")) {
                                return List.of( new Proxy(Proxy.Type.HTTP, new InetSocketAddress(properties.proxy().hostname(),
                                        properties.proxy().port() == null ? 0 : properties.proxy().port())));
                            } else if (url.getProtocol().startsWith("sock")) {
                                return List.of( new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(properties.proxy().hostname(),
                                        properties.proxy().port() == null ? 0 : properties.proxy().port())));
                            }
                        } catch (MalformedURLException e) {
                            log.error("", e);
                        }
                        return Collections.emptyList();
                    }

                    @Override
                    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                        log.error("Could not established connection a proxy/socks server.");
                    }
                };
    }

    @Bean
    Supplier<Authenticator> authenticatorSupplier(AppProperties properties) {
        return properties.proxy() == null ?
                () -> null :
                () -> new java.net.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new java.net.PasswordAuthentication(properties.proxy().username(), properties.proxy().password().toCharArray());
                    }
                };
    }

    @Bean
    public HttpClient.Builder httpClientBuilder(Supplier<ProxySelector> proxySupplier,
                                                  Supplier<Authenticator> authenticatorSupplier) {

        HttpClient.Builder httpClientBuilder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30));

        // Proxy
        Optional.ofNullable(proxySupplier.get()).ifPresent(httpClientBuilder::proxy);
        Optional.ofNullable(authenticatorSupplier.get()).ifPresent(httpClientBuilder::authenticator);

        return httpClientBuilder;
    }

    @Bean
    public HttpClient httpClient(HttpClient.Builder httpClientBuilder) {
        return httpClientBuilder.build();
    }

    @Bean
    JdkClientHttpRequestFactory clientHttpRequestFactory(HttpClient httpClient) {
        return new JdkClientHttpRequestFactory(httpClient);
    }

    @Bean
    RestClient.Builder restClientBuilder(RestClientBuilderConfigurer restClientBuilderConfigurer,
                                         ClientHttpRequestFactory clientHttpRequestFactory) {
        RestClient.Builder builder = RestClient.builder().requestFactory(clientHttpRequestFactory);
        return restClientBuilderConfigurer.configure(builder);
    }
}
