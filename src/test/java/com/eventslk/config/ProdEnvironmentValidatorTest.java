package com.eventslk.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProdEnvironmentValidatorTest {

    private final ProdEnvironmentValidator validator = new ProdEnvironmentValidator(new MockEnvironment());

    @Test
    void shouldRejectUnsupportedProfile() {
        assertThatThrownBy(() -> validator.validateProfilesAndEnvironment(new String[]{"qa"}, "http://localhost:8761/eureka/"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Unsupported profile");
    }

    @Test
    void shouldRequireDefaultZoneInProd() {
        assertThatThrownBy(() -> validator.validateProfilesAndEnvironment(new String[]{"prod"}, ""))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("EUREKA_CLIENT_SERVICEURL_DEFAULTZONE");
    }

    @Test
    void shouldAllowDevWithoutDefaultZone() {
        assertThatCode(() -> validator.validateProfilesAndEnvironment(new String[]{"dev"}, null))
                .doesNotThrowAnyException();
    }
}
