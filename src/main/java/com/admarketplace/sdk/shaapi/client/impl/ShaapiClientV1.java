package com.admarketplace.sdk.shaapi.client.impl;

import com.admarketplace.sdk.shaapi.client.HttpExecutor;
import com.admarketplace.sdk.shaapi.client.ShaapiClient;
import com.admarketplace.sdk.shaapi.handler.ProductResponseHandler;
import com.admarketplace.sdk.shaapi.handler.TokenResponseHandler;
import com.admarketplace.sdk.shaapi.model.AuthType;
import com.admarketplace.sdk.shaapi.model.ProductResponse;
import com.admarketplace.sdk.shaapi.model.TokenResponse;
import com.admarketplace.shaapi.api.model.v1.Product;
import com.admarketplace.shaapi.api.model.v1.ProductIdentifier;
import lombok.Builder;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.net.URIBuilder;

import java.net.URI;
import java.util.Collection;

import static org.apache.hc.core5.http.Method.DELETE;
import static org.apache.hc.core5.http.Method.POST;
import static org.apache.hc.core5.http.Method.PUT;

/**
 * Provides an implementation for the {@link ShaapiClient} interface, facilitating interactions with the Shopping Ads Asset API (SHAAPI).
 * This implementation leverages an {@link HttpExecutor} to perform HTTP requests for various operations such as authentication,
 * product insertion, updates, and deletion within SHAAPI system.
 *
 * <p>Key operations include:</p>
 * <ul>
 *     <li>Obtaining an authentication token through {@link #getToken(String)}.</li>
 *     <li>Upserting (inserting or updating) products via {@link #upsertProducts(String, String, Collection)}.</li>
 *     <li>Deleting products using {@link #deleteProducts(String, String, Collection)}.</li>
 * </ul>
 *
 * <p>Each method constructs and sends HTTP requests to SHAAPI service endpoints, handling URI construction, request execution,
 * and response parsing through the {@link HttpExecutor}.</p>
 *
 * <p>Authentication to SHAAPI service is managed via Base64-encoded credentials for token retrieval, and bearer tokens for other
 * API interactions.</p>
 *
 * <p>Errors and exceptions encountered during HTTP request execution are handled gracefully, with error information encapsulated in response objects.</p>
 */
@Builder
public class ShaapiClientV1 extends HttpExecutor implements ShaapiClient {
    private static final String API_VERSION = "v1";

    private final URI shaapiUrl;
    private final URI authServiceUrl;

    private final TokenResponseHandler tokenResponseHandler = new TokenResponseHandler();
    private final ProductResponseHandler productResponseHandler = new ProductResponseHandler();

    @Override
    public TokenResponse getToken(String encodedCredentials) {
        try {
            var uri = new URIBuilder(authServiceUrl).setPathSegments("oauth2", API_VERSION, "auth").build();
            return sendRequest(uri, POST, AuthType.BASIC, encodedCredentials, null, tokenResponseHandler);
        } catch (Exception e) {
            return new TokenResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, getMessage(e), null, null);
        }
    }

    @Override
    public ProductResponse upsertProducts(String accountId, String accessToken, Collection<Product> products) {
        return sendProductsRequest(PUT, accountId, accessToken, products);
    }

    @Override
    public ProductResponse deleteProducts(String accountId, String accessToken, Collection<ProductIdentifier> products) {
        return sendProductsRequest(DELETE, accountId, accessToken, products);
    }

    private ProductResponse sendProductsRequest(Method method, String accountId, String accessToken, Collection<? extends ProductIdentifier> products) {
        try {
            var uri = new URIBuilder(shaapiUrl).setPathSegments("asset", API_VERSION, accountId, "products").build();
            return sendRequest(uri, method, AuthType.BEARER, accessToken, products, productResponseHandler);
        } catch (Exception e) {
            return new ProductResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, getMessage(e), null);
        }
    }

    private String getMessage(Exception e) {
        return "An unexpected error occurred during the request: " + e.getMessage();
    }
}
