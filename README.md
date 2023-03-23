# DEXILON unofficial Java Client
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

This is a lightweight library that works as a connector to the [Dexilon Trading API](https://dexilon.gitbook.io/dexilon-knowledge-base/). [Dexilon API Swagger](https://api.staging.dexilon.io/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/)

## Installation
Replace `LATEST_VERSION` with the latest version number and paste the snippet below in `pom.xml`
```
<dependency>
    <groupId>com.migal.trading</groupId>
    <artifactId>dexilon-java-sdk</artifactId>
    <version>LATEST_VERSION</version>
</dependency>
```
Run `mvn install` where `pom.xml` is located to install the dependency.

### Run Example
The examples are located under **src/test/java**. You need to pass `TEST_METAMASK_ADDRESS` and `TEST_PRIVATE_KEY` as an environment variables to every test class run

`DexilonClientImpl` - the main Client class. It has 3 constructors:

- public DexilonClientImpl() {} - For usage with public endpoints and default API endpoint

- public DexilonClientImpl(String ethAddress, String privateKey) {} - for usage with endpoints require Authorization and default API endpoint 

- public DexilonClientImpl(String apiUrl, String dexilonChainUrl, String ethAddress, String privateKey) {} - allows to override default endpoints and pass credentials for endpoints require Authorization


## Contributing

Contributions are welcome.<br/>
If you've found a bug within this project, please open an issue to discuss what you would like to change.<br/>
