package com.admarketplace.sdk.shaapi.handler;

import com.admarketplace.sdk.shaapi.model.ProductCountResponse;
import com.admarketplace.sdk.shaapi.util.TestUtils;
import com.admarketplace.shaapi.api.model.v1.ProductCount;
import com.admarketplace.shaapi.api.model.v1.ShaapiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.HttpEntities;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductCountResponseHandlerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testHandleResponseWithSuccessfulProductCountResponse() throws Exception {
        CloseableHttpResponse response = CloseableHttpResponse.adapt(new BasicClassicHttpResponse(HttpStatus.SC_OK));
        ShaapiResponse<ProductCount> shaapiResponse = new ShaapiResponse<>(TestUtils.SUCCESS, null);
        response.setEntity(HttpEntities.create(objectMapper.writeValueAsString(shaapiResponse), ContentType.APPLICATION_JSON));

        ProductCountResponse productCountResponse = new ProductCountResponseHandler().handleResponse(response);

        assertThat(productCountResponse).isNotNull();
        assertThat(productCountResponse.getHttpStatus()).isEqualTo(HttpStatus.SC_OK);
        assertThat(productCountResponse.getMessage()).isEqualTo(TestUtils.SUCCESS);
        assertThat(productCountResponse.getResult()).isNull();
    }

    @Test
    void testHandleResponseWithEmptyProductCountResponse() {
        CloseableHttpResponse response = CloseableHttpResponse.adapt(new BasicClassicHttpResponse(HttpStatus.SC_METHOD_NOT_ALLOWED));
        ProductCountResponse productCountResponse = new ProductCountResponseHandler().handleResponse(response);

        assertThat(productCountResponse).isNotNull();
        assertThat(productCountResponse.getHttpStatus()).isEqualTo(HttpStatus.SC_METHOD_NOT_ALLOWED);
        assertThat(productCountResponse.getMessage()).contains(TestUtils.UNEXPECTED_ERROR);
        assertThat(productCountResponse.getResult()).isNull();
    }

    @Test
    void testHandleResponseWithUnexpectedProductCountResponse() throws Exception {
        CloseableHttpResponse response = CloseableHttpResponse.adapt(new BasicClassicHttpResponse(HttpStatus.SC_OK));
        response.setEntity(HttpEntities.create(objectMapper.writeValueAsString(TestUtils.INVALID_JSON), ContentType.APPLICATION_JSON));

        ProductCountResponse productCountResponse = new ProductCountResponseHandler().handleResponse(response);

        assertThat(productCountResponse).isNotNull();
        assertThat(productCountResponse.getHttpStatus()).isEqualTo(HttpStatus.SC_OK);
        assertThat(productCountResponse.getMessage()).contains(TestUtils.UNEXPECTED_ERROR);
        assertThat(productCountResponse.getResult()).isNull();
    }
}