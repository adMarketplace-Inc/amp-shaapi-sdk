package com.admarketplace.sdk.shaapi.handler;

import com.admarketplace.sdk.shaapi.model.ProductResponse;
import com.admarketplace.sdk.shaapi.util.TestUtils;
import com.admarketplace.shaapi.api.model.v1.Failure;
import com.admarketplace.shaapi.api.model.v1.ShaapiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.HttpEntities;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.message.BasicHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ProductResponseHandlerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TEST_REQUEST_ID = "req-12345-abcde";

    @Test
    void testHandleResponseWithSuccessfulShaapiResponse() throws Exception {
        CloseableHttpResponse response = CloseableHttpResponse.adapt(new BasicClassicHttpResponse(HttpStatus.SC_OK));
        ShaapiResponse<List<Failure>> shaapiResponse = new ShaapiResponse<>(TestUtils.SUCCESS, null);
        response.setEntity(HttpEntities.create(objectMapper.writeValueAsString(shaapiResponse), ContentType.APPLICATION_JSON));

        ProductResponse productResponse = new ProductResponseHandler().handleResponse(response);

        assertThat(productResponse).isNotNull();
        assertThat(productResponse.getHttpStatus()).isEqualTo(HttpStatus.SC_OK);
        assertThat(productResponse.getMessage()).isEqualTo(TestUtils.SUCCESS);
        assertThat(productResponse.getResults()).isNull();
        assertThat(productResponse.getRequestId()).isNull();
    }

    @Test
    void testHandleResponseWithEmptyShaapiResponse() {
        CloseableHttpResponse response = CloseableHttpResponse.adapt(new BasicClassicHttpResponse(HttpStatus.SC_METHOD_NOT_ALLOWED));
        ProductResponse productResponse = new ProductResponseHandler().handleResponse(response);

        assertThat(productResponse).isNotNull();
        assertThat(productResponse.getHttpStatus()).isEqualTo(HttpStatus.SC_METHOD_NOT_ALLOWED);
        assertThat(productResponse.getMessage()).contains(TestUtils.UNEXPECTED_ERROR);
        assertThat(productResponse.getResults()).isNull();
    }

    @Test
    void testHandleResponseWithUnexpectedShaapiResponse() throws Exception {
        CloseableHttpResponse response = CloseableHttpResponse.adapt(new BasicClassicHttpResponse(HttpStatus.SC_OK));
        response.setEntity(HttpEntities.create(objectMapper.writeValueAsString(TestUtils.INVALID_JSON), ContentType.APPLICATION_JSON));

        ProductResponse productResponse = new ProductResponseHandler().handleResponse(response);

        assertThat(productResponse).isNotNull();
        assertThat(productResponse.getHttpStatus()).isEqualTo(HttpStatus.SC_OK);
        assertThat(productResponse.getMessage()).contains(TestUtils.UNEXPECTED_ERROR);
        assertThat(productResponse.getResults()).isNull();
    }

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("requestIdWithHeaderDataProvider")
    void testRequestIdExtractionWithHeader(String ignoredScenarioName, int httpStatus, Object responseBody) throws Exception {
        //Given
        var handler = new ProductResponseHandler();
        var response = CloseableHttpResponse.adapt(new BasicClassicHttpResponse(httpStatus));
        response.setHeader(new BasicHeader("x-amp-request-id", TEST_REQUEST_ID));

        if (responseBody != null) {
            response.setEntity(HttpEntities.create(objectMapper.writeValueAsString(responseBody), ContentType.APPLICATION_JSON));
        }

        //When
        ProductResponse productResponse = handler.handleResponse(response);

        //Then
        assertThat(productResponse)
                .isNotNull()
                .extracting(ProductResponse::getRequestId)
                .isEqualTo(TEST_REQUEST_ID);
    }

    private static Stream<Arguments> requestIdWithHeaderDataProvider() {
        return Stream.of(
                arguments("Valid requestId header with successful response", HttpStatus.SC_OK, new ShaapiResponse<>(TestUtils.SUCCESS, null)),
                arguments("Valid requestId header with error response (empty entity)", HttpStatus.SC_BAD_REQUEST, null),
                arguments("Valid requestId header with deserialization error", HttpStatus.SC_OK, TestUtils.INVALID_JSON)
        );
    }
}