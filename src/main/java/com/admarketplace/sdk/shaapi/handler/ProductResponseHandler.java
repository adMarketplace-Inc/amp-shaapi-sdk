package com.admarketplace.sdk.shaapi.handler;

import com.admarketplace.sdk.shaapi.model.ProductResponse;
import com.admarketplace.shaapi.api.model.v1.ShaapiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.io.IOException;

/**
 * Handles HTTP responses for product-related API calls within SHAAPI SDK.
 * This class is responsible for interpreting the HTTP response from product-related endpoints,
 * converting them into a {@link com.admarketplace.sdk.shaapi.model.ProductResponse} object that can be easily
 * used within the SDK.
 *
 * <p>The handler deals with various HTTP status codes, ensuring successful responses are correctly
 * parsed and any errors are appropriately transformed into exceptions or error messages within the
 * {@code ProductResponse}.</p>
 *
 * <p>This class leverages Jackson for JSON parsing, converting the raw response body into
 * {@code ProductResponse} instances or extracting error information as needed.</p>
 */
public final class ProductResponseHandler implements HttpClientResponseHandler<ProductResponse> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Processes the {@link ClassicHttpResponse} from product-related API endpoints,
     * converting it into a {@link com.admarketplace.sdk.shaapi.model.ProductResponse} object.
     *
     * @param response The HTTP response to be processed.
     * @return A {@code ProductResponse} representing the outcome of the API call.
    */
    @Override
    public ProductResponse handleResponse(ClassicHttpResponse response) {
        int statusCode = response.getCode();
        if (response.getEntity() == null) {
            return getErrorResponse(statusCode, "An unexpected error occurred. The server response is empty.");
        }

        try (var inputStream = response.getEntity().getContent()) {
            ShaapiResponse shaapi = objectMapper.readValue(inputStream, ShaapiResponse.class);
            return new ProductResponse(statusCode, shaapi.message(), shaapi.results());
        } catch (IOException e) {
            return getErrorResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred during deserialization: " + e.getMessage());
        }
    }

    private ProductResponse getErrorResponse(int statusCode, String message) {
        return new ProductResponse(statusCode, message, null);
    }
}
