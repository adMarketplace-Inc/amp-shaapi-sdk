package com.admarketplace.sdk.shaapi.handler;

import com.admarketplace.sdk.shaapi.model.ProductResponse;
import com.admarketplace.sdk.shaapi.util.TestUtils;
import com.admarketplace.shaapi.api.model.v1.ShaapiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.HttpEntities;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductResponseHandlerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testHandleResponseWithSuccessfulShaapiResponse() throws Exception {
        CloseableHttpResponse response = CloseableHttpResponse.adapt(new BasicClassicHttpResponse(HttpStatus.SC_OK));
        ShaapiResponse shaapiResponse = new ShaapiResponse(TestUtils.SUCCESS, null);
        response.setEntity(HttpEntities.create(objectMapper.writeValueAsString(shaapiResponse), ContentType.APPLICATION_JSON));

        ProductResponse productResponse = new ProductResponseHandler().handleResponse(response);

        assertThat(productResponse).isNotNull();
        assertThat(productResponse.getHttpStatus()).isEqualTo(HttpStatus.SC_OK);
        assertThat(productResponse.getMessage()).isEqualTo(TestUtils.SUCCESS);
        assertThat(productResponse.getResults()).isNull();
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
        assertThat(productResponse.getHttpStatus()).isEqualTo(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        assertThat(productResponse.getMessage()).contains(TestUtils.UNEXPECTED_ERROR);
        assertThat(productResponse.getResults()).isNull();
    }
}