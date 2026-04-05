package com.homni.featuretoggle.infrastructure.security.sso;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.oidc")
public record SsoProperties(String defaultAdminEmail) {

    public void validate(String issuerUri) {
        if (issuerUri == null || issuerUri.isBlank()) {
            throw new SsoConfigurationException(
                    "OIDC_ISSUER_URI is required. Set spring.security.oauth2.resourceserver.jwt.issuer-uri "
                            + "to your OIDC provider's issuer URL (e.g. https://auth.example.com/realms/myrealm)");
        }
        if (defaultAdminEmail == null || defaultAdminEmail.isBlank()) {
            throw new SsoConfigurationException(
                    "OIDC_ADMIN_EMAIL is required. Set app.oidc.default-admin-email "
                            + "to the email of the first admin user");
        }
    }
}
