[![Lifecycle:Experimental](https://img.shields.io/badge/Lifecycle-Experimental-339999)](<Redirect-URL>)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=bcgov_moh-hni-esb&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=bcgov_moh-hni-esb)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=bcgov_moh-hni-esb&metric=alert_status)](https://sonarcloud.io/dashboard?id=bcgov_moh-hni-esb)[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=bcgov_moh-hni-esb&metric=ncloc)](https://sonarcloud.io/dashboard?id=bcgov_moh-hni-esb)

# MOH HNI ESB (HNSecure)

The `HNI ESB` application acts as a single point of access for the [HNClient-v2 application](https://github.com/bcgov/moh-hnclient-v2). 

The application performs an authorization check against the JWT that must be passed in the Authorization header and also does light message validation before routing the message to a final destination. 

 ![hnclientv2](https://user-images.githubusercontent.com/1767127/88949525-36f92f80-d248-11ea-9de7-1479222f1cfd.png)

# Configuration

Prerequisites:
- Apache Maven 3.6.1+
- Java 11

## Step 1: Configure and run HNI ESB

HNI ESB must be configured to validate a JWT as part of the OAuth 2 flow. The following parameters need to be configured depending on the Keycloak environment:
- `Authorized Party` - found in application.properties this is the client id of the client retrieving a JWT
- `Audience` - found in application.properties
- `Issuer` - still in the code this is the URL of the realm issuing the token
- `RemoteJWKSet` - still in the code this is the URL of the `certs` endpoint of the realm issuing the token

# Run the applications
`HNI ESB` can be run from the command line:

```
cd hnsecure
mvn compile camel:run
```

After the application is running, you can send a message using the [HNClient-v2 application](https://github.com/bcgov/moh-hnclient-v2).

#Testing External Endpoints without connectivity
`external-endpoint-stubs` creates mock endpoints for testing hn-secure routes that send to external endpoints. The endpoints will return a simple response.
It can be run from the command line:

```
cd external-endpoint-stubs
mvn spring-boot:run
