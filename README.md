# LocalEGA-TSD-proxy

## TSD File API documentation

https://test.api.tsd.usit.no/v1/docs/tsd-api-integration.html

NB: access is restricted to UiO network. Please, contact TSD support for the access, if needed.

## Configuration

Environment variables used:


| Variable name                      | Default value                                                        | Description                                        |
|------------------------------------|----------------------------------------------------------------------|----------------------------------------------------|
| SSL_ENABLED                        | true                                                                 | Enables/disables TLS for DOA REST endpoints        |
| SERVER_KEYSTORE_PATH               | /etc/ega/ssl/server.cert                                             | Path to server keystore file                       |
| SERVER_CERT_PASSWORD               |                                                                      | Password for the keystore                          |
| BROKER_HOST                        | public-mq                                                            | Public RabbitMQ broker hostname                    |
| BROKER_PORT                        | 5671                                                                 | Public RabbitMQ broker port                        |
| BROKER_VHOST                       | /                                                                    | Public RabbitMQ broker virtual host                |
| ROOT_CERT_PATH                     | /etc/ega/ssl/CA.cert                                                 | Path to the CA file for RabbitMQ connectivity      |
| ROOT_CERT_PASSWORD                 |                                                                      | Passphrase to unlock CA file                       |
| CLIENT_CERT_PATH                   | /etc/ega/ssl/client.cert                                             | Path to the client cert for RabbitMQ connectivity  |
| CLIENT_CERT_PASSWORD               |                                                                      | Passphrase to unlock client cert file              |
| BROKER_USERNAME                    | admin                                                                | Public RabbitMQ broker username                    |
| BROKER_PASSWORD                    | guest                                                                | Public RabbitMQ broker password                    |
| EXCHANGE                           | cega                                                                 | RabbitMQ exchange to publish messages to           |
| ROUTING_KEY                        | files.inbox                                                          | RabbitMQ routing key                               |
| CLIENT_ID                          |                                                                      | Elixir AAI client ID                               |
| CLIENT_SECRET                      |                                                                      | Elixir AAI client secret                           |
| CEGA_AUTH_URL                      | https://egatest.crg.eu/lega/v1/legas/users/%s?idType=username        | Central EGA authentication REST endpoint URL       |
| CEGA_USERNAME                      |                                                                      | Central EGA auth endpoint username                 |
| CEGA_PASSWORD                      |                                                                      | Central EGA auth endpoint password                 |
| TSD_HOST                           | api.tsd.usit.no                                                      | TSD File API URL                                   |
| TSD_PROJECT                        | p11                                                                  | TSD project code                                   |
| TSD_APP_ID                         | ega                                                                  | TSD application ID                                 |
| TSD_ACCESS_KEY                     |                                                                      | TSD File API access key                            |
| PASSPORT_PUBLIC_KEY_PATH           | /etc/ega/jwt/passport.pem                                            | Path to the public key for passport JWT validation |
| OPENID_CONFIGURATION_URL           | https://login.elixir-czech.org/oidc/.well-known/openid-configuration | URL of the OpenID configuration endpoint           |
| VISA_PUBLIC_KEY_PATH               | /etc/ega/jwt/visa.pem                                                | Path to the public key for visas JWT validation    |


## Sample Docker Swarm entry

```
...
  proxy:
    image: uiobmi/localega-tsd-proxy:latest
    ports:
      - 443:8080
    deploy:
      restart_policy:
        condition: on-failure
        delay: 5s
        window: 120s
    environment:
      - ROOT_CERT_PASSWORD
      - SERVER_CERT_PASSWORD
      - CLIENT_CERT_PASSWORD
      - CLIENT_ID=test
      - CLIENT_SECRET=test
      - CEGA_USERNAME
      - CEGA_PASSWORD
      - TSD_HOST
      - TSD_ACCESS_KEY
    secrets:
      - source: rootCA.p12
        target: /etc/ega/ssl/CA.cert
      - source: server.p12
        target: /etc/ega/ssl/server.cert
      - source: client.p12
        target: /etc/ega/ssl/client.cert
      - source: jwt.pub.pem
        target: /etc/ega/jwt/passport.pem
      - source: jwt.pub.pem
        target: /etc/ega/jwt/visa.pem
...
```
