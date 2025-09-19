package com.admarketplace.sdk.shaapi.client;

import com.admarketplace.sdk.shaapi.model.AuthType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.HttpEntities;

import java.io.IOException;
import java.net.URI;

/**
 * Provides a mechanism for executing HTTP requests within the Shopping Ads Asset API (SHAAPI) SDK.
 * This class is designed to abstract the complexities involved in sending different types of HTTP requests
 * and processing their responses.
 *
 * <p>It supports various HTTP methods, including POST, PUT, and DELETE, allowing for flexible interactions
 * with web services. The class manages the creation of HTTP requests, setting of headers, and serialization
 * and deserialization of request and response bodies.</p>
 */
public abstract class HttpExecutor {

    private static final String AUTH_HEADER = "Authorization";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String ACCEPT_HEADER = "Accept";
    private static final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * Executes an HTTP request with the specified parameters and returns the response.
     * The method handles the instantiation of the HTTP client, setting up the request
     * according to the provided method, URL, headers, body, and executing the request.
     *
     * @param uri The URI against which the request is to be executed.
     * @param method The HTTP method to use for the request.
     * @param authType The type of authentication required for the request.
     * @param authHeader The authorization header value.
     * @param body The request body, which may be {@code null} for methods that do not require it.
     * @param responseHandler A handler for processing the response into a desired format or object.
     * @param <T> The type of the response object expected.
     * @return An instance of {@code T}, as processed by the provided response handler.
     * @throws IOException If an I/O error occurs during request execution.
     */
    protected <T> T sendRequest(URI uri, Method method, AuthType authType, String authHeader, Object body, HttpClientResponseHandler<T> responseHandler) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            ClassicHttpRequest request = createRequest(uri, method, authType, authHeader, body);
            return httpClient.execute(request, responseHandler);
        }
    }

    private ClassicHttpRequest createRequest(URI uri, Method method, AuthType authType, String authHeader, Object body) throws JsonProcessingException {
        var request = switch (method) {
            case GET -> new HttpGet(uri);
            case POST -> new HttpPost(uri);
            case PUT -> new HttpPut(uri);
            case DELETE -> new HttpDelete(uri);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };

        addHeaders(request, authType, authHeader);
        addBody(request, body);
        return request;
    }

    private void addHeaders(ClassicHttpRequest request, AuthType authType, String authHeader) {
        request.setHeader(AUTH_HEADER, authType.getPrefix() + authHeader);
        request.setHeader(CONTENT_TYPE_HEADER, ContentType.APPLICATION_JSON.getMimeType());
        request.setHeader(ACCEPT_HEADER, ContentType.APPLICATION_JSON.getMimeType());
    }

    private void addBody(ClassicHttpRequest request, Object body) throws JsonProcessingException {
        if (body != null && request != null) {
            request.setEntity(HttpEntities.create(objectMapper.writeValueAsString(body), ContentType.APPLICATION_JSON));
        }
    }
}

