package com.admarketplace.sdk.shaapi.client;

import com.admarketplace.sdk.shaapi.client.impl.ShaapiClientV1;
import com.admarketplace.sdk.shaapi.model.ProductResponse;
import com.admarketplace.sdk.shaapi.model.TokenResponse;
import com.admarketplace.shaapi.api.model.v1.Product;
import com.admarketplace.shaapi.api.model.v1.ProductIdentifier;

import java.util.Collection;

/**
 * Interface for interacting with the Shopping Ads Asset API (SHAAPI). It outlines methods for authentication,
 * and managing products within SHAAPI system. Implementations are responsible for the details of
 * communication with SHAAPI endpoints, including handling of HTTP requests and responses.
 *
 * <p>Typical workflow involves:</p>
 * <ol>
 *    <li>Authenticating to obtain a token via {@link #getToken(String)}</li>
 *    <li>Using the token to upsert (insert or update) products with {@link #upsertProducts(String, String, Collection)}</li>
 *    <li>Using the token to delete products with {@link #deleteProducts(String, String, Collection)}</li>
 * </ol>
 *
 * @see ShaapiClientV1 for the concrete implementation
 */
public interface ShaapiClient {

   /**
    * Retrieves an authentication token using the provided Base64-encoded credentials.
    *
    * @param encodedCredentials Base64-encoded client credentials ("clientId:clientSecret")
    * @return A {@link TokenResponse} containing the access token and related information,
    *         or an appropriate response indicating failure to authenticate.
    */
   TokenResponse getToken(String encodedCredentials);

   /**
    * Upsert a collection of products within SHAAPI.
    * This operation can insert new products or update existing ones based on the provided data.
    *
    * @param accountId The account identifier for which products are being upserted.
    * @param accessToken A valid access token for SHAAPI service authentication.
    * @param products A collection of {@link Product} objects to be upserted.
    * @return A {@link ProductResponse} containing the result of the upsert operation,
    *         including success or failure details for individual products.
    */
   ProductResponse upsertProducts(String accountId, String accessToken, Collection<Product> products);

   /**
    * Delete a collection of products from SHAAPI.
    *
    * @param accountId The account identifier from which products are being deleted.
    * @param accessToken A valid access token for SHAAPI service authentication.
    * @param products A collection of {@link ProductIdentifier} objects representing the products to be deleted.
    * @return A {@link ProductResponse} containing the result of the delete operation,
    *         including success or failure details for individual product deletions.
    */
   ProductResponse deleteProducts(String accountId, String accessToken, Collection<ProductIdentifier> products);
}
