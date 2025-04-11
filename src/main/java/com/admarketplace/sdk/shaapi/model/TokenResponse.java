package com.admarketplace.sdk.shaapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the response received from SHAAPI service for token retrieval requests.
 * This class encapsulates the details of the authentication token, including its value and expiration,
 * as well as any error information if the token retrieval was unsuccessful.
 *
 * <p>The {@code TokenResponse} includes:</p>
 * <ul>
 *     <li>{@code httpStatus}: The HTTP status code of the response, indicating the overall success or failure of the request.</li>
 *     <li>{@code message}: A message from SHAAPI service providing additional context about the failure if any.</li>
 *     <li>{@code accessToken}: The authentication token value, which can be used in subsequent API requests.</li>
 *     <li>{@code expiresIn}: The number of seconds until the token expires, after which it can no longer be used.</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public class TokenResponse {
    private int httpStatus;
    private String message;
    private String accessToken;
    private Integer expiresIn;
}
