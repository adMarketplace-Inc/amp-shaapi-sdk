package com.admarketplace.sdk.shaapi.handler;

import com.admarketplace.authorization.api.model.v1.AuthenticationResponse;
import com.admarketplace.sdk.shaapi.model.TokenResponse;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Handles HTTP responses for token retrieval requests within SHAAPI SDK.
 * This class is tasked with processing the HTTP response received from the token endpoint,
 * extracting the authentication token and other relevant information, and packaging it into a
 * {@link com.admarketplace.sdk.shaapi.model.TokenResponse} object.
 *
 * <p>The handler ensures that both successful token acquisitions and errors are processed correctly,
 * allowing for graceful handling of various HTTP status codes and response bodies. In the case of successful
 * token retrieval, information such as the token itself and its expiry time are parsed.
 * For error scenarios, the handler constructs a {@code TokenResponse} that encapsulates the error details
 * including the HTTP status code and error message.</p>
 *
 * <p>This class leverages Jackson for JSON parsing, converting the raw response body into
 * {@code TokenResponse} instances or extracting error information as needed.</p>
 */
public final class TokenResponseHandler implements HttpClientResponseHandler<TokenResponse> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Processes the {@link ClassicHttpResponse} from the token endpoint,
     * extracting the necessary information and creating a {@link TokenResponse} object.
     *
     * @param response The HTTP response to be processed.
     * @return A {@code TokenResponse} encapsulating the token details or error information.
     */
    @Override
    public TokenResponse handleResponse(ClassicHttpResponse response) {
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
            AuthenticationResponse auth = objectMapper.readValue(responseBody, AuthenticationResponse.class);
            return new TokenResponse(statusCode, auth.message(), auth.accessToken(), auth.expiresIn());
        } catch (JacksonException e) {
            String errorMessage = "An unexpected error occurred during deserialization: "
                    + e.getMessage()
                    + " response body: "
                    + responseBody;
            return getErrorResponse(statusCode, errorMessage);
        }
    }

    private TokenResponse getErrorResponse(int statusCode, String message) {
        return new TokenResponse(statusCode, message, null, null);
    }
}
