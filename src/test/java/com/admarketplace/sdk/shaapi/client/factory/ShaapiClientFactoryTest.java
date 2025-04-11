package com.admarketplace.sdk.shaapi.client.factory;

import com.admarketplace.sdk.shaapi.client.ShaapiClient;
import com.admarketplace.sdk.shaapi.util.TestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


class ShaapiClientFactoryTest {

    @Test
    void testGetValidInstance() {
        ShaapiClient shaapiClient = ShaapiClientFactory.getInstance(TestUtils.AUTH_URL, TestUtils.SHAAPI_URL, TestUtils.V1);
        assertNotNull(shaapiClient);
    }

    @Test
    void testGetInstanceWithNullUrl() {
        assertThrows(IllegalArgumentException.class, () -> ShaapiClientFactory.getInstance(null, TestUtils.SHAAPI_URL, TestUtils.V1));
    }

    @Test
    void testGetInstanceWithNullShaapiUrl() {
        assertThrows(IllegalArgumentException.class, () -> ShaapiClientFactory.getInstance(TestUtils.AUTH_URL, null, TestUtils.V1));
    }

    @Test
    void testGetInstanceWithInvalidUrl() {
        assertThrows(IllegalArgumentException.class, () -> ShaapiClientFactory.getInstance("https://example.com/ java-%%$^&&", null, TestUtils.V1));
    }

    @Test
    void testGetInstanceWithInvalidProtocol() {
        assertThrows(IllegalArgumentException.class, () -> ShaapiClientFactory.getInstance("www.example.net", null, TestUtils.V1));
    }

    @Test
    void testGetInstanceWithInvalidVersion() {
        assertThrows(IllegalArgumentException.class, () -> ShaapiClientFactory.getInstance(TestUtils.AUTH_URL, TestUtils.SHAAPI_URL, null));
    }
}