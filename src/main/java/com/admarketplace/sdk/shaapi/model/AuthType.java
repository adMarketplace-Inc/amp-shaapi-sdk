package com.admarketplace.sdk.shaapi.model;

import lombok.Getter;

/**
 * Enumerates the types of authentication supported by the Shopping Ads Asset API (SHAAPI) SDK.
 * This enumeration is used to specify the authentication method to be applied in HTTP request headers
 * when interacting with SHAAPI service.
 *
 * <p>Supported authentication types include:</p>
 * <ul>
 *     <li>{@code BEARER}: Used for token-based authentication. The 'Bearer' scheme is typically
 *     used with OAuth 2.0 access tokens.</li>
 *     <li>{@code BASIC}: Utilized for basic authentication where a user ID and password are
 *     encoded and passed in the header.</li>
 * </ul>
 */
@Getter
public enum AuthType {
    BEARER("Bearer "),
    BASIC("Basic ");

    private final String prefix;

    AuthType(String prefix) {
        this.prefix = prefix;
    }
}
