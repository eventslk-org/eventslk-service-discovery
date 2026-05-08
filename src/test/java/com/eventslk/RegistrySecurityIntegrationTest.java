package com.eventslk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.profiles.active=dev",
                "DISCOVERY_ADMIN_USER=admin",
                "DISCOVERY_ADMIN_PASSWORD=secret"
        }
)
class RegistrySecurityIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldAllowRegistryWithoutAuthenticationInDev() {
        ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldAllowActuatorMetricsWithoutAuthenticationInDev() {
        ResponseEntity<String> registryResponse = restTemplate.getForEntity("/", String.class);
        ResponseEntity<String> metricsResponse = restTemplate.getForEntity("/actuator/metrics", String.class);

        assertThat(registryResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(registryResponse.getBody()).contains("Eureka");

        assertThat(metricsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(metricsResponse.getBody()).contains("names");
    }
}
