package com.eventslk;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class ProdProfileLoadTest {

    @Test
    void shouldLoadProdProfileWhenRequiredVariablesArePresent() {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(Main.class)
                .profiles("prod")
                .properties(
                        "SERVER_PORT=0",
                        "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://discovery:changeit@localhost:8761/eureka/",
                        "eureka.client.register-with-eureka=false",
                        "eureka.client.fetch-registry=false",
                        "DISCOVERY_ADMIN_USER=test",
                        "DISCOVERY_ADMIN_PASSWORD=test"
                )
                .run()) {
            assertThat(context.isActive()).isTrue();
        }
    }
}
