package com.eventslk.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

@Component
public class ProdEnvironmentValidator implements ApplicationRunner {

    private static final Set<String> ALLOWED_PROFILES = Set.of("dev", "prod");

    private final Environment environment;

    public ProdEnvironmentValidator(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        validateProfilesAndEnvironment(
                environment.getActiveProfiles(),
                environment.getProperty("EUREKA_CLIENT_SERVICEURL_DEFAULTZONE")
        );
    }

    void validateProfilesAndEnvironment(String[] activeProfiles, String defaultZone) {
        for (String profile : activeProfiles) {
            if (!ALLOWED_PROFILES.contains(profile)) {
                throw new IllegalStateException(
                        "Unsupported profile '%s'. Allowed profiles: %s".formatted(profile, ALLOWED_PROFILES)
                );
            }
        }

        if (Arrays.asList(activeProfiles).contains("prod") && (defaultZone == null || defaultZone.isBlank())) {
            throw new IllegalStateException(
                    "Missing required env var EUREKA_CLIENT_SERVICEURL_DEFAULTZONE when running with prod profile"
            );
        }
    }
}
