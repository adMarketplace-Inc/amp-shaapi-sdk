package com.admarketplace.sdk.shaapi.client.factory;

import com.admarketplace.sdk.shaapi.client.ShaapiClient;
import com.admarketplace.sdk.shaapi.client.impl.ShaapiClientV1;
import lombok.experimental.UtilityClass;

import java.net.URI;
import java.net.URL;

/**
 * A utility class to instantiate and configure instances of {@link ShaapiClient}.
 * This factory ensures that all {@link ShaapiClient} instances are correctly initialized with the necessary URLs and API version.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * String authServiceUrl = "https://auth.example.com";
 * String shaapiServiceUrl = "https://api.example.com";
 * String apiVersion = "v1";
 * ShaapiClient shaapiClient = ShaapiClientFactory.getInstance(authServiceUrl, shaapiServiceUrl, apiVersion);
 * }</pre>
 */
@UtilityClass
public class ShaapiClientFactory {

    /**
     * Creates a new {@link ShaapiClient} instance.
     *
     * @param authURL The base URL provided by adMarketplace for the authentication service.
     * @param shaapiURL The base URL provided by adMarketplace for SHAAPI service.
     * @param apiVersion The version of the API to be used (e.g., "v1").
     * @return A configured {@link ShaapiClient}.
     * @throws IllegalArgumentException If any of the parameters are invalid or incomplete information was provided.
     */
    public static ShaapiClient getInstance(String authURL, String shaapiURL, String apiVersion) {
        validateVersion(apiVersion);
        return ShaapiClientV1.builder()
                .authServiceUrl(validateURL(authURL))
                .shaapiUrl(validateURL(shaapiURL))
                .build();
    }

    private static URI validateURL(String url) {
        try {
            return new URL(url).toURI();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URLs. Please inform the correct URLs provided by adMarketplace.");
        }
    }

    private static void validateVersion(String apiVersion) {
        if (!"v1".equals(apiVersion)) {
            throw new IllegalArgumentException("Invalid API version. This SDK supports only 'v1'.");
        }
    }
}
