package com.admarketplace.sdk.shaapi.model;

import com.admarketplace.shaapi.api.model.v1.Failure;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Represents the response received from SHAAPI service for product-related requests.
 * This class encapsulates the outcome of operations such as product insertion, update, and deletion,
 * providing details about the status of the request, any messages returned by the service, and
 * a list of failures if any issues were encountered during the operation.
 *
 * <p>The {@code ProductResponse} includes:</p>
 * <ul>
 *     <li>{@code httpStatus}: The HTTP status code of the response, indicating the overall success or failure of the request.</li>
 *     <li>{@code message}: A message from SHAAPI service providing additional context about the response, which could be an error message or a success confirmation.</li>
 *     <li>{@code requestId}: The unique request identifier extracted from the {@code x-amp-request-id} HTTP response header, providing traceability for API requests. May be {@code null} if not provided by the service.</li>
 *     <li>{@code results}: A list of {@link com.admarketplace.shaapi.api.model.v1.Failure} objects, each representing a specific failure encountered during the operation. This list is not present if the operation was successful.</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public class ProductResponse {
    private int httpStatus;
    private String message;
    private String requestId;
    private List<Failure> results;

    public ProductResponse(int httpStatus, String message, List<Failure> results) {
        this(httpStatus, message, null, results);
    }
}
