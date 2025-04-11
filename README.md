# Shopping Ads Asset Service (SHAAPI) SDK

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)

---
## Description

`amp-shaapi-sdk` provides a client that can communicate with adMarketplace Shopping Ads Asset Sync API to ingest and manage product listing ads (shopping ads) in adMarketplace DSP.

---
## Technologies & Dependencies

* Java 17
* Maven 3.8
* Apache HttpClient
* Lombok
* AMP Data Models

---
## Folder Structure Conventions

### Top-level directory structure

    .
    ├── src                     # Source files
    ├── pom.xml                 # Maven build configuration
    └── README.md               # Project documentation

### Source files

    .
    ├── src
    │   ├── main
    │   │   ├── java
    │   │   │   └── com.admarketplace.sdk.shaapi
    │   │   │       ├── client              # SDK client for API interaction
    │   │   │       ├── handler             # SDK response handlers
    │   │   │       ├── model               # SDK response model 
    │   │   └── resources
    │   └── test
    │       ├── java
    │       │   └── com.admarketplace.shaapi
    │       │       ├── client              # SDK client unit tests
    │       │       ├── handler             # SDK response handlers tests
    │       │       ├── util                # Utility class for tests
    │       └── resources
    └── ...

---
## Build locally

* Install JDK 17
* Install Maven 3.8+
* Lombok plugin is required to be installed into IDE.
* Run `mvn clean package` to build executable jar

---
## Sample Usage

### Adding SDK Dependency

To use the SDK in your project, add the following dependency from Maven Central:

#### Maven:
```xml
<dependency>
    <groupId>com.admarketplace</groupId>
    <artifactId>amp-shaapi-sdk</artifactId>
    <version>1.0.1</version>
</dependency>
```

#### Gradle:
```kotlin
implementation("com.admarketplace:amp-shaapi-sdk:1.0.1")
```

### Input variables

| Name               | Description                               |
|--------------------|-------------------------------------------|
| AMP_SHAAPI_URL     | Base URL for adMarketplace SHAAPI Service |
| AMP_AUTH_URL       | Base URL for adMarketplace Auth Service   |
| AMP_CLIENT_ID      | Client ID for authentication              |
| AMP_CLIENT_SECRET  | Client secret for authentication          |
| AMP_SHAAPI_VERSION | SHAAPI version                            |



### Client Instantiation
```java
import com.admarketplace.sdk.shaapi.client.factory.ShaapiClientFactory;

    String shaapiUrl = System.getenv("AMP_SHAAPI_URL");
    String authUrl = System.getenv("AMP_AUTH_URL");
    String version = System.getenv("AMP_SHAAPI_VERSION");

    ShaapiClient shaapiClient = ShaapiClientFactory.getInstance(authUrl, shaapiUrl, version);
```

### Token Retrieval
```java
import com.admarketplace.sdk.shaapi.model.TokenResponse;
import java.util.Base64;

    String clientId = System.getenv("AMP_CLIENT_ID");
    String clientSecret = System.getenv("AMP_CLIENT_SECRET");

    // Encode credentials
    String encodedCredentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

    // Retrieve token
    TokenResponse tokenResponse = shaapiClient.getToken(encodedCredentials);

    if (tokenResponse.getHttpStatus() != 200) {
        // Error handling
    }
```

### Build Upsert Request
```java
import com.admarketplace.sdk.shaapi.model.ProductResponse;
import com.admarketplace.shaapi.api.model.v1.Product;

    // Prepare product list
    List<Product> products = List.of(Product.builder()
            .id("id")
            .country("US")
            .language("en")
            //... Other fields
            .build());

    // Upsert products
    ProductResponse productResponse = shaapiClient.upsertProducts(tokenResponse.accessToken(), products);
    
    if (productResponse.getHttpStatus() != 200) {
        // Error handling
    }
```

### Build Delete Request
```java
import com.admarketplace.sdk.shaapi.model.ProductResponse;
import com.admarketplace.shaapi.api.model.v1.ProductIdentifier;

    // Prepare product list
    List<ProductIdentifier> products = List.of(Product.builder()
            .id("id")
            .country("US")
            .language("en")
            .build());

    // Delete products
    ProductResponse productResponse = shaapiClient.deleteProducts(tokenResponse.accessToken(), products);
    
    if (productResponse.getHttpStatus() != 200) {
        // Error handling
    }
```
---
## Copyright

[AdMarketplace](https://admarketplace.com/)

![AdMarketplace_logo](amp-logo-img.jpg)
