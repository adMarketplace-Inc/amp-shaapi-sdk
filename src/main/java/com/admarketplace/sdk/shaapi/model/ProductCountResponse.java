package com.admarketplace.sdk.shaapi.model;

import com.admarketplace.shaapi.api.model.v1.ProductCount;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Response object returned by the <i>products/count</i> endpoint.
 *
 * <ul>
 *     <li><b>httpStatus</b> – HTTP status code delivered by SHAAPI service.</li>
 *     <li><b>message</b> – human-readable description of the outcome.</li>
 *     <li><b>result</b> – product count summary containing account ID and available product count.</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public class ProductCountResponse {
    private int httpStatus;
    private String message;
    private ProductCount result;
}
