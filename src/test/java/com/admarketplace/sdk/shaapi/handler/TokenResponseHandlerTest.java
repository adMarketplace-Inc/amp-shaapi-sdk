package com.admarketplace.sdk.shaapi.handler;

import com.admarketplace.authorization.api.model.v1.AuthenticationResponse;
import com.admarketplace.sdk.shaapi.model.TokenResponse;
import com.admarketplace.sdk.shaapi.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.HttpEntities;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenResponseHandlerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testHandleResponseWithSuccessfulAuthResponse() throws Exception {
        CloseableHttpResponse closeableHttpResponse = CloseableHttpResponse.adapt(new BasicClassicHttpResponse(HttpStatus.SC_OK));
        AuthenticationResponse authResponse = new AuthenticationResponse(TestUtils.ACCESS_TOKEN, "Bearer", 3600, null);
        closeableHttpResponse.setEntity(HttpEntities.create(objectMapper.writeValueAsString(authResponse), ContentType.APPLICATION_JSON));

        TokenResponse response = new TokenResponseHandler().handleResponse(closeableHttpResponse);

        assertThat(response).isNotNull();
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.getAccessToken()).isEqualTo(TestUtils.ACCESS_TOKEN);
        assertThat(response.getExpiresIn()).isEqualTo(3600);
        assertThat(response.getMessage()).isNull();
    }

    @Test
    void testHandleResponseWithEmptyTokenResponse() {
        CloseableHttpResponse closeableHttpResponse = CloseableHttpResponse.adapt(new BasicClassicHttpResponse(HttpStatus.SC_METHOD_NOT_ALLOWED));
        TokenResponse response = new TokenResponseHandler().handleResponse(closeableHttpResponse);

        assertThat(response).isNotNull();
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.SC_METHOD_NOT_ALLOWED);
        assertThat(response.getMessage()).contains(TestUtils.UNEXPECTED_ERROR);
        assertThat(response.getAccessToken()).isNull();
    }

    @Test
    void testHandleResponseWithUnexpectedTokenResponse() throws Exception {
        CloseableHttpResponse closeableHttpResponse = CloseableHttpResponse.adapt(new BasicClassicHttpResponse(HttpStatus.SC_OK));
        closeableHttpResponse.setEntity(HttpEntities.create(objectMapper.writeValueAsString(TestUtils.INVALID_JSON), ContentType.APPLICATION_JSON));

        TokenResponse response = new TokenResponseHandler().handleResponse(closeableHttpResponse);

        assertThat(response).isNotNull();
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        assertThat(response.getMessage()).contains(TestUtils.UNEXPECTED_ERROR);
        assertThat(response.getAccessToken()).isNull();
    }
}