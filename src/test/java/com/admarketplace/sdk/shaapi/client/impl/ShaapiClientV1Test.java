package com.admarketplace.sdk.shaapi.client.impl;

import com.admarketplace.sdk.shaapi.client.ShaapiClient;
import com.admarketplace.sdk.shaapi.client.factory.ShaapiClientFactory;
import com.admarketplace.sdk.shaapi.handler.ProductResponseHandler;
import com.admarketplace.sdk.shaapi.handler.TokenResponseHandler;
import com.admarketplace.sdk.shaapi.model.ProductResponse;
import com.admarketplace.sdk.shaapi.model.TokenResponse;
import com.admarketplace.sdk.shaapi.util.TestUtils;
import com.admarketplace.shaapi.api.model.v1.Failure;
import com.admarketplace.shaapi.api.model.v1.Product;
import com.admarketplace.shaapi.api.model.v1.ProductIdentifier;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShaapiClientV1Test {

    @Mock
    private CloseableHttpClient httpClient;
    private MockedStatic<HttpClients> mockedStatic;

    private ShaapiClient shaapiClient;

    @BeforeEach
    void setUp() {
        shaapiClient = ShaapiClientFactory.getInstance(TestUtils.AUTH_URL, TestUtils.SHAAPI_URL, TestUtils.V1);

        mockedStatic = mockStatic(HttpClients.class);
        mockedStatic.when(HttpClients::createDefault).thenReturn(httpClient);
    }

    @AfterEach
    void tearDown() {
        if (mockedStatic != null) {
            mockedStatic.close();
        }
    }

    @Test
    void testGetTokenWithValidCredentials() throws Exception {
        TokenResponse expectedResponse = new TokenResponse(HttpStatus.SC_OK,null, TestUtils.ACCESS_TOKEN, 3600);
        when(httpClient.execute(any(ClassicHttpRequest.class), any(TokenResponseHandler.class))).thenReturn(expectedResponse);

        TokenResponse actualResponse = shaapiClient.getToken("encodedCredentials");
        assertToken(actualResponse, expectedResponse);
    }

    @Test
    void testGetTokenWithInvalidCredentials() throws Exception {
        TokenResponse expectedResponse = new TokenResponse(HttpStatus.SC_BAD_REQUEST, "The request could not be processed due to invalid data or formatting.", null, null);
        when(httpClient.execute(any(ClassicHttpRequest.class), any(TokenResponseHandler.class))).thenReturn(expectedResponse);

        TokenResponse actualResponse = shaapiClient.getToken("invalidCredentials");
        assertToken(actualResponse, expectedResponse);
    }

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("productTestProvider")
    void testUpsertProducts(String scenarioName, String accessToken, List<Product> products, ProductResponse expectedResponse) throws Exception {
        when(httpClient.execute(any(ClassicHttpRequest.class), any(ProductResponseHandler.class))).thenReturn(expectedResponse);

        ProductResponse actualResponse = shaapiClient.upsertProducts(TestUtils.ACCOUNT_ID, accessToken, products);
        assertProduct(expectedResponse, actualResponse);
    }

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("productTestProvider")
    void testDeleteProducts(String scenarioName, String accessToken, List<ProductIdentifier> products, ProductResponse expectedResponse) throws Exception {
        when(httpClient.execute(any(ClassicHttpRequest.class), any(ProductResponseHandler.class))).thenReturn(expectedResponse);

        ProductResponse actualResponse = shaapiClient.deleteProducts(TestUtils.ACCOUNT_ID, accessToken, products);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getHttpStatus()).isEqualTo(expectedResponse.getHttpStatus());
        assertThat(actualResponse.getMessage()).isEqualTo(expectedResponse.getMessage());
        assertThat(actualResponse.getResults()).isEqualTo(expectedResponse.getResults());

        verify(httpClient).execute(any(ClassicHttpRequest.class), any(ProductResponseHandler.class));
        verify(httpClient).close();
    }

    private void assertToken(TokenResponse actualResponse, TokenResponse expectedResponse) throws IOException {
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getHttpStatus()).isEqualTo(expectedResponse.getHttpStatus());
        assertThat(actualResponse.getMessage()).isEqualTo(expectedResponse.getMessage());
        assertThat(actualResponse.getAccessToken()).isEqualTo(expectedResponse.getAccessToken());
        assertThat(actualResponse.getExpiresIn()).isEqualTo(expectedResponse.getExpiresIn());

        verify(httpClient).execute(any(ClassicHttpRequest.class), any(TokenResponseHandler.class));
        verify(httpClient).close();
    }

    private void assertProduct(ProductResponse expectedResponse, ProductResponse actualResponse) throws IOException {
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getHttpStatus()).isEqualTo(expectedResponse.getHttpStatus());
        assertThat(actualResponse.getMessage()).isEqualTo(expectedResponse.getMessage());
        assertThat(actualResponse.getResults()).isEqualTo(expectedResponse.getResults());

        verify(httpClient).execute(any(ClassicHttpRequest.class), any(ProductResponseHandler.class));
        verify(httpClient).close();
    }

    private static Stream<Arguments> productTestProvider() {
        return Stream.of(
                Arguments.of(
                        "Successful case with single product",
                        TestUtils.ACCESS_TOKEN,
                        getProductList("123"),
                        new ProductResponse(HttpStatus.SC_OK, "Success", null)
                ),
                Arguments.of(
                        "Successful case with multiple products",
                        TestUtils.ACCESS_TOKEN,
                        getProductList("123", "456"),
                        new ProductResponse(HttpStatus.SC_OK, "Success", null)
                ),
                Arguments.of(
                        "Invalid access token case",
                        TestUtils.ACCESS_TOKEN,
                        getProductList("123"),
                        new ProductResponse(HttpStatus.SC_UNAUTHORIZED, "Your access token is invalid or has expired. Please re-authenticate and try again.", null)
                ),
                Arguments.of(
                        "Partial success case (some products fail)",
                        TestUtils.ACCESS_TOKEN,
                        getProductList("123", "@128B~!"),
                        new ProductResponse(HttpStatus.SC_MULTI_STATUS,"Partial Success", List.of(
                                new Failure(207, Product.builder().id("@128B~!").build(), List.of("Invalid ID")))
                        )
                ),
                Arguments.of(
                        "Upsert failure scenario",
                        TestUtils.ACCESS_TOKEN,
                        getProductList("789"),
                        new ProductResponse(HttpStatus.SC_BAD_REQUEST,"Upsert Failed", List.of(
                                new Failure(400, Product.builder().id("789").build(), List.of("Server error")))
                        )
                )
        );
    }

    private static List<Product> getProductList(String... ids) {
         return Arrays.stream(ids)
                .map(id -> (Product) Product.builder().id(id).build())
                .toList();


    }
}
