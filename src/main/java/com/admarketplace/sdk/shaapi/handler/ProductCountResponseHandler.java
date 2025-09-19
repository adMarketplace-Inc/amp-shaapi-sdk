package com.admarketplace.sdk.shaapi.handler;

import com.admarketplace.sdk.shaapi.model.ProductCountResponse;
import com.admarketplace.shaapi.api.model.v1.ProductCount;
import com.admarketplace.shaapi.api.model.v1.ShaapiResponse;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Handles HTTP responses for product count API calls within SHAAPI SDK.
 * This class is responsible for interpreting the HTTP response from the products/count endpoint,
 * converting them into a {@link ProductCountResponse} object that can be easily
 * used within the SDK.
 *
 * <p>The handler deals with various HTTP status codes, ensuring successful responses are correctly
 * parsed and any errors are appropriately transformed into exceptions or error messages within the
 * {@code ProductCountResponse}.</p>
 *
 * <p>This class leverages Jackson for JSON parsing, converting the raw response body into
 * {@code ProductCountResponse} instances or extracting error information as needed.</p>
 */
public final class ProductCountResponseHandler implements HttpClientResponseHandler<ProductCountResponse> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Processes the {@link ClassicHttpResponse} from the products/count API endpoint,
     * converting it into a {@link ProductCountResponse} object.
     *
     * @param response The HTTP response to be processed.
     * @return A {@code ProductCountResponse} representing the outcome of the API call.
     */
    @Override
    public ProductCountResponse handleResponse(ClassicHttpResponse response) {
        int statusCode = response.getCode();

        if (response.getEntity() == null) {
            return getErrorResponse(statusCode, "An unexpected error occurred. The server response is empty.");
        }

        String responseBody;
        try (var inputStream = response.getEntity().getContent()) {
            responseBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return getErrorResponse(statusCode, "I/O error while reading response body: " + e.getMessage());
        }

        try {
            ShaapiResponse<ProductCount> shaapi = objectMapper.readValue(responseBody, new TypeReference<>() {
            });
            return new ProductCountResponse(statusCode, shaapi.message(), shaapi.results());
        } catch (JacksonException e) {
            String errorMessage = "An unexpected error occurred during deserialization: "
                    + e.getMessage()
                    + " response body: "
                    + responseBody;
            return getErrorResponse(statusCode, errorMessage);
        }
    }

    private ProductCountResponse getErrorResponse(int statusCode, String message) {
        return new ProductCountResponse(statusCode, message, null);
    }
}
