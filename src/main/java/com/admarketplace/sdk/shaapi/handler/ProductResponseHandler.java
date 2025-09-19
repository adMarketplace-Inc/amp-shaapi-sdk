package com.admarketplace.sdk.shaapi.handler;

import com.admarketplace.sdk.shaapi.model.ProductResponse;
import com.admarketplace.shaapi.api.model.v1.Failure;
import com.admarketplace.shaapi.api.model.v1.ShaapiResponse;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * Handles HTTP responses for product-related API calls within SHAAPI SDK.
 * This class is responsible for interpreting the HTTP response from product-related endpoints,
 * converting them into a {@link com.admarketplace.sdk.shaapi.model.ProductResponse} object that can be easily
 * used within the SDK.
 *
 * <p>The handler deals with various HTTP status codes, ensuring successful responses are correctly
 * parsed and any errors are appropriately transformed into exceptions or error messages within the
 * {@code ProductResponse}. Additionally, it extracts the request ID from the {@code x-amp-request-id}
 * HTTP response header when available.</p>
 *
 * <p>This class leverages Jackson for JSON parsing, converting the raw response body into
 * {@code ProductResponse} instances or extracting error information as needed.</p>
 */
public final class ProductResponseHandler implements HttpClientResponseHandler<ProductResponse> {
    private static final String REQUEST_ID_HEADER_NAME = "x-amp-request-id";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Processes the {@link ClassicHttpResponse} from product-related API endpoints,
     * converting it into a {@link com.admarketplace.sdk.shaapi.model.ProductResponse} object.
     *
     * <p>The method extracts the request ID from the {@code x-amp-request-id} header if present,
     * providing traceability for API requests.</p>
     *
     * @param response The HTTP response to be processed.
     * @return A {@code ProductResponse} representing the outcome of the API call, including
     * the request ID if available in the response headers.
     */
    @Override
    public ProductResponse handleResponse(ClassicHttpResponse response) {
        int statusCode = response.getCode();
        String requestId = extractRequestId(response);

        if (response.getEntity() == null) {
            return getErrorResponse(statusCode, "An unexpected error occurred. The server response is empty.", requestId);
        }

        String responseBody;
        try (var inputStream = response.getEntity().getContent()) {
            responseBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return getErrorResponse(statusCode, "I/O error while reading response body: " + e.getMessage(), requestId);
        }

        try {
            ShaapiResponse<List<Failure>> shaapi = objectMapper.readValue(responseBody, new TypeReference<>() {
            });
            return new ProductResponse(statusCode, shaapi.message(), requestId, shaapi.results());
        } catch (JacksonException e) {
            String errorMessage = "An unexpected error occurred during deserialization: "
                    + e.getMessage()
                    + " response body: "
                    + responseBody;
            return getErrorResponse(statusCode, errorMessage, requestId);
        }
    }

    private ProductResponse getErrorResponse(int statusCode, String message, String requestId) {
        return new ProductResponse(statusCode, message, requestId, null);
    }

    private String extractRequestId(ClassicHttpResponse response) {
        return Optional.ofNullable(response.getFirstHeader(REQUEST_ID_HEADER_NAME))
                .map(Header::getValue)
                .orElse(null);
    }
}
