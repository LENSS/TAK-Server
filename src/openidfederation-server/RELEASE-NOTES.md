# Connect2id Server Release Notes

## 18.0 (2024-04-02)

### Summary

* Implements support for OpenID Connect Client-Initiated Backchannel 
  Authentication (CIBA) Flow - Core 1.0.

  The CIBA `poll` mode for token delivery is supported. The `ping` and `push`
  modes may be implemented in a future release.

  All three login hint types are supported: `login_hint_token`, `id_token_hint`
  and `login_hint`. A Connect2id server deployment can be configured to accept 
  only those login hint types that are allowed by policy and required by the 
  use cases of client applications.

  To support CIBA login hint tokens the STS endpoint of the Connect2id server 
  is given the capability to issue them. To mint a `login_hint_token` the STS 
  endpoint requires the presence of a native IdP app session for the subject 
  (end-user). The token is single-use, cryptographically secured and has a 
  length and encoding optimised for presentation in QR codes on user devices.
 
  The CIBA `id_token_hint` support is identical to that for regular OpenID 
  authentication requests.

  To support CIBA `login_hint` the Connect2id server receives a new 
  `LoginHintResolver` SPI to resolve login hints to subject identifiers. A 
  plugin that delegates the resolution to a web endpoint (webhook) is provided.
  
  Signed CIBA requests are supported.
  
  The CIBA `binding_message` request parameter is supported. Deployments can 
  configure a regular expression to enforce compliance of the message to a 
  given pattern. The default pattern allows combinations of four to sixteen 
  alphanumeric and space characters.

  The CIBA `user_code` request parameter is supported, however its usage is 
  discouraged. A `login_hint_token` should be used to pre-authorise a CIBA 
  request from a native IdP app.

  CIBA requests that are authenticated and pass initial validation are directed
  to a `CIBARequestHandler` SPI to perform the end-user authentication and 
  authorisation. A handler would typically message the native IdP app 
  instance(s) for the subject (end-user) resolved from the `login_hint_token`,
  `id_token_hint` or `login_hint`. If end-user is successfully authenticated 
  and consent was obtained the handler submits the authorisation to a CIBA
  authorisation sessions API, to continue the flow. If the end-user couldn't be
  authenticated or consent was not given the CIBA request is left to time out, 
  according to the `expires_in` parameter of the authentication request 
  acknowledgement return the client.

* Adds support for subject (end-user) sessions for native IdP apps. An OpenID 
  provider may utilise a native app to provide a user authentication factor 
  during sign-in, to authorise CIBA requests and for other purposes. Such 
  subject sessions have a `ctx` (context) value `NIA` (native IdP app), to 
  differentiate them for `web` and `NCG` (native client group) sessions.

* Subject (end-user) sessions receive a new optional `jkt` (JWK thumbprint) 
  field to enable a session to be bound to a private JWK. The thumbprint is a 
  BASE64URL-safe encoded SHA-256 hash. For web sessions the private key may be 
  a non-extractable Web Crypto API key. For native app sessions the private key 
  may be a non-extractable Android Keystore or iOS Keychain key. A future 
  Connect2id server release will incorporate changes to support DPoP-style 
  binding of subject sessions in the authorisation session API, in the CIBA 
  flow and in the OpenID Connect SSO for native apps flow.   

* Enables binding of subject (end-user) sessions to a fingerprint of the web
  browser or native app used to establish the session. The thumbprint is a 
  BASE64URL-safe encoded SHA-256 hash. The authorisation session (login) web
  API is updated to automatically check the current user agent fingerprint 
  against the value stored when the session was created. If the fingerprint 
  values don't match the subject session is ended, triggering an authentication 
  prompt.

  The user agent fingerprint binding of subject sessions can replace plugins 
  that use the `WebSSOEligibilityChecker` SPI to add a custom user agent 
  fingerprint check after the regular Connect2id server checks that determine
  whether a web-based OpenID authentication request or an OAuth 2.0 
  authorisation request is eligible for single sign-on (SSO). 

* The subject (end-user) sessions of OpenID relying parties using OpenID 
  Connect SSO for Native Apps 1.0 will now bear the context value `NCG` (native
  client group), replacing the value `native`. The `op.sso.device.*` 
  configuration properties are replaced by `op.sso.nativeClientGroup.*`. This
  breaking change is made to differentiate the sessions for native client 
  groups and those for a native IdP app.

* Adds support for the optional `dpop_bound_access_tokens` client metadata 
  parameter, specified in RFC 9449, section 5.2. When a client is registered
  with a value of `true` it must always use DPoP. The default value is `false`.


### Configuration

* /WEB-INF/oidcProvider.properties

    * `op.ciba.enable` -- New optional configuration property to enable / 
      disable CIBA support. Disabled by default.
    
    * `op.ciba.tokenDeliveryModes` -- New optional configuration property 
      listing the enabled CIBA token delivery modes. Only the `poll` mode is 
      supported. The default value is all supported.
    
    * `op.ciba.hintTypes` -- New optional configuration property listing the 
      enabled CIBA hint types. Supported hint types: `login_hint_token`,  
      `id_token_hint` and `login_hint`. The default value is all supported.

    * `op.ciba.requestJWSAlgs` -- New optional configuration property listing 
      the enabled JWS algorithms for signed CIBA requests. Supported JWS 
      algorithms: `RS256`, `RS384`, `RS512`, `PS256`, `PS384`, `PS512`, 
      `ES256`, `ES256K`, `ES384` and `ES512`. The default value is all 
      supported.

    * `op.ciba.bindingMessagePattern` -- New optional configuration property 
      for a regular expression pattern for matching legal CIBA binding 
      messages. Messages that don't match the pattern will be rejected with an
      `invalid_binding_message` error. The pattern must be BASE64 encoded to
      prevent character escaping issues. The default unencoded pattern is 
      `[\w ]{4,16}`, allowing combinations of four to sixteen alphanumeric and 
      space characters.

    * `op.ciba.supportUserCode` -- New optional configuration property to 
      enable / disable support for the CIBA `user_code` parameter. The default 
      value is `true`.

    * `op.ciba.defaultRequestLifetime` -- New optional configuration property 
      for the default lifetime of CIBA requests (`auth_req_id`), in seconds. 
      Applied when a CIBA request doesn't specify a `requested_expiry` 
      parameter. Must not be shorter than 60 seconds. The default value is 600 
      seconds (10 minutes).
    
    * `op.ciba.maxRequestLifetime` -- New optional configuration property for 
      the maximum allowed lifetime of the requested_expiry parameter of CIBA
      requests (`auth_req_id`), in seconds. Must not be shorter than the 
      default lifetime of CIBA requests. The default value is the default 
      request lifetime.
    
    * `op.sts.jar.apiAccessTokenSHA256.*` -- New optional configuration
      property, specifying an access token for the STS web API to mint OAuth 
      2.0 JWT-secured Authorisation Requests (JAR), also called request objects 
      in OpenID Connect. The value of the configuration property is the SHA-256 
      hash (in hexadecimal format) of the token. The hashed storage is intended 
      to prevent accidental leakage of the token through configuration files, 
      logs, etc. The token is of type Bearer, non-expiring and must contain at 
      least 32 random alphanumeric characters to make brute force guessing 
      impractical. If not specified minting of JARs by the STS web API is 
      disabled.

      Additional access tokens, to facilitate token roll-over or for other
      needs, can be configured by appending a dot (.) with a unique label to
      the property name, e.g. as `op.sts.jar.apiAccessTokenSHA256.1=abc...`.
    
      Replaces the deprecated general purpose `op.sts.apiAccessTokenSHA256` 
      STS web API access token configuration.
    
    * `op.sts.privateKeyJWT.apiAccessTokenSHA256.*` -- New optional 
      configuration property, specifying an access token for the STS web API to 
      mint private key JWTs (`private_key_jwt`) for client authentication. The 
      value of the configuration property is the SHA-256 hash (in hexadecimal 
      format) of the token. The hashed storage is intended to prevent 
      accidental leakage of the token through configuration files, logs, etc. 
      The token is of type Bearer, non-expiring and must contain at least 32 
      random alphanumeric characters to make brute force guessing impractical. 
      If not specified minting of private key JWTs by the STS web API is 
      disabled.

      Additional access tokens, to facilitate token roll-over or for other
      needs, can be configured by appending a dot (.) with a unique label to
      the property name, e.g. as 
      `op.sts.privateKeyJWT.apiAccessTokenSHA256.1=abc...`.
    
      Replaces the deprecated general purpose `op.sts.apiAccessTokenSHA256` 
      STS web API access token configuration.
    
    * `op.sts.loginHintToken.apiAccessTokenSHA256.*` -- New optional 
      configuration property, specifying an access token for the STS web API to 
      mint login hint tokens (`login_hint_token`) for CIBA. The value of the 
      configuration property is the SHA-256 hash (in hexadecimal format) of the 
      token. The hashed storage is intended to prevent accidental leakage of 
      the token through configuration files, logs, etc. The token is of type 
      Bearer, non-expiring and must contain at least 32 random alphanumeric 
      characters to make brute force guessing impractical. If not specified 
      minting of login hint tokens by the STS web API is disabled.

      Additional access tokens, to facilitate token roll-over or for other
      needs, can be configured by appending a dot (.) with a unique label to
      the property name, e.g. as 
      `op.sts.loginHintToken.apiAccessTokenSHA256.1=abc...`.
    
    * `op.sts.loginHintToken.allowDirectIssue` -- New optional configuration
      property to allow direct issue of login hint tokens (`login_hint_token`)
      for CIBA, bypassing the requirement for an access token 
      (`op.sts.loginHintToken.apiAccessTokenSHA256.*`). If `true` a valid 
      native IdP app session ID (`sub_sid`) is sufficient to authorise the 
      issue. The default value is `false`. 
      
    * `op.sts.apiAccessTokenSHA256` -- The general master access token for the 
       STS web API is deprecated for removal. Configure purpose specific 
       `op.sts.*.apiAccessTokenSHA256.*` tokens instead.
    
    * `op.sso.nativeClientGroup.enable` -- Replaces the optional 
      `op.sso.device.enable` configuration property for OpenID Connect SSO 
      for Native Apps 1.0 introduced in Connect2id server 16.0 (breaking 
      change).
    
    * `op.sso.nativeClientGroup.sessionMaxLifetime` -- Replaces the optional 
      `op.sso.device.sessionMaxLifetime` configuration property for OpenID 
      Connect SSO for Native Apps 1.0 introduced in Connect2id server 16.0 
      (breaking change).
    
    * `op.sso.nativeClientGroup.sessionAuthLifetime` -- Replaces the optional 
      `op.sso.device.sessionAuthLifetime` configuration property for OpenID 
      Connect SSO for Native Apps 1.0 introduced in Connect2id server 16.0 
      (breaking change).
    
    * `op.sso.nativeClientGroup.sessionMaxIdleTime` -- Replaces the optional 
      `op.sso.device.sessionMaxIdleTime` configuration property for OpenID 
      Connect SSO for Native Apps 1.0 introduced in Connect2id server 16.0 
      (breaking change).

* /WEB-INF/sessionStore.properties

    * `sessionStore.nativeIdPAppQuotaPerSubject` -- New optional configuration
       property for the maximum number of concurrent native IdP app sessions a 
       subject (end-user) may have. Must not exceed 5 concurrent sessions. Zero 
       disables native IdP app sessions. The default value is 1.

* /WEB-INF/infinispan-*.xml

    * Adds a new `login_hint_tokens` table. In existing Connect2id server 
      deployments with an SQL RDBMS or DynamoDB the server will automatically 
      create the table on startup. For SQL databases the automatic table 
      creation is enabled by default and can be turned off by setting the 
      `dataSource.createTableIfMissing` Java system property to `false`.
    
* /WEB-INF/infinispan-*-{mysql|postgres95|sqlserver|oracle|h2}.xml

    * Adds new `dpop_bound_access_tokens`, `backchannel_token_delivery_mode`, 
      `backchannel_client_notification_endpoint`, 
      `backchannel_authentication_request_signing_alg` and 
      `backchannel_user_code_parameter` columns to the `clients` table. In
      existing Connect2id server deployments with an SQL RDBMS the server will
      automatically add the new column (with an appropriate default value) on
      startup. For SQL databases the automatic column addition is enabled by 
      default and can be turned off by setting the
      `dataSource.createTableIfMissing` Java system property to `false`.

* /WEB-INF/infinispan-multitenant-stateless-redis-sentinel3-mysql.xml

    * Removes the deprecated multi-tenant stateless Redis sentinel / MySQL 
      configuration.

* /WEB-INF/loginHintResolverWebAPI.properties -- New properties file specifying 
  the default configuration of the web-based resolver (webhook) for CIBA login 
  hints (`login_hint`) (implements the `LoginHintResolver` SPI). Can be
  overridden with Java system properties.

    * `op.loginHintResolver.webAPI.enable` -- New optional configuration 
      property, enables / disables the login hint resolver. Disabled (`false`) 
      by default.
    
    * `op.loginHintResolver.webAPI.url` -- New configuration property for the 
      endpoint URL of the web-based resolver. Required when the resolver is 
      enabled.
    
    * `op.loginHintResolver.webAPI.apiAccessToken` -- New configuration 
      property for the access token of type Bearer for the login hint resolver.
      Required when the resolver is enabled.
    
    * `op.loginHintResolver.webAPI.connectTimeout` -- New optional 
      configuration property for the HTTP connect timeout, in milliseconds. 
      The default value is zero, implies none or determined by the underlying 
      HTTP client.
    
    * `op.loginHintResolver.webAPI.readTimeout` -- New optional configuration 
      property for the HTTP response read timeout, in milliseconds. The default 
      value is zero, implies none or determined by the underlying HTTP client.


### Web API

* /clients

    * Supports registration of clients with the optional 
      `dpop_bound_access_tokens` metadata field to require a DPoP proof JWT at
      the token endpoint (RFC 9449). 

    * Supports registration of clients with the optional 
      `backchannel_token_delivery_mode`, 
      `backchannel_authentication_request_signing_alg` and 
      `backchannel_user_code_parameter` metadata fields for CIBA use.

* /token

    * Requires clients to submit a DPoP proof JWT when registered with a 
      `dpop_bound_access_tokens` client metadata value `true` (RFC 9449).

    * Adds CIBA grant support using the `poll` mode (polling with immediate 
      response).

* /authz-sessions/rest/v3/

    * The authorisation session start request object receives a new optional 
      `fpt` (user agent fingerprint) parameter, as a BASE64URL-safe encoded 
      SHA-256 hash. When the authorisation session is started with a 
      fingerprint for the user agent and there is a current subject (end-user)
      session with a `fpt` value that doesn't match, the Connect2id server 
      ends the current subject session and returns an authentication prompt. In
      all other cases the authorisation session proceeds as usual.

      The subject session `fpt` may be set when the session is created. After 
      that the `fpt` value cannot be modified, i.e. it remains immutable until 
      the session is ended or expires.

* /ciba-sessions/rest/v1

    * New integration endpoint for handling CIBA requests.

    * /ciba-sessions/rest/v1/authorize -- Protected resource for authorising 
      pending CIBA requests (`auth_req_id`) after successful end-user 
      authentication and consent, typically performed by a native IdP app. The 
      resource is accessed by a callback token issued by the 
      `CIBARequestHandler` SPI.

* /authz-store/rest/v3/auth-req-ids

    * New protected resource, the GET method returns the current CIBA 
      `auth_req_id`s for which an end-user authorisation was received and its 
      tokens have not been requested by the client yet.

* /authz-store/rest/v3/inspection

    * New optional `auth_req_id` form parameter, inspects the pending 
      authorisation for the specified CIBA request ID. Returns `404` if the 
      request ID is invalid / expired. Must not be used together with another
      form parameter.

* /session-store/rest/v2

    * The subject session object receives a new optional `jkt` (JWK thumbprint) 
      field to bind the session to a private JWK. The thumbprint is a
      BASE64URL-safe encoded SHA-256 JSON Web Key (JWK) hash (RFC 7638).
    
    * The subject session object receives a new optional `fpt` (web browser or
      native app fingerprint) field to bind the session to a fingerprint of the
      user agent. The thumbprint is a BASE64URL-safe encoded SHA-256 hash.
    
    * Introduces new subject session object `ctx` (context) values `NCG` 
      (native client group) and `NIA` (native IdP app). The `device` value 
      introduced in Connect2id server 16.0 is deprecated and replaced by `NCG`. 

* /monitor/v1/metrics

    * `cibaEndpoint.successfulRequests` -- New meter of successful CIBA 
      requests.

    * `cibaEndpoint.invalidRequests` -- New meter of CIBA requests failed with
      an `invalid_request`, `unknown_user_id`, `invalid_binding_message` or
      `missing_user_code` error.
    
    * `cibaEndpoint.invalidClientErrors` -- New meter CIBA requests failed with
      an `invalid_client` error.
    
    * `cibaEndpoint.unauthorizedClientErrors` -- New meter of CIBA requests
      failed with an `unauthorized_client` error.
    
    * `cibaEndpoint.serverErrors` -- New meter of CIBA requests failed with a
      `server_error` error.

    * `tokenEndpoint.ciba.successfulRequests`,
      `tokenEndpoint.ciba.invalidClientErrors`,
      `tokenEndpoint.ciba.unauthorizedClientErrors`,
      `tokenEndpoint.ciba.invalidGrantErrors`,
      `tokenEndpoint.ciba.invalidScopeErrors`,
      `tokenEndpoint.ciba.serverErrors` -- New token endpoint meters for the 
       CIBA grant. The CIBA-specific `authorization_pending`, `expired_token` 
       and `access_denied` errors count as `invalid_grant` errors. 
    
    * `tokenEndpoint.ciba.handlerTimer` -- New token endpoint timer for 
       handling CIBA grants.

    * `authzStore.authRequestIDIssues` -- New meter for the issue of CIBA 
       authentication request IDs.

    * `authzStore.numGenericCodes` -- New gauge for the number of currently 
       active OAuth 2.0 authorisation codes and CIBA authentication request 
       IDs. Replaces the deprecated `authzStore.numAuthzCodes` gauge. 
    
    * `authzStore.numAuthzCodes` -- Deprecates the gauge, which will now mirror 
      the new `authzStore.numGenericCodes` gauge.

    * `authzStore.cibaExchanges` -- New meter for the successful CIBA
      authentication request ID for token exchanges.
    
    * `loginHintTokenStore.numTokens` -- New gauge for the number of stored 
       login hint tokens.


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:5.11

    * `LoginHintResolver` -- New SPI to resolve a `login_hint` request 
      parameter to a subject identifier at the OpenID provider.

    * `CIBARequestHandler` -- New SPI to handle the end-user authentication and
      consent for validated CIBA requests.


### Resolved issues

* Replaces the Connect2id server integration web APIs `expired_client_secret` 
  error code with a `client_metadata_conflict` code (issue server/1061).

* Restores the HTTP POST entity body size checking at the token endpoint, 
  limits the maximum size to 50 thousand characters (issue server/1062).

* Adds HTTP POST entity body size checking at the PAR endpoint, limits the 
  maximum size to 50 thousand characters (issue server/1068).

* The logs of SPI implementation loading must record the canonical (full) class
  name, not the simple name (issue server/1063).

* The Connect2id server must load and initialise all available 
  `AuthorizationRequestValidator` SPI implementations. The rule that at most 
  one may be configured as enabled is not affected (issue server/1065).

* The Connect2id server must load and initialise all available `PARValidator` 
  SPI implementations. The rule that at most one may be configured as enabled 
  is not affected (issue server/1066).

* The Connect2id server must load and initialise all available
  `CustomTokenResponseComposer` SPI implementations. The rule that at most
  one may be configured as enabled is not affected (issue server/1064).

* The Connect2id server must load and initialise all available
  `TokenIntrospectionResponseComposer` SPI implementations. The rule that at 
  most one may be configured as enabled is not affected (issue server/1067).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:5.11

* Upgrades to com.nimbusds:oauth2-authz-store:28.8

* Upgrades to com.nimbusds:session-store:22.1

* Upgrades to com.nimbusds:common:3.7

* Upgrades to com.thetransactioncompany:java-property-utils:2.1

* Updates to com.nimbusds:oauth2-grant-handlers:1.4



## 17.2.1 (2025-03-27)

### Resolved issues

* Fixes a transient AWS SDK dependency misalignment that was causing DynamoDB
  connection establishment to fail in a credentials related method (issue
  server/1072).


### Dependency changes

* Updates to com.nimbusds:c2id-server-property-source:2.0.2



## 17.2 (2025-03-25)

### Summary

* Upgrades the software statement verifier (SSV) plugin (implements the
  `RegistrationInterceptor` SPI) to support strict JWT type checking. Explicit 
  JWT typing is recommended to prevent accidental or malicious JWT confusion. 
  See JSON Web Token Best Current Practices (RFC 8725), section 3.11.


### Configuration

* /WEB-INF/softwareStatementVerifier.properties

    * `op.ssv.jwtTypes` -- New optional configuration property. Specifies the
      accepted `typ` (type) JWT header values of the software statements, as
      comma and / or space separated list. If blank or omitted the JWT type
      checking will accept software statements with no `typ` header or the
      header value `JWT`.


### Resolved issues

* Fixes an NPE affecting HTTP DELETE `/authz-sessions/rest/v3/{sid}` requests 
  when the Connect2id server is configured with the optional
  `op.authz.includeSubjectSessionInFinalResponse=true` and the authorisation is
  cancelled before the subject authentication is submitted (at the `auth` step) 
  (issue server/1071).


### Dependency changes

* Updates to com.nimbusds:software-statement-verifier:2.3.1

* Updates to com.h2database:h2:2.3.232

* Updates to org.postgresql:postgresql:42.7.4



## 17.1.1 (2025-02-26)

### Resolved issues

* The URN for ID tokens in OpenID Connect Native SSO 1.0 must be 
  `urn:openid:params:token-type:id_token` (issue oidc-sdk/492).

* Updates JSON parsing to prevent parsing of JSON with excessive object nesting
  in the JSON Smart library. This addresses CVE 2024-57699 in JSON Smart (issue 
  oidc-sdk/494).

* The Redis configuration for the identifier-based access tokens database 
  (`authzStore.idAccessTokenMap`) in 
  `WEB-INF/infinispan-*-stateless-redis-*.xml` should use the 
  `redisCache{Host|Port|Password}` properties, not 
  `redisMap{Host|Port|Password}` (issue server/1054).    

* Fixes NPE in the OIDC SDK OIDCScopeValue.resolveClaimNames(Scope, Map) when 
  the OIDCScope.Value specifies null associated claim names. The NPE was 
  triggered when processing authorisations from OAuth 2.0 grant handler SPIs 
  that include the `offline_access` scope value (issue oidc-sdk/499, 
  server/1057).


### Dependency changes

* Upgrades to com.nimbusds:oauth2-oidc-sdk:11.23.1

* Updates to com.nimbusds:c2id-server-jwkset:2.0.2

* Updates to com.nimbusds:nimbus-jose-jwt:10.0.2

* Updates to net.minidev:json-smart:2.5.2

* Updates to com.google.code.gson:gson:2.12.1

* Updates to Dropwizard Metrics 4.2.28

* Updates to commons-io:commons-io:2.17.0

* Updates to org.apache.commons:commons-lang3:3.17.0



## 17.1 (2025-01-22)

## Summary

* Enhances the security of `private_key_jwt` client authentication by
  supporting strict `aud` (audience) checking of the received JWTs, as mandated
  by the new FAPI 2.0 profile and by the soon-to-be published errata of OpenID
  Connect Core 1.0, RFC 7523 and FAPI 1.0.

  The strict check requires the `aud` JWT claim to be the issuer URL of the
  Connect2id server (as configured by `op.issuer`), and if the `aud` is a JSON
  array, to contain no other values. The endpoint URL is not accepted as an
  `aud` value when strict checking is enabled.

  The strict JWT audience check can be enabled globally, for all clients, or
  for selected clients only. Using the latter, deployments can migrate to
  strict audience checking gradually and over time. The client authentication
  interceptor SPI can be used to monitor the client usage of `private_key_jwt`
  and notify client administrators if an `aud` change is desired.

  The default JWT audience check remains unchanged, allowing the `aud` JWT
  claim to contain the issuer URL, the endpoint URL, and to be multi-valued, in
  accordance with the original versions of OpenID Connect Core 1.0, section 9,
  and the JSON Web Token (JWT) Profile for OAuth 2.0 Client Authentication and
  Authorization Grants (RFC 7523).

* Upgrades the support for OpenID Connect Native SSO for Mobile Apps 1.0 to
  draft 07. The token type URI for ID tokens (in the `subject_token_type`
  parameter) becomes `urn:openid:params:token-type:id_token`, for device
  secrets (in the `actor_token_type` parameter)
  `urn:openid:params:token-type:device_secret`.


### Configuration

* /WEB-INF/oidcProvider.properties

    * `op.token.authJWTAudience` -- New optional configuration property for the
      JWT `aud` (audience) check in `client_secret_jwt` and `private_key_jwt`
      client authentication.

      Supported JWT audience checks:

        * `LEGACY` -- The `aud` may be multi-valued and contain values other
          than the endpoint URL or the issuer URL of the OpenID provider /
          OAuth 2.0 authorisation server.

        * `STRICT` -- The `aud` must be single-valued and set to the issuer URL
          of the OpenID provider / OAuth 2.0 authorisation server.

      The default value is `LEGACY`.

    * `op.token.authJWTAudienceQuery` -- New optional configuration property to
      override the JWT `aud` (audience) check for selected clients.

      The JSON query is applied to the custom `data` JSON object of the
      registered client metadata and must return a `LEGACY` or `STRICT` string
      (case-insensitive). The default value is no overriding JSON query.

      Example JSON query to look up the audience check in the custom `data`
      client registration field: `.authJWTAudience`. Clients which
      registration doesn't contain a `data.authJWTAudience` set to `LEGACY` or
      `STRICT` will be authenticated according to the globally configured
      `op.token.authJWTAudience`.


### Resolved issues

* HTTP 400 Bad Request responses from the `key-store/rest/v1` web API due to a
  concurrent JWK set modification exception must be given an
  `error_description` message (issue server/1043).

* Hardens authorisation request parsing when dealing with an illegal URI
  representation in the redirect_uri parameter. URISyntaxException messages
  must not be included in the error_description to avoid illegal chars
  according to RFC 6749, section 5.2 (issues oidc-sdk/487, server/1046).

* Logs an OP5186 error when a `client_secret` update fails due to a concurrent
  client removal (issue server/1045).


### Dependency changes

* Upgrades to com.nimbusds:oauth2-oidc-sdk:11.21.2

* Updates to com.nimbusds:c2id-server-jwkset:2.0.1

* Updates to com.nimbusds:c2id-server-key-store:1.7.3

* Updates to com.nimbusds:tenant-manager:10.1.6

* Updates to com.nimbusds:oauth2-authz-store:26.9.1

* Updates to com.nimbusds:oidc-session-store:21.2.1

* Updates to com.nimbusds:nimbus-jose-jwt:10.0.1

* Updates to com.nimbusds:nimbus-jwkset-loader:7.3.1

* Updates to com.google.crypto.tink:tink:1.16.0

* Updates to net.thisptr:jackson-jq:1.2.0



## 17.0 (2024-12-17)

## Summary

* Introduces a key store with a web API supporting online generation, rotation, 
  revocation and historic archival of the Connect2id server keys.

  The key store supports 3 operation modes:

    * Dynamic -- Enables online generation, rotation, revocation and archival 
      of the server keys, which are stored in the database. The private key
      material is encrypted with an AES key before it's committed to the 
      database. This is the default key store mode.
    
    * Static -- The server keys are statically configured. Online key 
      generation, rotation, revocation and archival is not supported. The 
      database is not utilised for key storage. This mode is equivalent to the 
      key configuration method in previous Connect2id server releases 1.x to 
      16.x.
    
    * PKCS#11 with static -- Selected signing server keys, such as RSA and EC
      keys, are loaded from a PKCS#11 store (HSM). Keys which types or 
      algorithms are not provided or supported by the PKCS#11 interface are 
      statically configured. As with the pure static mode, online key 
      generation, rotation, revocation and archival is not supported. The 
      database is not utilised for key storage.

  The key store web API does not allow retrieval of private and secret key 
  material. The keys for the OpenID provider (`op`) context and the OpenID 
  Federation 1.0 (`federation`) context are configured, managed and stored in 
  strict separation from one another.

  Existing Connect2id server deployments that wish to switch to the new key 
  store (in the dynamic mode) can import their keys using the 
  `keyStore.importIfEmpty.op` configuration property. OpenID Federation 1.0 
  entity keys can be imported using the `keyStore.importIfEmpty.federation` 
  configuration property.

  Existing Connect2id server deployments have the choice to continue using 
  the old `jose.jwkSet` and `jose.federationJWKSet` configuration properties or
  the `WEB-INF/jwkSet.json` and `WEB-INF/federationJWKSet.json` files to set 
  the server keys. This applies also to the `pkcs11.*` configuration properties 
  and the `WEB-INF/jose.properties` file. Note that these methods are 
  deprecated and may be removed in a future major release.  

* Weak 1024-bit RSA server keys using the `jose.allowWeakKeys` configuration
  exception are no longer supported. The accepted RSA key sizes are 2048, 3072 
  and 4098 bits.

* Production deployments of the Connect2id server can now set the Java system
  property `env` to `prod` (production), e.g. with `-Denv=prod`, which will 
  prevent the server from starting up if it is accidentally configured with the 
  demo key store encryption JWK (`keyStore.encJWK`) or master API access token 
  (`*.apiAccessTokenSHA256`).

* Removes the `/authz-sessions/rest/v1` web API which was deprecated in 
  Connect2id server 2.4. Use the current v3 of the API instead.

* Removes the `/authz-sessions/rest/v2` web API which was deprecated in 
  Connect2id server 5.0. Use the current v3 of the API instead.

* Removes the `/direct-authz/rest/v1` web API which was deprecated in 
  Connect2id server 2.4. Use the current v2 of the API instead.

* Updates the Connect2id server JWKs generator  
  (`com.nimbusds:c2id-server-jwkset:2.0`) and command line tool 
  (`jwks-gen.jar`) to support key store encryption keys and various new 
  options. The update includes breaking changes to the Java API and the CLI 
  arguments. The previous Connect2id server JWKs generator (version 1.x) 
  remains compatible.


### Configuration

* `env` -- New optional Java system property configuration. When set to `prod` 
  (production) the Connect2id server will not start up if it is configured with 
  the demo key store encryption JWK (`keyStore.encJWK`) or master API access 
  token (`*.apiAccessTokenSHA256`). The default value is `dev` (development).

* `/WEB-INF/keyStore.properties` -- New configuration file for the key store
  introduced in this release. Every property in the configuration file can be
  overridden with a Java system property.

    * `keyStore.encJWK` -- New optional configuration property. Specifies a 
      128-bit AES key for encrypting the stored private key material. Required,
      unless the Connect2id server is configured with static JWK sets and / or 
      a PKCS#11 (HSM) store. The key must be a JWK of type `oct` (octet 
      sequence key), with `use` (key use) `enc` (encryption) and an optional 
      `kid` (key ID). The JSON string may be additionally BASE64URL encoded, to 
      prevent character escape issues in shell environments. 
      
      When this configuration property is specified, it overrides any 
      encryption key in the optional `WEB-INF/keyStoreEncJWK.json` file.
    
    * `keyStore.apiAccessTokenSHA256` -- New optional configuration property. 
      Specifies the access token for the key store web API, represented by its 
      SHA-256 hash (in hexadecimal format). The hash is a measure to prevent 
      accidental leakage of the token through configuration files, logs, etc. 
      The token is of type Bearer, non-expiring and must contain at least 32 
      random alphanumeric characters to make brute force guessing impractical. 
      If not specified the key store web API is disabled.

      Additional access tokens, for token roll-over or other needs, can be
      configured by appending a dot (.) with a unique label to the property 
      name, e.g. as `keyStore.apiAccessTokenSHA256.1=abc...`.
    
    * `keyStore.defaultRSAKeySize` -- New optional configuration property. 
      Specifies the default size of new generated RSA JWKs. The supported sizes 
      are 2048, 3072 and 4096 bits. The default value is 2048.
    
    * `keyStore.generateIfEmpty.op`, `keyStore.generateIfEmpty.federation` -- 
      New optional configuration properties. When true and the specified JWK 
      set context (`op` for OpenID provider, `federation` for OpenID Federation 
      1.0) is empty, the key store will automatically generate the required JWK 
      set. The default value is `true` (enabled).
    
    * `keyStore.importIfEmpty.op`, `keyStore.importIfEmpty.federation` -- New 
      optional configuration properties. Specify a JWK set to import into the 
      key store if the specified context (`op` for OpenID provider, 
      `federation` for OpenID Federation 1.0) is empty. The JSON string may be 
      additionally BASE64URL encoded, to prevent character escape issues in 
      shell environments. The default value is none.
    
    * `keyStore.importExp` -- New optional configuration property. Specifies  
      the time, as seconds since the Unix epoch, after which any configured JWK 
      sets for import will be ignored. Must be not more than 1 day ahead of the 
      current time when the Connect2id server starts up.
    
    * `keyStore.jwkSetCacheLifetime` -- New optional configuration property.
      Controls the local caching of JWK sets retrieved from the key store. The
      cache lifetime is specified in seconds. Zero or negative disables 
      caching. Must not exceed `600` seconds (10 minutes). The default value is 
      `60` seconds (1 minute).
    
    * `keyStore.staticJWKSet.op`, `keyStore.staticJWKSet.federation` -- New 
      optional configuration properties. Specify a static JWK set for the 
      specified context (`op` for OpenID provider, `federation` for OpenID 
      Federation 1.0). The JSON string may be additionally BASE64URL encoded, 
      to prevent character escape issues in shell environments. The default 
      value is none.
    
      When a static JWK set is specified for a given context the key store web 
      API operations for key rotation, revocation and history retrieval are 
      disabled.
    
    * `keyStore.pkcs11.op.enable`, `keyStore.pkcs11.federation.enable` -- New 
      optional configuration property. Enables / disables PKCS#11 (HSM) support 
      for the specified context (`op` for OpenID provider, `federation` for 
      OpenID Federation 1.0). The default value is `false` (disabled).
      
      Replaces the deprecated `pkcs11.enable` for the OpenID provider context.
    
    * `keyStore.pkcs11.op.configFile`, `keyStore.pkcs11.federation.configFile` 
      -- New optional configuration properties. Specify the location of the Sun 
      PKCS#11 provider configuration for the specified context (`op` for OpenID 
      provider, `federation` for OpenID Federation 1.0). The configuration can 
      be alternatively passed inline as a string. The string may be 
      additionally BASE64URL encoded, to prevent character escape issues in 
      shell environments.
    
      Replaces the deprecated `pkcs11.configFile` for the OpenID provider 
      context.
    
    * `keyStore.pkcs11.op.password`, `keyStore.pkcs11.federation.password` -- 
      New optional configuration properties. Specifies the password (PIN) 
      required to unlock the HSM for the specified context (`op` for OpenID 
      provider, `federation` for OpenID Federation 1.0). 

      Replaces the deprecated `pkcs11.password` for the OpenID provider
      context.
    
    * `keyStore.pkcs11.op.keyIDs.*`, `keyStore.pkcs11.federation.keyIDs.*` -- 
      New optional configuration properties. Specify a list of identifiers 
      (aliases) of PKCS#11 keys to load from the HSM for the specified context 
      (`op` for OpenID provider, `federation` for OpenID Federation 1.0). If 
      omitted or blank all recognised and supported keys will be loaded.
     
      Replaces the deprecated `pkcs11.keyIDs.*` for the OpenID provider
      context.

* `/WEB-INF/keyStoreEncJWK.json` -- New optional file containing the 128-bit 
  AES key in JWK format for the key store. Overridden by the `keyStore.encJWK` 
  configuration property.

* `/WEB-INF/jwkSet.json` -- The file for specifying a static Connect2id server 
  JWK set for the OpenID provider context is removed but remains supported.

* `/WEB-INF/federationJWKSet.json` -- The file for specifying a static 
  Connect2id server JWK set for the OpenID Federation 1.0 context is removed 
  but remains supported.

* `/WEB-INF/jose.properties` -- The configuration file is removed and no longer
  supported.

    * `jose.allowWeakKeys` -- The configuration property is removed and no 
      longer supported. 1024-bit RSA keys are no longer accepted with a special 
      exception. The accepted RSA key sizes are 2048, 3072 and 4098 bits.

    * `pkcs11.enable` -- Deprecated, use `keyStore.pkcs11.op.enable` instead.

    * `pkcs11.configFile` -- Deprecated, use `keyStore.pkcs11.op.configFile` 
      instead.

    * `pkcs11.password` -- Deprecated, use `keyStore.pkcs11.op.password` 
      instead.
    
    * `pkcs11.keyIDs.*` -- Deprecated, use `keyStore.pkcs11.op.keyIDs.*` 
      instead.

* `/WEB-INF/infinispan-*-{mysql|oracle|postgres95|sqlserver}.xml`

    * Upgrades the SQL schema by adding new `jwks` and `jwks_history` tables. 
      In existing deployments the Connect2id server will automatically add the 
      new tables on startup, unless `dataSource.createTableIfMissing` is 
      disabled.
    
* `/WEB-INF/infinispan-*-dynamodb.xml`

    * Upgrades the DynamoDB schema by adding new `jwks` and `jwks_history` 
      tables. In existing deployments the Connect2id server will automatically 
      add the new tables on startup.


### Web API

* `/key-store/rest/v1/` -- New web API for the Connect2id server key store. The 
  web API is disabled unless an access token is configured 
  (`keyStore.apiAccessTokenSHA256`).

    * `/key-store/rest/v1/{ctx}` -- The current JWK set for the specified key 
      context (`op` for OpenID provider, `federation` for OpenID Federation 
      1.0). The GET method retrieves the current JWK set. The private material 
      of JWKs of type `RSA`, `EC` and `OKP` and the secret material of JWKs of 
      type `oct` is masked. The custom `tpr` (thumbprint) JWK parameter 
      indicates the SHA-256 thumbprint (RFC 7638) of each JWK. Supported query 
      parameters: `skip_cache={false|true}`, `pkcs11_only={false|true}`.

    * `/key-store/rest/v1/{ctx}/generate` -- The POST method generates a new 
      JWK set for the specified key context (`op` for OpenID provider, 
      `federation` for OpenID Federation 1.0). Supported form parameters: 
      `rsa={2048|3072|4096}`, `no_eddsa={false|true}`, 
      `revoke_all_active_as_compromised={false|true}`.
    
    * `/key-store/rest/v1/{ctx}/rotate` -- The POST method rotates the public 
      signing JWKs, public encryption JWKs and symmetric JWK for the optional 
      encryption of JWT-encoded access tokens for the specified key context 
      (`op` for OpenID provider, `federation` for OpenID Federation 1.0). The
      superseded keys are marked as such. Supported form parameters: 
      `rsa={2048|3072|4096}`, `no_eddsa={false|true}`.
    
    * `/key-store/rest/v1/{ctx}/{kid}` -- The DELETE method removes a 
      superseded JWK with the specified `kid` (key ID) for the specified key 
      context (`op` for OpenID provider, `federation` for OpenID Federation 
      1.0).
  
    * `/key-store/rest/v1/{ctx}/history` -- The GET method retrieves a 
      timestamped JWK set history for the specified key context (`op` for 
      OpenID provider, `federation` for OpenID Federation 1.0).

* `/authz-sessions/rest/v1` -- Deprecated web API removed. Use the current
  `/authz-sessions/rest/v3` instead. 

* `/authz-sessions/rest/v2` -- Deprecated web API removed. Use the current
  `/authz-sessions/rest/v3` instead.

* `/direct-authz/rest/v1` -- Deprecated web API removed. Use the current
  `/direct-authz/rest/v2` instead.


### Resolved issues

* The `WEB-INF/*.properties` configuration files are made optional and may be 
  removed from Connect2id server deployments in which the configuration is
  supplied entirely via Java system properties (issue server/1037). 

* Removes the deprecated `/authz-sessions/rest/v1`, `/authz-sessions/rest/v2`
  and `/direct-authz/rest/v1` web APIs (issue server/1038).

* Changes the Oracle database schema for the `data` column of the `clients`
  table from `NVARCHAR2(2000)` to `NCLOB` to allow storage of JSON objects
  with a size greater than 2 thousand characters. The column type change 
  applies only to new Connect2id server deployments. Existing deployments can 
  continue using their existing schema (with `NVARCHAR2(2000)`) (issue 
  server/1036). 

* Fixes the fatal `[OP0140] Configuration property conflict` log message, must
  name the `op.authz.feedSubjectSessionClaimsIntoIDToken` configuration 
  property as deprecated (issue server/1034).


### Dependency changes

* Adds com.nimbusds:c2id-server-key-store:1.7

* Upgrades to com.nimbusds:tenant-manager:10.1.5

* Upgrades to com.nimbusds:c2id-server-jwkset:2.0

* Upgrades to com.nimbusds:nimbus-jose-jwt:9.47

* Upgrades to com.nimbusds:nimbus-jwkset-loader:7.3

* Upgrades to com.nimbusds:oauth2-authz-store:26.9

* Updates to com.nimbusds:oidc-session-store:21.2

* Upgrades to com.nimbusds:common:3.6

* Updates to Infinispan 14.0.33.Final

* Updates to Bouncy Castle 1.79

* Updates to com.google.crypto.tink:tink:1.15.0

* Updates to org.slf4j:slf4j-api:2.0.12

* Updates to Log4j 2.24.2



## 16.1.1 (2024-11-11)

### Resolved issues

* Fixes an issue that affected the raising of the authentication prompt in the 
  presence of an `WebSSOEligibilityChecker` SPI plugin, for example due to an 
  ACR step-up request (issue server/1031).



## 16.1 (2024-10-17)

## Summary

* New plugin interface for adding custom checks whether an OAuth 2.0 
  authorisation / OpenID authentication request is eligible for web single 
  sign-on (SSO), after the Connect2id server has completed its own checks.

* It is now possible to specify a timestamp when posting a revocation event to 
  the revocation API of the Connect2id server. This is intended for use cases 
  that require exact control over which tokens and authorisations are to be 
  revoked, based on their `iat` (issued-at time).  

* Connect2id server deployments with an AWS DynamoDB database can now enable 
  deletion protection for the tables in the database configuration.

### Configuration

* /WEB-INF/oidcProvider.properties

    * op.sso.disableForSelectedClients -- Deprecated for removal, use the new 
      `WebSSOEligibilityChecker` SPI instead.

* /WEB-INF/infinispan-*-dynamodb.xml

    * Upgrades the dynamodb schema to v3.0.

    * New `dynamodb.enableDeletionProtection` configuration property of type
      boolean (`true`|`false`). If `true` deletion protection will be enabled  
      for all DynamoDB tables. If `false` this configuration has no effect on  
      tables with already enabled deletion protection, so that their protection
      cannot be accidentally lifted by a change in the Connect2id server 
      configuration. To lift the deletion protection for a table use the AWS 
      console or another method.


### Web API

* /authz-sessions/rest/v3/

    * Adds a `WebSSOEligibilityChecker` SPI for plug-in of additional custom 
      checks whether an OAuth 2.0 authorisation / OpenID authentication request 
      is eligible for web single sign-on (SSO), after the Connect2id server has 
      completed its own checks.

* /authz-store/rest/v3/

    * The `revocation` resource receives a new optional `timestamp` (revocation
      timestamp, as seconds since the Unix epoch) form parameter. When posting 
      a new revocation for a `subject`, `client_id` or `actor` this form 
      parameter can be used to specify the time of the revocation event. The 
      default `timestamp` value is the current time.

* /session-store/rest/v2/

    * The `sessions` resource receives a new optional `ctx` (session context) 
      query parameter for requests to retrieve the sessions for a specified 
      `subject`. Can be used to filter the returned sessions according to their
      context, for example `web` or `device`.


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:5.10

    * WebSSOEligibilityChecker -- New SPI for plugging additional checks 
      whether an OAuth 2.0 authorisation / OpenID authentication request is 
      eligible for web single sign-on (SSO), after the Connect2id server has 
      completed its own checks.
    
      Prior to calling this SPI the Connect2id server ensures the following
      conditions are met for a request to be eligible for SSO:

        * A subject (end-user) session is present.
        
        * The subject session authentication lifetime (`auth_life`), if
          specified for the session, has not expired.
        
        * If the request is an OpenID authentication request with a
          maximum authentication age (`max_age`) or an ACR level
          (`acr_values`), that the subject session satisfies them.
        
        * If a particular user identity is required (via an `id_token_hint`), 
          that it matches session subject.
        
        * The request doesn't specify a prompt `login`, `select_account` or 
          `create`.
        
        * A Connect2id server configuration doesn't trigger an authentication
          prompt.

      If the OAuth 2.0 authorisation / OpenID authentication request is
      eligible for SSO the `isEligible` check method returns `true`. Else the 
      method returns `false`, to cause the Connect2id server to prompt the 
      end-user for authentication.
    
    * Updates the `SelfContainedAccessTokenClaimsCodec` SPI. The 
      `TokenCodecContext` interface receives a new `getClaimNamesCompressor()`
      method that returns a `ClaimNamesCompressor` intended to reduce the size 
      of the consented claims array in self-contained (JWT-encoded) access 
      tokens. The exposed `ClaimNamesCompressor` has been used by the default 
      SPI implementation included in the Connect2id server to compress the
      strings in the `clm` (claim names) JSON array, by employing a highly 
      efficient bitfield-based algorithms which uses the dictionary configured 
      by `claimsCompression.properties`.


### Resolved issues

* Enabling DynamoDB continuous backups (point-in-time recovery, PITR) for a 
  table may require the request to be retried due to a transient 
  `ContinuousBackupsUnavailableException` with a message suggesting retrial.
  This DynamoDB behaviour is observed at times when enabling continuous
  backups after new table creation, despite the prior `waitForActive` call.
  The request will be retried 5 times, with a wait time increasing by 1 second 
  (issue dynamodb/25).

* Fixes a bug to ensure DynamoDB continuous-backups (PITR) is applied to a 
  table that was left in an incomplete PITR state after a
  `ContinuousBackupsUnavailableException` without retrial when DynamoDB
  reported enabled continuous backups and disabled PITR (issue dynamodb/25).

* Authorisation requests with an invalid `response_type`, `max_age` or 
  `display` parameter that include illegal characters according to RFC 6749, 
  section 4.1.2.1, must produce an `invalid_request` error and not result in an 
  HTTP 500 in the authorisation session web API (issues server/1027, 
  oidc-sdk/482, oidc-sdk/483, oidc-sdk/484).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:5.10

* Upgrades to com.nimbusds:oauth2-oidc-sdk:11.20.1

* Updates to com.nimbusds:oauth2-authz-store:26.7

* Updates to com.nimbusds:oidc-session-store:20.5

* Updates to Infinispan 14.0.32.Final



## 16.0 (2024-09-30)

### Summary

* Adds support for OpenID Connect Native SSO for Mobile Apps 1.0 (based on 
  draft 06 and the anticipated future draft 07).

  This OpenID Connect extension enables a group of native applications, such as
  mobile applications of the same vendor, to establish a shared device-based 
  session with the Connect2id server (as OpenID provider) for the end-user. 
  The session enables the participating applications to benefit from a 
  device-based single sign-on (SSO). Issued ID tokens and refresh tokens are
  cryptographically bound to the device session. Closing or expiring the device 
  session automatically disables any refresh tokens bound to it.

  The device session is represented by an opaque cryptographically secured 
  `device_secret` generated by the Connect2id server and linked to a subject
  (end-user) session stored in the server. The device sessions can be managed 
  via the Connect2id subject session store web API. The Connect2id server 
  maintains strict isolation between web sessions (used for web SSO) and 
  device sessions (used for device SSO).

  A client application can end a device session by calling the token revocation 
  endpoint with the `token` parameter set to the `device_secret` value and the 
  `token_type_hint` parameter to `device_secret`.

  On Android participating applications can store the `device_secret` and the 
  ID token(s) required for the device SSO in the `EncryptedSharedPreferences`. 
  On iOS applications can use the Keychain services API. 

  A `DeviceSSOHandler` SPI is provided to enable deployments to make decisions 
  about the tokens to issue in a response to a back-channel device SSO from a 
  client. A default plugin enabling a range of configurations, such as which 
  scope values must trigger end-user interaction at the OpenID provider, is 
  included.

* Includes a new plugin for sourcing OpenID Connect claims about a subject 
  (end-user) from the subject session `claims` field. The plugin implements the
  `AdvancedClaimsSource` SPI from the Connect2id server SDK. 
 
  The `claims` field of a subject session can be conveniently populated with 
  attributes at the time of its creation, which occurs when the end-user is 
  authenticated with the Connect2id server. For a closed or expired subject 
  session the source returns no claims. This makes the claims source suitable 
  for client applications that require UserInfo endpoint access only when the 
  end-user is logged in with the OpenID provider. ID tokens are always issued 
  in the presence of a subject session, thus any ID token bound sourcing of 
  claims by this plugin is guaranteed.

  See `/WEB-INF/sessionClaimsSource.properties` for the plugin configuration
  properties.

  Note that the default `op.idToken.includeSubjectSessionClaims` configuration 
  is at cross-purposes with this plugin. This configuration property must 
  therefore be disabled, or, if automatic feeding of selected claims from the 
  subject session into the issued ID tokens is still desired, specify the exact 
  names of the claims to feed.

* Consolidates the Connect2id server plugin (SPI implementations) Maven 
  dependencies for handling the OAuth 2.0 grants `client_credentials`, 
  `password`, `urn:ietf:params:oauth:grant-type:jwt-bearer`,
  `urn:ietf:params:oauth:grant-type:saml2-bearer` and 
  `urn:ietf:params:oauth:grant-type:token-exchange` into a single  
  `com.nimbusds:oauth2-grant-handlers` dependency. The consolidated dependency  
  unifies the grant handler features and improves code reuse. The new 
  dependency includes the default `LocalDeviceSSOHandler` implementation for
  handling back-channel device SSO requests.


### Configuration

* /WEB-INF/oidcProvider.properties

    * op.sso.device.enable -- New optional configuration property to enable /
      disable OpenID Connect Native SSO 1.0. The default value is `false` 
      (disabled).
    
    * op.sso.device.sessionMaxLifetime -- New optional configuration property 
      that sets the maximum device session lifetime, in minutes. A negative 
      value implies no time limit. Must not be zero. The default value is 
      `259200` minutes (180 days).
    
    * op.sso.device.sessionAuthLifetime -- New optional configuration property
      that sets the device session maximum authentication lifetime, in minutes. 
      A negative value implies no time limit. Must not be zero. The default 
      value is `10080` minutes (30 days).
    
    * op.sso.device.sessionMaxIdleTime -- New optional configuration property 
      that sets the maximum device session idle time, in minutes. A negative 
      value implies no time limit. Must not be zero. The default value is 
      `1440` minutes (10 days).

* /WEB-INF/infinispan-*-{mysql|oracle|postgres95|sqlserver}.xml

    * Upgrades the SQL schema by adding a new `ctx` (context) column to the 
      `subject_sessions` table. In existing deployments the Connect2id server 
      will automatically add the new column on startup, unless 
      `dataSource.createTableIfMissing` is disabled.

* /WEB-INF/deviceSSOHandler.properties -- New properties file specifying the 
  default configuration of the local handler for OpenID Connect back-channel 
  device SSO requests (implements the `DeviceSSOHandler` SPI). Can be 
  overridden with Java system properties.

    * op.deviceSSOHandler.enable -- Enables / disables the handler. Disabled 
      (`false`) by default.
    
    * op.deviceSSOHandler.scopeRequiringInteraction -- Scope values that 
      require end-user interaction (re-authentication or explicit consent) at 
      the authorisation endpoint, as space separated list. Back-channel device 
      SSO requests that include any of these scope values will trigger an
      `interaction_required` error at the token endpoint. None by default.
    
    * op.deviceSSOHandler.accessToken.lifetime -- The access token lifetime, in 
      seconds. If zero, blank or omitted the default access token lifetime 
      configured by `authzStore.accessToken.defaultLifetime` applies.
    
    * op.deviceSSOHandler.accessToken.encoding -- The access token encoding. 
      The default value is `SELF_CONTAINED`.
      
      Supported encodings:
      
        * `IDENTIFIER` -- The access token is a secure identifier. The 
           associated authorisation is looked up by a call to the Connect2id 
           server token introspection endpoint.
     
        * `SELF_CONTAINED` -- Self-contained access token. The associated
           authorisation is encoded in the access token itself, as a signed and
           optionally encrypted JSON Web Token (JWT). Can also be looked up by 
           a call to the Connect2id server token introspection endpoint.

    * op.deviceSSOHandler.accessToken.encrypt -- If `true` enables additional 
      encryption of self-contained (JWT-encoded) access tokens. Disabled 
      (`false`) by default.
    
    * op.deviceSSOHandler.accessToken.audienceList -- Optional audience for the 
      access tokens, as comma and / or space separated list of values.
    
    * op.deviceSSOHandler.accessToken.includeClientMetadataFields -- Names of 
      client metadata fields to include in the optional access token `data` 
      field, empty set if none. To specify a member within a field that is a
      JSON object member use dot (.) notation.
    
    * op.deviceSSOHandler.refreshToken.issue -- Enables / disables refresh 
      token issue. Enabled (`true`) by default.
    
    * op.deviceSSOHandler.refreshToken.lifetime -- The refresh token lifetime, 
      in seconds. Zero for no expiration. If -1, blank or omitted the default 
      refresh token lifetime configured by 
      `authzStore.refreshToken.defaultLifetime` applies.
    
    * op.deviceSSOHandler.refreshToken.maxIdleTime -- The refresh token maximum 
      idle time, in seconds. Zero for no idle time expiration. The default 
      value is `0`.
    
    * op.deviceSSOHandler.refreshToken.rotate -- If `true` causes the refresh 
      token to be updated (rotated) on each refresh token use. If blank or 
      omitted the default rotation setting configured by
      `op.deviceSSOHandler.refreshToken.rotate` applies.
    
    * op.deviceSSOHandler.claimsTransport -- The transport to use for 
      authorised claims. The default value is `USERINFO`:
      
      Supported values:
   
        * `USERINFO` -- To release the claims at the UserInfo endpoint of the
          Connect2id server by presenting the issued access token.
        
        * `ID_TOKEN` -- To release the claims in the ID token. If an ID token 
          is not issued for a particular request the claims will be diverted 
          for release at the UserInfo endpoint.

* /WEB-INF/clientGrantHandler.properties

    * op.grantHandler.clientCredentials.simpleHandler.accessToken.lifetime --
      If zero or omitted the default access token lifetime configured by
      `authzStore.accessToken.defaultLifetime` applies. The previously
      configured property value of 600 seconds is removed.
    
    * op.grantHandler.clientCredentials.simpleHandler.accessToken.encoding -- 
      When omitted or blank receives a default value `SELF_CONTAINED`.
    
    * op.grantHandler.clientCredentials.simpleHandler.accessToken.encrypt -- 
      When omitted or blank receives a default value `false`.

* /WEB-INF/selfIssuedJWTBearerHandler.properties

    * op.grantHandler.selfIssuedJWTBearer.accessToken.lifetime -- If zero or 
      omitted the default access token lifetime configured by
      `authzStore.accessToken.defaultLifetime` applies. The previously 
      configured property value of 300 seconds is removed.

    * op.grantHandler.selfIssuedJWTBearer.accessToken.encoding -- When omitted 
      or blank receives a default value `SELF_CONTAINED`.

    * op.grantHandler.selfIssuedJWTBearer.accessToken.encrypt -- When omitted 
      or blank receives a default value `false`.

* /WEB-INF/sessionClaimsSource.properties -- New properties file specifying the
  default configuration of the subject session-based claims source (implements 
  the `AdvancedClaimsSource` SPI). Can be overridden with Java system 
  properties.

    * op.sessionClaimsSource.enable -- Enables / disables the claims source. 
      Disabled by default.
    
    * op.sessionClaimsSource.supportedClaims -- The names of the supported 
      (standard and custom) OpenID Connect claims, as a comma and / or space 
      separated list. Support for a pattern of claims can be indicated with the 
      `*` wildcard character, for example as `https://idp.example.com/*` for a 
      set of claims having a common URI prefix in their name. A single claim 
      set to `*` indicates support for all claims supported by the OpenID 
      provider without explicitly listing them.


### Web API

* /.well-known/openid-configuration

    * native_sso_supported -- New optional metadata field of type boolean to  
      indicate support for OpenID Connect Native SSO for Mobile Apps 1.0. The  
      default value is `false`. Omitted if not supported.

* /token

    * Supports the `device_secret` token request parameter for OpenID Connect 
      native SSO with an OAuth 2.0 authorisation code grant. For native SSO the 
      code grant scope must include the `openid` and `device_sso` values.
     
    * Supports the `device_secret` token response parameter for OpenID Connect
      native SSO with an OAuth 2.0 authorisation code or token exchange grant.

* /token/revoke

    * Supports `device_secret` revocation, which has the effect of closing the
      device session. Requires the `token` parameter to be set to the value of
      `device_secret` and the optional `token_type_hint` parameter to be set to 
      `device_secret`.

* /session-store/rest/v2/

    * Subject (end-user) sessions receive a new mandatory `ctx` (context) 
      field, to identify the context of their use.
     
      The context `web` is for sessions created by the Connect2id server in the 
      OAuth 2.0 authorisation code and implicit flows which involve a web 
      browser. A web session links to a cookie stored by the Identity Provider 
      login page in the user's browser. This is the default context for 
      sessions. Existing sessions found in the Connect2id server subject 
      session store receive the default value `web`.
      
      The context `device` is for sessions created by the Connect2id server for
      OpenID Connect native SSO. A device session links to a `device_secret` 
      stored by participating client applications on the user's device.
      
      The Connect2id server isolates sessions with different contexts, so that 
      a session with the `web` context cannot be used in a `device` context and 
      vice versa.

      All sessions for a given subject (end-user), regardless of their context, 
      count toward the session quota, as configured by the 
      `sessionStore.quotaPerSubject` property.

      The session store web API allows the creation of sessions with other
      context values, for use by auxiliary services and integrations that 
      require or will benefit from isolated sessions.


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:5.8

    * New `DeviceSSOHandler` SPI for authorising the scope values and other 
      token properties for native SSO back-channel token requests, which 
      requests are based on the OpenID Connect native SSO profile for 
      the token exchange grant (RFC 8693). A default plugin is provided, see 
      `/WEB-INF/deviceSSOHandler.properties`.

    * The `TokenExchangeGrantHandler` SPI receives a new
      `TokenIntrospection.getOIDCClientMetadata` helper method to obtain the
      OAuth 2.0 / OpenID relying party metadata for a `subject_token` that is
      a locally issued access token.
    
    * Updates all OAuth 2.0 grant handler SPIs by providing a 
      `GrantHandlerContext.resolveClaimNames(Scope)` helper method that 
      resolves the claim names for all values in the specified scope that 
      expand to claims. Recognises all standard OpenID Connect scope values as 
      well as any custom mappings configured in the Connect2id server. The
      `processGrant` methods with the `InvocationContext` are deprecated, 
      in favour of a new `processGrant` method with a `GrantHandlerContext` 
      argument that extends `GrantHandlerContext`. The updated SPIs are:
        * `ClientCredentialsGrantHandler`
        * `PasswordGrantHandler`
        * `SelfIssuedJWTGrantHandler`
        * `ThirdPartyJWTGrantHandler`
        * `SelfIssuedSAML2GrantHandler`
        * `ThirdPartySAML2GrantHandler`
        * `TokenExchangeGrantHandler`


### Resolved issues

* Modifies processing of the optional `id_token_hint` parameter at the 
  authorisation endpoint and the end-session endpoint to treat the ID token 
  hint as invalid for clients that are registered with an 
  `id_token_signed_response_alg` metadata parameter `none` (unsecured ID 
  token) or `HSxxx` (HMAC-SHA-2 secured ID token). These JWS algorithms cannot 
  establish the authenticity of the ID token as being issued by the OpenID 
  provider, only digitally signed tokens can, such as tokens signed with the 
  default `RS256` JWS algorithm. Clients that use the `id_token_hint` parameter 
  to provide a hint to the Connect2id server about the end-user identity or 
  session in OpenID authentication and OpenID RP-initiated logout requests must 
  switch to a digital signature algorithm, such as `RS256` (issue server/1013).

* Fixes a regression introduced in v14.0 that caused the names of the 
  authorised UserInfo endpoint claims for an access token to include the names 
  of claims for which the Connect2id server has a previous long-lived consent 
  on record for the subject and client_id. The bug affected long-lived access 
  token authorisations that include no UserInfo claims. Access token 
  authorisations that include at least one UserInfo claim were not affected
  (issue server/1022). 

* Increases the maximum acceptable clock skew for the `iat` (issued-at) DPoP 
  proof claim checks from 30 to 120 seconds. The change was prompted by the 
  observation that mobile devices can experience system clock drift in the 
  range up to 120 seconds (issue server/1014). 

* Authorisation requests with an invalid `code_challenge_method` that include 
  illegal characters according to RFC 6749, section 4.1.2.1, must produce an 
  `invalid_request` error and not result in an HTTP 500 in the authorisation 
  session web API (issue server/1020).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:5.8

* Upgrades to com.nimbusds:oauth2-oidc-sdk:11.20

* Upgrades to com.nimbusds:oauth2-authz-store:26.5.5

* Upgrades to com.nimbusds:oidc-session-store:20.4

* Updates to com.nimbusds:c2id-server-jwkset:1.30.6

* Upgrades to com.thetransactioncompany:java-property-utils:2.0

* Updates to Infinispan 14.0.31.Final

* Updates to org.apache.common:commons-lang3:3.15.0

* Removes com.nimbusds:oauth-client-grant-handler

* Removes com.nimbusds:oauth-jwt-self-issued-grant-handler

* Removes com.nimbusds:oauth-grant-handlers-web

* Adds com.nimbusds:oauth2-grant-handlers:1.3

* Adds com.nimbusds:oidc-claims-source-session:1.0



## 15.9.1 (2024-08-09)

### Resolved issues

* Edits the `OP2015` and `OP1002` log INFO ID token configuration messages for
  better clarity (issue server/1008).
>>>>>>> master



## 15.9 (2024-08-08)

### Configuration

* /WEB-INF/oidcProvider.properties

    * op.idToken.includeSubjectSessionClaims -- New optional configuration
      property to control the automatic inclusion of members from the subject
      session `claims` JSON object in issued ID tokens. Applies to regular and
      `prompt=none` OpenID authentication requests as well as ID token
      refreshes. An `*` (asterisk) selects all members. The member names can
      alternatively be specified as comma and / or space separated list. An
      empty list disables the inclusion. The default value is `*` (include
      all).

    * op.authz.feedSubjectSessionClaimsIntoIDToken -- Deprecated for removal,
      use `op.idToken.includeSubjectSessionClaims` instead.


### Dependency changes

* Updates to com.nimbusds:oauth2-authz-store:26.5.2

* Upgrades to com.nimbusds:common:3.4

* Updates to net.thisptr:jackson-jq:1.0.0-preview.20240207



## 15.8 (2024-07-26)

### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:5.6

    * The `TokenExchangeGrantHandler` SPI receives a new 
      `TokenIntrospection.getOIDCClientMetadata` helper method to access the 
      OAuth 2.0 / OpenID relying party metadata for a `subject_token` that is
      a locally issued access token.


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:5.6

* Updates to Infinispan 14.0.29.Final

* Updates to com.h2database:h2:2.3.230

* Updates to Dropwizard Metrics 4.2.26



## 15.7.1 (2024-06-27)

### Resolved issues

* Fixes the return value of the `IDTokenIssueEvent.getLocalSubject` method (in 
  the `IDTokenIssueEventListener` SPI) to return the local subject and not the 
  pairwise subject value when the ID token is issued in response to an OAuth 
  2.0 authorisation code grant (issue server/1001).

* Removes the redundant automatic setting of the  
  `tls_client_certificate_bound_access_tokens` client metadata field when a 
  client is registered for `self_signed_tls_client_auth`, an artefact from 
  Connect2id server v6.x when persistence of the 
  `tls_client_certificate_bound_access_tokens` client metadata field was not 
  supported (issue server/1003).

* Calls to the token introspection endpoint with a blank `token` value and a 
  `token_type_hint` set to `access_token` must produce an HTTP 400 Bad Request,
  not an HTTP 500 Internal Server Error (issue server/1004).

* Calls to the token revocation endpoint with a blank `token` value must 
  produce an HTTP 400 Bad Request, not an HTTP 500 Internal Server Error (issue 
  oidc-sdk/471).

* Calls with `client_secret_jwt` or `private_key_jwt` authentication with an 
  empty or blank `client_id` must produce an HTTP 400 Bad Request, not an HTTP
  500 Internal Server Error (issue oidc-sdk/472).


### Dependency changes

* Updates to com.nimbusds:oauth2-oidc-sdk:11.13

* Upgrades to com.nimbusds:nimbus-jose-jwt:9.40

* Updates to com.nimbusds:c2id-server-jwkset:1.30.6

* Updates to org.postgresql:postgresql:42.7.3



## 15.7 (2024-06-03)

### Summary

* Enables issue of DPoP access tokens (RFC 9449) for the JWT and SAML 2.0 
  bearer OAuth 2.0 grants.

* Updates the CustomTokenResponseComposer SPI to provide access the OpenID 
  claims source and a generic JWT signer.

* Updates the IDTokenIssueEvent in the IDTokenIssueEventListener SPI to provide
  access to the local subject, useful in cases when the ID token `sub` 
  (subject) is a pairwise identifier.


### Web API

* /token

    * Enables support for issue of DPoP-bound access tokens (RFC 9449) for the 
      following OAuth 2.0 grants:
    
      * `urn:ietf:params:oauth:grant-type:jwt-bearer` (RFC 7523)
      
      * `urn:ietf:params:oauth:grant-type:saml2-bearer` (RFC 7522)


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:5.5

    * The `CustomTokenResponseComposer` SPI is updated, adding methods to the
      `TokenResponseContext` to access the OpenID claims source and a generic
      JWT signer.
    
    * IDTokenIssueEvent, part of the IDTokenIssueEventListener SPI, provides 
      access to the local ID token subject, useful in cases when the ID token 
      `sub` (subject) claim is a pairwise identifier.


### Resolved issues

* The Connect2id server must accept signed request objects (JARs) with the JWT 
  `typ` (type) header values `oauth-authz-req+jwt` (see RFC 9101) and `JWT`
  (issue server/999).

* The logout JWT in OpenID Connect back-channel logout notifications must 
  include an `exp` (expiration time) claim. The expiration for the  logout JWTs 
  is set 5 minutes into future (issue server/1000).

* Improves the parse performance of JSON numbers in JWT claims sets (issue 
  nimbus-jose-jwt/546). 


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:5.5

* Updates to com.nimbusds:oauth2-oidc-sdk:11.12

* Updates to com.nimbusds:c2id-server-jwkset:1.30.5

* Updates to com.nimbusds:nimbus-jose-jwt:9.39.3

* Updates to com.google.code.gson:gson:2.11.0

* Updates to commons-codec:commons-codec:1.17.0



## 15.6 (2024-05-10)

### Summary

* New plugin interface (Service Provider Interface, or SPI) for listening to 
  end-user authentication and consent events at the authorisation endpoint of
  the Connect2id server.

* Enables delivery of back-channel logout notifications in the issuer aliasing 
  mode "isolation" in response to end-user logout at the logout session API.


### Web API

* /logout-sessions/rest/v1/

    * Delivers back-channel logout notifications when OpenID provider / OAuth 
      2.0 server issuer aliasing mode `PERSISTED_GRANT_ISOLATION` is 
      configured. Previously this mode caused the delivery of back-channel 
      logout notifications resulting from logout session API calls to be 
      blocked.

* /session-store/rest/v2/

    * Delivers back-channel logout notifications in response to end-session 
      (`DELETE`) calls when OpenID provider / OAuth 2.0 server issuer aliasing 
      mode `PERSISTED_GRANT_ISOLATION` is configured. Previously this mode 
      caused the delivery of back-channel logout notifications resulting from 
      end-session API calls to be blocked.


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:5.4

    * New `SubjectAuthAndConsentEventListener` SPI for listening to subject 
      authentication and consent events.
      
      For clients using the code flow (`response_type=code`), the event is 
      dispatched when the client submits a valid authorisation code at the 
      token endpoint of the Connect2id server.
    
      For clients using an implicit (`response_type=token`, 
      `response_type=id_token`, `id_token token`) or hybrid flow 
      (`response_type=code id_token`, `response_type=code token`,
	  `response_type=code id_token token`), the event is dispatched when the 
      request at the authorisation endpoint of the Connect2id server 
      successfully completes.


### Resolved issues

* Fixes StackOverflowException in SPI calls that use the `com.nimbusds.openid.
  connect.provider.spi.internal.sessionstore.SubjectAuthentication.getAMRList` 
  method (issue server/993).

* Revises the OpenID Connect Back-Channel Logout 1.0 policy when issuer alias
  mode `PERSISTED_GRANT_ISOLATION` is configured. Back-channel logout 
  notifications were previously blocked in this mode. Starting with this 
  release notifications resulting from API-originating logout / end-session 
  requests will be delivered. Note that the delivery of back-channel logout 
  notifications on subject session expiration remains blocked since subject 
  sessions do not record issuer alias information (issue server/992).

* Prevents Connect2id server startup when a given OpenID claim is advertised as 
  supported by two or more enabled claims sources. Disabled claims sources are
  not checked. The exception will be logged at FATAL level using the `OP7004` 
  code, detailing the name of the claim and the claim source (issue 
  server/994).

* Compressed (`zip=DEFLATE`) JWE request objects (JARs) with cipher texts of 
  compressed plain text that are too large must be rejected to conserve CPU and
  memory resources on decompression. When JWE DEFLATE compression is utilised
  a limit of 100K cipher text characters is enforced. Note that all request 
  objects passed by URL (`request_uri`) are already being limited to 50 KBytes
  in size (issue jose-jwt/545).

* Support serialisation of null valued JWT top-level claims returned by the
  `encode` and `advancedEncode` methods of 
  `SelfContainedAccessTokenClaimsCodec` SPI implementations. Previously such 
  JWT claims were ignored and serialised (issue authz-store/234).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:5.4

* Updates to com.nimbusds:oauth2-oidc-sdk:11.10.3

* Upgrades to com.nimbusds:nimbus-jose-jwt:9.39

* Updates to com.nimbusds:c2id-server-jwkset:1.30.2

* Upgrades to com.nimbusds:oauth2-authz-store:26.5.1

* Upgrades to com.nimbusds:oidc-session-store:19.0

* Upgrades to com.nimbusds:tenant-manager:9.1

* Updates to com.nimbusds:tenant-registry:9.0.1



## 15.5 (2024-04-23)

### Configuration

* /WEB-INF/authzStore.properties

    * authzStore.revocation.checkBias -- New optional configuration property
      to adjust the checking of revocation timestamps by adding a negative or
      positive time bias, in seconds. The default value is 0 seconds (no bias).

      A token is deemed active (non-revoked) if there are no recorded 
      revocations for the token subject and / or client ID after the token 
      issued-at time ("iat"). The resolution of revocations and token issued-at 
      times is in seconds.
      
      With no (zero) bias tokens issued at the same second when a revocation is
      recorded are considered active (non-revoked). This enables a subject and 
      / or client ID to have its tokens revoked and then be immediately issued 
      again with active tokens, within the same second.
      
      A positive bias of 1 ensures tokens issued within the same second of a
      revocation are considered revoked, preventing any ambiguity in the time
      sequence of revocation and token issue events. A bias greater than 1 
      should generally not be used.
      
      A negative bias can mitigate false revocation positives in a Connect2id
      server cluster where the clocks of the individual server instances are 
      out of sync.

* /WEB-INF/httpClaimsSource.properties

    * op.httpClaimsSource.includeScope -- New optional configuration property 
      of type boolean. Enables / disables inclusion in the request of the 
      associated consented scope. Disabled by default.


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:5.3

    * New `ClaimsSourceRequestContext.getScope` method that returns the
      associated consented scope:

        * When sourcing claims for a UserInfo endpoint response this is the 
          scope of the access token.
      
        * When sourcing claims for an ID token to be returned at the token 
          endpoint this is the scope of the OAuth 2.0 grant (such as an 
          `authorization_code` or `refresh_token` grant).
      
        * When sourcing claims for an ID token to be returned at the
          authorisation endpoint (for a `response_type` that contains the 
          `id_token` value) this is the scope of the end-user consent.
    
        * When sourcing claims for an ID token returned at the Connect2id 
          server direct authorisation endpoint.
        
        * In all other cases the scope is not provided and will be `null`.


### Resolved issues

* The Connect2id server must reject `op.claims.map.*` configuration properties
  with `id_token:` and `access_token:` prefixed claim names. These prefixes are
  intended for use in the authorisation session API and other documented 
  locations only, using them in a custom OpenID claims map can lead to 
  unintended side effects (issue server/991).

* The AS0214 log INFO message must be recorded at the start of a revocation
  (issue authz-store/233).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:5.3

* Upgrades to com.nimbusds:oauth2-authz-store:26.3

* Updates to com.nimbusds:oidc-session-store:17.4

* Updates to com.nimbusds:oidc-claims-source-http:3.1

* Updates to net.minidev:json-smart:2.5.1

* Updates to BouncyCastle 1.78

* Updates to com.google.crypto.tink:tink:1.13.0

* Updates to commons-codec:commons-codec:1.16.1

* Updates to commons-io:commons-io:2.16.1

* Updates to Log4j 2.23.1



## 15.4 (2024-03-11)

### Summary

* Adds a new Security Token Service (STS) web API to issue signed JWTs for 
  JWT-secured Authorisation Requests (JAR, see RFC 9101) and `private_key_jwt` 
  authentication. Intended when an Identity Provider based on the Connect2id 
  server acts an OAuth 2.0 client / OpenID relying party in federated login 
  scenarios. The signature of an issued JWT can be validated using the server 
  JWK set published at the `/jwks.json` endpoint.


### Configuration

* /WEB-INF/oidcProvider.properties

    * op.sts.apiAccessTokenSHA256.* -- New optional configuration property.
      Specifies a master access token for the STS web API, represented by its 
      SHA-256 hash (in hexadecimal format). The hashed storage is intended to 
      prevent accidental leakage of the token through configuration files, 
      logs, etc. The token is of type Bearer, non-expiring and must contain at 
      least 32 random alphanumeric characters to make brute force guessing 
      impractical. If not specified the web API is disabled.

      Additional access tokens, to facilitate token roll-over or for other 
      needs, can be configured by appending a dot (.) with a unique label to 
      the property name, e.g. as `op.sts.apiAccessTokenSHA256.1=abc...`.

* /WEB-INF/log4j.xml

    * Updates the `PatternLayout`s of the `RollingFile` and `Console` appenders
      to comply with a recent Log4j library change.


### Web API

* /sts/rest/v1/

    * New Security Token Service (STS) web API. Enabled if it has a master API 
      access token (`op.sts.apiAccessTokenSHA256`) configured.

* /sts/rest/v1/issuer

    * New resource to issue signed JWTs for JWT-secured Authorisation Requests 
      (JAR) and `private_key_jwt` authentication. Supports the `RSxxx`, `PSxxx`
      and `ESxxx` JWS algorithm families. The JWTs are generated according to a
      built-in template that defines the acceptable JWT header parameters and 
      claims set. The template ensures the JWT is compliant with the respective 
      specification and potential errors, such as including a `sub` claim in 
      signed request object (JAR), are prevented.

      Supported templates:

        * `JAR` -- For issue of signed JWT-secured Authorisation Requests 
          (JAR), also called request objects in OpenID Connect.
        
        * `private_key_jwt` -- For issue of private key client authentication 
          JWTs for use at the token and other endpoints of an OAuth 2.0 
          authorisation server / OpenID provider.


### Resolved issues

* Corrects the OP5109 log INFO message that records whether a master API token
  for the client registration endpoint is configured (issue server/978).

* Fixes the shipped `/WEB-INF/log4j.xml` configuration to comply with a recent 
  Log4j library change. In Connect2id server 15.3 this caused the log lines to
  contain only the log message (`%m`), with timestamp, etc. missing (issue 
  server/980).

* Logs token success response (OP6225) and error response (OP6226) at INFO 
  level, with client ID, grant type and other details (issue server/982).   


### Dependency changes

* Updates to org.slf4j:slf4j-api:2.0.9

* Updates to com.github.dubasdey:log4j2-jsonevent-layout:0.0.8



## 15.3 (2024-03-05)

### Web API

* /session-store/rest/v2/

    * The `sessions` resource receives a new optional `skip_last_used_update` 
      query parameter for GET requests for individual subject sessions. When 
      `true` the internal timestamp that records the last session use will not
      be updated, leaving the session maximum idle time expiration unaffected.
      When `false` the last used timestamp of the session will be updated. The
      default value is `false`. Only Connect2id server deployments that persist 
      the subject sessions to a database or Redis can skip the update of the
      session last used timestamp. In deployments using Infinispan in-memory 
      replication clustering the query parameter will be disregarded and have
      no effect.
      

### Resolved issues

* Updates the SQL store connector to log the SQL transaction isolation level at
  INFO level at Connect2id server startup. The log message receives the IS0143
  identifier (issue sql-store/38).

* Removes a redundant ConcurrentMap.remove call in the subject session store 
  when updating the last used timestamp of a retrieved persisted subject 
  session with a positive `max_idle` value (issue session-store/97).

* Improves the performance of the authorisation session web API, the 
  authorisation code grant processing, the refresh token grant processing and 
  the token introspection processing by skipping the last used timestamp update 
  of retrieved subject sessions where appropriate (issue server/975).

* Adds a org.apache.logging.log4j:log4j-slf4j-impl dependency to enable jOOQ 
  query logging at DEBUG level (issue server/976).


### Dependency changes

* Updates to com.nimbusds:oauth2-oidc-sdk:11.10.1

* Updates to com.nimbusds:oauth2-authz-store:26.2.2

* Updates to com.nimbusds:oidc-session-store:17.3

* Updates to Infinispan 14.0.24.Final

* Updates to Jersey JAX-RS 3.1.5

* Updates to com.google.guava:guava:32.1.3-jre

* Updates to commons-io:commons-io:2.15.1

* Updates to commons-codec:commons-codec:1.16.0

* Updates to Dropwizard Metric 4.2.25

* Updates to Log4j 2.23.0

* Updates to org.kohsuke.metainf-services:metainf-services:1.11

* Updates to com.nimbusds:infinispan-cachestore-sql:8.2

* Updates to com.h2database:h2:2.2.224

* Updates to org.postgresql:postgresql:42.7.2

* Updates to org.mariadb.jdbc:mariadb-java-client:2.7.12

* Updates to com.microsoft.sqlserver:mssql-jdbc:12.6.1.jre11

* Updates to com.oracle.database.jdbc:ojdbc11:21.13.0.0

* Adds org.apache.logging.log4j:log4j-slf4j-impl:2.23.0

* Removes org.apache.commons:commons-compress:1.24.0



## 15.2 (2024-02-16)

### Configuration

* /WEB-INF/httpClaimsSource.properties

    * op.httpClaimsSource.includeSubjectSessionID -- New optional configuration
      property of type boolean. Enables / disables inclusion in the request of 
      the subject (end-user) session ID  where the claims sourcing was 
      authorised. Disabled by default.

    * op.httpClaimsSource.includeSubjectSession -- New optional configuration
      property of type boolean. Enables / disables inclusion in the request of 
      the subject (end-user) session where the claims sourcing was authorised. 
      Disabled by default.


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:5.2

    * New `ClaimsSourceRequestContext.getSubjectSessionID` method that returns
      the ID of the associated subject (end-user) session where the claims 
      sourcing was authorised.
    
    * New `SubjectSessionID` interface to represent subject (end-user) session
      identifiers.


### Resolved issues

* When the `op.issuerAliasMode` configuration property is set to 
  PERSISTED_GRANT_ISOLATION back-channel logout notifications on subject 
  session close and expiration events must be disabled (issue server/969). 


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:5.2

* Upgrades to com.nimbusds:oidc-claims-source-http:3.0

* Updates to Log4j 2.22.1



## 15.1.1 (2024-01-29)

### Resolved issues

* Fixes generation of the optional "sid" (session ID) claim in ID tokens which
  relied on a non-thread safe use of a SHA-256 message digest. In Connect2id
  server deployments when two or more "sid" claims need to be generated within
  a short length of time (<< 1 second) this could result in the "sid" claim
  receiving an incorrectly computed value or cause the value to leak into the
  "sid" claim of another ID token. Connect2id server deployments with clients /
  OpenID relying parties registered to receive logout notifications with a
  "sid" parameter are strongly advised to update (issue server/967).

### Dependency changes

* Updates to com.nimbusds:infinispan-cachestore-dynamodb:6.0.1

* Updates to com.nimbusds:c2id-server-property-source:2.0.1

* Updates to com.nimbusds:software-statement-verifier:2.2.7



## 15.1 (2024-01-08)

### Configuration

* /WEB-INF/oidcProvider.properties

    * op.reg.rejectNonTLSRedirectionURIs -- The configuration property is 
      extended to apply to native applications (OAuth 2.0 clients registered 
      with `application_type` set to `native`). Previously it applied only to 
      web applications (clients registered with `application_type` set to 
      `web`). The default value remains `true` (non-TLS web host URLs 
      rejected).


### Resolved issues

* Configured custom scope value to claim name mappings (`op.claims.map.*`) must
  be observed when processing `prompt=none` and equivalent OpenID
  authentication requests (issue server/961).

* Processing of OAuth 2.0 authorisation and OpenID authentication requests is 
  hardened to ignore query parameters with illegal escape sequences in the 
  query parameter name or value. Previously such illegal escape sequences in 
  the query string would produce an unchecked exception resulting in an HTTP 
  500 Server Error in the authorisation session start (POST) request (issue 
  server/958).

* Logs an `OP1202` INFO message whenever the SSO for an end-user is disabled 
  due to the requesting client matching the optional 
  `op.sso.disableForSelectedClients` configuration (issue server/959).


### Dependency changes

* Updates to com.nimbusds:oauth2-oidc-sdk:11.9.1



## 15.0 (2024-01-02)

### Summary

* Upgrades to Java 17.

* Upgrades to Jakarta Servlet 6.0 and Jakarta JAX-RS 3.1. The minimum supported 
  Apache Tomcat version becomes 10.1.x. 

* The Connect2id server SDK (v5.0) is upgraded to Java 17 and Jakarta Servlet 
  6.0. Plugins that retrieve a `ServletContext` from the `InitContext` 
  interface will receive a `jakarta.servlet.ServletContext` instead of a
  `javax.servlet.ServletContext`.

* Refresh tokens issued by the Connect2id server can be optionally set to 
  expire after a period of idle time, expressed in seconds. This enables an 
  identity provider to establish a session for a given end-user and client 
  application, with the refresh token remaining active as long as the client
  keeps using it frequently enough to obtain new tokens. When use of the 
  refresh token stops, due to the end-user leaving the client application, and 
  the maximum idle time is reached, the refresh token becomes invalidated. The 
  client application must make a new authorisation request, to log in the 
  end-user and / or obtain their consent, in order to receive a new refresh 
  token.

  The refresh token maximum idle time is settable per end-user and / or client,
  can be applied to both regular and rotated refresh tokens, and is disabled by 
  default. The refresh token maximum idle time is independent of the optional 
  refresh token lifetime setting, and should not exceed it.   

* For token responses containing an access and refresh token, the lifetime of 
  the access token will be automatically trimmed so that it doesn't exceed the 
  maximum idle time or lifetime of the refresh token, whichever is shorter.


### Configuration

* /WEB-INF/oidcProvider.properties

    * op.idToken.ignoreClaimsSourceErrors -- New optional configuration 
      property, deprecating the existing `op.idToken.ignoreUserInfoError` which
      will be removed in a future major Connect2id server release.

* /WEB-INF/infinispan-*-{mysql|oracle|postgres95|sqlserver}.xml

    * Sets the HikariCP property "poolName" to `sqlStore` so that all SQL 
      connection pool related metrics appear under the `sqlStore.*` prefix 
      instead of the name of the first declared Infinispan map / cache 
      (`sessionStore.subjectMap` in the regular Connect2id server edition, or 
       `tenantRegistry.tenants` in the multi-tenant Connect2id server edition).

    * Upgrades the SQL schema by adding a new `rtm` (refresh token maximum idle 
      time) column to the `long_lived_authorizations` table. In existing 
      deployments the Connect2id server will automatically add the new column 
      on startup, unless `dataSource.createTableIfMissing` is disabled.


### Web API

* /authz-sessions/rest/v3/

    * The consent object receives a new optional `refresh_token.max_idle`
      parameter. May be used to specify a maximum idle time, in seconds, for 
      the issued refresh token. If the refresh token is not used within this 
      time period the Connect2id server will invalidate it due to inactivity. 
      The default value is `0` (no idle time expiration).

* /direct-authz/rest/v2

    * The request object receives a new optional `refresh_token.max_idle`
      parameter. May be used to specify a maximum idle time, in seconds, for
      the issued refresh token. If the refresh token is not used within this
      time period the Connect2id server will invalidate it due to inactivity.
      The default value is `0` (no idle time expiration).

* /token

    * The token response will include a `refresh_token_expires_in` parameter  
      for responses that contain a refresh token that is set to expire. When 
      present the value is a positive integer indicating the number of seconds 
      until the refresh token expiration, similar to the standard access token 
      `expires_in` parameter. Responses with refresh tokens that don't have a
      maximum lifetime set will not include this parameter. Clients can use 
      this parameter as a hint when to make a new request to the Connect2id 
      server authorisation endpoint. The `invalid_grant` error code remains the 
      standard and recommended method for clients to detect when the refresh
      token has become invalid, due to expiration or revocation.

    * For token responses with an expiring refresh token, the lifetime of the 
      issued access token is guaranteed to never exceed the lifetime or the 
      maximum idle time of the refresh token. If the lifetime of the
      access token would exceed the refresh token lifetime or maximum idle 
      time, it will be automatically trimmed to achieve expiration parity with  
      the refresh token.

* /authz-store/rest/v3/authorizations

    * The OAuth 2.0 / OpenID Connect authorisation object receives a new 
      optional `rtm` member, representing the refresh token maximum idle time, 
      in seconds. The default value is `0` (no idle time expiration).

* /monitor/v1/metrics

    * All SQL store Hikari connection pool metrics are now published under the 
      `sqlStore.pool.*` prefix. The `[infinispan-cache-name].sqlStore.pool.*` 
      prefix is no longer used.


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:5.1

    * Upgrades to Java 17.
    
    * The `InitContext.getServletContext()` method returns 
      `jakarta.servlet.ServletContext` instead of 
      `javax.servlet.ServletContext` (breaking change).
     
    * The `ServletInitContext` constructor accepts
      `jakarta.servlet.ServletContext` instead of
      `javax.servlet.ServletContext` (breaking change).
    
    * The `RefreshTokenSpec` adds support for setting a maximum idle time for
      the refresh token.


### Resolved issues

* Fixes the HTML representation of the static pages for HTTP 404, 405, 500 and 
  all other errors that aren't handled by the web application (issue 
  server/953).

* Removes support for legacy `cnf.x5t` encoding in access tokens issued by 
  Connect2id server 7.x and older releases (issue authz-store/229).

* The access token lifetime must be automatically trimmed to match the lifetime
  or the remaining lifetime of the refresh token (for expiring refresh 
  tokens)(issue authz-store/228).

* The `rtl` (refresh token lifetime) value of authorisation objects returned by
  the authorisation store web API must reflect the remaining lifetime in 
  respect to `rti` (refresh token issue time) when the refresh token was 
  configured for rotation (issue authz-store/224).

* The `rti` (refresh token issue time) value for long-lived authorisation 
  objects with configured rotation must be updated after a refresh (issue 
  authz-store/224).

* The expiration time of self-contained (JWT-encoded) refresh tokens must not
  be advanced when the refresh token is set for rotation and a new refresh 
  token is returned at the token endpoint in response to a refresh token grant 
  (issue authz-store/225).

* Removes the /client-reg/* endpoint alias to /clients/* that was deprecated in
  Connect2id server 3.0 (2015-03-26) (issue server/911). 

* Removes the authorisation store and session banner pages (issue server/763).

* The SQL store ExpiredEntryPagedReaper must log the IS0128 debug message in 
  all execution paths (issue sql-store/37).

* Fixes the Infinispan metadata recreation for authorisation code entries 
  persisted to an SQL database. The bug was introduced in Connect2id server 
  9.1.1 in response to issue authz-store/176 and caused expired entries to
  remain persisted in the "pending_codes" table (issue authz-store/230).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:5.1

* Updates to com.nimbusds:oauth2-oidc-sdk:11.9

* Updates to com.nimbusds:c2id-server-property-source:2.0

* Updates to com.nimbusds:tenant-manager:9.0.2

* Updates to com.nimbusds:tenant-registry:9.0

* Upgrades to com.nimbusds:oauth2-authz-store:26.2.1

* Upgrades to com.nimbusds:oidc-session-store:17.1

* Updates to com.nimbusds:oauth-grant-handlers-web:2.0

* Updates to com.nimbusds:nimbus-jwkset-loader:6.0

* Updates to com.nimbusds:content-type:2.3

* Upgrades to com.nimbusds:common:3.0.3

* Updates to com.thetransactioncompany:cors-filter:3.0

* Updates to Infinispan 14.0.21.Final

* Updates to com.nimbusds:infinispan-cachestore-common:4.0

* Updates to com.nimbusds:infinispan-cachestore-sql:8.1.1

* Updates to com.nimbusds:infinispan-cachestore-dynamodb:6.0

* Updates to com.nimbusds:infinispan-cachestore-redis:11.0

* Upgrades to Jakarta Servlet API 6.0.0

* Upgrades to JAX-RS 3.1

* Upgrades to Jersey 3.1.4

* Updates to DropWizard Metrics 4.2.23

* Updates to commons-io:commons-io:2.15.0

* Updates to org.apache.commons:commons-compress:1.24.0

* Updates to org.mariadb.jdbc:mariadb-java-client:2.7.11



## 14.11 (2023-12-08)

### Web API

* /logout-sessions/rest/v1/

    * Logout requests initiated by an OpenID Relying Party (RP) with a  
      `post_logout_redirect_uri` parameter will be allowed to proceed if the 
      RP includes its `client_id` parameter. Previously such redirections were 
      allowed to proceed only when a valid `id_token_hint` was provided in the 
      request. With this change RPs that wish to perform a post-logout 
      redirection have the choice to include an ID token hint, their client ID, 
      or both, in order to enable the Connect2id server to validate the URI 
      by checking it against the registered `post_logout_redirect_uris` 
      metadata parameter for the RP.


### Resolved issues

* The client registration endpoint must allow registration of native 
  applications with a localhost or loopback IP frontchannel_logout_uri (issue 
  server/950).  

* The SQL database connector must not serialise the jOOQ Query to an
  intermediate String unless when dealing with Oracle (N)CLOB chunking. By 
  using direct Query execution a PreparedStatement can be correctly inferred 
  (issue sql-store/35).

* Updates SQLStore.write() to switch from the deprecated jOOQ mergeInto() to
  an insertInto() for PostgreSQL and Oracle databases (issue sql-store/34).


### Dependency changes
 
* Updates to com.nimbusds:oauth2-oidc-sdk:11.7.1

* Updates to com.nimbusds:oauth2-authz-store:24.8.1

* Updates to com.nimbusds:oidc-session-store:16.8.1

* Updates to com.nimbusds:nimbus-jose-jwt:9.37.3

* Updates to com.nimbusds:infinispan-cachestore-sql:7.4.3

* Upgrades to org.jooq.pro-java-11:jooq:3.18.7

* Updates to net.minidev:json-smart:2.5.0

* Updates to com.google.crypto.tink:tink:1.12.0

* Updates to BouncyCastle 1.77

* Updates to com.unboundid:unboundid-ldapsdk:6.0.11

* Updates to com.nimbusds:tenant-registry:8.3.1



## 14.10 (2023-11-22)

### Summary

* Connect2id server deployments with an SQL database receive an optimised purge 
  task and SQL query for expired records, such as records for expired subject 
  sessions or identifier-based access tokens. The page limit in the SQL query 
  to select expired records is made configurable, to enable further performance 
  tuning. 

  A Java system property to override the maximum lifetime of SQL connections in 
  the connection pool is also made available. 


### Configuration

* /WEB-INF/infinispan-*-{mysql|oracle|postgres95|sqlserver}.xml

    * Upgrades the SQL store schema to v3.2. 
  
    * dataSource.maxLifetime -- New optional Java system property to override 
      the default maximum lifetime of SQL connections in the Hikari connection 
      pool. The value is expressed in microseconds and must not be shorter than
      30000 (30 seconds). The default value is 1800000 (30 minutes).
    
      This configuration can be used to address Hikari warnings (recorded in 
      the Connect2id server log) "Failed to validate connection <JDBC driver 
      name> (Closed Connection)".
    
    * dataSource.expiredQueryPageLimit -- New optional Java system property to
      override the default page limit of SQL queries to select expired records,  
      such as the records of expired subject sessions. The page limit value is 
      1000 records.

      This configuration can be used to optimise the retrieval of expired 
      records by the Infinispan entry purge task.

    * Upgrades the SQL database connector and the `sessionStore.sessionMap`, 
      `authzStore.idAccessTokenMap`, `authzStore.expendedTokenMap`, 
      `op.authSessionMap`, `clients.registrationsMap` SQL definitions to select
      only expired records from the respective tables when the Infinispan purge
      task runs.


### Resolved issues

* The expired entry reaper in Connect2id server deployments with an SQL
  database must not terminate when an unchecked exception is encountered during
  an SQL select or delete query. The exception must be swallowed and an 
  appropriate error logged (issue sql-store/31, sql-store/32). 

* The `infinispan-replication-*.xml` configurations must not use passivation  
  for `sessionStore.sessionMap` and `sessionStore.subjectMap` as this is 
  incompatible with shared cache stores (issue server/943).

* Reduces and aligns the memory `max-count` limits in the 
  `infinispan-*-local-h2.xml` configurations (issue server/944).

* The page LIMIT in the SQL select query run by the purge task must be inlined 
  (issue sql-store/29).


### Dependency changes

* Updates to com.nimbusds:oauth2-authz-store:24.8

* Updates to com.nimbusds:oidc-session-store:16.8

* Updates to com.nimbusds:infinispan-cachestore-sql:7.4.1

* Updates to com.zaxxer:HikariCP:5.1.0

* Updates to Log4j 2.22.0 



## 14.9 (2023-11-13)

### Summary

* Connect2id server deployments with an AWS DynamoDB receive rate limiting of 
  the paged scan and delete requests that purge the database of expired subject 
  sessions. This enhancement guards regular requests to DynamoDB from 
  potentially being starved of their provisioned database read and write 
  capacity when a purge scan is taking place. Moderating the purge scans may 
  also smooth spikes in DynamoDB consumption over time and thus enable the 
  provisioned capacity to be lowered to save costs.

  Note, in deployments where native DynamoDB TTL expiration is enabled for the 
  subject sessions, by setting the "dynamodb.enableTTL.sessionStore.sessionMap"
  Java system property to `true`, the sessions will be expired automatically by 
  DynamoDB and the Connect2id server doesn't need to run purge scans on the 
  sessions table. The TTL expiration suits Connect2id server deployments that 
  have no OpenID relying parties registered to receive logout and session 
  expiration notifications. Such notifications can be generated only when the
  sessions are expired by the Connect2id server itself.


### Configuration

* /WEB-INF/infinispan-*-dynamodb.xml

    * Upgrades the dynamodb schema to v2.1.

* /WEB-INF/infinispan-*-{stateless|replication}-dynamodb.xml

    * Scan and delete requests that purge the `sub_sessions` table of expired
      subject sessions are rate limited to 10% of the reported provisioned read 
      capacity for the table. For example, if the table is provisioned with 100 
      read capacity units, the consumed purge scan read and delete operations 
      will be rate-limited to 10 capacity units.

      To specify a different value set the "dynamodb.purgeMaxReadCapacity" Java
      system property to the desired maximum read capacity units that may be 
      consumed during a purge, as an absolute value, e.g. `20`, or as a 
      percentage of the current provisioned read capacity of the table, e.g.
      `20%`. Any write capacity consumed to delete expired items is bounded by 
      the "dynamodb.purgeMaxReadCapacity" and will always stay below it. The
      default value of "dynamodb.purgeMaxReadCapacity" is `10%`, as explained 
      above.


### Resolved issues

* The expired entry reaper in Connect2id server deployments with an AWS 
  Dynamo database must not terminate when an unchecked parse or another 
  exception is thrown when parsing a retrieved DynamoDB item. This may occur in 
  DynamoDB items manipulated outside the Connect2id server APIs. Instead, the 
  exception must be swallowed and an error with the offending item logged. This 
  is now done with a `DS0152` log error (issue dynamodb-store/21).

* The `*.dynamoDB.deleteTimer` metrics must include DynamoDB delete requests
  performed as part of purges of expired items (issue dynamodb-store/22).

* Removes legacy comma separator support in `Scope.parse(String)` (issue 
  oidc-sdk/445).


### Dependency changes

* Updates to com.nimbusds:oauth2-oidc-sdk:11.6

* Updates to com.nimbusds:nimbus-jose-jwt:9.37.1

* Updates to com.nimbusds:oauth2-authz-store:24.7.3

* Updates to com.nimbusds:oidc-session-store:16.7.5

* Upgrades to com.nimbusds:infinispan-cachestore-dynamodb:5.2



## 14.8.3 (2023-11-08)

### Resolved issues

* Fixes a bug introduced in v14.8.1 that affects Connect2id server deployments
  with an SQL database. The bug resulted in repeat duplicate SQL delete queries 
  when purging expired records in the database, causing excessive slowdown of 
  the purge task in SQL tables with many expired records, such as records for
  subject sessions (issue server/938, sql-store/25).

* The `*.sqlStore.deleteTimer` metrics must include SQL delete queries 
  performed as part of purges of expired records (issue sql-store/26).


### Dependency changes

* Updates to com.nimbusds:oauth2-authz-store:24.7.2

* Updates to com.nimbusds:oidc-session-store:16.7.4

* Updates to com.nimbusds:infinispan-cachestore-sql:7.1.1



## 14.8.2 (2023-11-02)

### Resolved issues

* Forces removal of the org.springframework:spring-test dependency erroneously 
  introduced as compile scope dependency of  
  org.apache.logging.log4j:log4j-web:2.21.1 (issue server/936).


### Dependency changes

* Forces removal of org.springframework:spring-test



## 14.8.1 (2023-11-02)

### Resolved issues

* Updates the expired entry reaper for Connect2id server deployments with an 
  SQL database to conserve memory by employing paged key set seek, in sets of
  up to 100 SQL records and interleaving the record deletion between the pages. 
  Intended to prevent OOM errors in deployments with a very large number of 
  sessions and other expiring objects (issue server/935).

* Optimises the `dataSource.createTableIfMissing` implementation for Oracle 
  Databases when the queried table has a very large number of records, causing
  Connect2id server startup to pause for times longer than 1 minute at startup.
  The issue is addressed by switching from LIMIT 0 to LIMIT 1 in the query to 
  obtain the table's column names (issue server/933).

* The expired entry reaper in Connect2id server deployments with an SQL 
  database must not terminate when an unchecked parse or another exception is 
  thrown when parsing a retrieved SQL record. This may occur in SQL records 
  manipulated outside the Connect2id server APIs. Instead, the exception must 
  be swallowed and an error with the offending SQL record logged. This is now 
  done with an `IS0141` log error (issue sql-store/23).

* Fixes the default value and parsing of the optional `sessions` form 
  parameter of the `/session-store/rest/v2/purge` resource (issue 
  session-store/95).


### Dependency changes

* Updates to com.nimbusds:oauth2-authz-store:24.7.1

* Updates to com.nimbusds:oidc-session-store:16.7.3

* Upgrades to com.nimbusds:infinispan-cachestore-sql:7.1

* Updates to Log4j 2.21.1

* Updates to Dropwizard Metrics 4.2.20.



## 14.8 (2023-10-20)

### Summary

* Implements configurable replay prevention of "client_secret_jwt" and  
  "private_key_jwt" client authentication JWT assertions based on the optional 
  JWT ID ("jti") claim. The implementation is based on the new expended token
  registry introduced in Connect2id server 14.0.

* Updates the PostgreSQL configuration to enable setting of a JDBC "schema" 
  parameter. Intended for Connect2id server deployments that want to use a 
  database schema other than the default "public".


### Configuration

* /WEB-INF/oidcProvider.properties

    * op.token.authJWTPreventReplay -- New optional configuration property. If 
      `true` replay of "client_secret_jwt" and "private_key_jwt" client
      assertions will be prevented, by caching the JWT "jti" claim for the 
      duration of the assertion lifetime but no longer than 5 minutes. The 
      default value is `true`.
    
    * op.token.authJWTExpMaxAhead -- New optional configuration property. 
      Sets the maximum allowed number of seconds of the expiration time (exp) 
      claim in "client_secret_jwt" and "private_key_jwt" client assertions  
      ahead of the current time. Assertions with longer expiration time will be
      rejected with an `invalid_client` error. If zero or negative this check 
      is disabled. When enabled the value must be between 10 and 600 seconds. 
      The default value is -1 (disabled).

* /WEB-INF/infinispan-*-postgres95.xml

    * dataSource.databaseSchema -- New optional Java system property to set 
      the PostgreSQL schema to use. Corresponds to the HikariCP "schema" 
      configuration property. The default value is empty (implies the default 
      "public" PostgreSQL schema).


### Web API

* /par

    * Requests with "client_secret_jwt" and "private_key_jwt" authentication
      will be prevented from replaying a used JWT assertion, unless the JWT
      assertion is missing the optional the JWT ID (jti) claim or the replay
      prevention is disabled by setting the op.token.authJWTPreventReplay
      configuration property to `false`.

* /token

    * Requests with "client_secret_jwt" and "private_key_jwt" authentication
      will be prevented from replaying a used JWT assertion, unless the JWT
      assertion is missing the optional the JWT ID (jti) claim or the replay
      prevention is disabled by setting the op.token.authJWTPreventReplay
      configuration property to `false`.
    
* /token/introspect

    * Requests with "client_secret_jwt" and "private_key_jwt" authentication
      will be prevented from replaying a used JWT assertion, unless the JWT
      assertion is missing the optional the JWT ID (jti) claim or the replay
      prevention is disabled by setting the op.token.authJWTPreventReplay
      configuration property to `false`.


### Resolved issues

* Updates the authz-session log INFO "OP2101" and "OP2103" messages to include 
  the current issuer URL when issuer aliasing is enabled (issue server/925).

* Updates the authz-session log DEBUG "OP2130" and WARN "OP2131", "OP2132" 
  messages to include the authorisation session ID (issue server/925).


### Dependency changes

* Upgrades to com.nimbusds:oauth2-oidc-sdk:11.4

* Upgrades to com.nimbusds:oauth2-authz-store:24.7

* Updates to com.nimbusds:software-statement-verifier:2.2.6

* Updates to org.apache.santuario:xmlsec:2.2.6

* Updates to com.nimbusds:infinispan-cachestore-sql:7.0.6

* Updates to org.mariadb.jdbc:mariadb-java-client:2.7.10



## 14.7 (2023-10-15)

### Summary

* The support for native applications is updated to enable registration of 
  redirection http URIs with a loopback IP. Native applications with a 
  localhost or loopback http redirection URI may also use a variable port, in 
  cases when the application is not guaranteed to be able to bind to a 
  predetermined port.

* The Connect2id server can now be configured to load only selected signing 
  keys from a PKCS#11 (HSM) store. This can enable deployments where the HSM is 
  shared with other applications.

* The Connect2id server `/jwks.json` endpoint may include public-only signing
  keys, for historical or other purposes.

* The Infinispan configuration supports the setting of custom expiration 
  intervals for all Connect2id server objects that require expiration. The 
  expiration interval of subject sessions remains separately configurable. This 
  enables strategies such as:
      
    * Dedicating a set of Connect2id server nodes to processing OAuth 2.0 and 
      OpenID Connect requests and one (or more) nodes to the task of object 
      expiration.
    
    * Disabling the periodic execution of the subject session expiration task 
      and invoking it externally, for example by a cron-style job at a time
      that is deemed optimal.


### Configuration

* /WEB-INF/jwkSet.json

    * The JWK set may include JWK instances without private parameters. These 
      keys will be published at the `/jwks.json` endpoint and not used 
      internally by the Connect2id server.
    
      Public signing and encryption Connect2id server keys may be included to 
      be published at the `/jwks.json` endpoint for historical purposes.

* /WEB-INF/federationJWKSet.json

    * The federation entity JWK set may include JWK instances without private 
      parameters. These keys will be included in issued Entity Configurations 
      and not used internally by the Connect2id server.

      Public signing federation entity keys may be included for historical 
      purposes.

* /WEB-INF/jose.properties

    * pkcs11.keyIDs.* -- New optional configuration property. Specifies an 
      explicit list of identifiers (aliases) of PKCS#11 keys to load from the
      HSM device (when an HSM is configured). If omitted or blank all 
      recognised keys will be loaded.
      
      This configuration property can be used to filter the PKCS#11 keys to 
      load from an HSM that is shared by several applications.

* /WEB-INF/sessionStore.properties

    * sessionStore.sessionMap.expirationInterval -- Increases the default value 
      from 300000 ms (5 minutes) to 600000 ms (10 minutes).
    
    * sessionStore.internal.subjectIndexPurgeInterval -- Receives a new default 
      value of -1 (disabled).

* /WEB-INF/infinispan-*.xml

    * Adds a new "infinispan.defaultExpirationInterval" configuration property 
      with a default value of 300000 ms (5 minutes) to enable override of the 
      default expiration purge interval for all Infinispan maps and caches 
      where an expiration is required by the Connect2id server. Non-expiring 
      maps and caches are not affected by this configuration property. The 
      session map expiration interval override remains in the 
      "sessionStore.sessionMap.expirationInterval" configuration property.

* /WEB-INF/infinispan-stateless-{mysql|oracle|postgres95|sqlserver}.xml

    * Enabled for the "sessionStore.sessionMap.expirationInterval" 
      configuration property.

* /WEB-INF/infinispan-multitenant-stateless-{mysql|oracle|postgres95|sqlserver}.xml

    * Enabled for the "sessionStore.sessionMap.expirationInterval" 
      configuration property. 


### Web API

* /clients/

    * Native applications (with the `application_type` metadata parameter set 
      to `native`) may register redirection URIs with a loopback IP address -- 
      127.0.0.1 in IPv4 and 0:0:0:0:0:0:0:1 (short form ::1) in IPv6. 
      Previously clients using the loopback interface to receive OAuth 2.0 
      redirections could only register with the "localhost" hostname in the 
      URL.

    * Native applications (with the `application_type` metadata parameter set 
      to `native`) may register localhost and loopback IP redirection URIs with 
      a variable port, by specifying port zero in the URI, for example 
      `http://localhost:0/callback`. Authorisation requests can then use any 
      port in the 1 to 65535 range, provided the other components in the 
      redirection URI match the registered URI exactly. Example permitted 
      redirection URI for on the given registered example: 
      `http://localhost:1234/callback`.

* /session-store/rest/v2/

    * The `/purge` resource is updated, changing the default action to force a 
      purge of the expired session only. The purging of expired and orphaned 
      index keys is redundant and become optional operations now.  
      
      Changes to the resource:
    
        * Adds a new form parameter with name `sessions` and a default value 
          `true`. When `true` all expired sessions will be purged. When `false` 
          the expired sessions purge will be skipped.
        
        * Adds a new form parameter with name `index` and a default value  
          `false`. When `true` all expired subject index keys will be purged. 
          When `false` the expired subject index keys purge will be skipped.
        
        * Adds a new form parameter with name `orphaned_index_keys` and a 
          default value `false`. When `true` all orphaned subject index keys
          will be purged. When `false` the orphaned subject index keys purge
          will be skipped.
        
        * Adds a new form parameter with name `async` and a default value 
          `false`. When `true` the purges will be performed asynchronously.
         
        * The `async` query parameter of the `/purge` resource is deprecated. 
          Use the new `async` form parameter instead.


### Resolved issues

* The default value of sessionStore.internal.subjectIndexPurgeInterval is 
  changed to -1 (disabled). In most practical cases the periodic purge task is 
  redundant, due to the automatic subject index max_life expiration (in 
  deployments with DynamoDB or Redis) or the purge on new subject session 
  insertion when the subject session quota is reached. The session store 
  `/purge` resource remains available (issue session-store/91).

* Changes the SS0233 log message to level WARN when the purged number of 
  orphaned subject keys is greater than zero, otherwise the level remains INFO 
  (issue session-store/90).

* Fixes an issue affecting grant isolation at the token endpoint when the  
  Connect2id server is configured with issuer aliasing in mode 
  PERSISTED_GRANT_ISOLATION (issue server/923).


### Dependency changes

* Upgrades to com.nimbusds:oauth2-oidc-sdk:11.2

* Updates to com.nimbusds:oauth2-authz-store:24.6.4

* Updates to com.nimbusds:oidc-session-store:16.7.1

* Updates to com.nimbusds:c2id-server-jwkset:1.30

* Upgrades to com.nimbusds:nimbus-jwkset-loader:5.3

* Updates to Infinispan to 14.0.19.Final



## 14.6 (2023-09-28)

* /WEB-INF/sessionStore.properties

    * sessionStore.quotaPerSubject -- The maximum configurable session quota 
      per subject (end-user) is increased from 10 to 25 sessions. 


### Resolved issues

* Enhances and refactors the Redis store debug and trace level logging (issue 
  redis-store/7).

* Prune max_idle expired entries on Redis store retrieval or iteration (scan)
  instead of waiting for the final max_lifetime expiration in the Redis store 
  (issue/redis-store/10).

* Adds an entry expiration check on Redis store retrieval based on the stored 
  Infinispan entry metadata to prevent situations where a max_idle expired 
  entry is assumed as not expired by Infinispan (since v14.x) (issue 
  server/899, issue redis-store/8).    

* Makes Redis store iteration (scan) safe with concurrent key deletion or 
  expiration (issue redis-store/11).  


### Dependency changes

* Upgrades to com.nimbusds:oauth2-oidc-sdk:11.0
 
* Updates to com.nimbusds:oidc-session-store:16.5

* Updates to com.nimbusds:nimbus-jose-jwt:9.35

* Upgrades to Infinispan to 14.0.17.Final

* Updates to com.nimbusds:infinispan-cachestore-sql:7.0.5

* Updates to com.microsoft.sqlserver:mssql-jdbc:12.2.0.jre11

* Updates to com.oracle.database.jdbc:ojdbc11:21.9.0.0

* Updates to com.nimbusds:infinispan-cachestore-redis:10.1.1

* Updates to com.nimbusds:infinispan-cachestore-dynamodb:5.0.2

* Updates to com.unboundid:unboundid-ldapsdk:6.0.10

* Updates to commons-io:commons-io:2.11.0



## 14.5 (2023-09-03)

### Web API

* /clients

    * Prevents registration of clients with "redirect_uris" that include a 
      "code", "state" or "response" query parameter. OAuth 2.0 generally allows 
      a client "redirect_uri" to include query parameters, which may be used,
      for instance, to process authorisation responses from multiple OAuth 2.0
      servers or OpenID providers. This is a security measure that follows a 
      recent recommendation that clients must not include query parameters in a
      registered "redirect_uri" with names used by the OAuth 2.0 authorisation 
      response.

* /authz-sessions/rest/v3/

    * The authorisation session object (obtainable via an HTTP GET request)
      receives an optional "auth_req.max_age" member of type array to 
      represent the maximum authentication age in OpenID authentication 
      requests.

    * The authorisation session object (obtainable via an HTTP GET request)
      receives an optional "auth_req.acr_values" member of type array to 
      represent the Authentication Context Class Reference values in OpenID 
      authentication requests.

    * The authorisation session object (obtainable via an HTTP GET request)
      receives an optional "auth_req.login_hint" member of type string to 
      represent the login hint in OpenID authentication requests.
  
    * Rejects OAuth 2.0 authorisation and OpenID authentication requests with 
      a "redirect_uri" that includes a "code", "state" or "response" query 
      parameter with an "invalid_request" error. Redirection URIs with query 
      parameters that match the name of an OAuth 2.0 authorisation response 
      parameter are deemed unsafe. Clients that use such query parameters in a 
      "redirect_uri" must re-register with a new compliant redirection URI. 


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.59

  * The CustomTokenResponseComposer SPI extends Lifecycle. 


### Resolved issues

* Alters the Oracle "clients" table definition of the "jwks" column from
  VARCHAR2(4000) to CLOB (issue server/910).

* Logs the IDTokenIssueEventListener SPI and AccessTokenIssueEventListener SPI
  enabled status (issue server/915).

* Isolates expended rotated self-contained (JWT) refresh tokens by subject
  (end-user) session ID when the refresh token is issued in a OAuth 2.0 
  authorisation code grant. For clients with multiple instances per subject 
  (end-user) where the consent is transient (long_lived=false) (issue 
  authz-store/223).

* Logs the OP8041 INFO message only when there are explicit OpenID Connect 
  Federation 1.0 clients reaped (issue server/916).

* Removes redundant Infinispan externalisers used in the OpenID Connect 
  Federation 1.0 explicit client index (issue server/917). 

* Fixes an HTTP 500 Internal Server Error at the authorisation session web API 
  when receiving an OpenID authentication request with certain illegal JSON in 
  the claims parameter that produced an OAuth 2.0 "error_description" with an 
  illegal double-quote character according to RFC 6749, section 5.2 (issue 
  server/919).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.59

* Updates to com.nimbusds:oauth2-oidc-sdk:10.14.2

* Updates to com.nimbusds:oauth2-authz-store:24.6

* Updates to net.minidev:json-smart:2.4.11

* Updates to com.google.crypto.tink:tink:1.10.0



## 14.4 (2023-08-11)

### Summary

* Connect2id server deployments that implement OAuth 2.0 Rich Authorisation 
  Requests (RAR) (RFC 9396) can use a new "op.rar.supportedTypes" configuration
  property to let the server check the types of "authorization_details" in 
  received requests and reject requests with unsupported RAR types. The 
  supported types will also be advertised in the
  "authorization_details_types_supported" OpenID provider metadata field.

* Connect2id server deployments with an embedded H2 database are upgraded from
  2.1.x to the latest stable 2.2.x release. Database files created in the older 
  H2 version are not compatible. Existing records (where they must be retained) 
  must be migrated.


### Configuration

* /WEB-INF/oidcProvider.properties

    * op.rar.supportedTypes -- New optional configuration property to list the 
      supported OAuth 2.0 Rich Authorisation Request (RAR) (RFC 9396) types.
      When specified the "authorization_details" in OAuth 2.0 authorisation / 
      OpenID authentication requests and token requests will be checked and  
      those with an unsupported type will be rejected with an
      "invalid_authorization_details" error. The types will also be advertised 
      in the "authorization_details_types_supported" OpenID provider metadata 
      field. The default value is none (no check).


### Web API

* /.well-known/openid-configuration

    * authorization_details_types_supported -- New optional metadata field 
      listing the supported OAuth 2.0 Rich Authorisation Request (RAR) 
      (RFC 9396) types. Omitted if not specified.

* /authz-sessions/rest/v3/

    * The authorisation session object (obtainable via an HTTP GET request) 
      receives an optional "auth_req.authorization_details" member to  
      represent the Rich Authorization Request (RAR) (RFC 9396) parameter in 
      OAuth 2.0 authorisation requests and OpenID authentication requests.


### Resolved issues

* The UserInfo endpoint must not request claims from the sourcing SPI that are 
  pre-set (e.g. supplied via "preset_claims.userinfo" in the authorisation 
  session API) (issue server/885).

* Updates JWT minting to support the inclusion of null-valued top-level claims, 
  which can legitimately occur in data returned from the claims source SPI. The 
  claims set in plain (unsecured) UserInfo responses and JWT-secured UserInfo 
  responses will thus be identical for those cases when claims with null values 
  are included, previously omitted in a JWT (issue server/906).

* The "authorization_details" parameter must be obtainable in the 
  AuthorizationRequestValidator and PARValidator SPIs (issue server/907).


### Dependency changes

* Updates to com.nimbusds:oauth2-authz-store:24.5.3

* Updates to com.nimbusds:oidc-session-store:16.4.4

* Updates to com.h2database:h2:2.2.220



## 14.3 (2023-08-07)

### Summary

* Connect2id server 14.x deployments with a Redis store should update to this 
  release which fixes an issue related to ProtoBuf marshalling.  

* The OAuth 2.0 / OpenID Connect SDK dependency was updated to v10.13.2 which 
  includes native OAuth 2.0 Rich Authorisation Requests (RAR) (RFC 9396) 
  support. CustomTokenResponseComposer SPI plugins that implement RAR should
  be recompiled and updated if feasible to utilise the new type-safe methods of
  AccessTokenResponse when adding an "authorization_details" parameter to the 
  response.

  Built-in RAR support is on the Connect2id server roadmap and will be included
  in a future release.


### Configuration

* /WEB-INF/oidcProvider.properties

    * op.reg.clientIDByteLength -- Updates the configuration property check, 
      the length of generated client identifiers must not exceed 48 bytes.


### Resolved issues

* The client registration endpoint must return HTTP 400 Bad Request on a 
  preferred_client_id that exceeds the max number of characters (80) that can 
  be stored (issue server/901).

* Fixes the authorisation code ProtoBuf marshalling in replication cluster and
  Redis based Connect2id server deployments (issue server/902).


### Dependency changes

* Upgrades to com.nimbusds:oauth2-oidc-sdk:10.13.2

* Updates to com.nimbusds:oauth2-authz-store:24.5.2

* Updates Infinispan to 14.0.13.Final

* Updates to org.slf4j:slf4j-api:2.0.7



## 14.2 (2023-07-17)

### Configuration

* /WEB-INF/authzStore.properties

    * authzStore.refreshToken.rotatedReuseGracePeriod -- New optional 
      configuration property specifying a grace period in seconds during which 
      a client may repeat a request with the same rotated refresh token without 
      triggering the customary token revocation. Intended to enable token 
      request retrial on slow / poor networks where the HTTP response times out 
      after issue of the new token. Currently supported only for refresh tokens 
      linked to short-lived (transient) authorisations. The default value is 5 
      seconds.


### Web API

* /token

    * A client may repeat a token request with a rotated refresh token within
      the configured "authzStore.refreshToken.rotatedReuseGracePeriod" period
      without triggering the customary token revocation when the Connect2id 
      detects a replay of a rotated refresh token. Intended to enable token
      request retrial on slow / poor networks where the HTTP response times out
      after issue of the new token. Currently supported only for refresh tokens
      linked to short-lived (transient) authorisations.

* /monitor/v1/metrics

    * "authzStore.rotatedRefreshTokenReplayRevocations" -- New meter of 
      authorisation revocations due to rotated refresh token replay.
    
    * "authzStore.rotatedRefreshTokenAllowedReuses" -- New meter of the allowed  
      reuses of rotated refresh tokens within the configured grace period.


### Resolved issues

* Fixes Redis HMGET retrieval of the "last used" (u) field in the Infinispan 
  Redis connector that caused subject session max_idle > 0 to not be observed 
  in Connect2id server deployments with a Redis store (issue server/899).

* Writes to Oracle database CLOB / NCLOB fields with long strings that trigger
  the string concatenation work-around must escape quote chars (issue 
  sql-store/20).


### Dependency changes

* Updates to com.nimbusds:oauth2-authz-store:24.5.1

* Updates Infinispan to 14.0.11.Final

* Updates to com.nimbusds:infinispan-cachestore-sql:7.0.4

* Updates to com.nimbusds:infinispan-cachestore-redis:10.0.2

* Updates to BouncyCastle 1.74

* Updates to com.google.crypto.tink:tink:1.9.0



## 14.1 (2023-06-30)

### Summary

* Upgrades OpenID Connect Federation 1.0 policy support to draft 29.


### Configuration

* /WEB-INF/oidcProvider.properties

    * op.authz.responseJWTIncludeX5C -- New optional configuration property
      of type boolean to enable / disable inclusion of the X.509 certificate
      chain ("x5c") header parameter in signed OAuth 2.0 authorisation 
      responses (JARM) when the signing JWK is provisioned with a certificate. 
      The default value is true (enabled).


### Resolved issues

* The com.nimbusds:c2id-server-sdk dependency must not be SNAPSHOT, but 4.58
  (issue server/898).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.58

* Upgrades to com.nimbusds:oauth2-oidc-sdk:10.10.1

* Updates to io.dropwizard.metrics:metrics-core:4.2.19



## 14.0 (2023-06-27)

### Summary

* Support for refresh token rotation on an individual authorisation basis, 
  overriding the global Connect2id server configuration. 

  Self-contained (JWT-encoded) refresh tokens, which are linked to transient / 
  non-persisted ("long_lived":false) authorisations, also receive the ability 
  to be rotated.
 
  Previously refresh token rotation could only be set globally and apply only 
  to identifier-based refresh tokens, which are linked to long-lived / 
  persisted ("long_lived":true) authorisations.

* Support for ID token refresh at the token endpoint, settable on an individual 
  authorisation basis. The token response will include a new ID token when the
  refresh token authorisation allows such refresh and the subject (end-user) 
  session bound to the refresh token is still present. If the session was 
  closed or expired no ID token will be included in the token response.
 
  An authorised OpenID relying party may use the ID token refresh to do a 
  back-channel check whether the end-user session with the OpenID provider 
  still exists (i.e. that the session wasn't closed or expired). OpenID relying 
  parties should use the standard "prompt=none" OpenID authentication request 
  to ensure the end-user is actively present and properly authenticated, 
  since this method involves the front-channel (the browser). An ID token 
  refresh thus isn't equivalent to or a substitute for a "prompt=none" OpenID 
  authentication request.

* Public clients registered for OAuth 2.0 mutual TLS (RFC 8705) and the OAuth 
  2.0 refresh token grant will receive refresh tokens that are client X.509 
  certificate bound.

* Allows registration of native applications (the client "application_type" 
  metadata field set to "native") with non-localhost https redirection URIs,
  e.g. https://app.example.com/callback.

* Introduces "urn:c2id:introspection_endpoint" as alternative fixed scope value
  for authorising access to the token introspection endpoint of the Connect2id 
  server. Authorisations with a scope value set to the token introspection 
  endpoint, e.g. https://op.example.com/token/introspect continue to be 
  supported. The fixed scope value is intended for introspecting clients 
  registered in Connect2id server deployment configured with OP / AS issuer 
  aliases.  

* A new "op.authz.limitScopeToRegistered" configuration property to filter the
  requested scope values in the consent prompt of the authorisation session API
  to those registered in the metadata of the requesting client. If disabled or
  no "scope" client metadata field is registered, the authorisation request 
  scope will be passed to the consent prompt as it is. Enabled by default.

* The Connect2id server configuration properties were revised and given 
  appropriate default values where applicable. Intended to reduce the
  configuration effort and the number of Java system properties necessary to 
  deploy a Connect2id server.

* Updates the TokenIntrospectionResponseComposer SPI to provide access to the
  subject (end-user) session where the token consent occurred, when the session 
  is still present (not closed or expired). The subject session may be used by
  customised token introspection endpoints to include selected claims or other
  details from the session in the introspection responses.

* Updates the AdvancedClaimsSource SPI to provide access to the subject 
  (end-user) session where the claims sourcing was authorised, provided the 
  session is still present (not closed or expired). The subject session may be 
  used to source claims for UserInfo responses and ID tokens. 

* A Software Bill Of Materials (SBOM) in CycloneDX JSON and XML format is
  included in the c2id.war under /WEB-INF/sbom/CycloneDX-Sbom.json and 
  /WEB-INF/sbom/CycloneDX-Sbom.json

* Upgrades to Infinispan 14.0.

  Due to the switch of the internal object serialisation in Infinispan to 
  Protocol Buffers (Protobuf) existing in-memory data in Connect2id server 
  13.x deployments in replication cluster mode or with Redis as in-memory data 
  and cache store will not be recognised and hence ignored by a Connect2id 
  server v14.0 deployment. If such in-memory data (subject sessions, etc.) 
  needs to be preserved it must be manually migrated. 

* Adds Oracle 12c r1+ Database support.

* Removes LDAP backend database support as part of the Infinispan 14.0 
  upgrade. Connect2id server deployments with an LDAP backend database can 
  migrate to a supported SQL RDBMS or to AWS DynamoDB.


### Configuration

* /WEB-INF/oidcProvider.properties

    * op.authz.limitToRegisteredScope -- New optional configuration property of
      type boolean to limit the requested scope values in the consent prompt 
      for OAuth 2.0 authorisation / OpenID authentication requests to those 
      registered the in the "scopes" client metadata field (provided the 
      metadata field is set). The default value is true.
    
    * op.authz.feedSubjectSessionClaimsIntoIDToken -- No longer a required 
      configuration property, receives a default value of true.
    
    * op.reg.allowOpenRegistration -- Receives a default value false.
    
    * op.reg.rejectNonTLSRedirectionURIs -- Receives a default value true.
    
    * op.reg.refreshAccessTokenOnUpdate -- Receives a default value true.
    
    * op.reg.clientSecretLifetime -- Receives a default value 0 (no 
      expiration).
    
    * op.reg.alwaysRefreshClientSecretOnUpdate -- Receives a default value 
      true.
    
    * op.reg.resourceRetriever.httpConnectTimeout -- Increases the default 
      value to 1000 ms.
    
    * op.reg.resourceRetriever.httpReadTimeout -- Increases the default value 
      to 1000 ms.
    
    * op.idToken.defaultLifetime -- Receives a default value 300 seconds.
    
    * op.idToken.jwsAlgs -- Receives a default value of all supported, with 
      "none" excluded.
    
    * op.idToken.jweAlgs -- Receives a default value of all supported.
    
    * op.idToken.jweEncs -- Receives a default value of all supported.
    
    * op.idToken.ignoreUserInfoError -- Receives a default value true.
    
    * op.authz.sessionLifetime -- Receives a default value 15 minutes.
    
    * op.authz.responseTypes -- Receives a default value of all supported.
    
    * op.authz.responseModes -- Receives a default value of all standard 
      supported.
    
    * op.authz.requestJWSAlgs -- Receives a default value of all supported,
      with "none" excluded.
    
    * op.authz.requestJWEAlgs -- Receives a default value of all supported.
    
    * op.authz.requestJWEEncs -- Receives a default value of all supported.
    
    * op.authz.responseJWSAlgs -- Receives a default value of all supported.
    
    * op.authz.responseJWEAlgs -- Receives a default value of all supported.
    
    * op.authz.responseJWEEncs -- Receives a default value of all supported.
    
    * op.authz.includeClientInfoInAuthPrompt -- Receives a default value false.
    
    * op.authz.includeOtherConsentedScopeAndClaimsInPrompt -- Receives a 
      default value false.
    
    * op.authz.alwaysPromptForConsent -- Receives a default value false.
    
    * op.authz.requireIDTokenHintWithPromptNone -- Receives a default value 
      false.
    
    * op.authz.advertisedScopes -- Receives a default value "openid".
    
    * op.authz.advertisedClaims -- Receives a default value "sub".
    
    * op.authz.advertisedDisplayTypes -- Receives a default value page.
    
    * op.token.authMethods -- Receives a default value of all supported, with
      "tls_client_auth" and "self_signed_tls_client_auth" excluded.
    
    * op.token.authJWSAlgs -- Receives a default value of all supported.
    
    * op.userinfo.jwsAlgs -- Receives a default value of all supported.
    
    * op.userinfo.jweAlgs -- Receives a default value of all supported.
    
    * op.userinfo.jweEncs -- Receives a default value of all supported.
    
    * op.logout.sessionLifetime -- Receives a default value of 10 minutes.
    
    * op.logout.backChannel.httpConnectTimeout -- Increases the default value
      to 1000 ms.
    
    * op.logout.backChannel.httpReadTimeout -- Increases the default value to
      1000 ms.
    
    * op.federation.httpConnectTimeout -- Replaces the 
      op.federation.httpRequestTimeout configuration property, increases the 
      default value to 1000 ms.
    
    * op.federation.httpReadTimeout -- Increases the default value to 1000 ms.
    
* /WEB-INF/authzStore.properties
    
    * authzStore.refreshToken.defaultRotate -- New optional configuration 
      property of type boolean for the default refresh token rotation setting. 
      Can be overridden by individual authorisations. The default value is 
      false (no rotation).

    * authzStore.refreshToken.alwaysUpdate -- Deprecated, use 
      "authzStore.refreshToken.defaultRotate" instead.
    
    * authzStore.accessToken.includeX5C -- New optional configuration property 
      of type boolean to enable / disable inclusion of the X.509 certificate 
      chain ("x5c") header parameter in self-contained (JWT) access tokens when
      the signing JWK is provisioned with a certificate. The default value is 
      true (enabled).
    
    * authzStore.accessToken.disableSubjectSecurity -- New optional 
      configuration property of type boolean to disable the automatic 
      encryption of self-contained (JWT-encoded) access tokens with public 
      (non-pairwise) subject identifiers, in cases when the access tokens are 
      issued to clients registered for pairwise subjects in ID tokens and 
      UserInfo responses. The default value is false. Should be used only in 
      exceptional circumstances.
    
    * authzStore.code.lifetime -- Receives a default value of 300 seconds (5 
      minutes).
    
    * authzStore.accessToken.defaultLifetime -- Receives a default value 600
      seconds (10 minutes).
    
    * authzStore.accessToken.jwsAlg -- New optional configuration property to 
      replace authzStore.accessToken.jwsAlgorithm which becomes deprecated. The
      default value is RS256.
  
    * authzStore.accessToken.jweAlgorithm -- New optional configuration 
      property to replace authzStore.accessToken.jweAlgorithm which becomes
      deprecated. The default value is dir.
  
    * authzStore.accessToken.jweEnc -- New optional configuration property to
      replace authzStore.accessToken.jweMethod which becomes deprecated. The 
      default value is A128GCM.
  
    * authzStore.accessToken.allowDirectInspection -- Receives a default value 
      false.
  
    * authzStore.options.highlyAvailableMode -- Receives a default value true.
    
    * authzStore.options.legacyPlainKeysInStorage -- Removed, after being
      deprecated in Connect2id server 10.0. 

* /WEB-INF/sessionStore.properties

    * sessionStore.maxLifetime -- Receives a default value 259200 minutes (180 
      days).
    
    * sessionStore.authLifetime -- Receives a default value 43200 minutes (30
      days).
    
    * sessionStore.maxIdleTime -- Receives a default value 14400 minutes (10 
      days).
    
    * sessionStore.quotaPerSubject -- Receives a default value 5.
    
    * sessionStore.onQuotaExhaustion -- Receives a default value 
      CLOSE_OLD_SESSION.

* /WEB-INF/infinispan-*.xml
 
    * Upgrades the XML schema to Infinispan 14.0.
   
    * Replaces the Infinispan "op.clientRegTokenMap" with a new generic
      "authzStore.expendedTokenMap" capable of storing keys for expended tokens 
      that are rotated self-contained (JWT-encoded) refresh tokens, client 
      registration tokens, client_secret_jwt and private_key_jwt tokens, DPoP 
      tokens and other one-time-use objects.

* /WEB-INF/infinispan-stateless-oracle.xml

    * New Infinispan configuration for stateless clustering and an Oracle 
      database.

* /WEB-INF/infinispan-stateless-redis-oracle.xml

    * New Infinispan configuration for stateless clustering and an Oracle
      database and Redis for caching and storage of short-lived objects.

* /WEB-INF/infinispan-replication-oracle.xml

    * New Infinispan configuration for replication clustering and an Oracle
      database.

* /WEB-INF/infinispan-multitenant-stateless-oracle.xml

    * New multi-tenant Infinispan configuration for stateless clustering 
      and an Oracle database.

* /WEB-INF/infinispan-multitenant-stateless-redis-oracle.xml

    * New multi-tenant Infinispan configuration for stateless clustering and an 
      Oracle database and Redis for caching and storage of short-lived objects.

* /WEB-INF/infinispan-*-{mysql|postgres95|sqlserver|oracle|h2}.xml

    * New optional "dataSource.createTableIfMissing" Java system property. 
      When "true" (the default value) the Connect2id server will automatically 
      create the required SQL tables on startup. It will also perform automatic 
      table alternations where necessary in new major releases. When "false" 
      the database administrator must create or alter the tables manually 
      before server startup.

    * New optional "dataSource.maxPoolSize" Java system property. Controls the
      maximum size the SQL connection pool is allowed to reach, including both 
      idle and in-use connections. The default size is 5.
    
    * Upgrades the SQL schema (the Connect2id server will automatically add the 
      new table and columns on startup unless "dataSource.createTableIfMissing"
      is disabled):
    
        * Adds an "expended_tokens" table.
        
        * Adds a "sik" (session identifier key) column to the 
          "id_access_tokens" table.
        
        * Adds an "idr" (ID token rotate) column to the 
          "long_lived_authorizations" table.

* /WEB-INF/infinispan-*-ldap.xml

    * The LDAP backend database XML configurations are removed and no longer 
      supported.

* /WEB-INF/sql

    * New directory containing the required SQL statements for manual table 
      creation, to be used when the Connect2id is configured to disable 
      automatic table creation (if missing) on server startup (with  
      dataSource.createTableIfMissing=false).


### Web API

* /clients

    * Allows registration of native applications (where the client 
      "application_type" metadata field is set to "native") with non-localhost 
      https redirection URIs.

* /token

    * Supports issue of rotated self-contained (JWT-encoded) refresh tokens.
     
    * Supports ID token refresh. Requires the refresh token authorisation to 
      explicitly allow ID token refresh and the subject (end-user) session 
      bound to the refresh token to be still present (not closed or expired),
      else an ID token will not be included in the token response.
  
      The expiration of the refreshed ID token will be set according to the 
      globally configured "op.idToken.defaultLifetime". An ID token lifetime
      supplied to the consent object (with "id_token.lifetime") will not be 
      replicated in refreshed ID tokens.  
    
      The "max_age" OpenID authentication request parameter will trigger 
      inclusion of the "auth_time" claim only in the ID token issued in the 
      direct response to it, not in refreshed ID tokens. For an OpenID 
      relying party to receive the "auth_time" claim in refreshed ID tokens 
      it must be registered as client for the "require_auth_time" metadata 
      parameter.
     
      The refreshed ID token will include all consented claims as well as 
      any claims found in the subject session "claims" field (unless the
      "op.authz.feedSubjectSessionClaimsIntoIDToken" configuration property
      prevents this). Preset ID token claims supplied to the consent object
      (with "preset_claims.id_token") will not be replicated in refreshed ID 
      tokens.
    
* /token/introspect

    * Introduces "urn:c2id:introspection_endpoint" as alternative fixed scope
      value for authorising access to the token introspection endpoint of the 
      Connect2id server. 

* /authz-sessions/rest/v3/

    * Adds an optional "refresh_token.rotate" parameter of type boolean to the
      consent object. Sets the refresh token rotation for the current 
      authorisation. When omitted the "authzStore.refreshToken.defaultRotate" 
      configuration will apply.
     
    * Adds an optional "id_token.allow_refresh" parameter of type boolean to 
      the consent object. Allows an OpenID relying party to receive a new ID 
      token at the token endpoint in exchange for a valid refresh token, with 
      the subject session bound to the refresh token still being present.

* /direct-authz/rest/v2/

    * Adds an optional "refresh_token.rotate" parameter of type boolean to the
      request object. Sets the refresh token rotation for the individual 
      authorisation. When omitted the "authzStore.refreshToken.defaultRotate" 
      configuration will apply.

    * Adds an optional "id_token.allow_refresh" parameter of type boolean to 
      the request object. Allows an OpenID relying party to receive a new ID
      token at the token endpoint in exchange for a valid refresh token, with 
      the subject session bound to the refresh token still being present.
    
    * Adds an optional "claims_data" parameter of type JSON object to the 
      request object. The parameter will be passed in the request to retrieve 
      the consented OpenID claims from the configured source(s). The claims 
      data will be included in a "cld" (claims data) field in the issued access 
      token(s) and in the long-lived authorisations if the consent is 
      persisted. If the claims data must be kept confidential from the client
      either an identifier access token encoding must be chosen or if a
      self-contained (JWT) access token is chosen it must be additionally
      encrypted. An AdvancedClaimsSource SPI implementation can retrieve the
      claims data JSON object by a call to the 
      "ClaimsSourceRequestContext.getClaimsData" method.

      This parameter is identical to the "claims_data" parameter in the consent
      object of the authorisation session web API.

* /authz-store/rest/v3/

    * New "rtr" authorisation field of type boolean for the refresh token 
      rotate setting. The default value is false. 

    * New "idr" authorisation field of type boolean for the allow ID token 
      refresh setting. The default value is false. 

    * Removes the "/config" resource for retrieval (via HTTP GET) of the public 
      authorisation store configuration.

* /monitor/v1/metrics

    * Adds new "authzStore.numExpendedTokenEntries" gauge.
     
    * Removes the "clientStore.numCachedExpendedTokens" gauge (replaced by the  
      new "authzStore.numExpendedTokenEntries" gauge). 


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.58

    * The RefreshTokenSpec class receives a new optional rotation setting. If 
      empty the default Connect2id server refresh token rotation policy will
      apply.
     
    * The IDToken spec class receives a new optional allow refresh setting. The 
      default setting is no ID token refresh allowed. 

    * The PasswordGrantHandler SPI can set a refresh token rotation preference.
     
    * The TokenExchangeGrantHandler SPI can set a refresh token rotation 
      preference.
    
    * The TokenIntrospectionResponseComposer SPI adds access to the subject 
      (end-user) session used for the token consent, provided the session is
      still present (not closed or expired). The session object can be accessed
      via the "TokenIntrospectionContext.getSubjectSession" method. The subject
      session may be used by customised token introspection endpoints to
      include subject claims or other details from the session in the 
      responses.
    
    * The TokenIntrospectionResponseComposer SPI extends Lifecycle.
    
    * The AdvancedClaimsSource SPI adds access to the subject (end-user)
      session where the claims sourcing was authorised, provided the session is
      still present (not closed or expired). The session object can be accessed
      via the "ClaimsSourceRequestContext.getSubjectSession" method.
    
      The subject session is supplied in the following cases:
   
         * Claims sourcing for the UserInfo endpoint where the subject
           session where the claims consent occurred is still present (not 
           expired or closed);
         
         * Claims sourcing for ID token issue for an OAuth 2.0 authorisation 
           code, implicit (including OpenID Connect hybrid response type) and 
           refresh token grants;
         
         * Claims sourcing for a direct authorisation request where a valid
           subject session ID was supplied, or a new subject session was 
           created.
         
         * Claims sourcing made available to the 
           TokenIntrospectionResponseComposer SPI.
    
      The subject session may be used as a source of subject authentication
      claims, or claims from the optional "claims" session field.

    * Adds an "AccessTokenAuthorization.getSubjectSessionKey" method, returns 
      null by default. Intended to represent the new encrypted subject session 
      ID key attribute ("sik") in access tokens. Connect2id server deployments
      with a custom codec (SelfContainedAccessTokenClaimsCodec SPI
      implementation) for the JWT claims for access tokens should update it to 
      include the new attribute.
    
    * The IdentifierAccessTokenCodec SPI extends Lifecycle.
    
    * The SelfContainedAccessTokenClaimsCodec SPI extends Lifecycle.
    
    * Removes deprecated ServiceContext interface.
    
    * Removes deprecated InitContext.getIssuer method. Use the SPI request 
      context getIssuer method instead.
    
    * Removes deprecated InitContext.getOPIssuer method. Use the SPI request
      context getIssuer method instead.
    
    * Removes deprecated InitContext.getTokenEndpoint method.
    
    * Removes deprecated InitContext.getServiceContext method.


### Resolved issues

* The SQL store must not set the client "application_type" to the default 
  value "web" on record retrieval (issue server/838).

* The Microsoft SQL Server id_access_tokens table column cnf must be VARCHAR
  (100), not NVARCHAR(100) (issue authz-store/199).

* Retrieving an authorisation record by refresh token must echo the submitted 
  refresh token value in the returned record instead of recreating it from the 
  persisted "rts" field (issue authz-store/203). 

* Authorisation updates must always return a refresh token in the current 
  format, not the legacy format (issue authz-store/204).

* The direct authorisation endpoint must always return a refresh token in 
  the current, not the legacy format (issue server/837).
 
* Store retrieval of identifier-based access tokens must recreate the local 
  subject when the subject type is pairwise (issue authz-store/201).

* Removes redundant persistence of registration_client_uri, recreates it 
  dynamically from the OP / AS issuer URL and the client_id (issue server/512).

* The "cnf" column of the "id_access_tokens" SQL table must be increased 
  from 100 to 150 VARCHAR for H2 and MS SQL Server to accommodate token  
  authorisations that use both a x5t#S256 and a jkt binding (issue 
  authz-store/206).
 
* Adds missing DynamoDB persistence of the "cld" (claims data) authorisation 
  record field (issue authz-store/210).

* Automatic revocation of all authorisations on replay of a rotated refresh 
  token (issue authz-store/212). 

* Persist only the subject session ID key in pending authorisation code 
  entries (omitting the appended HMAC), to prevent exploitation of session 
  IDs in case of unauthorised access to the backend database or a database 
  record leak (issue server/863). 
 
* Fixes NPE in DirectAuthorizationRequest.getResolvedClaims (issue server/867).

* Consented non-requested OpenID claims should not be included in the saved 
  claims field ("scs") of authorisations and authorisation records (issue 
  server/868).

* Client registration POST with empty "jwks" must produce HTTP 400 instead of 
  HTTP 500 (issue server/878).

* The server configuration must be rejected when mTLS client authentication is
  enabled and an op.tls.clientX509CertHeader is not configured (issue 
  server/882).

* Tokens issued within 500ms of a revocation event must not be marked as 
  revoked (issue authz-store/211).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.58

* Upgrades to com.nimbusds:oauth2-oidc-sdk:10.9.1

* Upgrades to com.nimbusds:c2id-server-jwkset:1.29.1
 
* Updates to com.nimbusds:c2id-server-property-source:1.1.2
 
* Updates to com.nimbusds:tenant-manager:8.0
 
* Updates to com.nimbusds:tenant-registry:8.3
 
* Updates to com.nimbusds:oauth2-authz-store:24.4.2
 
* Updates to com.nimbusds:oidc-session-store:16.4.3

* Updates to com.nimbusds:software-statement-verifier:2.2.5
 
* Upgrades to com.nimbusds:common:2.52

* Upgrades to com.nimbusds:infinispan-cachestore-common:3.1

* Upgrades to BouncyCastle 1.73.

* Upgrades to Infinispan 14.0.10.Final

* Upgrades to com.nimbusds:infinispan-cachestore-sql:7.0.2
 
* Upgrades to com.nimbusds:infinispan-cachestore-dynamodb:5.0.1 

* Upgrades to com.nimbusds:infinispan-cachestore-redis:10.0.1

* Updates to com.nimbusds:jgroups-dynamodb-ping:1.2.6

* Updates to com.thetransactioncompany:pretty-json:1.5 

* Adds com.oracle.database.jdbc:ojdbc11:21.8.0.0

* Updates to com.unboundid:unboundid-ldapsdk:6.0.9



## 13.7.4 (2023-05-09)

### Resolved issues

* The /logout-sessions/rest/v1 API must URL-encode the state parameter in the
  final post-logout redirection URI (issue server/873).


### Dependency changes

* Updates to com.nimbusds:software-statement-verifier:2.2.4



## 13.7.3 (2023-04-14)

### Resolved issues

* The validator of signing Connect2id server RSA and EC keys that are backed 
  by a PKCS#11 store (HSM) must use the default or BouncyCastle JCA providers
  for the signature verification step to prevent public key extraction 
  errors in jdk.crypto.cryptoki/sun.security.pkcs11.P11RSAKeyFactory / 
  P11ECKeyFactory (issue server/857).



## 13.7.2 (2023-04-11)

### Resolved issues

* Fixes the OAuth 2.0 token exchange grant policy to allow both confidential 
  and public clients. The client grant authorisation check must be adjusted 
  accordingly (issue server/853).

* The OP6201 log INFO message should include the OAuth 2.0 grant type for 
  unsupported_grant_type errors when a password, client_credentials, 
  urn:ietf:params:oauth:grant-type:jwt-bearer, 
  urn:ietf:params:oauth:grant-type:saml2-bearer or 
  urn:ietf:params:oauth:grant-type:token-exchange grant handler plugin is 
  unavailable (issue server/855).

### Dependency changes

* Updates to org.mariadb.jdbc:mariadb-java-client:2.7.9

* Updates to org.postgresql:postgresql:42.5.4

* Updates to com.microsoft.sqlserver:mssql-jdbc:11.2.3.jre11



### Dependency changes

* Updates to org.mariadb.jdbc:mariadb-java-client:2.7.9
 
* Updates to org.postgresql:postgresql:42.5.4

* Updates to com.microsoft.sqlserver:mssql-jdbc:11.2.3.jre11



## 13.7.1 (2023-04-05)

### Resolved issues

* Loading of a TokenExchangeGrantHandler SPI implementation was not reflected 
  in the OpenID provider / OAuth 2.0 authorisation server metadata (issue 
  server/849).


### Dependency changes

* Updates to com.unboundid:unboundid-ldapsdk:6.0.8

* Updates Log4j to 2.20.0



## 13.7 (2023-03-30)

### Configuration

* /WEB-INF/oidcProvider.properties

    * op.idToken.includeX5C -- New optional configuration to control 
      inclusion of the "x5c" (X.509 certificate chain) header parameter in 
      issued ID tokens when the signing JWK is provisioned with a certificate.  
      The default value is `true`.
     
    * op.reg.allowNonTLSLogoutURIsForTest -- New optional configuration 
      property to allow registration of non-TLS (plain HTTP) front and 
      back-channel logout URIs for test and development purposes. The default  
      value is `false` (not allowed). Must not be allowed in production!


### Resolved issues

* The JWK thumbprint (jkt) confirmation must be persisted in the "cnf" 
  column of the "id_access_tokens" SQL table for identifier-based DPoP
  access tokens (issue authz-store/205).


### Dependency changes

* Updates to com.nimbusds:oauth2-oidc-sdk:10.7.1
 
* Updates to com.nimbusds:oauth2-authz-store:19.5.1

* Updates to net.minidev:json-smart:2.4.10
 
* Updates to com.google.crypto.tink:tink:1.8.0
 
* Updates to com.google.code.gson:gson:2.10.1

* Updates to com.fasterxml.jackson.core:jackson-databind:2.13.4.2



## 13.6 (2023-02-22)

### Summary

* Updates to the logout session API.


### Web API

* /logout-sessions/rest/v1/
 
    * Adds new "sub_session_closed" parameter to the logout-end message, of 
      type boolean {true|false}. When true indicates the Connect2id server 
      closed the end-user session in response to an IdP-initiated logout 
      request, or in response to an RP-initiated logout request with a 
      submitted end-user confirmation that included the choice to log out of 
      the IdP as well. To be used as a hint to delete the browser cookie 
      linked to the session. Note that the deletion of the session cookie is 
      not critical, because the session ID is invalidated on the server side.

    * The logout session API is updated to proceed with a requested 
      "post_logout_redirect_uri" when there is no present end-user session, 
      or the session has expired. Previously the redirection request by the 
      relying party (RP) would be silently ignored. This change conforms with 
      OpenID Connect RP-Initiated Logout 1.0 (see section 4).  


### Dependency changes

* Updates to com.nimbusds:nimbus-jose-jwt:9.31



## 13.5 (2023-02-20)

### Summary

* Single sign-on (SSO) can be disabled for selected clients.

* New session store web API resource for modifying the authentication 
  lifetime of an end-user session.

* Client secret store SPI plugins can return the encoded secret in client read
  responses with a new custom "encoded_client_secret" client metadata field.  


### Configuration

* /WEB-INF/oidcProvider.properties

    * op.sso.disableForSelectedClients -- New optional configuration property
      to disable single sign-on (SSO) for selected registered clients. Ensures 
      end-users will be always (re)authenticated on the first OAuth 2.0 
      authorisation / OpenID authentication request when end-user has an 
      existing session with the Connect2id server. Subsequent requests from 
      the client received into the same end-user session will be processed 
      as usual, without triggering re-authentication of the end-user.
    
      Disabling SSO for a client creates the effect of "virtual" 
      client-based sessions with the Connect2id server.
     
      Clients with disabled SSO are selected by configuring a JSON query 
      that accepts the client registration (as JSON object representation) 
      and returns a boolean `true` result. The default configuration 
      property is no selector specified.

      Example JSON query to disable SSO for clients which registered a custom  
      `data` JSON object containing a `disable_sso` member set to `true`:
      `.data.disable_sso==true`.

      The Connect2id server logs the configured JSON query at INFO level
      with the ID `OP0090`.
    

### Web API

* /session-store/rest/v2/

    * Adds a new `/sessions/subject-auth-life` resource supporting a PUT method 
      to change the authentication lifetime of a session. The value is 
      specified as an integer number of minutes, where -1 means infinite (no 
      timeout) and 0 implies the default lifetime from the  
      `sessionStore.authLifetime` configuration property. Returns HTTP 204 No 
      Content on success.

* /clients/

   * Connect2id server deployments with a `ClientSecretStoreCodec` plugin for 
     encoding (hashing or encrypting) client secrets before committing them 
     to storage will include the stored client secret in an 
     "encoded_client_secret" metadata field in responses to client 
     registration read (HTTP GET) requests. Note, in order to provide the 
     metadata field in registration read responses the 
     `ClientSecretStoreCodec.decode` method must return a 
     `DecodedSecret.withEncodedValue`.

* /monitor/v1/metrics

    * Adds new `sessionStore.sessionAuthLifetimeUpdates` meter.

### Resolved issues

* The OpenID Connect Federation 1.0 "value" policy check must support JSON 
  objects (issue oidc-sdk/419).

* Fixes a bug that prevented return of the state parameter in RP-initiated 
  logout requests with a post_logout_redirect_uri when there is no 
  frontchannel_logout_uri registered for the client (issue server/831).
 

### Dependency changes

* Updates to com.nimbusds:oauth2-oidc-sdk:10.7

* Upgrades to com.nimbusds:oidc-session-store:15.3

* Adds net.thisptr:jackson-jq:1.0.0-preview.20220705



## 13.4.1 (2023-02-09)

### Resolved issues

* The "aud" of request objects (JARs) passed by OpenID Connect Federation 1.0
  clients must include the OpenID provider issuer URL, not the authorisation 
  endpoint URL (issue server/825).

* Fixes a bug that prevented client metadata shaped by a 
  FinalMetadataValidator SPI plugin from appearing in the authentication 
  prompt message when the `op.authz.includeClientInfoInAuthPrompt` 
  configuration property is set to `true` and the requesting client is an 
  automatic OpenID Federation 1.0 client that was just registered (issue 
  server/826).

* The signing JWK feeder when dealing with X.509 certificate based JWKs should 
  bias the key selection to pick the key with the farthest certificate 
  expiration date. This is to ensure optimal roll-over of RSA and EC signing 
  JWKs with an X.509 certificate (issue jwk-set-loader/5).

* Fixes the SE2000 error log message on failing to find a signing key with a 
  currently valid X.509 certificate (according to its not-before and 
  not-after attributes). The message must apply to both regular (in-memory) 
  keys with an X.509 certificate and HSM keys with a certificate (issue 
  jwk-set-loader/4).


### Dependency changes

* Updates to com.nimbusds:nimbus-jose-jwt:9.30.1

* Updates to com.nimbusds:nimbus-jwkset-loader:5.2.2


## 13.4 (2023-01-30)

### Summary

* Updates the Connect2id server to support the Java 17 runtime. 
  
  Due to the `secp256k1` elliptic curve no longer being available in the 
  default Java Cryptography Architecture (JCA) provider the Connect2id 
  server will use the alternative open source BouncyCastle JCA provider for 
  the `ES256K` (`secp256k1` curve) JWS algorithm when it's used to secure ID 
  tokens, UserInfo JWTs, request objects (JAR), authorisation responses 
  (JARM) or self-contained (JWT) access tokens.

  The Java 11 runtime support remains.

* Adds support for registering OAuth 2.0 clients with `redirect_uri`  
  templates, to enable Connect2id server deployments to set the redirection 
  URI at the time when the authorisation request is processed.

  This can facilitate scenarios where the exact `redirect_uri` is not known 
  at the time of client registration or where a client may require a 
  multitude of redirection URIs that conform to a certain pattern. The 
  `redirect_uri` templates apply to authorisation requests as well as pushed 
  authorisation requests (PAR).

  Example template where the `[param]` is a placeholder for a parameter to 
  be set when the Connect2id server processes the authorisation request:

  `urn:c2id:redirect_uri_template:https://[param].example.com/login-callback`


### Web API

* /clients/

    * Supports registration of OAuth 2.0 web and native clients with 
      templates in the `redirect_uris` parameter. The template is a URN with 
      format `urn:c2id:redirect_uri_template:[URI]`, where URI is the final 
      redirection URI which must contain a single `[param]` placeholder. The 
      `[param]` placeholder will be set by the Connect2id server when it 
      processes authorisation requests from the client. 

* /authz-sessions/rest/v3/

    * Adds an optional `redirect_uri_template_param` parameter of type 
      string to the authorisation session start request object. Used to set 
      the `[param]` in a `redirect_uri` of an authorisation request where the 
      URI is a template. The template URI must be registered just as any 
      regular redirection URI in the client's record under the `redirect_uris` 
      field.
     
      The `[param]` setting will apply to all authorisation requests, 
      including JAR and PAR.
  
      If the Connect2id server doesn't set the `[param]` for some reason the 
      `redirect_uri` will remain unchanged, which will later cause the 
      redirection to fail because of the URN scheme.


### Dependency changes

* Upgrades to com.nimbusds:nimbus-jose-jwt:9.30

* Upgrades to com.nimbusds:c2id-server-jwkset:1.26.2

* Updates to com.nimbusds:oauth2-authz-store:19.5



## 13.3 (2023-01-23)

### Summary

* Token exchange (RFC 8693) plugins can now optionally specify the issue of 
  a refresh token and ID token (in addition to the access token) when 
  authorising a request received via the TokenExchangeGrantHandler SPI. The 
  plugin can also flag the authorisation as long-lived (persisted), to cause 
  the granted scope values and other attributes to be remembered for the 
  subject and the requesting client. This also enables control of the 
  refresh token encoding (if issued) - persisted or stateless.

* Resource owner password credentials grant plugins can now specify the 
  issue of stateless (JWT-encoded) refresh tokens. Previously only persisted 
  refresh tokens could be issued.

* Updates the plugin for handling OAuth 2.0 grants at an external web 
  service (web hook) to support token exchange (RFC 8693) authorisations for 
  refresh token and ID token issue.


### Web API

* /token

    * Adds support for refresh token and ID token issue for a OAuth 2.0 token 
      exchange grant (RFC 8693).


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.52

    * The TokenExchangeAuthorization class is updated to support optional 
      persistence of the authorisation (with the long-lived flag), issue of 
      a refresh token (stateless or persisted) and issue of an ID token.

    * The PasswordGrantAuthorization class is updated to support issue of a 
      stateless refresh token when the long-lived authorisation flag is set 
      to `false`. Previously only persisted refresh tokens could only be 
      issued, when the long-lived authorisation flag was set to `true`.


### Resolved issues

* The AES key from client_secret derivation for shared JSON Web Encryption 
  (JWE) of ID tokens, UserInfo responses and other objects must remove the 
  right-most bits, not the left-most. See OpenID Connect Core 1.0 errata 
  2020-07-24 (issue oidc-sdk/412).

* The clients web API GET by client_id must handle client identifiers that 
  are OpenID Connect Federation 1.0 entity IDs (and URLs in general) seamlessly 
  (issue server/824). 


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.52

* Updates to com.nimbusds:oauth2-oidc-sdk:10.5.1

* Updates to com.nimbusds:nimbus-jose-jwt:9.29

* Updates to com.nimbusds:oauth-grant-handlers-web:1.0.4



## 13.2.1 (2023-01-19)

### Resolved issues

* Updates the Woodstox Core dependency used in the SAML 2.0 assertion grant 
  SPI, to address a potential stack overflow vulnerability in the XML DTD parse 
  code (CVE-2022-40152). Note that the CVE has been incorrectly filed to an 
  XStream dependency (a different project). Connect2id server deployments 
  that don't use a SAML 2.0 assertion grant plugin for exchanging SAML 2.0 
  tokens for OAuth 2.0 tokens are not affected (issue server/820).    

* Streaming registered OpenID Connect Federation 1.0 clients from the 
  federation client index must observe the tenant ID (issue server/640).

* Fixes NPE that prevented clean up of expired OpenID Connect Federation 1.0 
  automatic clients (issue server/657).


### Dependency changes

* Updates to com.fasterxml.woodstox:woodstox-core:5.4.0

* Updates Dropwizard Metrics to 4.2.15


## 13.2 (2023-01-12)

### Summary

* Upgrades OpenID Connect Federation 1.0 draft 25 support to publish a 
  signed JWK set at the URL advertised in the `signed_jwks_uri` OpenID 
  provider metadata found in the entity configuration.

* Fixes two bugs affecting deployments of Connect2id server v13.0 and v13.1 
  with an SQL database. Updating is strongly recommended (see issue 
  server/816 for details).


### Web API

* /.well-known/openid-configuration

    * signed_jwks_uri -- New optional metadata field specifying an endpoint 
      where the OpenID provider JWK set is published as a signed JWT. 
      Available when OpenID Connect Federation 1.0 is enabled, else omitted. 

* /jwks.jwt -- New endpoint publishing the OpenID provider JWK set as a 
  signed JWT when OpenID Connect Federation 1.0 is enabled. The JWT is 
  signed with the `RS256` algorithm using the first RSA key in the configured 
  Connect2id server federation entity JWK set. The JWT `typ` (type) header is 
  set to `jwk-set+jwt`. The JWT contains the `iss` (issuer), `sub` (subject),
  `iat` (issued-at time) and `keys` (JWK set keys) claims, as specified in 
  OpenID Connect Federation 1.0, section 4.1.


### Resolved issues

* Fixes a bug introduced in Connect2id server 13.0, multi-tenant edition, 
  affecting deployments with MySQL, PostgreSQL and MS SQL Server that may 
  cause false HTTP 404 (invalid authorisation session ID) responses from the 
  authorisation session web API. Connect2id server 13.0 and 13.1 
  multi-tenant deployments are strongly recommended updating (issue
  server/816).

* Fixes a bug introduced in Connect2id server 13.0 affecting deployments 
  with MySQL, PostgreSQL and MS SQL Server that causes incorrect PAR URI 
  rejections at the authorisation endpoint. Connect2id server 13.0 and 13.1 
  deployments are strongly recommended updating (issue server/818).

* Fixes non-critical NPE when writing HTTP 404 responses at the 
  `.well-known/openid-federation` endpoint when OpenID Connect Federation 1.0
  is disabled (issue server/817).

* Optimises OpenID Connect Federation 1.0 related logging (issue server/815).

* The PARValidator SPI must be invoked with an AuthenticationRequest if the 
  validated authorisation request has the "openid" scope value (issue 
  server/819).


### Dependency changes

* Upgrades to com.nimbusds:oauth2-oidc-sdk:10.5



## 13.1 (2022-12-23)

### Summary

* OpenID Connect Federation 1.0 upgrade.

  The pushed authorisation request (PAR) endpoint is now able to handle 
  automatic registration clients authenticating with a public key using a 
  JWT assertion (`private_key_jwt`) or mutual TLS (`tls_client_auth` or
  `self_signed_tls_client_auth`).

  The entity configuration, trust chain resolution and client registration 
  is updated to draft 25 of the OpenID Connect Federation 1.0 specification.

  See https://openid.net/specs/openid-connect-federation-1_0.html

* The token introspection endpoint receives a new configuration to control the
  pruning of audience ("aud") values in responses.


### Configuration

* /WEB-INF/oidcProvider.properties

    * op.token.introspection.pruneAudience -- New optional configuration 
      property to override the default Connect2id server filtering (since  
      v7.17 (2019-10-12)) of audience ("aud") values in token introspection 
      responses. If true causes the audience to be pruned to the client_id 
      of the introspecting client, intended to prevent revealing information 
      about recipients other than the intended in multi-audience tokens. If 
      false the complete token audience will always be shown. Has no effect 
      on tokens that don't specify an audience. The default value is true.
    
    * op.federation.autoClientAuthMethods.par -- New optional configuration 
      property listing the enabled methods for authenticating OpenID 
      Connect Federation 1.0 automatic client registration requests at the 
      OAuth 2.0 pushed authorisation request (PAR) endpoint. Supported 
      methods: `private_key_jwt`, `tls_client_auth` and
      `self_signed_tls_client_auth`. 
    
    * op.federation.logoURI -- New optional configuration property 
      specifying the logo URI of the OpenID provider as an OpenID Connect 
      Federation 1.0 entity. Will appear in the entity configuration 
      published at the `/.well-known/openid-federation` endpoint, under the 
      `metadata.federation_entity.logo_uri` claim.

* jose.federationJWKSet -> jose.federation.jwkSet -- Renames the Java system 
  property name for setting the OpenID Connect Federation 1.0 entity JWK set.
  The Java system property overrides the `/WEB-INF/federationJWKSet.json` 
  JWK set file content.


### Web API

* /.well-known/openid-federation

    * Updates the published entity configuration to OpenID Connect 
      Federation 1.0 draft 25.

    * metadata.federation_entity.logo_uri -- New optional federation entity 
      claim, configured by the op.federation.logoURI property.

* /par

    *  Supports OpenID Connect Federation 1.0 compliant automatic 
       registration clients that use the `private_key_jwt`, 
       `tls_client_auth` and `self_signed_tls_client_auth` methods (must be 
       enabled in the op.federation.autoClientAuthMethods.par configuration 
       property).

* /federation/clients

    * Updates explicit client registration to OpenID Connect Federation 1.0 
      draft 25.

* /token/introspect

    * Pruning of the audience ("aud") values in token introspection 
      responses can be now be controlled by the optional 
      op.token.introspection.pruneAudience configuration property.


### Resolved issues

* The PAR endpoint should clear client_secret_post, client_secret_jwt and 
  private_key_jwt form parameter artifacts from the stored authorisation 
  request (issue server/813).


### Dependency changes

* Upgrades to com.nimbusds:oauth2-oidc-sdk:10.4

* Updates to com.nimbusds:nimbus-jose-jwt:9.25.6

* Upgrades to com.nimbusds:tenant-manager:7.4

* Upgrades to com.nimbusds:oauth2-authz-store:19.4



## 13.0 (2022-11-30)

### Summary

* Individual clients can be registered to require use of Proof Key for Code 
  Exchange by OAuth Public Clients (RFC 7636) by means of the 
  `code_challenge_method` client metadata.  

* Upgrades the OpenID provider / OAuth 2.0 authorisation server issuer alias 
  model.

  Issuer aliasing was introduced in v12.3 (2021-09-17) to enable a 
  Connect2id server deployment to migrate seamlessly and over time from one 
  issuer identifier URL to another. Issuer aliases can also be used when an 
  OpenID provider / OAuth 2.0 authorisation server is known by multiple URLs.

  This release introduces two differentiated issuer alias modes 
  (configurable by `op.issuerAliasMode`):

    * MIGRATION -- Intended to facilitate issuer URL migration or 
      deployments where the OpenID provider is known by multiple URLs. This 
      is the default mode and how the Connect2id server previously behaved 
      with enabled issuer aliases.
  
    * PERSISTED_GRANT_ISOLATION -- Enforces complete OAuth 2.0 grant 
      isolation between issuer aliases. Has the effect of disabling long-lived 
      (persisted) consent, forcing issue of self-contained (stateless) 
      refresh tokens only, and blocking the use of any previously issued 
      identifier-based refresh tokens. This mode is intended for deployments 
      that for some reason choose not to operate a multi-tenant Connect2id 
      server where the OpenID providers / OAuth 2.0 authorisation servers 
      completely isolated.

  For security reasons both issuer alias modes will now behave as follows:

    * Prevent switching of the issuer URL during an OAuth authorisation code,
      implicit or hybrid flow (which may involve the PAR endpoint). 
    
    * Prevent switching of the issuer URL in the authorisation session API 
      at the user authentication or consent step.
    
    * The token introspection endpoint will mark any token issued under a 
      different alias as invalid and the scope to access the endpoint must also 
      be set to the current issuer URL.
    
    * The UserInfo endpoint will reject access tokens issued under a 
      different alias.

  Note, in the MIGRATION issuer alias mode refresh tokens which are tied to 
  long-lived (persisted) consent can be shared across all issuer aliases. 
  The resulting access tokens however will be issued and remain valid for 
  the current issuer alias only.
 
  Finally, the issuer aliasing was updated to enable dynamic addition and 
  removal of issuer alias URLs, with no changes to the Connect2id server 
  configuration.

* Upgrades H2 SQL database support from v1.x to v2.x. This is a breaking change 
  that affects the persisted H2 data format. Data stored by H2 v1.x is not 
  compatible and cannot be read by H2 v2.x. Connect2id server deployments 
  that use H2 to persist server data will need to perform a migration. See 
  the Data Migration guide for more information.


### Configuration

* /WEB-INF/oidcProvider.properties

    * op.issuerAliases -- New optional configuration property for Connect2id 
      server deployments that need to support issuer alias URLs of the OpenID 
      provider / OAuth 2.0 authorisation server. By setting the 
      configuration property to "*" (asterisk) the HTTP reverse proxy in front 
      of the Connect2id server is enabled to determine the whitelisted 
      issuer alias URLs when setting the "Issuer" security header. This can 
      be useful in deployments where issuer aliases must be added or removed 
      dynamically, without restarting the server (in the regular edition) or 
      updating the OpenID provider / OAuth 2.0 authorisation server 
      configuration via the tenants web API (in the multi-tenant edition). 
      Previously the Connect2id server supported only a static whitelist of 
      allowed issuer aliases.

    * op.issuerAliasMode -- New optional configuration property introducing 
      two differentiated modes of issuer aliasing:
    
      * MIGRATION -- Enables seamless migration over time to a new issuer URL.
        This is the default mode and how the Connect2id server previously     
        behaved with enabled issuer aliases.
      
      * PERSISTED_GRANT_ISOLATION -- Enforces persisted grant isolation between
        issuer aliases: disables long-lived (persisted) consent; forces issue
        of self-contained (stateless) refresh tokens; blocks the use of any
        previously issued identifier-based refresh tokens.

    * op.reg.httpMaxRequestSize -- New optional configuration property 
      enabling override of the size limit of the entity body of HTTP POST and 
      PUT requests to the client registration web API. Configurable via Java 
      system property only! The default value is 250 thousand (250000) 
      characters.

* /WEB-INF/infinispan-*-redis-*.xml

    * New `redisMapPassword` and `redisCachePassword` configuration 
      properties of type string to set a password for accessing Redis. The 
      default value is no password.

* /WEB-INF/infinispan-*-{mysql|postgres95|sqlserver|h2}.xml

    * Adds new "code_challenge_method" column to the "clients" table. In 
      existing Connect2id server deployments with an SQL RDBMS the server will
      automatically add the new column (with an appropriate default value) on
      startup.

* /WEB-INF/infinispan-*-ldap.xml

    * Adds new "oauthCodeChallengeMethod" attribute to the
      "oauthClientMetadata" object classes. Connect2id server deployments 
      with an LDAP v3 backend database (such as OpenLDAP or OpenDJ) must 
      update the LDAP schema manually to version 1.19
      see https://bitbucket.org/connect2id/server-ldap-schemas/src/1.19/ , the
      OpenLDAP schema diff
      https://bitbucket.org/connect2id/server-ldap-schemas
      /diff/src/main/resources/oidc-client-schema-openldap.ldif?at=1.19
      &diff2=97f372ce82ddd94e08a3937c4169f3a190aed2b2
      and the OpenDJ schema diff
      https://bitbucket.org/connect2id/server-ldap-schemas
      /diff/src/main/resources/oidc-client-schema-opendj.ldif?at=1.19
      &diff2=97f372ce82ddd94e08a3937c4169f3a190aed2b2


### Web API

* /clients

    * Supports registration of clients with the optional custom 
      `code_challenge_method` metadata field of type string and values `S256` 
      and `plain` to force the client to use a code challenge method (see Proof 
      Key for Code Exchange by OAuth Public Clients, RFC 7636) at the 
      authorisation and the pushed authorisation request (PAR) endpoints. 
      The default value is no code challenge method.

      Note that the Connect2id server `op.authz.allowedPKCE` and `op.authz.
      requiredPKCE` configuration properties will always override this 
      client metadata.


### Resolved issues

* Upgrades the security of the authorisation code grant at the token 
  endpoint by adding an immediate code invalidation to complement the usual 
  invalid_grant OAuth 2.0 error in the following cases: 1) mismatch between 
  token request client_id (for a public or successfully authenticated 
  confidential client) and the client_id associated with the issued code at 
  the authorisation endpoint; 2) invalid or missing redirect_uri; 3) missing, 
  invalid or unexpected code_verifier (PKCE); 4) mismatch between the code 
  issuer and the tenant issuer at the token endpoint (issue authz-store/195).

* Improves the data layer performance of code for token exchange at the 
  token endpoint (issue authz-store/195).

* Updates the token endpoint unauthorized_client error description in the 
  case when the request is rejected because the client is not registered for 
  the grant type (issue server/798).


### Dependency changes

* Updates to com.nimbusds:oauth2-authz-store:19.3

* Updates to com.nimbusds:oidc-session-store:15.1.1

* Upgrades to com.nimbusds:tenant-manager:7.3.1

* Upgrades to com.nimbusds:tenant-registry:7.1

* Updates to com.google.code.gson:gson:2.10

* Updates to com.nimbusds:infinispan-cachestore-sql:5.0

* Updates to com.nimbusds:infinispan-cachestore-redis:9.2.9
 
* Upgrades to org.jooq.pro-java-11:jooq:3.17.4

* Updates to com.zaxxer:HikariCP:5.0.1

* Updates to org.postgresql:postgresql:42.5.1

* Upgrades to com.h2database:h2:2.1.214



## 12.18 (2022-10-25)

### Configuration

* /WEB-INF/oidcProvider.properties

    * op.metadataOverlay -- New optional configuration property for a JSON 
      object overlay to apply to the OpenID provider / OAuth 2.0 
      authorisation server metadata published at the 
      ".well-known/openid-configuration" and 
      ".well-known/oauth-authorization-server" endpoints. Non-null values in 
      the overlay object replace existing metadata fields, null values 
      remove them. Note, the overlay does not affect the internal Connect2id 
      server configuration and after its application the resulting JSON 
      object is not checked for being a legal representation of OpenID 
      provider / OAuth 2.0 authorisation server metadata. If set the overlay 
      must be represented as a JSON object string, and can be additionally 
      BASE64 encoded to ease passing the configuration property from a 
      command line shell.


### Web API

* /authz-sessions/rest/v3/

    * Pushed authorisation request (PAR) URIs will become invalidated after 
      their use at the authorisation endpoint. Previously a PAR URI will 
      remain valid until its expiration configured by the op.par.lifetime 
      property. 


### Resolved issues

* Logs warning under AS0277 when revoking an authorisation by self-contained 
  (JWT-encoded) access token which local (public) subject or client_id are 
  not encoded (issue authz-store/194).


### Dependency changes

* Updates to com.nimbusds:oauth2-authz-store:18.2.1

* Updates to io.prometheus:simpleclient:0.16.0

* Updates to io.prometheus:simpleclient_servlet:0.16.0

* Updates to io.prometheus:simpleclient_dropwizard:0.16.0

* Updates to Log4j 2.19.0



## 12.17 (2022-09-14)

### Web API

* /authz-store/rest/v2/revocation

    * Adds support for an optional "quiet" query parameter when posting a 
      revocation. When set to `quiet=true` an HTTP 204 No Content response 
      will be returned; if any authorisation(s) were matched by the 
      revocation parameters and removed they will not be returned in the 
      response body.


### Resolved issues

* The authorisation session web API must not set the "required_sub" 
  parameter in the authentication prompt to the end-user ID when the 
  Connect2id server is configured with alwaysPromptForAuth=true and the 
  end-user has an active session. This resulted in a incorrect OpenID 
  Connect login_required error if the current end-user is (re)authenticated 
  to another subject (end-user ID) as a result of the authentication prompt. 
  The fix corrects the behaviour so that the original session is closed and a 
  new one with the new subject (end-user ID) is started (issue server/781).

* The op.grantHandler.tokenExchange.webAPI.actorToken.types configuration 
  property of the token exchange grant handler plugin must support setting 
  of no actor token types accepted. The default value must also be none 
  (issue grant-handlers-web/1).


### Dependency changes

* Updates to com.nimbusds:oauth2-authz-store:18.2

* Updates to com.nimbusds:oidc-session-store:14.9.2

* Updates to com.nimbusds:oauth-grant-handlers-web:1.0.3

* Updates to com.nimbusds:tenant-manager:6.0.4

* Updates to com.nimbusds:tenant-registry:6.0.3

* Updates to com.google.crypto.tink:tink:1.7.0

* Updates DropWizard to 4.2.12

* Updates to com.unboundid:unboundid-ldapsdk:6.0.6

* Updates to com.nimbusds:infinispan-cachestore-sql:4.2.8

* Updates to org.postgresql:postgresql:42.5.0

* Updates to org.mariadb.jdbc:mariadb-java-client:2.7.6

* Updates to com.microsoft.sqlserver:mssql-jdbc:11.2.1.jre11



## 12.16.1 (2022-08-18)

### Resolved issues

* Fixes missing logging of the base configuration properties in the 
  web-based token exchange grant handler (issue server/776).

* Fixes test that erroneously removed the SPI manifests for the web-based 
  password, client credentials and token exchange grant handlers (issue 
  server/778). 


### Dependency changes

* Updates to com.nimbusds:oauth-grant-handlers-web:1.0.1



## 12.16 (2022-08-12)

### Summary

* Adds new plugin for handling OAuth 2.0 token exchange (RFC 8693) grants that 
  passes processing of the grant authorisation to an external web service 
  (web hook). The plugin implements the TokenExchangeGrantHandler SPI 
  introduced in Connect2id server 12.14.

  Features:

  * Supports arbitrary "subject_token" and "actor_token" types.
  
  * The acceptable "subject_token", "actor_token" and requested token types 
    are configurable.

  * Optional automatic introspection of the received "subject_token" of type 
    access token. Calls upon the internal Connect2id server introspection for 
    access tokens that are locally issued, or one or more configured token 
    introspection endpoints compliant with RFC 7662.
  
  * Optional automatic JWT verification of the received "subject_token" of 
    type JWT, access token or ID token. The JWT signature is verified using 
    a set of JWKs at one or more configured URLs.
  
  * Received "subject_token" and "actor_token" instances can also be 
    passed in their original form for verification by the web service itself.
  
  * Supports passing of selected client metadata parameters to the web 
    service, in addition to the client_id and confidential status, to be 
    used as inputs in the authorisation decision. The "scope" and "data" 
    client metadata fields are included by default.
  
  * Supports setting of HTTP connect and read timeouts, for the underlying web 
    service, the configured token introspection endpoints and JWK set URLs.

* Replaces the existing plugin for handling OAuth 2.0 client credentials 
  grants at an external web service (web hook) with a new one that enables  
  configuration of the client metadata fields to pass to the handler. Also 
  supports multi-tenant operation.

* Replaces the existing plugin for handling OAuth 2.0 resource own password 
  credentials grants at an external web service (web hook) with a new one 
  that enables configuration of the client metadata fields to pass to the 
  handler. Also supports multi-tenant operation.


### Configuration

* /WEB-INF/tokenExchangeGrantHandlerWebAPI.properties -- New configuration 
  file for the new web-based token exchange grant handler, containing the 
  default configuration properties. They can be selectively overridden with 
  Java system properties. 

* /WEB-INF/clientGrantHandlerWebAPI.properties

    * op.grantHandler.clientCredentials.webAPI.customParams -- New optional  
      configuration property to list the names of custom token request 
      parameters to include as top-level members in the handler request JSON 
      object.
    
    * op.grantHandler.clientCredentials.webAPI.clientMetadata -- New optional 
      configuration property to list the names of client metadata fields to 
      include in the "client" JSON object which is part of handler request. 
      If not specified the following client metadata fields are included by 
      default: "scope", "application_type", "sector_identifier_uri", 
      "subject_type", "default_max_age", "require_auth_time", 
      "default_acr_values", "data"

* /WEB-INF/passwordGrantHandlerWebAPI.properties

    * op.grantHandler.clientCredentials.webAPI.customParams -- New optional 
      configuration property to list the names of custom token request 
      parameters to include as top-level members in the handler request JSON 
      object.

    * op.grantHandler.clientCredentials.webAPI.clientMetadata -- New optional 
      configuration property to list the names of client metadata fields to 
      include in the "client" JSON object which is part of handler request. 
      If not specified the following client metadata fields are included by 
      default: "scope", "application_type", "sector_identifier_uri", 
      "subject_type", "default_max_age", "require_auth_time", 
      "default_acr_values", "data".


### Web API

* /authz-sessions/rest/v3/

    * Designates the "invalid_target" OAuth 2.0 error code, defined in RFC 
      8707, as a standard acceptable code to indicate an error condition 
      during end-user authentication / consent. Deployments that use this 
      error code are no longer required to list it in the op.authz.
      customErrorCodes configuration.

### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.51

  * Adds DefaultTokenIntrospectionResponseComposer class.
  
  * Adds DefaultTokenRequestParameters class.


### Resolved issues

* Updates the systemPropertiesURL configuration property to support AWS S3 
  URLs in the new style virtual format (issue server/773).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.51
 
* Upgrades to com.nimbusds:oauth2-oidc-sdk:9.41
 
* Adds com.nimbusds:oauth-grant-handlers-web:1.0
 
* Removes com.nimbusds:oauth-password-grant-web-api:1.5
 
* Updates to com.nimbusds:oauth-client-grant-handler:2.0.3
 
* Updates to com.nimbusds:oauth-jwt-self-issued-grant-handler:1.1.1
 
* Updates to com.nimbusds:tenant-manager:6.0.3
 
* Updates to com.nimbusds:tenant-registry:6.0.2
 
* Updates to com.nimbusds:oauth2-authz-store:18.1.1
 
* Updates to com.nimbusds:oidc-session-store:14.9.1
 
* Updates to com.nimbusds:c2id-server-jwkset:1.26
 
* Updates to com.nimbusds:infinispan-cachestore-common:2.4.1

* Updates to com.nimbusds:infinispan-cachestore-ldap:3.1.3

* Updates to com.nimbusds:infinispan-cachestore-sql:4.2.7

* Updates to com.nimbusds:infinispan-cachestore-dynamodb:4.2.1

* Updates to org.postgresql:postgresql:42.4.1

* Updates to com.nimbusds:c2id-server-property-source:1.1

* Upgrades to com.thetransactioncompany:java-property-utils:1.17

* Updates to com.amazonaws:aws-java-sdk-*:1.12.264

* Updates to DropWizard Metrics 4.2.10

* Updates to Log4j 2.18.0



## 12.15 (2022-07-17)

### Summary

* Updates OpenID Connect RP-Initiated Logout 1.0 support to draft 02. 
  Introduces new `logout_hint`, `client_id` and `ui_locales` request
  parameters. See https://openid.net/specs/openid-connect-rpinitiated-1_0.html 

* PrivateKeyJWTCertificateVerifier SPI plugins can override the default 
  `error_description` and `error_uri` in `invalid_client` errors returned to 
  the authenticating OAuth 2.0 client.

* New `dynamodb.enableContBackups` configuration property to enable DynamoDB 
  continuous backups / point-in-time recovery for tables holding crucial or 
  long-lived Connect2id server data. Previously continuous backups could be 
  enabled only via the AWS CLI, SDK, API or web console.


### Configuration

* /WEB-INF/infinispan-*-dynamodb.xml

    * New `dynamodb.enableContBackups` configuration property of type 
      boolean (`true`|`false`) to enable continuous backups / point-in-time 
      recovery for all DynamoDB tables where crucial or long-lived Connect2id 
      server data is persisted: `id_access_tokens`, 
      `long_lived_authorizations`, `revocation_journal`, `clients`, 
      `federation_clients` and `tenants` (in the multi-tenant Connect2id 
      server edition). Applied at Connect2id server startup on new table 
      creation as well as for existing tables. The default value is `false` 
      (no continuous backups).


### Web API

* Logout (end-session) endpoint

    * id_token_hint -- Relying parties can submit ID token hints encrypted 
      with JSON Web Encryption (JWE) for confidentiality. The ID token can be 
      encrypted with a public encryption RSA or EC JWK published at the 
      Connect2id server's `jwks.json` endpoint. A relying party that is 
      provisioned with a `client_secret` can alternatively encrypt the ID 
      token with a symmetric AES key using the JWE `dir` algorithm and a JWE 
      method listed in the `id_token_encryption_enc_values_supported` OpenID 
      provider metadata field, as specified in OpenID Connect Core 1.0 
      incorporating errata set 1, section 10.2.

    * client_id -- New optional RP-initiated logout request parameter, of 
      type string, representing the client ID of the relying party. A 
      relying party should use it to identify itself in a request when the 
      recommended `id_token_hint` parameter isn't included or when the 
      `id_token_hint` represents a symmetrically encrypted (JWE) ID token so 
      the OpenID provider can resolve the relying party's registered 
      `client_secret` necessary for the ID token decryption. If both 
      `id_token_hint` and `client_id` are included in a logout request the 
      client ID must be found in the ID token audience.

      Note, a valid `id_token_hint` remains required for RP-initiated logout
      requests that include a `post_logout_redirect_uri` parameter.

    * logout_hint -- New optional RP-initiated logout request parameter, of 
      type string, representing a hint about the end-user that is logging 
      out, such as the user's email address, telephone number or username. 
      Analogous to the `login_hint` OpenID authentication request parameter.
    
    * ui_locales -- New optional parameter, of type string and consisting of 
      one or more space delimited BCP47 (RFC 7231) language tags, representing 
      the end-user's preferred languages and scripts for the logout UI.

* /logout-sessions/rest/v1/

    * Adds support for the optional `client_id` RP-initiated logout request
      parameter. The Connect2id server will use it to identify the calling 
      relying party when the recommended `id_token_hint` logout request 
      parameter isn't included or represents an ID token that is 
      symmetrically encrypted with a `client_secret`. If both 
      `id_token_hint` and `client_id` are present in a logout request the  
      Connect2id will check the ID token was issued to the `client_id`; if 
      not an `invalid_id_token_hint` error will be returned.
    
    * New `id_token_hint_present` parameter in the logout prompt message, of 
      type boolean (true|false), to show if the relying party included an 
      `id_token_hint` in the logout request.
     
      Note, if the `id_token_hint` logout request parameter failed the 
      Connect2id server verification (covers all standard ID token checks, 
      save for its `exp` claim), the logout session API will return an 
      `invalid_id_token_hint` error. Hence, the `id_token_hint_present` when 
      true will always indicate a valid ID token.
    
    * New optional `op_logout` parameter in the logout confirmation message, 
      of type boolean (true|false) and a default value `false`, to indicate 
      an end-user request for IdP-wide logout in addition to confirming the 
      RP logout. This new parameter deprecates the existing `confirm_logout` 
      parameter.
    
    * New optional `logout_hint` parameter in the logout prompt message, of
      type string, representing the `logout_hint` RP-initiated logout request
      parameter.
  
    * New optional `ui_locales` parameter in the logout prompt, logout end
      and logout error messages, of type string array, representing the
      `ui_locales` RP-initiated logout request parameter.
    
    * New `invalid_request` error code to indicate an invalid RP-initiated 
      logout request.


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.49

    * New ExposedInvalidClientException class that extends the common
      InvalidClientException for representing OAuth 2.0 invalid_client 
      errors, to indicate that the default Connect2id server error_description 
      and error_uri must be overridden with specific values.

      The Connect2id has a security policy to log the message of
      InvalidClientException instances and return a general
      error_description in the HTTP 401 Unauthorized response that doesn't
      reveal the exact cause why client authentication failed. The new 
      ExposedInvalidClientException lets client authentication related 
      plugins override this policy and set the error_description and 
      error_uri in the HTTP 401 Unauthorized response. This facility must be 
      used judiciously.
    
    * Connect2id server plugins implementing the  
      PrivateKeyJWTCertificateVerifier SPI can throw the new 
      ExposedInvalidClientException instead of the common 
      InvalidClientException to override the default Connect2id server 
      error_description and error_uri in the resulting HTTP 401 Unauthorized 
      response.

      When using the ExposedInvalidClientException to set a custom invalid 
      client error_description care must be taken not to divulge sensitive 
      or more information than necessary. 
    

### Resolved issues

* Updates the access token (as subject_token) introspection in token exchange 
  grant handling (RFC 8693) to mark tokens which client_id doesn't match the 
  client_id of the requesting OAuth 2.0 client as invalid. In addition, an 
  OP6216 warning will be logged when this condition is encountered (issue 
  server/768).

* The logout session web API must not log request query strings at INFO level 
  (issue server/770).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.49
 
* Upgrades to com.nimbusds:oauth2-oidc-sdk:9.38

* Upgrades to com.nimbusds:lang-tag:1.7



## 12.14 (2022-06-30)

### Summary

* Support for OAuth 2.0 Token Exchange (RFC 8693). This is an OAuth 2.0 
  extension that specifies a generic mechanism for clients to obtain an access 
  token in exchange for another token, which type and encoding can be arbitrary 
  and which issuer can be the same OAuth 2.0 authorisation server or another 
  trusted 3rd party token service. This grant also supports impersonation 
  (act-as) and delegation (on-behalf-of) scenarios. See https://datatracker.
  ietf.org/doc/html/rfc8693


### Web API

* /clients

    * Supports registration of clients for the OAuth 2.0 token exchange grant 
      ("urn:ietf:params:oauth:grant-type:token-exchange"). The clients can 
      be confidential (with authentication credentials) or public.

* /token

    * Supports the OAuth 2.0 token exchange grant (RFC 8693), identified by 
      the grant_type "urn:ietf:params:oauth:grant-type:token-exchange". 
      Requires a TokenExchangeGrantHandler SPI plugin.  

* /monitor/v1/metrics

    * Adds new `tokenEndpoint.tokenExchange.successfulRequests`, 
      `tokenEndpoint.tokenExchange.invalidClientErrors`, 
      `tokenEndpoint.tokenExchange.unauthorizedClientErrors`,
      `tokenEndpoint.tokenExchange.invalidGrantErrors` and 
      `tokenEndpoint.tokenExchange.invalidScopeErrors` meters for the OAuth 
      2.0 token exchange grant.


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.48

    * TokenExchangeGrantHandler -- New SPI for implementing OAuth 2.0 token 
      exchange (RFC 8693) scenarios. Accepts `subject_token` and `actor_token` 
      instances of any token type and issuer. The `requested_token_type` must 
      be an access token (locally issued). The access token can be of type 
      Bearer, with a client X.509 certificate binding (RFC 8705), or DPoP 
      bound (draft-ietf-oauth-dpop-09). Issue of other types of tokens as 
      well as refresh tokens currently isn't supported.
    
    * ClientCredentialsGrantHandler -- Adds new `processGrant` method to the 
      SPI to enable handling of token parameters other than scope and 
      provide access to the configured OP / AS issuer URI (necessary for grant 
      handler plugins in multi-tenant Connect2id server deployments). The 
      old `processGrant` method is deprecated. The new `processGrant` method 
      has a default implementation that passed the call to the old 
      deprecated method so existing plugins can continue functioning as they 
      are.
    
    * ResourceOwnerPasswordCredentialsGrant -- Adds new `processGrant` 
      method to the SPI to enable handling of token parameters other than 
      scope and provide access to the configured OP / AS issuer URI 
      (necessary for grant handler plugins in multi-tenant Connect2id server 
      deployments). The old `processGrant` method is deprecated. The new 
      `processGrant` method has a default implementation that passed the 
      call to the old deprecated method so existing plugins can continue 
      functioning as they are.
    
    * SelfIssuedJWTGrantHandler -- Adds new `processGrant` method to the SPI 
      to enable handling of token parameters other than scope and provide 
      access to the configured OP / AS issuer URI (necessary for grant 
      handler plugins in multi-tenant Connect2id server deployments). The 
      old `processGrant` method is deprecated. The new `processGrant` method 
      has a default implementation that passed the call to the old 
      deprecated method so existing plugins can continue functioning as they 
      are.
    
    * ThirdPartyJWTGrantHandler -- Adds new `processGrant` method to the SPI 
      to enable handling of token parameters other than scope and provide 
      access to the configured OP / AS issuer URI (necessary for grant 
      handler plugins in multi-tenant Connect2id server deployments). The 
      old `processGrant` method is deprecated. The new `processGrant` method 
      has a default implementation that passed the call to the old 
      deprecated method so existing plugins can continue functioning as they 
      are.
    
    * SelfIssuedSAML2GrantHandler -- Adds new `processGrant` method to the SPI 
      to enable handling of token parameters other than scope and provide 
      access to the configured OP / AS issuer URI (necessary for grant 
      handler plugins in multi-tenant Connect2id server deployments). The 
      old `processGrant` method is deprecated. The new `processGrant` method 
      has a default implementation that passed the call to the old 
      deprecated method so existing plugins can continue functioning as they 
      are.
    
    * ThirdPartySAML2GrantHandler -- Adds new `processGrant` method to the SPI 
      to enable handling of token parameters other than scope and provide 
      access to the configured OP / AS issuer URI (necessary for grant 
      handler plugins in multi-tenant Connect2id server deployments). The 
      old `processGrant` method is deprecated. The new `processGrant` method 
      has a default implementation that passed the call to the old 
      deprecated method so existing plugins can continue functioning as they 
      are.
    
    * Adds a `ClaimsSpec` field to the `GrantAuthorization` class. This 
      enables plugins implementing the `ClientCredentialsGrantHandler` SPI 
      to authorise OAuth 2.0 clients registered for the client_credentials 
      grant to receive an access token for OpenID claims at the UserInfo 
      endpoint. This also enables the existing Connect2id server feature 
      where authorised OpenID claims specified with the `access_token:` prefix 
      will be fed into the access token.  


### Resolved issues

* Increases the entity size limit of HTTP requests to the client 
  registration endpoint from 20K chars to 250K chars to cater for client 
  registrations with exceptionally large metadata. The entity size limit has 
  been present to prevent DoS attacks in client registration that is open or 
  managed in a way that doesn't enforce a limit on the submitted client 
  metadata (issue server/765).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.48



## 12.13 (2022-06-20)

### Summary

* The default Connect2id server codec for self-contained (JWT-encoded) 
  access tokens can now insert selected elements from the client data field 
  and the authorisation data fields as top-level JWT claims. Deployments can 
  use this feature to conform to access token profiles without a custom 
  SelfContainedAccessTokenClaimsCodec plugin.


### Configuration

* /WEB-INF/authzStore.properties
    
    * authzStore.accessToken.codec.jwt.copyClientData -- New optional 
      configuration property of the default Connect2id server codec for 
      JWT-encoded access tokens. Lists names of members in the client 
      registration's "data" JSON object to copy as top-level JWT claims. An 
      "*" (asterisk) selects all members. If a custom JWT codec 
      (implementing the SelfContainedAccessTokenClaimsCodec SPI) is plugged
      this setting has no effect.
  
    * authzStore.accessToken.codec.jwt.moveAuthzData -- New optional
      configuration property of the default Connect2id server codec for
      JWT-encoded access tokens. Lists the names of members in the 
      authorisation "dat" (data) JSON object to move to top-level JWT claims 
      in access tokens minted by the default self-contained access token 
      encoder. An "*" (asterisk) selects all members. If a custom JWT codec
      (implementing the SelfContainedAccessTokenClaimsCodec SPI) is plugged 
      this setting has no effect.


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.45

  * Updates the SelfContainedAccessTokenClaimsCodec SPI by adding a new 
    TokenEncoderContext.getOIDCClientInformation method.
  
  * Updates the AccessTokenIssueEventListener and IDTokenIssueEventListener 
    SPIs by adding a new EventContext.getOIDCClientInformation method.


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.45

* Upgrades to com.nimbusds:oauth2-authz-store:18.1

* Upgrades to com.nimbusds:common:2.49



## 12.12 (2022-06-03)

### Summary

* New plugin interface (Service Provider Interface, or SPI) for accepting 
  qualified X.509 certificates to verify the digital signature in 
  private_key_jwt client authentications.

* New plugin interface (SPI) for intercepting client authentication success 
  and failure events at all Connect2id server endpoints where client 
  authentication occurs. Can be used for logging, reporting, audit, debugging 
  and other purposes.

* Introduces a secure random 12 byte "client_auth_id" to identify each 
  individual client authentication performed by the Connect2id server in log 
  messages, OAuth 2.0 invalid_client errors and calls to SPIs like the new 
  private key JWT certificate verifier and the client authentication 
  interceptor.

* Includes a web-based handler plugin for the OAuth 2.0 client credentials 
  grant, implementing the ClientCredentialsGrantHandler SPI from the 
  Connect2id server SDK. This handler is not compatible with the 
  multi-tenant edition of the Connect2id server. Disabled by default. The 
  default client credentials handler remains the existing local one (com.
  nimbusds:oauth-client-grant-handler:2.0.2).


### Web API

* /token

    * OAuth 2.0 invalid_client error objects include a "client_auth_id" to 
      identify the client authentication event in server log messages and 
      SPI calls.

* /token/introspect

    * OAuth 2.0 invalid_client error objects include a "client_auth_id" to
      identify the client authentication event in server log messages and
      SPI calls.

* /token/revoke

    * OAuth 2.0 invalid_client error objects include a "client_auth_id" to
      identify the client authentication event in server log messages and
      SPI calls.

* /par

    * OAuth 2.0 invalid_client error objects include a "client_auth_id" to
      identify the client authentication event in server log messages and
      SPI calls.


### Configuration

* /WEB-INF/clientGrantHandlerWebAPI.properties -- New configuration file for 
  the client credentials grant handler plugin that delegates processing of 
  the grant authorisation to a web-service. The configuration properties can 
  be overridden or set with Java system properties. 


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.44

    * com.nimbusds.openid.connect.provider.spi.clientauth.
      PrivateKeyJWTCertificateVerifier -- New SPI for verifying an X.509 
      certificate (x5c) in private_key_jwt} client authentications. This can 
      be used to enable private_key_jwt authentication based on qualified 
      certificates and without a prior client JWK set registration (via the 
      "jwks" or "jwks_uri" client metadata parameters).
       
      The SPI enables implementation of policies where only selected clients 
      are allowed or required to include a certificate for the 
      private_key_jwt, based on the client's registered metadata or other 
      criteria.
      
      A client can place the certificate in the private_key_jwt "x5c" header.
      Alternatively, the certificate can be put in the "x5c" parameter of a 
      matching public JWK and have the key pre-registered via the "jwks" or 
      "jwks_uri" client metadata parameter.
    
      Implementations must be thread-safe.

    * com.nimbusds.openid.connect.provider.spi.clientauth.
      ClientAuthenticationInterceptor -- New SPI for intercepting successful 
      and failed client authentications at all Connect2id server endpoints 
      where client authentication occurs, such as the token, token 
      introspection, token revocation and pushed authorisation request (PAR) 
      endpoints. Successful client authentications can be subjected to 
      additional checks and rejected with an OAuth 2.0 invalid_client error.

      Implementations must be thread-safe. Interceptors that create events 
      should use a separate thread for blocking operations.


### Resolved issues

* Fixes an HTTP 500 Internal Server Error on a token revocation request with 
  client authentication where the client_id resolves to an invalid client 
  registration (issue server/760).

* The message OP0131 ("Couldn't determine Connect2id server local host") 
  should be logged at WARN level, not ERROR (issue server/759).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.44

* Upgrades to com.nimbusds:oauth2-oidc-sdk:9.37.2

* Upgrades to com.nimbusds:nimbus-jose-jwt:9.23

* Updates to Infinispan 9.4.24

* Updates to com.unboundid:unboundid-ldapsdk:6.0.5

* Updates to com.nimbusds:oauth-password-grant-web-api:1.5

* Updates to com.nimbusds:oauth-client-grant-handler:2.0.2

* Adds com.nimbusds:oauth-client-grant-web-api:1.4



## 12.11 (2022-05-22)

### Configuration

* /WEB-INF/oidcProvider.properties

    * op.splashPage -- New configuration property for the splash page to 
      display at the Connect2id server issuer URL (`op.issuer`).

      Supported values: 
  
        * urn:c2id:splash_page:default -- The default splash page, an HTML 
          page showing the Connect2id server version, a list of the 
          available endpoints and links to public online documentation.
        * urn:c2id:splash_page:blank -- A blank page.
        * urn:c2id:splash_page:op_metadata -- Redirects (HTTP 301) to the 
          OpenID provider metadata at `/.well-known/openid-configuration`
        * https or http URL -- Redirects (HTTP 301) to the specified HTTPS 
          or HTTP URL.


### Resolved issues

* Fixes a bug that affected the correct handling of the subject session 
  "auth_life" property (for values > 0) in the authorisation session web API, 
  used to determine when the authentication lifetime (in minutes) of a 
  session expires and the subject (end-user) must be re-authenticated in the 
  same session (issue server/756).

* Adds custom static error pages for 404, 405 and other HTTP status codes 
  handled by the Servlet container to hide the Servlet container version and 
  other potentially sensitive information (issue server/745).



## 12.10 (2022-05-03)

### Summary

* Support for OpenID authentication requests with prompt=create to enable 
  relying parties to instruct the OpenID provider to present the user with a 
  sign-up screen. After the user is successfully registered the flow 
  proceeds as usual. Support for the "create" prompt value is advertised in 
  a new "prompt_values_supported" OpenID provider metadata field. Login 
  handlers integrating with the authorisation session API will receive 
  indication of a prompt=create in a new "create_account" {true|false} 
  parameter of the "auth" message. If the OpenID provider has no requirement 
  or wish to honour prompt=create the login handler can safely ignore the 
  "create_account" flag and render the usual user authentication screen. OpenID 
  prompt=create requests will always trigger an "auth" prompt message in the 
  authorisation session API, similarly to OpenID prompt=select requests.

  The Connect2id server will reject OpenID authentication requests with a 
  prompt parameter that contains values other than "create", in accordance 
  with the specification recommendation.

  This new prompt "create" value is specified in Initiating User Registration 
  via OpenID Connect - draft 04, see https://openid.net/
  specs/openid-connect-prompt-create-1_0.html

* Support for minting back-channel logout notification tokens with explicit 
  JWT typing. This is a simple measure to help relying parties simplify the 
  prevention of mix-up of logout token JWTs with other types of JWT without 
  having to examine the JWT claims structure. Enabled by default.


### Configuration

* /WEB-INF/oidcProvider.properties

    * op.logout.backChannel.jwtTypeExplicit -- New configuration property to 
      enable / disable explicit typing of the issued back-channel logout 
      tokens by setting the JWT type ("typ") header to "logout+jwt". Explicit
      logout token typing is a new recommendation in OpenID Connect 
      Back-Channel Logout 1.0 - draft 07, section 4.1. This is a simple measure
      to prevent mix-up of logout token JWTs with other types of JWT without 
      having to examine the JWT claims structure. Enabled by default.

      See https://openid.net/specs/openid-connect-backchannel-1_0.html

* /WEB-INF/infinispan-*-dynamodb.xml

    * Removes the default "dynamodb.region" setting of "us-east-1". The 
      purpose of this change is to enable DynamoDB configurations where the 
      AWS region is determined by the default AWS region provider chain, for 
      example by setting the "AWS_REGION" environment variable. The DynamoDB 
      store XML schema is updated to v1.19. See https://docs.aws.amazon.com/
      sdk-for-java/v1/developer-guide/java-dg-region-selection.html  


### Web API

* /.well-known/openid-configuration

    * prompt_values_supported -- New metadata field defined in Initiating 
      User Registration via OpenID Connect - draft 04. Lists the supported 
      prompt values in OpenID authentication requests. The Connect2id server 
      supports the following prompt values: none, login, consent, 
      select_account and create.
    
* /authz-sessions/rest/v3/

    * The authentication prompt (message with type "auth") receives a new 
      "create_account" member of type boolean to indicate an OpenID 
      authentication request with a prompt=create parameter.


### Resolved issues

* Sourcing of "access_token:*" claims must call the AdvancedClaimsSource SPI 
  instead of the basic ClaimsSource SPI in order to pass optional 
  "claims_data" (issue server/753, authz-store/191).


### Dependency changes

* Updates to com.nimbusds:c2id-server-sdk:4.43

* Updates to com.nimbusds:oauth2-oidc-sdk:9.35

* Updates to com.nimbusds:nimbus-jose-jwt:9.22

* Upgrades to com.nimbusds:oauth2-authz-store:17.9

* Updates to com.nimbusds:oidc-claims-source-ldap:1.6.1

* Updates to com.nimbusds:infinispan-cachestore-dynamodb:4.2

* Updates to com.amazonaws:aws-java-sdk-dynamodb:1.12.201

* Updates to com.nimbusds:c2id-server-property-source:1.0.4

* Updates to org.postgresql:postgresql:42.3.4

* Updates to org.slf4j:slf4j-api:1.7.36

* Updates to com.github.dubasdey:log4j2-jsonevent-layout:0.0.7

* Updates to com.nimbusds:token-event-publisher-aws-sqs:1.1.3

* Adds dependency to com.amazonaws:aws-java-sdk-sts:1.12.201



## 12.9 (2022-03-23)

### Configuration

* /WEB-INF/oidcProvider.properties

    * op.token.idToken.jwtType -- New configuration property to set the type 
      ("typ") header of issued ID tokens. Explicit JWT typing is a simple 
      measure to prevent mix-up of ID token JWTs with other types of JWT 
      without having to examine the JWT claims structure. Note this is a 
      non-standard feature and may result in rejection of ID tokens by 
      client libraries not able or not configured to handle typing. Typing 
      is disabled by default.

    * op.token.userinfo.jwtType -- New configuration property to set the 
      type ("typ") header of issued UserInfo JWTs. Explicit JWT typing is a 
      simple measure to prevent mix-up of UserInfo JWTs with other types of 
      JWT without having to examine the JWT claims structure. Note this is a 
      non-standard feature and may result in rejection of UserInfo JWTs by 
      client libraries not able or not configured to handle typing. Typing 
      is disabled by default.


### Web API

* /clients/

    * Adds support for a new optional "refresh_client_secret" client metadata 
      field of type boolean for use in client update requests. When the 
      "refresh_client_secret" is set to true in a client update request the 
      Connect2id server will refresh the client secret, overriding a disabled 
      op.reg.alwaysRefreshClientSecretOnUpdate configuration setting. When 
      "refresh_client_secret" is set to false or omitted the refresh of the 
      client secret will be determined by the configured op.reg.
      alwaysRefreshClientSecretOnUpdate setting.

      The intent of "refresh_client_secret" is to give registered clients a 
      direct control over how soon or often to refresh their secret. This 
      is a non-standard metadata field.


### Dependency changes

* Upgrades to com.nimbusds:oauth2-oidc-sdk:9.31

* Updates to com.nimbusds:nimbus-jwkset-loader:5.2.1

* Updates to com.nimbusds:lang-tag:1.6

* Updates to com.unboundid:unboundid-ldapsdk:6.0.4

* Updates to org.postgresql:postgresql:42.3.3



## 12.8 (2022-03-12)

### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.42

    * com.nimbusds.openid.connect.provider.spi.par.ValidatorContext -- Adds new
      getRawRequest method returning the original raw OAuth 2.0 authorisation / 
      OpenID authentication request, as received at the authorisation 
      endpoint and prior to any JAR unwrapping / resolution if JWT-secured.

    * com.nimbusds.openid.connect.provider.spi.authz.ValidatorContext -- Adds
      new getRawRequest method returning the original raw OAuth 2.0 
      authorisation / OpenID authentication request, as received at the 
      authorisation endpoint and prior to any JAR unwrapping / resolution if 
      JWT-secured.


### Resolved issues

* Fixes a vulnerability in the Connect2id server banner (splash) page that
  allowed injection of HTML or JavaScript via an invalid "Issuer" HTTP
  header. No feasible exploits found, but upgrading is generally recommended.
  The banner page also receives a Content-Security-Policy to allow only
  local content (issue server/733).

* Fixes a vulnerability at the token endpoint that allowed log injection of 
  CR and LF characters via a client_id prior to client validation. In 
  Connect2id server deployments with a plain text Log4j appender the 
  vulnerability may be exploited to compromise the integrity of the log 
  messages and potentially forge log events. The severity of the 
  vulnerability is low, upgrading is recommended (issue server/734).

* Fixes the log label for the token introspection HTTP request logging and 
  the OP6500 internal server error message (issue server/735).

* The token and UserInfo endpoints must return an HTTP 400 Bad Request with 
  an invalid_dpop_proof error when receiving a DPoP HTTP request header with 
  a header value that doesn't parse to a signed JWT. Previously the 
  Connect2id server ignored th DPoP header when JWT parsing failed (issue 
  server/736). 


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.42

* Updates to com.nimbusds:oauth2-oidc-sdk:9.28

* Updates to com.nimbusds:nimbus-jose-jwt:9.21

* Updates to com.nimbusds:common:2.48

* Updates to org.cryptomator:siv-mode:1.4.4

* Updates to net.minidev:json-smart:2.4.8



## 12.7 (2022-03-01)

### Web API

* /authz-sessions/rest/v3/

    * The DELETE call for returning an authorisation response error to the 
      OAuth 2.0 client adds support for an "error_uri" query parameter. See 
      RFC 6749, section 5.2.


* /monitor/v1/metrics

    * Adds new "authzEndpoint.invalidRequests" meter of invalid requests by 
      OAuth 2.0 clients and OpenID Connect relying parties at the OAuth 2.0 
      authorisation endpoint. Covers authorisation error responses with the 
      "invalid_request" and other codes (save for "access_denied" metered by 
      "authzEndpoint.failedSubjectAuthentications" and "authzEndpoint.
      consentDenials") as well as non-redirecting errors. 


### Resolved issues

* The authorisation session API DELETE /authz-sessions/rest/v3/{sid} call must 
  return an HTTP 400 Bad Request when illegal characters are present in a OAuth 
  2.0 error code or description, as specified in RFC 6749, section 5.2. 
  Previously illegal characters would produce a HTTP 500 Internal Server 
  Error (issue server/730).


### Dependency changes

* Updates to com.nimbusds:nimbus-jose-jwt:9.20

* Updates to com.nimbusds:oauth2-authz-store:17.8

* Updates to com.nimbusds:oidc-session-store:14.9

* Updates to com.nimbusds:common:2.46

* Updates to javax.servlet:javax.servlet-api:4.0.1

* Updates to org.apache.commons:commons-lang3:3.12.0

* Updates to javax.ws.rs:javax.ws.rs-api:2.1.1

* Updates to org.glassfish.jersey.containers:jersey-container-servlet:2.35

* Updates to com.google.code.gson:gson:2.9.0

* Updates to commons-codec:commons-codec:1.15

* Updates to io.prometheus:simpleclient:0.15.0

* Updates to io.prometheus:simpleclient_servlet:0.15.0

* Updates to io.prometheus:simpleclient_dropwizard:0.15.0

* Updates to Log4j 2.17.2



## 12.6.1 (2022-02-10)

### Resolved issues

* Fixes op.map.claims.* system property override for configuring the custom 
  scope-to-claims mapper in the single-tenant edition of the Connect2id 
  server. The multi-tenant server edition is not affected. The single-tenant 
  server edition will log at startup the configured mapping (at level INFO 
  with ID OP0080) (issue server/725).

* OpenID Connect for Identity Assurance: MinimalVerificationSpec.parse must 
  allow trust_framework set to an empty JSON object (issue oidc-sdk/385).

* OpenID Connect for Identity Assurance: The birthplace claim must allow ISO 
  3166-1 Alpha-3 country codes. (issue server/728).


### Dependency changes

* Updates to com.nimbusds:oauth2-oidc-sdk:9.25

* Updates to com.nimbusds:nimbus-jose-jwt:9.19

* Updates to org.postgresql:postgresql:42.3.2



## 12.6 (2022-01-17)

### Summary

* Upgrades OpenID Connect for Identity Assurance 1.0 support to the latest 
  implementers' draft 12 from 6 September 2021. See 
  https://openid.net/specs/openid-connect-4-identity-assurance-1_0.html

* Upgrades the AuthorizationRequestValidator and PARValidator SPIs to enable 
  read-only access to the OpenID provider / OAuth 2.0 authorisation server 
  metadata for plugin configuration and other purposes.


### Configuration

* /WEB-INF/oidcProvider.properties
  
  * op.assurance.supportedDocumentTypes -- New optional configuration 
    property listing the supported document types if OpenID Connect for 
    Identity Assurance 1.0 support is enabled. Corresponds to the 
    "documents_supported" OpenID provider metadata parameter.
  
  * op.assurance.supportedMethodsForDocuments -- New optional configuration 
    property listing the supported coarse identity verification methods for 
    evidences of type document if OpenID Connect for Identity Assurance 1.0 
    support is enabled. Corresponds to the "documents_methods_supported" 
    OpenID provider metadata parameter.
  
  * op.assurance.supportedValidationMethodsForDocuments -- New optional 
    configuration property listing the supported validation methods for 
    evidences of type document if OpenID Connect for Identity Assurance 1.0 
    support is enabled. Corresponds to the 
    "documents_validation_methods_supported" OpenID provider metadata 
    parameter.
  
  * op.assurance.supportedVerificationMethodsForDocuments -- New optional 
    configuration property listing the supported person verification methods 
    for evidences of type document if OpenID Connect for Identity Assurance 
    1.0 support is enabled. Corresponds to the 
    "documents_verification_methods_supported" OpenID provider metadata 
    parameter.
  
  * op.assurance.supportedElectronicRecordTypes -- New optional 
    configuration property listing the supported electronic record types if 
    OpenID Connect for Identity Assurance 1.0 support is enabled. 
    Corresponds to the "electronic_records_supported" OpenID provider 
    metadata parameter.
  
  * op.assurance.supportedAttachments -- New optional configuration property 
    listing the supported attachment types if OpenID Connect for Identity 
    Assurance 1.0 support is enabled. Corresponds to the 
    "attachments_supported" OpenID provider metadata parameter. Attachment 
    types: embedded, external.
  
  * op.assurance.supportedDigestAlgs -- New optional configuration property 
    listing the supported digest algorithms for external attachments if 
    OpenID Connect for Identity Assurance 1.0 support is enabled. 
    Corresponds to the "digest_algorithms_supported" OpenID provider 
    metadata parameter. If external attachments are supported must at least 
    include sha-256. 
  
  * op.assurance.supportedIDDocumentTypes -- Becomes deprecated, the 
    corresponding "id_documents_supported" OpenID provider metadata 
    parameter in no longer in use in OpenID Connect for Identity Assurance 1.0.
  
  * op.assurance.supportedIdentityVerificationMethods -- Becomes deprecated, 
    the corresponding "id_documents_verification_methods_supported" OpenID 
    provider metadata parameter is no longer in use in OpenID Connect for 
    Identity Assurance 1.0.


### Web API

* /.well-known/openid-configuration

  * documents_supported -- New metadata field introduced in draft 12 of 
    OpenID Connect for Identity Assurance 1.0. Lists the supported document 
    types. Replaces "id_documents_supported".
  
  * documents_methods_supported -- New metadata field introduced in draft 12 of 
    OpenID Connect for Identity Assurance 1.0. Lists the supported coarse 
    identity verification methods for evidences of type document. Replaces 
    "id_documents_verification_methods_supported".
  
  * documents_validation_methods_supported -- New metadata field introduced 
    in draft 12 of OpenID Connect for Identity Assurance 1.0. Lists the 
    supported validation methods for evidences of type document.
  
  * documents_verification_methods_supported -- New metadata field introduced 
    in draft 12 of OpenID Connect for Identity Assurance 1.0. Lists the 
    supported person verification methods for evidences of type document.
  
  * electronic_records_supported -- New metadata field introduced in draft 
    12 of OpenID Connect for Identity Assurance 1.0. Lists the supported 
    electronic record types.
  
  * attachments_supported -- New metadata field introduced in draft 12 of 
    OpenID Connect for Identity Assurance 1.0. Lists the supported 
    attachment types: embedded, external.
  
  * digest_algorithms_supported -- New metadata field introduced in draft 12 of 
    OpenID Connect for Identity Assurance 1.0. Lists the supported digest 
    algorithms for external attachments.


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.41

    * com.nimbusds.openid.connect.provider.spi.par.ValidatorContext -- Adds new
      getReadOnlyOIDCProviderMetadata method returning the OpenID provider / 
      Authorisation server metadata.

    * com.nimbusds.openid.connect.provider.spi.authz.ValidatorContext -- Adds 
      new getReadOnlyOIDCProviderMetadata method returning the OpenID 
      provider / Authorisation server metadata.


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.41

* Updates to com.nimbusds:oauth2-oidc-sdk:9.20.1

* Updates to com.nimbusds:oauth2-authz-store:17.7

* Updates to com.nimbusds:oidc-session-store:14.8

* Updates to com.nimbusds:content-type:2.2

* Updates to com.nimbusds:c2id-server-property-source:1.0.3

* Removes and updates selected OpenSAML 3.4.6 transitive dependencies

* Replaces javax.activation:javax.activation-api:jar:1.2.0 with jakarta.
  activation:jakarta.activation-api:jar:1.2.1

* Updates to com.nimbusds:infinispan-cachestore-sql:4.2.6

* Updates to com.zaxxer:HikariCP:4.0.3

* Updates to org.mariadb.jdbc:mariadb-java-client:2.7.3

* Updates to org.postgresql:postgresql:42.3.1

* Updates to com.nimbusds:infinispan-cachestore-dynamodb:4.1.9

* Updates to com.nimbusds:tenant-registry:6.0.1

* Updates to com.amazonaws:aws-java-sdk-dynamodb:1.12.132

* Updates to com.github.dubasdey:log4j2-jsonevent-layout:0.0.5

* Updates to AWS Java SDK 1.12.132

* Updates DropWizard to 4.1.29

* Updates Prometheus SimpleClient to 0.14.1

* Updates Log4j to 2.17.1



## 12.5.4 (2021-12-18)

### Resolved issues

* Updates Log4j to 2.17.0 to address a critical DoS vulnerability described in
  CVE-2021-45105, see https://cve.mitre.org/cgi-bin/cvename.cgi?
  name=CVE-2021-45105 (issue server/711).

### Dependency changes

* Updates Log4j to 2.17.0



## 12.5.3 (2021-12-16)

### Resolved issues

* Fixes op.checkSession.iframe and op.checkSession.cookieName configuration 
  property parsing to support Java system property override (issue server/709).


### Dependency changes

* Updates to com.nimbusds:software-statement-verifier:2.2.2

* Updates to org.apache.commons:commons-compress:1.21



## 12.5.2 (2021-12-15)

### Resolved issues

* Updates Log4j to 2.16.0 to address a critical vulnerability described in
  CVE-2021-45046, see https://cve.mitre.org/cgi-bin/cvename.cgi?
  name=CVE-2021-45046 (issue server/708).

### Dependency changes

* Updates Log4j to 2.16.0

* Updates to org.slf4j:slf4j-api:1.7.32

* Updates to com.google.code.gson:gson:2.8.9

* Updates to com.google.crypto.tink:tink:1.6.1

* Updates BouncyCastle to 1.70.

* Updates to com.unboundid:unboundid-ldapsdk:6.0.3



## 12.5.1 (2021-12-10)

### Resolved issues

* Updates Log4j to 2.15.0 to address a critical vulnerability described in 
  CVE 2021-44228, see https://cve.mitre.org/cgi-bin/cvename.cgi?
  name=CVE-2021-44228 (issue server/707).

* Logs a WARN instead of INFO for OP5114 when the 
  op.reg.allowLocalhostRedirectionURIsForTest configuration property is 
  enabled (issue server/702).

* Increases the default HTTP claims source op.httpClaimsSource.connectTimeout 
  and op.httpClaimsSource.readTimeout values from 250ms to 1000ms to prevent 
  timeouts on slow HTTP connections or slow claims sources (issue server/704).

* Updates the op.httpClaimsSource.supportedClaims documentation to explain 
  that setting the property to "*" indicates support for all claims supported 
  by the OpenID provider without explicitly listing them (issue server/703).


### Dependency changes

* Updates Log4j to 2.15.0

* Updates to com.nimbusds:oidc-claims-source-http:2.2.1



## 12.5 (2021-11-29)

### Configuration

* /WEB-INF/oidcProvider.properties

    * op.reg.allowLocalhostRedirectionURIsForTest -- New configuration property
      to allow registration of HTTP and HTTPS localhost redirection URIs for 
      the purpose of testing and developing OAuth 2.0 web application 
      clients. The default value is false (not allowed). Must not be used in 
      production!
    

### Web API

* /clients
    
    * Allows registration of a frontchannel_logout_uri with a custom URI 
      scheme. Intended to support front-channel logout notifications to 
      mobile applications (application_type=native) with a custom URI scheme.
      Previously only https URLs were allowed. The http URL scheme remains 
      disallowed.

* /authz-sessions/rest/v3/

    * Consent: The API is updated to support opting out of the additional 
      encryption of self-contained (JWT-encoded) access tokens which the 
      Connect2id server will apply when the OpenID relying party is 
      registered for pairwise subjects (with `subject_type=pairwise`) and the 
      access token subject is also set for a pairwise identifier (with
      `access_token.sub_type=PAIRWISE`). The default behaviour of the 
      Connect2id server is to always apply encryption to the JWT-encoded 
      access tokens when the OpenID relying party is registered for pairwise 
      subjects, in order to prevent exposing of information about the 
      underlying subject ID which would happen if the self-contained access 
      token was only signed. When the token subject is made pairwise there 
      is still a theoretical possibility for the OpenID relying party to 
      perform some correlation between the end-users, by observing the 
      variations of the pairwise identifier across multiple token audiences 
      (resource servers), hence the strict default Connect2id policy to also 
      encrypt access tokens with a pairwise subject. To opt out of the 
      default encryption use `access_token.encrypt=false` in the consent 
      object.

* /direct-authz/rest/v2/

    * Direct authorisation request: The API is updated to support opting out 
      of the additional encryption of self-contained (JWT-encoded) access 
      tokens which the Connect2id server will apply when the OpenID relying 
      party is registered for pairwise subjects (with 
      `subject_type=pairwise`) and the access token subject is also set for 
      a pairwise identifier (with `access_token.sub_type=PAIRWISE`). See the 
      explanation about the related authorisation session web API change. To 
      opt out of the default encryption use `access_token.encrypt=false` in the
      consent object. 


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.39

    * com.nimbusds.openid.connect.provider.spi.grants.AccessTokenSpec

        * Refactors the class for Optional<Boolean> self-contained access 
          token preference.


### Resolved issues

* Includes the JWK kid and crv (for EC keys) in the OP0102 log error message to 
  ease key identification when a server JWK fails the signing JWK validation 
  on startup (issue server/696).   


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.39

* Updates to com.nimbusds:oauth2-oidc-sdk:9.20

* Updates to com.nimbusds:c2id-server-jwkset:1.26



## 12.4 (2021-10-27)

### Summary

* Expands the cryptographic capabilities of the Connect2id server with the 
  ES256K algorithm for JWS, RSA-OAEP-384 and RSA-OAEP-512 for JWE and XC20P
  (extended nonce ChaCha20 / Poly1305) as JWE encryption method. The ES256K and 
  EdDSA (with 25519 curve) JWS algorithms can now be used to sign ID tokens, 
  UserInfo responses and authorisation responses (JARM). JWT-encoded access 
  tokens can now be signed with the ES256, ES256K, ES384 and ES512 JWS 
  algorithms.

  Specifications:

    * ES256K: https://datatracker.ietf.org/doc/html/rfc8812#section-3.1
    * RSA-OAEP-384 and RSA-OAEP-512: https://www.w3.org/TR/WebCryptoAPI/
    * XC20P: https://datatracker.ietf.
      org/doc/html/draft-amringer-jose-chacha-02#section-4.1


### Configuration

* /WEB-INF/jwkSet.json

    * Adds support for including an optional signing JSON Web Key (JWK) of type 
      (kty) EC and with curve (crv) secp256k1 for performing signatures with 
      the ES256K JWS algorithm.

* /WEB-INF/oidcProvider.properties

    * op.token.authJWSAlgs -- Adds token endpoint private_key_jwt client 
      authentication support for the ES256K JWS algorithm.
    
    * op.authz.requestJWSAlgs -- Adds request object / JAR support for the 
      ES256K JWS algorithm.
    
    * op.authz.requestJWEAlgs -- Adds request object / JAR support for the 
      RSA-OAEP-384 and RSA-OAEP-512 JWE algorithms.
    
    * op.authz.requestJWEEncs -- Adds request object / JAR support for the 
      XC20P (extended nonce ChaCha20 / Poly1305) JWE encryption method.
    
    * op.authz.responseJWSAlgs -- Adds JARM support for the ES256K and EdDSA 
      (with 25519 curve) JWS algorithms.
    
    * op.authz.responseJWEAlgs -- Adds JARM support for the RSA-OAEP-384 and 
      RSA-OAEP-512 JWE algorithms.
    
    * op.authz.responseJWEEncs -- Adds JARM support for the XC20P (extended 
      nonce ChaCha20 / Poly1305) JWE encryption method.
    
    * op.idToken.jwsAlgs -- Adds ID token support for the ES256K and EdDSA 
      (with 25519 curve) JWS algorithms.

    * op.idToken.jweAlgs-- Adds ID token support for the RSA-OAEP-384 and 
      RSA-OAEP-512 JWE algorithms.
    
    * op.idToken.jweEncs -- Adds ID token support for the XC20P (extended 
      nonce ChaCha20 / Poly1305) JWE encryption method.
    
    * op.userinfo.jwsAlgs -- Adds UserInfo JWT response support for the 
      ES256K and EdDSA (with 25519 curve) JWS algorithms.

    * op.userinfo.jweAlgs -- Adds UserInfo JWT response support for the 
      RSA-OAEP-384 and RSA-OAEP-512 JWE algorithms.
    
    * op.userinfo.jweEncs -- Adds UserInfo JWT response support for the 
      XC20P (extended nonce ChaCha20 / Poly1305) JWE encryption method.

* /WEB-INF/authzStore.properties

    * authzStore.accessToken.jwsAlgorithm -- Adds support for signing 
      self-contained (JWT) access tokens with the ES256, ES256K, ES384 and 
      ES512 JWS algorithms.
    
    * authzStore.accessToken.jweMethod -- Adds support for direct encryption of 
      self-contained (JWT-encoded) access tokens with the XC20P (extended 
      nonce ChaCha20 / Poly1305) JWE encryption method.
      

### Resolved issues

* Updates the HTTP claims source connector to include an "Accept: 
  application/json" HTTP header in the outgoing requests (issue httpcs/1).

* Updates the AS0213 log INFO message to include the type of the introspected 
  access token (issue server/692).

* Updates the SE3000 log INFO message to indicate when a X.509 certificate is
  present for a loaded server JWK (issue server/694).


### Dependency changes

* Upgrades to com.nimbusds:oauth2-oidc-sdk:9.19

* Updates to com.nimbusds:oauth2-authz-store:17.6

* Updates to com.nimbusds:oauth2-session-store:14.7
  
* Upgrades to com.nimbusds:c2id-server-jwkset:1.24

* Updates to com.nimbusds:nimbus-jwkset-loader:5.2

* Updates to com.nimbusds:nimbus-jose-jwt:9.15.2

* Updates to com.nimbusds:oidc-claims-source-http:2.2



## 12.3 (2021-09-17)

### Summary

* Supports issuer URL aliases. An issuer alias URL can be configured to migrate  
  a Connect2id server deployment seamlessly and over time from one issuer 
  identifier URL (`op.issuer`) to another. Issuer aliases can also be used when 
  an OpenID provider / OAuth 2.0 authorisation server is known by multiple 
  URLs.

  The allowed issuer aliases, if any, are configured in a new optional
  `op.issuerAliases.*` property.
 
  A Connect2id server endpoint or API will process a request under an issuer
  alias when the HTTP request header "Issuer" is present and set to a value 
  matching a configured alias. If no "Issuer" header is specified the default
  issuer configured in `op.issuer` will be assumed. The default `op.issuer` 
  will also be assumed when the "Issuer" header is explicitly set to it. If the
  "Issuer" header is set to an issuer URL that isn't configured the Connect2id 
  server will return an HTTP 400 error with a message.

  The "Issuer" header must be set by the reverse HTTP proxy or similar trusted 
  internal infrastructure. It must not be settable by client applications.
  Connect2id server deployments must scrub the incoming client application HTTP 
  requests from any "Issuer" headers.

  Issuer aliases are supported in the regular as well as the multi-tenant 
  Connect2id server edition.


### Configuration

* /WEB-INF/oidcProvider.properties

    * op.issuer.aliases.* -- New optional configuration property for setting 
      one or more issuer alias URLs of the OpenID provider / OAuth 2.0 
      authorisation server. Can be used to migrate from one issuer URL 
      (`op.issuer`) to another, or to operate an OpenID provider / OAuth 2.0 
      authorisation server that is known by multiple URLs. Blank if none.


#### Web API

* The standard OAuth 2.0 / OpenID Connect endpoints and Connect2id server 
  specific web APIs will process a request under a configured issuer alias 
  (`op.issuerAliases.*`) when the HTTP request includes an "Issuer" header set 
  to the issuer alias URL. The header value must match the configured issuer 
  alias URL exactly. If the "Issuer" header is set to an issuer URL that isn't 
  configured the Connect2id server will return an HTTP 400 "Bad Request" error 
  with an appropriate message.


### Resolved issues

* Updates log message OP6205 for reporting internal token handler errors to 
  include the client ID, authentication method and grant (issue server/693).


### Dependency changes

* Updates to com.nimbusds:tenant-manager:6.0.2

* Updates to com.nimbusds:tenant-registry:6.0

* Updates to com.nimbusds:oidc-session-store:14.6

* Updates to com.nimbusds:oauth2-authz-store:17.5

* Updates to com.nimbusds:nimbus-jose-jwt:9.14

* Updates to org.cryptomator:siv-mode:1.4.3

* Updates to com.unboundid:unboundid-ldapsdk:6.0.1

* Updates to com.google.code.gson:gson:2.8.8



## 12.2 (2021-08-26)

### Summary

* Supports issue of access tokens of type DPoP, where the token is bound to a
  private RSA or EC key generated and held by the OAuth 2.0 client, potentially
  in secure storage preventing the key's extraction. Use of the DPoP token at a  
  resource server requires proof of possession of the private key, preventing 
  the use of an accidentally or maliciously leaked token by an unauthorised 
  party. The original Bearer tokens in OAuth 2.0 offer no such protection to 
  constrain their use.

  DPoP is intended primarily for browser based applications (also commonly 
  called single page applications, or SPAs) where the alternative standard 
  method to constrain a token, by binding it to a client X.509 certificate (RFC 
  8705), is not suitable, due to the browsers' poor support for dealing with 
  client certificates and mutual TLS in JS application code.
  
  SPAs should use the WebCrypto browser API to generate the necessary private 
  keys (with disabled extraction) for signing the DPoP proofs and actual 
  signing.

  The Connect2id server accepts the following JWS algorithms for the signed 
  DPoP proof JWTs: RS256, RS384, RS512, PS256, PS384, PS512, ES256, ES256K, 
  ES384 and ES512.

  The Connect2id server will reject DPoP proof JWTs with an "iat" (issued-at
  time) that is more than 30 seconds behind or ahead of the current system 
  time. Proof JWTs with a repeated "jti" (JWT ID) will also be rejected to 
  prevent replay.
 
  See OAuth 2.0 Demonstrating Proof-of-Possession at the Application Layer 
  (draft-ietf-oauth-dpop-03).

* The Connect2id server can now be configured with longer RSA signing and 
  encryption keys, with lengths of 3072 and 4096 bits. The 2048 RSA key length 
  remains the recommended for now and also the default for in provided 
  jwkset-gen.jar tool for generating server JWK sets (the latest tool version 
  is 1.22).


### Configuration

* /WEB-INF/jwkSet.json

    * Supports RSA signing and encryption keys of lengths 3072 and 4096 bits, 
      in addition to 2048-bit RSA keys.


#### Web API

* /.well-known/openid-configuration, /.well-known/oauth-authorization-server

    * dpop_signing_alg_values_supported -- New metadata field, lists the 
      accepted JWS algorithms for the DPoP proof JWTs: RS256, RS384, RS512, 
      PS256, PS384, PS512, ES256, ES256K, ES384 and ES512.

* /token

    * Supports issue of access tokens of type DPoP for the following OAuth 2.0 
      grants:
        * authorisation code
        * refresh token
        * client credentials
        * resource owner password credentials
      
    * Supports issue of DPoP bound refresh tokens for public OAuth 2.0 clients.

* /token/introspect
  
    * Supports introspection of access tokens of type DPoP. Introspection 
      success responses for a DPoP token will have the "token_type" member set
      to "DPoP" and will include a "cnf.jkt" member set to the BASE64URL
      encoded SHA-256 thumbprint of the JWK used to sign the DPoP proof JWT.

* /userinfo

    * Supports access tokens of type DPoP. Those must be included in the 
      "Authorization" HTTP request header using the "DPoP" scheme.

* /monitor/v1/metrics

    * Adds new "dPoP.numCachedJTIs" metric of type gauge showing the number of
      locally cached DPoP proof JWT ID (jti) entries intended to ensure the 
      single use of received DPoP proofs at the token and UserInfo endpoints.


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.38

    * com.nimbusds.openid.connect.provider.spi.tokens.AccessTokenAuthorization

       * Adds default getJWKThumbprintConfirmation method to represent a DPoP
         JWK SHA-256 thumbprint confirmation.
       
    * com.nimbusds.openid.connect.provider.spi.tokens.MutableAccessTokenAuthorization
     
       * Implements AccessTokenAuthorization.getJWKThumbprintConfirmation
      
       * Adds withJWKThumbprintConfirmation setter method.

    * com.nimbusds.openid.connect.provider.spi.tokens.BaseSelfContainedAccessTokenClaimsCodec

       * Updates the base abstract codec for JWT-encoded access tokens to 
         support the "cnf.jkt" claim for DPoP.
       
    * com.nimbusds.openid.connect.provider.spi.tokens.introspection.BaseTokenIntrospectionResponseComposer

       * Updates the base abstract composer for customer token introspection
         responses support the "cnf.jkt" member for DPoP.


### Resolved issues

* Logs invalid refresh token (AS0270), refresh token request client_id - 
  encoded client mismatch (AS0274) and refresh token not permitted (AS0275) 
  events (issue authz-store/188).

* Improves the efficiency when loading issuer lookup cache entries in the 
  tenant registry (issue/tenant-registry/5).

* Fixes a bug in AuthorizationRequest.Builder(AuthorizationRequest) that
  prevented copy of OpenID authentication request parameters, which can affect
  modification of requests in AuthorizationRequestValidator and PARValidator
  SPI implementations (issue oidc-sdk/367).

* Refactors and extends handling of corrupted JSON in long_lived_authorizations
  items in a AWS DynamoDB table for get, put-if-absent, replace and remove
  operations to prevent an internal server error (HTTP 500) and clean up the
  detected corrupted items where feasible (issues authz-store/186, 187).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.38

* Upgrades to com.nimbusds:oauth2-oidc-sdk:9.15

* Upgrades to com.nimbusds:nimbus-jose-jwt:9.12.1

* Upgrades to com.nimbusds:c2id-server-jwkset:1.22

* Upgrades to com.nimbusds:oauth2-authz-store:17.4.1

* Updates to com.nimbusds:tenant-registry:5.3.4



## 12.1 (2021-07-05)

### Configuration

* /WEB-INF/oidcProvider.properties

    * op.idToken.includeJWTID -- New configuration property to enable / disable
      inclusion of a JWT ID claim ("jti") in issued ID tokens. Disabled by 
      default.


### Resolved issues

* Fixes JSON encoding issue that affected serialisation of the ~/7E character 
  into objects and for persistence since v11.3 (issue common/62).

* Fixes a bug that prevented completion of plain OAuth 2.0 authorisation 
  requests with prompt=none and resulted in a HTTP 500 server error. OpenID
  authentication requests were not affected (issue server/681).
  
* Fixes the label for the OP6520 log event at the token introspection endpoint
  (issue server/687).


### Dependency changes

* Updates to com.nimbusds:oauth2-oidc-sdk:9.9.1

* Updates to com.nimbusds:nimbus-jose-jwt:9.10.1

* Updates to com.nimbusds:oauth2-authz-store:17.2

* Updates to com.nimbusds:oidc-session-store:14.5.2

* Updates to com.nimbusds:common:2.45.4

* Updates to com.thetransactioncompany:pretty-json:1.4.3

* Adds com.google.code.gson:gson:2.8.7



## 12.0 (2021-06-09)

### Summary

* Uniform pairwise subject IDs

  Adds support for issuing pairwise subject identifiers with a uniform string 
  length which is not affected by the length of the local (system) subject 
  identifiers. This measure is intended to prevent leaking information about 
  the length of the local subject identifiers. Uniformity is configured with a 
  new "op.pairwiseSubjects.padLocalSubjectsToLength" property. It specifies a 
  length to which a local subject identifier will be padded before input to the 
  pairwise codec (SIV mode deterministic AES encryption). For uniform pairwise 
  subject lengths the property should be set to the maximum expected length of 
  the local subjects. Subject IDs longer than the configured length will result 
  in a pairwise encoding that is proportionally longer.

  Setting the property to -1 (negative integer) disables padding, which is the 
  behaviour of the pairwise codec in previous Connect2id server versions.
  
  Example pairwise IDs for local subjects "alice", "bob" and "claire" with no
  padding:

  myzM-ARj7vYBH9NOIog9Sf5xklhU_rkLenu8mOtfLv6L
  Q20o2EYNsamhlOh2RbteNp7Te7AilmMPVk3cyhmCBA
  rNW4ByxEQd06rNCLLMvH2-d1WRumrJcSrmDGA6FlvtxeGw
  
  With padding up to 10 characters:

  pxXoRPqPpLy3fBbLNV22KRCghztrFMtGV3AxDnQH_erfB4dwZ_0
  s10h_EEMtYHaNSZuMBznfvupKqC2mznkIGnmxRevffJGo4ybRrM
  uLfwVCpRUu1ltEeXqRt_Yik7SN5-yKabYwXan-xYCwgJlDZubSA

  Important: Changing the padding length will reset all previously issued
  pairwise subject identifiers!

  Configuration choices for "op.pairwiseSubjects.padLocalSubjectsToLength":
  
    - Configure a length to issue uniform pairwise subject IDs: if pairwise 
      subjects have never been issued or to benefit from the new improved 
      security. In the latter case don't forget to notify the relying parties 
      of the reset!
      
    - No padding: to ensure backward compatibility if pairwise subjects have 
      already been issued and a reset is not desirable. Also, if the local 
      subjects have a uniform length, for example when based on a GUUID which 
      has a constant length of 37 characters.

* OpenID provider managed sector IDs

  Connect2id server deployments that need to issue ID tokens and UserInfo 
  responses with pairwise (pseudonymous) subject (end-user) identifiers to two
  or more OpenID relying parties under single administrative control can now do 
  so without requiring the relying parties to host a JSON document for the 
  "sector_identifier_uri" client registration parameter. A single OpenID 
  relying party can also choose to register a "sector_identifier_uri" to ensure
  the pairwise "sub" values in the received ID tokens and UserInfo responses
  remain consistent if its registered redirection URI changes in future. See 
  OpenID Connect Dynamic Client Registration 1.0 incorporating errata set 1, 
  section 5, for an explanation of the "sector_identifier_uri" parameter and 
  its validation.
  
  For such relying parties an OpenID provider can now manage the sector IDs 
  internally, in some a database or portal, by assigning unique sector ID URNs 
  to them prior to registration. Those URNs will not get resolved and validated 
  in the same way a standard https URL in the "sector_identifier_uri" parameter 
  gets, by downloading a JSON document from the URL. Instead, the URN will be 
  used to provide the sector ID directly. The expected URN format is 
  `urn:c2id:sector_id:<id>` where `<id>` represents the sector ID assigned to a
  relying party or group of relying parties.
  
  To use this feature the new "op.reg.enableProviderManagedSectorIDs" 
  configuration property must be turned on. The client registration POST or PUT
  request must be authorised with a master API token or one-time registration 
  token with a "client-reg:set-sector-id-urn" scope value. Unauthorised 
  requests (if open registration is allowed) cannot use sector ID URNs.

* Access tokens with pairwise subjects

  The Connect2id server can now optionally issue access tokens with a subject 
  (end-user) identifier made pairwise for the token's intended audience 
  (resource server).
  
  Use tokens with a pairwise subject:

    * To keep the local (system) end-user ID confidential from the resource 
      servers, as privacy measure, especially if the subject identifier 
      represents or contains personal information, such as the user's email 
      address or social security number.
      
    * To minimise the possibility for resource servers to link or correlate the
      identity of users. The pairwise identifier for a given subject (end-user)
      in an access token will be different, unique and not-reversible for each 
      resource server (token audience).
      
  The Connect2id server computes the pairwise identifier for an access token by 
  applying strong deterministic SIV AES based encryption over the concatenation 
  of the token audience and the local subject identifier.

  pairwise_sub = SIVAES(aud|local_sub)
  
  The algorithm is identical to the one the Connect2id server uses to compute 
  pairwise subject identifiers for ID tokens and UserInfo responses and uses
  the same "subject-encrypt" AES key from the configured server JWK set.
  
  JWT-encoded as well identifier-based access tokens are supported.

  Note, to issue access tokens with a pairwise subject the token authorisation 
  must specify an explicit token audience for the intended resource server(s). 
  If the audience is a list of multiple values the first one will be used to 
  compute the pairwise subject. This enables the issue of tokens with a 
  pairwise subject where there are multiple audiences as aliases specified, for
  example the resource indicator, e.g. "https://api.example.com", as primary 
  audience and the resource server client ID, e.g. "Zoo0rah0", to enable 
  token introspection at the Connect2id server endpoint with an audience 
  restriction check on the client_id.
  
  Also note that access tokens with a pairwise subject that grant release of 
  OpenID claims at the UserInfo endpoint, as only or additional scope, need not
  specify the UserInfo endpoint or the OpenID provider issuer URL as audience.
  
  If required for audit purposes a pairwise subject identifier can be decrypted
  (reversed) with the Connect2id server's configured "subject-encrypt" JSON 
  Web Key (JWK). See https://bitbucket.org/connect2id/pairwise-subject-codec/ .
  
* Allows issue of the access and refresh tokens without a scope, to ease 
  handling of OAuth 2.0 Rich Authorisation Requests (RAR). See
  https://datatracker.ietf.org/doc/html/draft-ietf-oauth-rar-05


### Configuration

* /WEB-INF/oidcProvider.properties

    * op.pairwiseSubjects.padLocalSubjectsToLength -- New configuration 
      property to enable output of pairwise subject identifiers with a uniform 
      string length which is not affected by the length of the local (system) 
      subject identifier. Intended to prevent leaking information about the 
      length of the local subject identifiers. Uniformity is achieved by 
      padding local subject identifiers up to the specified length, which 
      should be the maximum expected length of the local subjects. Subjects 
      longer than the configured length will result in a pairwise encoding that 
      is proportionally longer.
      
      -1 (negative integer) disables padding.
      
      Important note: Changing the padding length will reset all previously 
      issued pairwise subject identifiers!

    * op.reg.enableProviderManagedSectorIDs -- New configuration property to 
      enable registration of OpenID provider managed sector identifiers for the 
      computation of pairwise subject (end-user) identifiers. A managed sector 
      ID can be registered with the "sector_identifier_uri" set to a URN with 
      the format `urn:c2id:sector_id:<id>`. The registration requires a master 
      API token or a one-time registration token with a 
      "client-reg:set-sector-id-urn" scope value. The default value is false 
      (disabled).

* /WEB-INF/infinispan-*-{mysql|postgres95|sqlserver|h2}.xml

    * Adds new "ats" (access token subject type) columns to the 
      "id_access_tokens" and "long_lived_authorizations" tables. In existing 
      Connect2id server deployments with an SQL RDBMS the server will 
      automatically add the new columns (with an appropriate default value) on 
      startup.

* /WEB-INF/infinispan-*-ldap.xml

    * Adds new "authzAccessTokenSubjectType" attribute to the 
      "oauth2IdAccessToken" and "oauth2Authz" object classes. Connect2id server
      deployments with an LDAP v3 backend database (such as OpenLDAP or OpenDJ)
      need to update the LDAP schema manually to version 1.17
      see https://bitbucket.org/connect2id/server-ldap-schemas/src/1.17/ , the
      OpenLDAP schema diff
      https://bitbucket.org/connect2id/server-ldap-schemas/diff/
      src/main/resources/oidc-authz-schema-openldap.ldif?at=1.17
      &diff1=4c78a00734bfeb9abdd4c9dec76d9fbc51216faa
      &diff2=cdfdb912753b17a0fed7c08aa500be53cb767cd7
      and the OpenDJ schema diff
      https://bitbucket.org/connect2id/server-ldap-schemas/diff/
      src/main/resources/oidc-authz-schema-opendj.ldif?at=1.17
      &diff1=4c78a00734bfeb9abdd4c9dec76d9fbc51216faa
      &diff2=cdfdb912753b17a0fed7c08aa500be53cb767cd7


### Web API

* /clients

    * Adds support for registering OpenID relying parties for pairwise subject
      identifiers where the "sector_identifier_uri" parameter is a URN for a 
      sector ID that is managed by the OpenID provider. The expected URN format 
      is `urn:c2id:sector_id:<id>`. The OpenID provider must manage the
      assignment of the URN sector IDs to relying parties prior to registration 
      and ensure that the ID stays unique for a given OpenID relying party,
      unless the sector ID is allowed to be shared by two more relying parties,
      typically when those are under single administrative control. To register 
      URN sector IDs the "op.reg.enableProviderManagedSectorIDs" configuration 
      property must be enabled. The registration must be authorised with a 
      master API token or a one-time registration token with a 
      "client-reg:set-sector-id-urn" scope value.
    
    * Will return an "invalid_client_metadata" OAuth 2.0 error with a suitable
      description message when the client registration request is not 
      authorised for a "preferred_client_id", "preferred_client_secret" or 
      custom "data" parameter. Previously those parameters will be silently
      scrubbed from the registration request if not authorised (by means of the
      master API token or a specific one-time registration token).

* /token

    * Supports issue of access tokens with a pairwise subject identifier 
      encoded for the token audience as sector identifier. If the token has 
      multiple audiences the first audience value in the list is taken as the 
      sector identifier. Access tokens for releasing OpenID claims at the 
      UserInfo endpoint that have pairwise subject need not include the 
      UserInfo endpoint or the OpenID issuer URL in its audience.
  
* /token/introspect

    * Supports introspection of access tokens with a pairwise subject 
      identifier. The standard "sub" (subject) field will be set to the 
      pairwise identifier.
      
* /token/revoke

    * Supports revocation of access tokens with a pairwise subject identifier.

* /userinfo

    * Supports access tokens with a pairwise subject identifier. Note, access
      tokens for OpenID claims that have a pairwise subject identifier need not
      include the UserInfo endpoint or the OpenID issuer URL in its audience.

* /authz-sessions/rest/v3/

    * Consent prompt: Adds new optional "sub_type" member of type string with 
      values "PUBLIC" (default) or "PAIRWISE" to the "access_token" JSON 
      object. When set to "PAIRWISE" the Connect2id server will issue an 
      access token with a pairwise subject identifier. This requires the 
      consent to specify a token "audience" (if multiple audience values are
      set the first in the list will used to compute the pairwise identifier).
      
    * Consent: The consent can now specify an empty or null "scope" to enable   
      handling of Rich Authorisation Requests (RAR). In this case the issued
      access and refresh tokens will not contain a scope. Previously a consent 
      PUT with an empty scope would cause an "access_denied" OAuth error to be 
      returned to the client. Integrations must now explicitly trigger an 
      "access_denied" OAuth 2.0 error via the authorisation session web API.
      
* /direct-authz/rest/v2/

    * Direct authorisation request: Adds new optional "sub_type" member of type 
      string with values "PUBLIC" (default) or "PAIRWISE" to the "access_token" 
      JSON object. When set to "PAIRWISE" the Connect2id server will issue an
      access token with a pairwise subject identifier. This requires the
      request to specify a token "audience" (if multiple audience values are
      set the first in the list will used to compute the pairwise identifier).
      
* /authz-store/rest/v3/
  
    * OAuth 2.0 / OpenID Connect authorisation object: Adds new optional "ats"
      (access token subject type) member of type string with values "PUBLIC" 
      (default) or "PAIRWISE".


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.36

* com.nimbusds.openid.connect.provider.spi.tokens.AccessTokenAuthorization

    * Adds new getSubjectType method to return the SubjectType for the
      access token (PUBLIC or PAIRWISE). The default implementation returns
      null (not specified).

    * Adds new getLocalSubject method to return the local (system) subject
      identifier for an access token since the existing getSubject can return a
      pairwise identifier. The default implementation calls getSubject for a
      PUBLIC access token subject, else returns null.

* com.nimbusds.openid.connect.provider.spi.tokens.MutableAccessTokenAuthorization

    * Adds new getSubjectType and withSubject methods for getting / setting the
      access token subject type (PUBLIC or PAIRWISE).

    * Adds new getLocalSubject and withLocalSubject methods for getting /
      setting the access token local subject identifier.
      
* com.nimbusds.openid.connect.provider.spi.grants.AccessTokenSpec

    * Adds support for specifying an access token subject type (PUBLIC or 
      PAIRWISE). When set to PAIRWISE the access token must specify an 
      audience (if multiple audience values are set the first in the list will 
      used to compute the pairwise identifier).
      
* com.nimbusds.openid.connect.provider.spi.tokens.introspection.TokenIntrospectionResponseComposer

    * TokenIntrospectionResponseComposer SPI implementations can access the 
      local (system) subject of access tokens with a pairwise subject 
      identifier via the AccessTokenAuthorization.getLocalSubject method.
      
* com.nimbusds.openid.connect.provider.spi.tokens.SelfContainedAccessTokenClaimsCodec

    * Connect2id server deployments with a SelfContainedAccessTokenClaimsCodec
      SPI implementation and which issue access tokens with pairwise subject
      identifiers should provide encoding / decoding for the  
      AccessTokenAuthorization.getSubjectType and getLocalSubject methods.


### Resolved issues

* Fixes leading JSON array bracket output for a GET on the clients endpoint 
  with no registered clients. The bug was introduced in 11.6.1 (issue 
  server/679).

* Enforces a string length limit of 10K chars when parsing JWT headers (after 
  the BASE64URL decoding). The 10K chars should be sufficient to accommodate 
  JWT headers with an X.509 certificate chain in the "x5c" header parameter 
  (issue nimbus-jose-jwt/424).

* Prevents StackOverflowError when parsing a JWT header with a very large 
  number of nested JOSE objects (issue nimbus-jose-jwt/425).

* Moves validation of configured signing RSA and EC keys from JWT issue time to 
  Connect2id server startup to improve performance (issue server/673).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.36

* Upgrades to com.nimbusds:oauth2-oidc-sdk:9.9
  
* Upgrades to com.nimbusds:nimbus-jose-jwt:9.10

* Upgrades to com.nimbusds:c2id-server-jwkset:1.19

* Upgrades to com.nimbusds:oauth2-authz-store:17.1.1
  
* Updates to com.nimbusds:oidc-session-store:14.5.1

* Upgrades to com.nimbusds:c2id-server-ldap-schemas:1.17

* Updates Infinispan to 9.4.23.Final.

* Updates to org.cryptomator:siv-mode:1.4.2

* Updates to com.google.crypto.tink:tink:1.6.0

* Updates to com.unboundid:unboundid-ldapsdk:6.0.0



## 11.6.2 (2021-05-21)

### Resolved issues

* Fixes the HTTP 401 error response for an HTTP GET /clients request with an
  invalid master access token. The bug was introduced in 11.6.1 (issue
  server/668).

* Fixes bug introduced in 11.3 (2021-03-31) that allowed OpenID authentication
  requests with response_type=id_token or response_type=id_token token to pass
  without a nonce (issue oidc-sdk/363).

* Updates the logout endpoint OP2711 log INFO message that an ID token hint is
  required when the RP requests a post-logout redirection (issue server/671).


### Dependency changes

* Updates to com.nimbusds:oauth2-oidc-sdk:9.5.2

* Updates to com.nimbusds:nimbus-jose-jwt:9.9.3

* Updates to Infinispan 9.4.22.Final

* Updates to com.google.crypto.tink:tink:1.5.0 



## 11.6.1 (2021-05-03)

### Resolved issues

* Fixes missing output of the "resource" parameter (RFC 8707) as URI string 
  list in the authorisation session API objects for OpenID authentication 
  requests. Plain OAuth 2.0 authorisation requests were not affected (issue 
  server/664).
  
* Switches HTTP GET /clients output to streaming to conserve memory when 
  reading a large number of OAuth 2.0 client registrations (issue server/665). 

* Improves logging of HTTP 302 errors at the client registration endpoint by 
  including additional details in the log message at INFO level, reduces the 
  number of log statements to log a condition (issue server/667).


### Dependency changes

* Upgrades to com.nimbusds:oauth2-oidc-sdk:9.5



## 11.6 (2021-04-27)

### Summary

* Adds a set of new Connect2id server configuration properties for setting up
  OAuth 2.0 servers conforming to version 2021-03-12 of the FAPI 1.0 Advanced 
  security profile. See Financial-grade API Security Profile 1.0 - Part 2: 
  Advanced (2021-03-12).
  
* Adds configuration properties for causing selected OAuth 2.0 authorisation 
  request parameters, including custom parameters, to appear in the 
  authentication or consent prompt in the authorisation web API. Intended to
  save HTTP GET calls to the authorisation session resource when access to 
  those parameters is needed.  


### Configuration

* /WEB-INF/oidProvider.properties

    * op.authz.alwaysRequireRedirectURI -- New configuration property to 
      specify whether the redirect_uri parameter is required for all 
      authorisation requests. The default value is false (required only for 
      OpenID authentication requests).
      
    * op.authz.alwaysRequireSignedRequestJWT -- New configuration property to 
      specify whether a JWS signed request JWT passed inline via "request" or 
      by URL reference via "request_uri" will be required for all authorisation 
      requests. The default value is false (not required unless the client is 
      explicitly registered for it).

    * op.authz.requireRequestJWTNotBefore -- New configuration property to
      specify whether received request object JWTs must include a not before
      (nbf) claim. The default value is false.
      
    * op.authz.maxLifetimeRequestJWTExpiration -- New configuration property to
      specify the maximum accepted lifetime in seconds of an expiration (exp) 
      claim in request JWTs. The lifetime is computed from the not before (nbf) 
      claim if present, otherwise from the current time. The default value is 
      -1 (not specified).

    * op.authz.maxAgeRequestJWTNotBefore -- New configuration property to 
      specify the maximum accepted age in seconds of a not before (nbf) claim 
      in request JWTs. The default value is -1 (not specified).
      
    * op.authz.alwaysRequireSignedResponse -- New configuration property to  
      specify whether all authorisation requests must specify a JWT-secured 
      response (JARM) or a "response_type" that includes an "id_token" to serve 
      as a detached signature. The default value is false.

    * op.authz.requestParamsInAuthPrompt -- New configuration property to 
      specify selected OAuth 2.0 authorisation request parameters to include in
      the authentication prompt, in a JSON object named "request". No 
      parameters are included by default.
    
    * op.authz.requestParamsInConsentPrompt -- New configuration property to
      specify selected OAuth 2.0 authorisation request parameters to include in
      the consent prompt, in a JSON object named "request". No parameters are 
      included by default.


### Web API

* /authz-sessions/rest/v3/

    * Authentication prompt: Adds new optional "request" member of type JSON 
      object to the authentication prompt ("auth"), to include selected 
      parameters from the OAuth 2.0 authorisation / OpenID authentication 
      request. The new configuration property op.authz.requestParamsInAuthPrompt 
      determines what parameters to include. Intended to replace a GET call to 
      the authorisation session resource for obtaining selected request 
      parameters during authentication.
      
    * Consent prompt: Adds new optional "request" member of type JSON object to 
      the consent prompt ("consent"), to include selected parameters from the 
      OAuth 2.0 authorisation / OpenID authentication request. The new 
      configuration property op.authz.requestParamsInConsentPrompt determines 
      what parameters to include. Intended to replace a GET call to the 
      authorisation session resource for obtaining selected request 
      parameters during consent. 
    
      
### Resolved issues

* The "resource" parameter (RFC 8707) as URI string list must be included in 
  the authorisation session object under "auth_req", fixes regression bug 
  (issue serer/658).
  
* The "prompt" parameter as string list must be included in the authorisation 
  session object under "auth_req" for plain OAuth 2.0 requests (custom 
  Connect2id server feature) (issue serer/660).


### Dependency changes

* Upgrades to com.nimbusds:oauth2-oidc-sdk:9.4.1

* Updates to com.nimbusds:nimbus-jose-jwt:9.9

* Updates to com.nimbusds:oauth2-authz-store:16.7.3

* Updates to com.nimbusds:oidc-session-store:14.4.4

* Updates to com.nimbusds:infinispan-cachestore-sql:4.2.5

* Updates to com.nimbusds:infinispan-cachestore-dynamodb:4.1.7

* Updates to com.nimbusds:tenant-manager:5.0.2

* Updates to com.nimbusds:tenant-registry:5.3.3

* Updates to net.minidev:json-smart:2.4.6



## 11.5 (2021-04-14)

### Configuration

* /WEB-INF/infinispan-stateless-redis-dynamodb.xml -- New Infinispan 
  configuration file for storing short-lived and cached data in Redis, and 
  long-lived data in DynamoDB. Long-lived data in DynamoDB can be transparently 
  cached by turning the optional Amazon DynamoDB Accelerator (DAX) for the 
  DynamoDB tables.
  
  This Infinispan configuration is suitable for single-region deployments in 
  AWS as well as multi-region deployments where only replication of long-lived 
  data in DynamoDB via the "global-tables" feature is required.
  
  This Infinispan configuration is an alternative to the existing available
  "infinispan-stateless-dynamodb.xml" configuration where the long-lived as 
  well as the short-lived and cached data is stored in DynamoDB. Both types of
  data in that configuration can be replicated via the "global-tables" feature.

* /WEB-INF/cors.properties

    * cors.enable -- New configuration property for disabling the CORS Filter.
      If `false` the CORS Filter is disabled and will pass all HTTP request and
      response headers unmodified. The CORS Filter can be disabled if the 
      Connect2id server is provisioned with a reverse proxy handling CORS. The
      default value is `true` enabling the CORS Filter to process cross-domain
      requests according to its configuration.


### Resolved issues

* Disables access to external entities in XML parsing in the OAuth 2.0 SDK 
  SAML2AssertionValidator, closing a potential vulnerability when processing
  OAuth 2.0 grants of type SAML 2.0 bearer assertion 
  (urn:ietf:params:oauth:grant-type:saml2-bearer). The exchange of SAML 2.0 
  bearer assertions for OAuth access tokens is not enabled by the Connect2id
  out of the box and requires a plugin. Deployments that have implemented such 
  a plugin for the SelfIssuedSAML2GrantHandler or ThirdPartySAML2GrantHandler 
  should upgrade (issue oidc-sdk/356).


### Dependency changes

* Updates to com.nimbusds:oauth2-oidc-sdk:9.3.2

* Updates to com.nimbusds:nimbus-jose-jwt:9.8.1

* Updates to com.nimbusds:nimbus-jwkset-loader:5.1

* Updates to com.nimbusds:oidc-session-store:14.4.2

* Updates to com.nimbusds:oauth2-authz-store:16.7.2

* Updates to com.nimbusds:common:2.45.1

* Updates to com.nimbusds:tenant-manager:5.0.1

* Updates to com.nimbusds:tenant-registry:5.3.1

* Updates to com.nimbusds:infinispan-cachestore-sql:4.2.4

* Updates to com.nimbusds:infinispan-cachestore-dynamodb:4.1.6

* Updates to net.minidev:json-smart:2.4.2

* Updates to com.thetransactioncompany:cors-filter:2.10

* Updates to com.nimbusds:software-statement-verifier:2.2.1



## 11.4 (2021-04-01)

### Summary

* Enables the following OAuth 2.0 grants in the multi-tenant Connect2id server
  edition:
  
    * The resource owner password credentials grant (password).
      
    * The JWT bearer assertion grant 
      (urn:ietf:params:oauth:grant-type:jwt-bearer), as defined in RFC 7523.
      
    * The SAML 2.0 bearer assertion grant 
      (urn:ietf:params:oauth:grant-type:saml2-bearer), as defined in RFC 7523.

  The client credentials grant has been available in the multi-tenant 
  Connect2id server since v7.7.1.


### SPI

* The following OAuth 2.0 grant handler SPIs become supported in the 
  multi-tenant Connect2id server edition:
  
    * PasswordGrantHandler
      
    * SelfIssuedJWTGrantHandler
      
    * ThirdPartyJWTGrantHandler
      
    * SelfIssuedSAML2GrantHandler
      
    * ThirdPartySAML2GrantHandler



## 11.3 (2021-03-31)

### Summary

* Upgrades to the AuthorizationRequestValidator and PARValidator SPIs to allow 
  for initialisation and shutdown code.
  
* Upgrades the Software Statement Verifier plugin (for the 
  RegistrationInterceptor SPI) to support the configuration of scope rules 
  based on JSON Path expressions. Intended for use in Open Banking.

* Upgrades the JSON serialisation in the Connect2id server.


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.31

* com.nimbusds.openid.connect.provider.spi.authz.AuthorizationRequestValidator

    * Lets the SPI extend Lifecycle which has default init, isEnabled and 
      shutdown methods.

      See https://www.javadoc.io/doc/com.nimbusds/c2id-server-sdk/4.31/
      com/nimbusds/openid/connect/provider/spi/authz/
      AuthorizationRequestValidator.html

* com.nimbusds.openid.connect.provider.spi.par.PARValidator

    * Lets the SPI extend Lifecycle which has default init, isEnabled and
      shutdown methods.

      See https://www.javadoc.io/doc/com.nimbusds/c2id-server-sdk/4.31/
      com/nimbusds/openid/connect/provider/spi/par/PARValidator.html


### Resolved issues

* Corrupted persisted long-lived authorisation records should be treated as 
  missing record and not result in a 500 Internal Server Error. Corrupted 
  entries are logged under AS0267 (issue authz-store/183).
  
* Corrupted persisted revocation journal entries should be treated as missing 
  entry and not result in a 500 Internal Server Error. Corrupted entries are 
  logged under AS0271 (issue authz-store/182). 

* Log uniform INFO messages on failed client authentication at the token 
  (OP6203), token introspection (OP6512), token revocation (OP6412) and PAR 
  (OP6203) endpoints (issue server/653).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.31

* Updates to com.nimbusds:oauth2-oidc-sdk:9.3

* Updates to com.nimbusds:oauth2-authz-store:16.7.1

* Updates to com.nimbusds:oidc-session-store:14.4.1

* Upgrades to com.nimbusds:common:2.45

* Updates to com.unboundid:unboundid-ldapsdk:5.1.4
  
* Updates to com.thetransactioncompany:pretty-json:1.4.1

* Updates to net.minidev:json-smart:2.3

* Adds com.jsoniter:jsoniter:0.9.23

* Updates to com.nimbusds:software-statement-verifier:2.2



## 11.2 (2021-03-07)

### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.30

* com.nimbusds.openid.connect.provider.spi.authz.AuthorizationRequestValidator

    * New SPI for performing additional custom validation as well as  
      modification of received OAuth 2.0 authorisation / OpenID authentication 
      requests. The validator has access to the registered client information 
      for the client_id in the authorisation request. If the validator rejects 
      the request it can set a standard or custom error code and also 
      optionally disable redirection back to the client redirect_uri.

      The loading of an AuthorizationRequestValidator SPI implementation is 
      logged at INFO level under OP2113. The cause for rejection of a request 
      is also logged at INFO level, under OP2114.
      
      Note, to perform additional custom validation of pushed authorisation 
      requests use the PARValidator SPI.

      See https://www.javadoc.io/doc/com.nimbusds/c2id-server-sdk/4.30/
      com/nimbusds/openid/connect/provider/spi/authz/
      AuthorizationRequestValidator.html

* com.nimbusds.openid.connect.provider.spi.par.PARValidator

    * Adds new PARValidator.validatePushedAuthorizationRequest method that also 
      enables optional modification of received Pushed Authorisation Request 
      (PAR). This method has a default implementation that calls the existing 
      validate only method. Existing plugins need not be updated.

      See https://www.javadoc.io/doc/com.nimbusds/c2id-server-sdk/4.30/
      com/nimbusds/openid/connect/provider/spi/par/PARValidator.html

* com.nimbusds.openid.connect.provider.spi.grants.SelfIssuedJWTGrantHandler

    * Upgrades the included OAuth 2.0 self-issued JWT bearer grant handler 
      plugin, see 
      https://bitbucket.org/connect2id/self-issued-jwt-bearer-grant-handler .

        * New op.grantHandler.selfIssuedJWTBearer.accessToken.includeClientMetadataFields
          configuration property to specify names of client metadata fields to
          include in the optional access token `data` field, empty set if none.
          To specify a member within a field that is a JSON object member use
          dot (`.`) notation.

        * The op.grantHandler.selfIssuedJWTBearer.enable configuration property 
          receives a default value `false` (disabled).

        * Lets op.grantHandler.selfIssuedJWTBearer.accessToken.audienceList
          also apply to identifier-based access tokens.

        * Makes the /WEB-INF/selfIssuedJWTBearerHandler.properties 
          configuration file optional.


### Resolved issues

* Adjusts DynamoDB item output of the "clm" and "cls" attributes to the 
  long_lived_authorizations table to prevent false HMAC check errors when a
  dynamodb.hmacSHA256Key is configured (issue authz-store/179).
  
* Updates revocation_journal DynamoDB parsing to include the illegal string on 
  a parse exception (issue authz-store/180).
  
* Updates OP2209 logging to include the JSON string in the exception message 
  when ID token minting fails due to an "aud" (audience) parse error (issue 
  server/644).
  
* Authorisation and token requests with a parameter included more than once, 
  save for "resource", must result in a invalid_request error (issue 
  oidc-sdk/345).
  
* Fixes new RSASSASigner(RSAKey) conversion to PrivateKey with a Hardware 
  Security Module (HSM) (issue nimbus-jose-jwt/404).
  
* Updates JSON parsing in the OAuth 2.0 SDK to catch non-documented and 
  unexpected exceptions (issue oauth-oidc-sdk/347).
  
* Allows OAuth 2.0 client metadata "software_version" of type JSON number and
  converts it to a JSON string in new and updated client registrations. This is
  done to accommodate non RFC 7591 compliant dynamic client registrations in 
  the UK Open Banking profile (issue oauth-oidc-sdk/348).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.30

* Updates to com.nimbusds:oauth2-authz-store:16.5.2

* Updates to com.nimbusds:oauth2-oidc-sdk:9.2.2

* Updates to com.nimbusds:nimbus-jose-jwt:9.6.1

* Updates to com.nimbusds:oauth-jwt-self-issued-grant-handler:1.1



## 11.1.1 (2021-02-19)

### Resolved issues

* Fixes bug that caused DynamoDB table creation without a range (sort) key to 
  fail in single-tenant Connect2id server 1.11 instances (issue server/636). 
* Adds extra logging around DynamoDB table creation to include the resolved 
  table spec (issue server/638).
* Empty or blank DynamoDB apply-range-key must return null in config API (issue 
  ispn-dynamodb/14).
* Require non-empty DynamoDB range key value when a range key is set (issue 
  ispn-dynamodb/15).
* Log stored HMAC, computed HMAC and original item when an invalid HMAC is
  detected in the DynamoDB connector (issue ispn-dynamodb/17).


### Dependency changes

* Updates to com.nimbusds:infinispan-cachestore-dynamodb:4.1.5



## 11.1 (2021-02-17)

### Summary

* Support for AWS DynamoDB in the multi-tenant edition of the Connect2id 
  server. Persisted tenant specific objects are isolated in their DynamoDB 
  tables by means of a range (sort) key named "tid".

* Reduces the footprint of AWS related dependencies by switching from the AWS
  Java SDK bundle to smaller service specific dependencies. The size of the 
  final WAR file is reduced from 244 megabytes to 68 megabytes. If an SPI 
  implementation (custom plugin) requires an AWS dependency previously 
  available through the AWS Java SDK bundle it now needs to be explicitly 
  included as a dependency.


### Configuration

* /WEB-INF/infinispan-multitenant-stateless-dynamodb.xml -- New Infinispan 
  configuration file for the multi-tenant edition of the Connect2id server with
  an AWS DynamoDB backend. The standard DynamoDB configuration properties from
  the regular Connect2id server edition apply, save for the
  "dynamodb.applyRangeKey" and "dynamodb.rangeKeyValue" properties that have no 
  effect.


### Resolved issues

* Fixes typo in the "invalid_client" error description (issue server/634).


### Dependency changes

* Updates to com.nimbusds:oauth2-authz-store:16.5

* Updates to com.nimbusds:oidc-session-store:14.3

* Updates to com.nimbusds:tenant-registry:5.3

* Upgrades to com.nimbusds:infinispan-cachestore-dynamodb:4.1.1

* Updates to com.nimbusds:jgroups-dynamodb-ping:1.2.5

* Replaces com.amazonaws:aws-java-sdk-bundle with the narrower 
  aws-java-sdk-dynamodb and aws-java-sdk-s3:1.11.955
  
* Updates to com.nimbusds:software-statement-verifier:2.1.1



## 11.0 (2021-02-09)

### Summary

* Supports OAuth 2.0 client authentication with a client X.509 certificate 
  issued by a trusted Certificate Authority (CA), as specified in OAuth 2.0 
  Mutual-TLS Client Authentication and Certificate-Bound Access Tokens (RFC 
  8705), section 2.2. 

  When registering a client for the "tls_client_auth" method the 
  "tls_client_auth_subject_dn" metadata parameter must be set to the expected 
  subject distinguished name (DN) of the client certificate. Other client 
  metadata parameters defined in RFC 8705 for identifying the certificate 
  subject are not presently supported.
  
  The client certificate is to be validated at a terminating TLS reverse proxy
  which must pass a successfully validated certificate to the Connect2id server 
  in the HTTP security header set by the existing "op.tls.clientX509CertHeader" 
  configuration property.

  Note, due to limitations of the commonly available TLS termination software, 
  configuring simultaneous validation of CA-issued client certificates 
  as well as acceptance of self-signed client certificates is normally not 
  possible. Because of that a Connect2id server deployment can be configured to 
  support either "tls_client_auth" or "self_signed_tls_client_auth" from RFC 
  8705, but not both.

* Supports the JWT-secured authorisation response mode (JARM). An OAuth 2.0 
  client can utilise JARM to receive signed and optionally encrypted 
  authorisation responses. All standard response modes in JARM are supported: 
  "query.jwt", "fragment.jwt", "form_post.jwt" and the "jwt" shorthand. 
  
  A client can request an RS256 signed authorisation response by setting the 
  optional "response_mode" authorisation request parameter to "jwt" or a more
  specific value, such as "query.jwt". If the client is registered with an 
  "authorization_signed_response_alg" metadata parameter all authorisation 
  responses will be signed with the specified JWS algorithm, even if there is
  no explicit request for JARM in the "response_mode" authorisation request
  parameter. If the client is also registered with the
  "authorization_encrypted_response_alg" and
  "authorization_encrypted_response_enc" metadata parameters the signed 
  authorisation response will be additionally encrypted.
  
  See Financial-grade API: JWT Secured Authorization Response Mode for OAuth 
  2.0 (JARM), draft 02 from 2018-10-17.

* Adds a codec plugin interface (SPI) for persisting client secrets in a 
  encrypted, hashed or otherwise encoded form. The codec interface also enables 
  import of OAuth 2.0 client registrations via the custom 
  "preferred_client_secret" metadata field where the secret is passed in a 
  hashed form, for example using the BCrypt, SCrypt, Argon2 or another hash 
  algorithm.
  
* Supports seamless "client_secret" rollover. After an update of a client's 
  secret via the client registration endpoint the previous secret will remain 
  valid for client authentication purposes for a period of 30 minutes. 
  HMAC-secured ID tokens hints ("id_token_hint" with JWS HS256, HS384 or HS512) 
  will also be checked against the previous replaced "client_secret".

* Upgrades the OAuth 2.0 / OpenID Connect SDK to major release 9.x which in 
  turn upgrades to Nimbus JOSE+JWT 9.x. The objective of the Nimbus JOSE+JWT 
  9.0 release was to shade the JSON Smart dependency and make it optional. 
  Plugged SPI implementations that rely on Nimbus JOSE+JWT 8.x and earlier 
  interfaces and classes may be affected and should be recompiled / rebuilt and
  if necessary, modified.
  
* Upgrades the schemas of backend RDBMS and LDAPv3 stores to support the client 
  metadata fields required for "tls_client_auth" and JARM. See the 
  configuration section below for more information.


### Configuration

* /WEB-INF/oidProvider.properties

    * op.authz.responseModes -- Adds support for the "query.jwt", 
      "fragment.jwt", "form_post.jwt" and "jwt" (shorthand) response modes from 
      JARM.

    * op.authz.responseJWSAlgs -- New configuration property, sets the accepted 
      JWS algorithms for signed OAuth 2.0 authorisation responses (JARM). 
      Supported JWS algorithms: HS256, HS384, HS512, RS256, RS384, RS512, 
      PS256, PS384, PS512, ES256, ES384 and ES512. 

    * op.authz.responseJWEAlgs -- New configuration property, sets the accepted 
      JWE algorithms for encrypted OAuth 2.0 authorisation responses (JARM). 
      Note, encryption is always applied after signing. Supported JWE 
      algorithms: RSA-OAEP-256, RSA-OAEP (use no longer recommended), RSA1_5 
      (use no longer recommended), ECDH-ES, ECDH-ES+A128KW, ECDH-ES+A192KW, 
      ECDH-ES+A256KW and dir.
      
    * op.authz.responseJWEEncs -- New configuration property, sets the accepted 
      JWE content encryption methods for encrypted OAuth 2.0 authorisation 
      responses (JARM). Supported JWE methods: A128CBC-HS256, A192CBC-HS384, 
      A256CBC-HS512, A128GCM, A192GCM and A256GCM
      
    * op.token.authMethods -- Adds support for "tls_client_auth", PKI client
      authentication where the client must include a CA-issued client X.509 
      certificate in requests. Note, the "tls_client_auth" and 
      "self_signed_tls_client_auth" methods cannot be enabled simultaneously.
      
    * op.tls.clientX509CertHeader -- The existing Connect2id server 
      configuration property for specifying an HTTP header name for receiving 
      validated client X.509 certificates from the TLS proxy will also apply to
      the new "tls_client_auth" method. The configuration property was 
      originally introduced in Connect2id server 6.12 to facilitate the  
      "self_signed_tls_client_auth" method.

* /WEB-INF/infinispan-*-{mysql|postgres95|sqlserver|h2}.xml

    * Adds new "tls_client_auth_subject_dn", "tls_client_auth_san_dns",
      "tls_client_auth_san_uri", "tls_client_auth_san_ip", 
      "tls_client_auth_san_email", "authorization_signed_response_alg",
      "authorization_encrypted_response_alg" and 
      "authorization_encrypted_response_enc" columns to the "clients" table. In 
      existing Connect2id server deployments with an SQL RDBMS the server will 
      automatically add the new columns (with an appropriate default value) on 
      startup.

* /WEB-INF/infinispan-*-ldap.xml

    * Adds new "oauthTLSClientAuthSubjectDN", "oauthTLSClientAuthSANDNS",
      "oauthTLSClientAuthSANURI", "oauthTLSClientAuthSANIP", 
      "oauthTLSClientAuthSANEmail", "oauthAuthorizationResponseJWSAlg",
      "oauthAuthorizationResponseJWEAlg" and "oauthAuthorizationResponseJWEEnc"
      attributes to the "oauthClientMetadata" object class. Connect2id server
      deployments with an LDAP v3 backend database (such as OpenLDAP or OpenDJ)
      need to update the LDAP schema manually to version 1.15
      see https://bitbucket.org/connect2id/server-ldap-schemas/src/1.15/ , the
      OpenLDAP schema diff
      https://bitbucket.org/connect2id/server-ldap-schemas/diff/
      src/main/resources/oidc-client-schema-openldap.ldif?at=1.15
      &diff1=c8ba0c6a092c409e43b9efe7360cd76a460a2b95
      &diff2=cb3676cefe8bbccbcdb4352cc707a53ef53ae28a
      and the OpenDJ schema diff
      https://bitbucket.org/connect2id/server-ldap-schemas/diff/
      src/main/resources/oidc-client-schema-opendj.ldif?at=1.15
      &diff1=c8ba0c6a092c409e43b9efe7360cd76a460a2b95
      &diff2=cb3676cefe8bbccbcdb4352cc707a53ef53ae28a

* /WEB-INF/oidcClientInfoMap.json -- File removed, switched to programmatic 
  configuration.


### Web API

* /.well-known/openid-configuration, /.well-known/oauth-authorization-server

    * response_modes_supported -- Will include the supported OAuth 2.0 response
      modes for JARM if enabled -- "query.jwt", "fragment.jwt", "form_post.jwt" 
      and "jwt".

    * authorization_signing_alg_values_supported -- New optional JSON array
      listing the supported JWS algorithms for signed OAuth 2.0 authorisation 
      responses (JARM).
      
    * authorization_encryption_alg_values_supported -- New optional JSON array
      listing the supported JWE algorithms for encrypted OAuth 2.0 
      authorisation responses (JARM). 
      
    * authorization_encryption_enc_values_supported -- New optional JSON array
      listing the supported JWE content encryption methods for encrypted OAuth 
      2.0 authorisation responses (JARM).

* /clients

    * Supports the "authorization_signed_response_alg", 
      "authorization_encrypted_response_alg" and 
      "authorization_encrypted_response_enc" client metadata parameters from
      JARM.
      
    * Supports registration of clients for PKI mutual-TLS client authentication 
      ("tls_client_auth") with the "tls_client_auth_subject_dn" metadata
      parameter used to set the expected subject DN in the client's 
      certificate. 

    * Updating a "client_secret" will cause the previous value to remain valid
      for client authentication purposes for another 30 minutes, to facilitate
      seamless rollover.


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.27.1

* com.nimbusds.openid.connect.provider.spi.secrets.ClientSecretStoreCodec -- 
  New Service Provider Interface (SPI) for encoding OAuth client secrets before 
  persisting them to storage. Can be used to symmetrically encrypt (e.g. with 
  AES) or to hash secrets (e.g. with SCrypt, BCrypt, Argon2) before committing 
  them to storage. Note, OAuth clients registered for "client_secret_jwt" 
  authentication where the secret must be available in plaintext to perform 
  HMAC must not be hashed. This also applies to secrets which may otherwise 
  require the plain secret to be available for decoding, for example to 
  facilitate symmetric encryption of ID tokens or UserInfo.
  
  The supplied SecretCodecContext provides access to the Connect2id server JWK 
  set to retrieve any configured symmetric keys for the client secret 
  encryption, as well as the client metadata to determine the registered client 
  authentication method.

  Implementations must be thread-safe.


### Resolved issues

* The Connect2id server should also include the previous replaced client_secret 
  when validating an id_token_hint secured with JWS HS256, HS384 and HS512 
  (issue server/12).
  
* Adds missing persistence for the "software_version" client metadata field in
  LDAP stores (iss #623).

* The Connect2id server must not redirect back to an OAuth 2.0 client with an 
  otherwise redirectable authorisation error if no "redirect_uri" was set in 
  the original plain OAuth 2.0 authorisation request and the client has more
  than one "redirect_uri" registered (issue server/630).
  
* Revises Connect2id server error reporting on invalid a "request_uri" and 
  "request" JWT in a authorisation request. Errors due to the JWT claims 
  including a "sub" (subject) claim that equals the "client_id" will be 
  reported to the client with an "invalid_request" code, instead of resulting 
  in a non-redirecting error. Errors due to a JWT that cannot be decrypted will 
  also be reported to the client with an "invalid_request_object" code, instead 
  of resulting in a non-redirecting error (issue server/631).
  
* Caches single-tenant static OP / AS metadata to increase performance (iss 
  server/627).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.27.1

* Upgrades to com.nimbusds:oauth2-oidc-sdk:9.1

* Upgrades to com.nimbusds:nimbus-jose-jwt:9.5

* Upgrades to com.nimbusds:c2id-server-ldap-schemas:1.15

* Updates to com.nimbusds:oauth2-authz-store:16.4.2

* Updates to com.nimbusds:oidc-session-store:14.2.2

* Updates to OpenSAML 3.4.6.

* Updates to BouncyCastle 1.68

* Updates to Infinispan 9.4.21.Final

* Updates to DropWizard Metrics 4.1.17

* Updates to org.jooq.pro-java-8:jooq:3.14.2

* Updates to org.mariadb.jdbc:mariadb-java-client:2.7.1

* Updates to org.postgresql:postgresql:42.2.18

* Updates to com.microsoft.sqlserver:mssql-jdbc:8.4.1.jre11

* Updates to Log4j 2.14.0

* Updates to commons-io:commons-io:2.8.0

* Updates to com.amazonaws:aws-java-sdk-bundle:1.11.936



## 10.4 (2020-12-21)

### Summary

* Adds a plugin for verifying software statements in OAuth 2.0 client 
  registration requests.
  
* Introducing configurable caching for selected metrics which report Connect2id 
  server object count in order to conserve database query resources.
  
* Upgrades OpenID Connect Federation 1.0 support to draft 14.


### Configuration

* /WEB-INF/monitor.properties

    * monitor.entryCountCacheTimeout -- New configuration property, specifies a
      timeout for caching entry count results, in seconds. Zero disables 
      caching, negative disables readings, causing the gauge to always return 
      -1. The default timeout value is 1800 seconds (30 minutes).
      
      Gauges with entry count caching:
      
        * authzSessionStore.numSessions
        * sessionStore.numSessions
        * clientStore.numRegistrations
        * clientStore.numCachedRemoteJWKSets
        * clientStore.numCachedRemoteRequestObjects
        * authzStore.numAuthzCodes
        * authzStore.numIdAccessTokens
        * authzStore.numLongLivedAuthorizations
        * authzStore.numRevocationJournalEntries


### Web API

* /federation/clients

    * Upgrades explicit federation Relying Party (RP) registration to draft 14. 
      The trust_anchor_id will now be returned as top-level entity statement 
      claim about the registered RP, instead of as RP metadata parameter. 

* /monitor/v1/metrics

    * Introduces caching to the following gauges which report the number of 
      persisted or cached Connect2id server objects for a given type. Intended 
      to conserve database resources when querying the object count is 
      expensive, for example in MySQL tables with millions of rows. The caching 
      and timeout is controlled by the new monitor.entryCountCacheTimeout 
      configuration property.

        * authzSessionStore.numSessions
        * sessionStore.numSessions
        * clientStore.numRegistrations
        * clientStore.numCachedRemoteJWKSets
        * clientStore.numCachedRemoteRequestObjects
        * authzStore.numAuthzCodes
        * authzStore.numIdAccessTokens
        * authzStore.numLongLivedAuthorizations
        * authzStore.numRevocationJournalEntries


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.24

* com.nimbusds.openid.connect.provider.spi.reg.FinalMetadataValidator

    * Adds a new "getReceivedMetadata" method to the ValidatorContext that 
      returns the original OAuth 2.0 client / OpenID relying party metadata as 
      received at the client registration endpoint.

* com.nimbusds.openid.connect.provider.spi.grants.ClientCredentialsGrantHandler

    * Upgrades the included OAuth 2.0 client credentials grant handler plugin,
      see https://bitbucket.org/connect2id/client-credentials-grant-handler .

        * New op.grantHandler.clientCredentials.simpleHandler.accessToken.includeClientMetadataFields
          configuration property to specify names of client metadata fields to 
          include in the optional access token `data` field, empty set if none. 
          To specify a member within a field that is a JSON object member use 
          dot (`.`) notation.
          
        * The op.grantHandler.clientCredentials.simpleHandler.enable 
          configuration property receives a default value false (disabled).
          
        * Lets op.grantHandler.clientCredentials.simpleHandler.accessToken.audienceList
          also apply to identifier-based access tokens.
          
        * Makes the /WEB-INF/clientGrantHandler.properties configuration file
          optional.

* com.nimbusds.openid.connect.provider.spi.reg.RegistrationInterceptor

    * Allows more than one RegistrationInterceptor SPI implementation to be 
      present, but only at most one can be enabled.
    
    * New plugin for verifying optional software statements included in OAuth 
      2.0 client registration requests. Also supports registration requests 
      encoded into a signed JWT and submitted over mutual TLS with a client 
      X.509 certificate, to conform with Open Banking and other profiles. See
      https://bitbucket.org/connect2id/software-statement-verifier .


### Resolved issues

* Submitted client X.509 certificate must be supplied to the
  RegistrationInterceptor SPI (issue server/618).

* Client certificate extraction log messages OP6020 and OP6021 must be assigned 
  to the appropriate Connect2id server endpoint (issue server/617).
  
* Improves exception messaging and logging when parsing corrupted string array 
  fields from SQL records (issue server/470). 
  
* Logs CustomTokenResponseComposer SPI implementation loading under OP6218 
  (issue server/620).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.24

* Upgrades to com.nimbusds:oauth2-oidc-sdk:8.29

* Updates to com.nimbusds:oauth2-authz-store:16.4

* Updates to com.nimbusds:oidc-session-store:14.2

* Upgrades to com.nimbusds:common:2.44

* Updates to io.dropwizard.metrics:*:4.1.16

* Updates to com.nimbusds:infinispan-cachestore-sql:4.2.3

* Updates to com.unboundid:unboundid-ldapsdk:5.1.3

* Upgrades to com.nimbusds:oauth-client-grant-handler:2.0

* New com.nimbusds:software-statement-verifier:2.1 dependency

* Updates to org.bouncycastle:*:1.67

* Updates to com.thetransactioncompany:cors-filter:2.9.1



## 10.3 (2020-11-25)

### Configuration

* /WEB-INF/oidcProvider.properties

    * op.authz.allowedPKCE -- New optional configuration property specifying 
      the allowed PKCE (RFC 7636) code challenge methods which OAuth 2.0 
      clients may use at the authorisation endpoint, as comma and / or space 
      separated list. The default allowed code challenge methods are "plain"
      and "S256" (all RFC 7636 methods).
      
      Authorisation requests which use a code challenge method that isn't 
      allowed by the configuration will be rejected with an invalid_request
      error.
      
      The allowed code challenge methods will be advertised in the OpenID
      provider / OAuth 2.0 authorisation server 
      "code_challenge_methods_supported" metadata field.
      
      
### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.23

* com.nimbusds.openid.connect.provider.spi.reg.RegistrationInterceptor

    * New SPI for intercepting and optionally modifying HTTP POST, GET, PUT and 
      DELETE requests at the client registration endpoint. Can be used to 
      process software statements (RFC 7591, section 2.3) and signed (JWT) 
      registration requests (such as those in Open Banking Dynamic Client 
      Registration).


### Resolved issues

* Fixes issue in the MySQL schema for the federation_clients table where MySQL
  5.7.x doesn't accept a second TIMESTAMP column with NON NULL declaration.
  MySQL 8.x is not affected (issue server/614).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.23

* Upgrades to com.nimbusds:oauth2-oidc-sdk:8.27

* Upgrades to com.thetransactioncompany:java-property-utils:1.16



## 10.2 (2020-11-13)

### Summary

* Includes the Connect2id server issuer URL as "iss" parameter in authorisation
  responses. Intended for OAuth 2.0 clients using more than one authorisation 
  server to prevent a class of attacks called "mix-up" attacks. Clients 
  interacting with a single OAuth 2.0 server and OpenID relying parties which 
  receive an ID token (and accordingly validate the "iss" claim) are not 
  affected. See OAuth 2.0 Authorization Server Issuer Identifier in 
  Authorization Response (draft-meyerzuselhausen-oauth-iss-auth-resp-01) for
  further details.
  
* Tightens the URI scheme policy for OAuth 2.0 client metadata to prevent 
  potential attacks using malicious "javascript", "data" and "vbscript" schemes
  in old and insufficiently protected web browsers. Deployments which utilise
  open client registration are advised to upgrade. See the article at  
  https://security.lauritz-holtmann.de/post/sso-security-redirect-uri/ for 
  further details.   
  
* Updates the logout web API to facilitate post-logout redirections in cases
  when the end-user session has expired.


### Configuration

* /WEB-INF/oidcProvider.properties

    * op.policy, op.tos, op.serviceDocs -- If the optional policy, terms of 
      service and service documentation links are specified by absolute URL, 
      the scheme must be either "https" or "http".


### Web API

* /.well-known/openid-configuration, /.well-known/oauth-authorization-server

    * Adds the "authorization_response_iss_parameter_supported" metadata 
      parameter (the value is set to true), as specified in OAuth 2.0 
      Authorization Server Issuer Identifier in Authorization Response 
      (draft-meyerzuselhausen-oauth-iss-auth-resp-01).

* /authz-sessions/rest/v3/

    * Includes an "iss" (issuer) parameter in OAuth 2.0 authorisation success 
      and error responses, as specified in OAuth 2.0 Authorization Server 
      Issuer Identifier in Authorization Response 
      (draft-meyerzuselhausen-oauth-iss-auth-resp-01). Intended for OAuth 2.0 
      clients using more than one authorisation server to prevent a class of 
      attacks called "mix-up" attacks. Clients interacting with a single OAuth 
      2.0 server and OpenID relying parties which receive an ID token (and 
      accordingly validate the "iss" claim) are not affected.
      
* /clients

    * Registration of OAuth 2.0 clients and OpenID relying parties with 
      "redirect_uris" or "post_logout_redirect_uris" containing the custom URI 
      schemes "javascript", "data" and "vbscript" is prohibited for security 
      reasons. Native applications can continue registering redirection URIs
      with custom URI schemes which don't match the prohibited.
      
    * Registration of OAuth 2.0 clients with "client_uri", "policy_uri" or 
      "tos_uri" with URI schemes other than "https" and "http" is prohibited
      for security reasons.

    * Registration of OpenID relying parties will no longer permit custom URI
      schemes for "frontchannel_logout_uri" and "backchannel_logout_uri". The 
      only permitted URI scheme is "https".
      
* /logout-sessions/rest/v1/

    * Updates the logout session web API to not return an "invalid_session" 
      error when the submitted end-user session SID (typically stored in a 
      browser cookie at the IdP domain) is invalid or expired. OpenID relying 
      parties (RP) making a logout request with a valid 
      "post_logout_redirect_uri" will thus be able to complete the redirection, 
      regardless of the end-user session state at the IdP.
      
      Following this change "invalid_session" errors will no longer be returned
      by the logout session web API.


### Resolved issues

* Prohibits registration of OAuth 2.0 clients and OpenID relying parties with 
  "redirect_uris" or "post_logout_redirect_uris" with the custom URI schemes 
  "javascript", "data" and "vbscript" for security reasons. Also requires 
  browser rendered URIs derived from client and server metadata, such as 
  "client_uri", "policy_uri" and "tos_uri" to have an "https" or "http" URI. 
  See https://security.lauritz-holtmann.de/post/sso-security-redirect-uri/ for 
  more information (issue server/611).
  
* Prohibits registration of OpenID relying parties with custom
  "frontchannel_logout_uri" and "frontchannel_logout_uri" URI schemes, thus
  making "https" the only allowed URI scheme (issue server/612).

* Improves INFO logging at the logout session endpoint (issue server/610).


### Dependency changes

* Upgrades to com.nimbusds:oauth2-oidc-sdk:8.26



## 10.1 (2020-10-29)

### Summary

* Adds support for OpenID Connect Federation 1.0 (draft 12) automatic clients. 
  Automatic clients don't register explicitly with the Connect2id server, 
  instead they proceed directly to the authorisation endpoint, where they 
  submit a signed request object (JWT) containing additional federation-
  specific claims, thus letting the request object also serve as an implicit 
  authenticated registration request. The Connect2id server will perform the 
  regular federation trust chain resolution and retrieve the client's metadata 
  from its federation entity statement published at a well-known endpoint. The 
  client registration will expire according to a configured policy - a set 
  lifetime or the trust chain lifetime. 
  
  See https://openid.net/specs/openid-connect-federation-1_0-12.html.
  
* Updates OpenID Connect for Identity Assurance 1.0 (draft 11) support to 
  accept "claims" authentication request parameters where the "verified_claims" 
  element is a JSON array. This is intended to enable requests for claims sets 
  with different verification requirements (e.g. different trust frameworks).
  Previously such requests will result in a invalid_request error at the 
  authorisation endpoint.
  
  Example "claims" request for two verifications, using the 
  "eidas_ial_substantial" and the "de_aml" trust frameworks: 
  ```
  {
    "userinfo": {
      "verified_claims": [
        {
          "verification": {
            "trust_framework": {
              "value": "eidas_ial_substantial"
            }
          },
          "claims": {
            "given_name": null,
            "family_name": null
          }
        },
        {
          "verification": {
            "trust_framework": {
              "value": "de_aml"
            }
          },
          "claims": {
            "birthdate": null
          }
        }
      ]
    }
  }
  ```
  
  The consent prompt in the authorisation session API will list the requested
  verified claims across all elements in the "verified_claims" JSON array. 
  
  Note that "claims" -> "verification" will only present the first requested 
  "verification" element if multiple are found. Use the "claims" -> 
  "raw_request" to obtain all verifications. A future version of the Connect2id
  server will update the consent prompt to simplify handling of verified claims 
  requests with multiple verifications.
  
  See https://openid.net/specs/openid-connect-4-identity-assurance-1_0-11.html, 
  section 6.3.3.
  
* Connect2id server deployments with an AWS DynamoDB backend can be configured 
  to include a SHA-256 based message authentication code (HMAC) in each stored
  item to guarantee its integrity and authenticity while stored in the 
  database.


### Configuration

* /WEB-INF/oidcProvider.properties

    * op.federation.clientRegistrationTypes -- The configuration property for
      the supported OpenID Connect Federation 1.0 client registration types now
      allows for automatic as well as explicit registration.
      
    * op.federation.autoClientAuthMethods.ar -- New configuration property  
      specifying the supported methods for authenticating implicit registration 
      requests of OpenID Connect Federation 1.0 automatic clients at the OAuth 
      2.0 authorisation endpoint. The only currently supported method is
      "request_object". 

    * op.federation.autoClientLifetime -- New configuration property for the 
      lifetime of registered OpenID Connect Federation 1.0 automatic clients, 
      in seconds. If zero or negative the lifetime will be determined by the 
      trust chain expiration time. When explicitly set must not be shorter than 
      5 minutes (300 seconds) to allow sufficient time for the completion of a
      single OAuth 2.0 flow with an authorisation, token and UserInfo request 
      by the registered relying party. The default lifetime is one hour (3600 
      seconds).

* /WEB-INF/infinispan-*-dynamodb.xml

    * Updates the DynamoDB store schema to v1.8 and adds the option to secure
      the integrity and authenticity of the stored DynamoDB items with a
      SHA-256 based Message Authentication Code (HMAC), appended to each item 
      in a "_hmac#256" binary attribute. If a retrieved DynamoDB item is 
      missing the "_hmac#256" attribute or its verification fails the 
      Connect2id server will produce an HTTP 500 Internal Server Error, log the 
      error with a "DS0131" code and increment the 
      "<cache-name>.dynamoDB.invalidItemHmacCounter" metric.  
      
      To enable this HMAC protection the Connect2id server must be configured 
      with a secret 256 bit key (as BASE-64 encoded string) in the 
      "dynamoDB.hmacSHA256Key" Java system property.
      
      The HMAC protection must be enabled for newly provisioned or empty 
      DynamoDB tables. Enabling the protection for DynamoDB tables with prior 
      existing items will cause HMAC validation errors on their retrieval.
      
* /WEB-INF/infinispan-stateless-dynamodb.xml

    * DynamoDB consistent reads for "sessionStore.sessionMap" can be enabled by
      setting the "dynamodb.consistentReads.sessionStore.sessionMap" Java 
      system property to true.


### Web API

* /authz-sessions/rest/v3/

    * The consent prompt "claims" parameter adds support for verified claims
      requests (OpenID Connect Identity Assurance 1.0) with multiple 
      verifications.
      
      The "claims" -> "new" -> "essential", "voluntary" arrays will list the 
      requested verified claims (prefixed with "verified:") across all 
      "verified_claims" elements (if multiple are found).
      
      The "claims" -> "verification" -> "id_token", "userinfo" objects will 
      include the first found verification element (if multiple are found), 
      the full list of verification objects can be obtained by parsing the 
      "claims" -> "raw_request" parameter (requires 
      `op.authz.includeRawClaimsRequestInPrompt=false`).   

* /monitor/v1/metrics

    * Adds new "<cache-name>.dynamoDB.invalidItemHmacCounter" metric of type
      counter for Connect2id server deployments with a DynamoDB database. 
      Counts the number of retrieved DynamoDB items which failed the HMAC 
      SHA-256 check (if enabled).


### SPI

* PasswordGrantHandler, JWTGrantHandler, SAML2GrantHandler

    * Consented claims which name is prefixed with "id_token:" will be included
      in the ID token instead of via the default method, the UserInfo endpoint.
      This feature is adopted from the authorisation session web API.
      
* Included OpenID Connect HTTP claims source

    * Updates the included AdvancedClaimsSource SPI implementation for sourcing
      OpenID claims from an HTTP endpoint to version 2.1 
      (com.nimbusds:oidc-claims-source-http:2.1). The JSON object representing
      the request will now include a "claims_transport" parameter hinting how 
      the requested claims are going to be transported to the relying party:
      "userinfo" for the UserInfo endpoint, "id_token" for the ID token, 
      omitted if not applicable (for claims requested by an internal plugin, 
      such as an access token codec).


### Resolved issues

* OpenID claims requests with JSON array of verified_claims must not fail 
  (issue server/605).

* Fixes NPE in OpenID Connect Federation 1.0 client registration error 
  processing logic (issue server/585).
  
* Front-channel logout requests must not include the issuer identifier (iss)
  as query parameter when a session identifier (sid) isn't included (issue 
  server/595).
  
* Updates the OAuth 2.0 authorisation endpoint to reject signed (JWS) request 
  objects (JARs) where the optional JWT "sub" (subject) claim is set to the 
  client_id value, see the latest cross-JWT confusion security recommendation 
  in https://tools.ietf.org/html/draft-ietf-oauth-jwsreq-29#section-10.8 (issue 
  server/588).
  
* Fixes the reported "request" (JAR) parameter name in illegal_request error 
  messages from the OAuth 2.0 authorisation endpoint (issue server/589).
  
* Ignore unknown key types in a JWK set, when parsing a client JWK set and in 
  other cases (issue nimbus-jose-jwt/377).
  
* Logs at INFO level the enabled / disabled status of every loaded claims 
  source at Connect2id server startup under OP7107 (issue server/600).
  
* Logs at TRACE level the decoded self-contained access token authorisation 
  under AS0544 (issue authz-server/178).
  
* HTTP POST and PUT requests to the client registration endpoint where the 
  entity body exceeds the hard-wired character limit must return a HTTP 400 Bad 
  Request, not HTTP 500 Internal Server Error. Raises the limit from 15 
  thousand to 20 thousand characters (issue server/579).


### Dependency changes

* Upgrades to com.nimbusds:oauth2-oidc-sdk:8.23.1

* Updates to com.nimbusds:nimbus-jose-jwt:8.20.1

* Updates to com.nimbusds:oauth2-authz-store:16.3

* Updates to com.nimbusds:oidc-claims-source-http:2.1

* Updates to BouncyCastle 1.66

* Updates to Infinispan 9.4.20

* Upgrades to com.nimbusds:infinispan-cachestore-dynamodb:3.7.2

* Updates to com.unboundid:unboundid-ldapsdk:5.1.1

* Updates to com.amazonaws:aws-java-sdk-bundle:1.11.880 



## 10.0 (2020-08-18)

### Summary

* Supports OpenID Connect Federation 1.0 (draft 12) with explicit client 
  registration. The extension to OpenID Connect enables seamless single sign-on 
  and identity provisioning in a federation of OpenID providers and relying 
  parties approved by one or more operators (trust anchors), potentially 
  involving intermediate authorities. See 
  https://openid.net/specs/openid-connect-federation-1_0-12.html
  
* Enables issue of refresh tokens for transient (non-persisted) authorisations,
  where the authorisation data is contained in the refresh token (as encrypted 
  JWT). Previously refresh tokens could be issued only for long-lived 
  (persisted) authorisations, where the refresh token is an encrypted key 
  pointing to the authorisation record. The Connect2id server can issue an
  unlimited number of self-contained refresh tokens for a given client and 
  subject (end-user). This can be useful in cases where an end-user has 
  instances of a public client on several devices, but the granted 
  authorisations must not be shared between the client instances. Revocation 
  applies to all client instances.
  
* Supports setting of global or client-specific policy for enforcing use of 
  pushed authorisation requests (PAR) only, and rejecting regular requests at 
  the authorisation endpoint (including JAR). The new op.par.require 
  configuration sets the global PAR policy. The new 
  require_pushed_authorization_requests client metadata parameter sets the PAR 
  policy for an individual OAuth 2.0 client. Clients which must use PAR will 
  receive an invalid_request error with message "PAR required" from the 
  authorisation endpoint if they attempt a regular request. See OAuth 2.0 
  Pushed Authorization Requests (draft-ietf-oauth-par-02).
  
* The authorisation store web API receives a new version with endpoint 
  /authz-store/rest/v3 with an updated "revocation" resource, to handle 
  revocation of non-persisted authorisations tied to self-contained refresh
  tokens. The previous web API version at endpoint /authz-store/rest/v3 remains 
  available. 
  
* Supports the tls_client_certificate_bound_access_tokens client metadata 
  parameter from OAuth 2.0 Mutual-TLS Client Authentication and 
  Certificate-Bound Access Tokens (RFC 8705). Intended for enforcing issue of 
  X.509 client certificate bound access tokens to a public (non-authenticating)
  client. The client must present a client certificate (self-signed or CA
  issued) at the token endpoint; if no certificate is presented the client will
  receive an invalid_client token error response with HTTP status code 401 
  Unauthorized.
  
* Supports configuration of unlimited additional access tokens for the 
  Connect2id server integration web APIs, to facilitate token roll-over and for 
  other purposes.
  
* Upgrades the OAuth 2.0 / OpenID Connect SDK to major release 8.0 which 
  removes the deprecated javax.mail dependency and all deprecated API methods 
  relying on its classes. ClaimsSource and AdvancedClaimsSource SPI 
  implementations which rely on the javax.mail.Address class for the "email" 
  claim need to switch to an alternative java.lang.String based getter / setter 
  method of the UserInfo class.


### Configuration

* /WEB-INF/federationJWKSet.json -- New JSON Web Key (JWK) set file for 
  configuring one or more RSA keys for signing the issued federation entity
  statements. The RSA keys must have a size of 2048 bits, a unique key 
  identifier (`kid`), a key use (`use`) set to signature (`sig`) and an
  algorithm (`alg`) set to `RS256`. The first RSA JWK in the list will be 
  used for signing. Previously used keys to facilitate roll-over can follow.
  The federation JWK set can be alternatively passed via a 
  `op.federationJWKSet` Java system property, optionally with additional 
  BASE64URL encoding to avoid shell escaping of special JSON characters. For
  more information regarding key generation and configuration see
  https://connect2id.com/products/server/docs/config/jwk-set#config   

* /WEB-INF/oidcProvider.properties

    * op.federation.enable -- Enables / disables support for OpenID Connect 
      Federation 1.0. Disabled by default.
    
    * op.federation.clientRegistrationTypes -- The supported client 
      registration types. Supported client registration types: explicit
      
    * op.federation.organizationName -- The organisation name in the 
      federation, blank if not specified.
      
    * op.federation.trustAnchors.* -- The configured trust anchors. Must 
      contain at least one entity ID.
     
    * op.federation.authorityHints.* -- The intermediate entities or trust 
      anchors that may issue an entity statement about the OpenID Connect 
      provider. Must contain at least one entity ID.
    
    * op.federation.constraints.maxPathLength -- The maximum path length when 
      resolving trust chains. The default value is 2 (up to two intermediates 
      to the trust anchor).
    
    * op.federation.constraints.permitted.* -- The explicitly permitted entity 
      IDs when resolving trust chains, blank if not specified.
    
    * op.federation.constraints.excluded.* -- The excluded entity IDs when 
      resolving trust chains, blank if not specified.
      
    * op.federation.httpRequestTimeout -- The HTTP read timeout (in 
      milliseconds) when resolving trust chains. Zero implies no timeout. Must 
      not be negative. The default value is 500 ms.
      
    * op.federation.httpReadTimeout -- The HTTP read timeout (in milliseconds) 
      when resolving trust chains. Zero implies no timeout. Must not be 
      negative. The default value is 500 ms.
      
    * op.federation.contacts.* -- List of contacts for this federation entity. 
      These may contain names, e-mail addresses, descriptions, phone numbers, 
      etc. Blank if not specified.
    
    * op.federation.policyURI -- URL of the federation entity policy. The URL 
      can be also be specified relative to the issuer URL (op.issuer). Blank if 
      not specified.
      
    * op.federation.homepageURI -- URL of the federation entity homepage. The 
      URL can be also be specified relative to the issuer URL (op.issuer). 
      Blank if not specified.
    
    * op.federation.trustMarks.* -- Trust marks about this federation entity as 
      signed JSON Web Tokens (JWT). Blank if not specified.
    
    * op.federation.entityStatementLifetime -- The lifetime of issued entity 
      statements, in seconds. The default lifetime is one week (604800 
      seconds).
      
    * op.reg.apiAccessTokenSHA256.* -- Supports configuration of additional API
      access tokens, to facilitate token roll-over or for other needs. Those 
      are configured by appending a dot (.) with a unique label to the property 
      name, e.g. as op.reg.apiAccessTokenSHA256.1=abc...
      
      The support for op.reg.secondaryAPIAccessTokenSHA256 remains, however
      deployments are encouraged to switch to the new configuration method.
      
    * op.authz.apiAccessTokenSHA256.* -- Supports configuration of additional
      API access tokens, to facilitate token roll-over or for other needs. 
      Those are configured by appending a dot (.) with a unique label to the 
      property name, e.g. as op.authz.apiAccessTokenSHA256.1=abc...
      
    * op.logout.apiAccessTokenSHA256.* -- Supports configuration of additional 
      API access tokens, to facilitate token roll-over or for other needs. 
      Those are configured by appending a dot (.) with a unique label to the 
      property name, e.g. as op.logout.apiAccessTokenSHA256.1=abc...
      
    * op.par.require -- Enforces a global pushed authorisation requests (PAR)
      only policy. If `true` the Connect2id server will accept authorisation 
      requests initiated via PAR only, regular authorisation requests will be 
      rejected at the authorisation endpoint with an invalid_request error. The 
      default value is `false`.

* /WEB-INF/sessionStore.properties

    * sessionStore.apiAccessTokenSHA256.* -- Supports configuration of 
      additional API access tokens, to facilitate token roll-over or for other 
      needs. Those are configured by appending a dot (.) with a unique label to 
      the property name, e.g. as sessionStore.apiAccessTokenSHA256.1=abc...
      
      The support for sessionStore.secondaryAPIAccessTokenSHA256 remains, 
      however deployments are encouraged to switch to the new configuration 
      method.
      
* /WEB-INF/authzStore.properties

    * authzStore.apiAccessTokenSHA256.* -- Supports configuration of additional 
      API access tokens, to facilitate token roll-over or for other needs. 
      Those are configured by appending a dot (.) with a unique label to the 
      property name, e.g. as authzStore.apiAccessTokenSHA256.1=abc...
      
      The support for authzStore.secondaryAPIAccessTokenSHA256 remains, however
      deployments are encouraged to switch to the new configuration method.

* /WEB-INF/monitor.properties

    * authzStore.apiAccessTokenSHA256.* -- Supports configuration of additional 
      API access tokens, to facilitate token roll-over or for a load balancer
      to monitor the health of the Connect2id server. Those are configured by 
      appending a dot (.) with a unique label to the property name, e.g. as 
      monitor.apiAccessTokenSHA256.1=abc...
      
      The support for monitor.secondaryAPIAccessTokenSHA256 remains, however
      deployments are encouraged to switch to the new configuration method. 
      
* /WEB-INF/infinispan-*.xml

    * Adds new "federation.registrationsMap" to Infinispan for tracking the 
      registered federation clients.
      
      In deployments with an SQL RDBMS (MySQL, PostreSQL, MS SQL Server, H2) 
      the Connect2id server on startup will automatically create a new 
      "federation_clients" table for persisting tracking metadata about the
      federation clients.
      
      In deployments with AWS DynamoDB the Connect2id server on startup will
      automatically create a new "federation_clients" table for persisting
      the federation clients metadata.
      
      In deployments with Redis the federation clients metadata will be stored
      in a separate database with number 15.
      
      In deployments with LDAP the federation clients metadata will be kept in
      memory only and will not be persisted. Support for LDAP persistence and 
      an appropriate schema will be added in a future release or upon request.

* /WEB-INF/infinispan-*-{mysql|postgres95|sqlserver|h2}.xml

    * Adds new "require_pushed_authorization_requests" and 
      "tls_client_certificate_bound_access_tokens" columns to the "clients" 
      table. In existing Connect2id server deployments with an SQL RDBMS the 
      server will automatically add the new columns (with an appropriate 
      default value) on startup.

* /WEB-INF/infinispan-*-ldap.xml

    * Adds new "oauthRequirePAR" and "oauthClientCertBoundAccessTokens" 
      attributes to the "oauthClientIdentity" object class. Connect2id server 
      deployments with an LDAP v3 backend database (such as OpenLDAP or OpenDJ) 
      need to update the LDAP schema manually to version 1.13 
      see https://bitbucket.org/connect2id/server-ldap-schemas/src/1.13/ , the 
      OpenLDAP schema diff
      https://bitbucket.org/connect2id/server-ldap-schemas/diff/
      src/main/resources/oidc-client-schema-openldap.ldif?at=1.13
      &diff2=135b1b503919b270a2cb4a8e44dfbfc8fd0a4e27
      and the OpenDJ schema diff
      https://bitbucket.org/connect2id/server-ldap-schemas/diff/
      src/main/resources/oidc-client-schema-opendj.ldif?at=1.13
      &diff2=135b1b503919b270a2cb4a8e44dfbfc8fd0a4e27


### Web API

* /.well-known/openid-federation

    * New endpoint for retrieving the OpenID provider's self-signed federation
      entity statement. See OpenID Connect Federation 1.0 (draft 12), 
      section 5.

* /federation/clients

    * New endpoint for explicit registration of federation entities as OpenID 
      relying parties. See OpenID Connect Federation 1.0 (draft 12), section 
      9.2.

* /.well-known/openid-configuration, /.well-known/oauth-authorization-server

    * Adds support for the "require_pushed_authorization_requests" metadata
      parameter from OAuth 2.0 Pushed Authorization Requests 
      (draft-ietf-oauth-par-02), controlled by the new op.par.require 
      configuration setting.
      
* /clients

    * Adds support for the "require_pushed_authorization_requests" client 
      metadata parameter from OAuth 2.0 Pushed Authorization Requests 
      (draft-ietf-oauth-par-02).
      
    * Supports setting of the "tls_client_certificate_bound_access_tokens" 
      client metadata parameter from OAuth 2.0 Mutual-TLS Client Authentication 
      and Certificate-Bound Access Tokens (RFC 8705).

* /authz-store/rest/v3

    * New version 3 of the authorisation store web API. Modifies the HTTP 
      response from the "revocation" resource as follows: Revocations will 
      always succeed with a 2xx HTTP status code. If the revocation matches 
      one or more long-lived (persisted) authorisations those will be returned
      with a 200 Success status, as previously. Else a 204 No Content status 
      will be returned (previously a 404 Not Found), as the revocation may 
      match a transient authorisation with a self-contained (JWT) access and / 
      or refresh token. 

* /authz-store/rest/v2

    * The previous version of the authorisation store web API remains 
      available.


### Resolved issues

* Increases the size of the jwks column for H2 from VARCHAR 10000 to VARCHAR 
  50000 (issue server/578). The H2 command to alter an existing clients table 
  is `ALTER TABLE "clients" ALTER COLUMN "jwks" SET DATA TYPE VARCHAR(50000);`.
  
* Changes the data type of the jwks column for MySQL from VARCHAR 10000 to JSON
  to increase the size while working around the limit of 65535 bytes per row 
  (issue server/578). The MySQL command to alter an existing clients table 
  is `ALTER TABLE clients MODIFY jwks JSON;`.

* All startup and configuration errors must be logged in c2id-server.log (issue 
  server/569)


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.22

* Upgrades to com.nimbusds:c2id-server-ldap-schemas:1.13

* Upgrades to com.nimbusds:tenant-manager:5.0

* Upgrades to com.nimbusds:tenant-registry:5.2

* Upgrades to com.nimbusds:oauth2-authz-store:16.2

* Upgrades to com.nimbusds:oidc-session-store:14.1.3

* Updates to com.nimbusds:nimbus-jose-jwt:8.18

* Upgrades to com.nimbusds:nimbus-jwkset-loader:5.0

* Upgrades to com.nimbusds:oauth2-oidc-sdk:8.19

* Updates to com.nimbusds:content-type:2.1

* Updates to com.nimbusds:oauth-password-grant-web-api:1.4

* Upgrades to com.nimbusds:common:2.43.1

* Upgrades to com.thetransactioncompany:java-property-utils:1.15

* Adds javax.activation:javax.activation-api:1.2.0

* Removes com.sun.mail:javax.mail:1.6.2



## 9.5.2 (2020-08-08)

### Resolved issues

* Fixes a bug which produced an internal server error (HTTP 500) at the token
  endpoint when a CustomTokenResponseComposer SPI implementation is installed 
  and the server produces a token error response for a particular setting of 
  the authorisation (issue server/581).



## 9.5.1 (2020-06-22)

### Resolved issues

* Replaces the BASE64 Apache Commons Codec with the BASE64 codec from the
  Nimbus JOSE+JWT library to prevent an unchecked IllegalArgumentException 
  exception due to illegal chars in a submitted authorisation code (issue 
  server/574, common/61).  

* Restores accepting client_secret_jwt and private_key_jwt client 
  authentication JWTs for the token revocation endpoint where the audience is
  set to the token endpoint URI, removed in Connect2id server v8.0. This 
  rollback is done to preserve backward compatibility with existing clients. 
  New clients should set the authentication JWT "aud" (audience) to the 
  exact endpoint URI as future Connect2id server releases may stop accepting 
  the issuer URI or the token endpoint URI for security reasons (issue 
  server/573).

* Logs the exception message for OP6412 when client authentication at the token 
  revocation endpoint fails (issue server/570).
  
* Exports public EdDSA keys from the server JWK set to /jwks.json (issue 
  server/568).


### Dependency changes
  
* Updates to com.nimbusds:common:2.38.1

* Updates to com.nimbusds:oidc-session-store:13.4.2

* Updates to com.nimbusds:jgroups-dynamodb-ping:1.2.4



## 9.5 (2020-05-22)

### Configuration

* /WEB-INF/oidcProvider.properties

    * op.authz.prohibitSwitchBetweenBasicResponseModes -- New optional 
      configuration property. If `true` client requests to switch between the 
      "query" and "fragment" response modes by setting the `response_mode` 
      authorisation request parameter are prohibited. The default value is 
      `false`.

    * op.token.requireClientX509Cert -- New optional configuration property. 
      If `true` the token endpoint will require a client X.509 certificate from 
      all clients, in order to enforce issue of client certificate bound access 
      tokens (RFC 8705). The default value is `false`.



## 9.4 (2020-05-20)

### Summary

* Adds support for issuing EdDSA signed JWT-encoded access tokens. Choose 
  EdDSA (RFC 8037) for increased performance and compact signatures. Connect2id 
  benchmarks show EdDSA signature generation with an Ed25519 key receiving a 
  62x boost over 2048-bit RSA (RS256), with verification remaining roughly on 
  par. The JWT signature size is reduced 4 fold.
  
  To roll-over to EdDSA signed JWT-encoded access tokens provision the 
  Connect2id server JWK set with a signing Ed25519 key and set the JWS 
  algorithm for access tokens to "EdDSA". Check the configuration notes for 
  details.  


### Configuration

* /WEB-INF/jwkSet.json

    * Introduces a new optional Ed25519 octet key pair JWK (key type "OKP") 
      with curve "Ed25519", use "sig" (signature) and requiring a unique key 
      ID. Intended for issuing EdDSA signed JWT-encoded access tokens. To 
      generate and roll-over the EdDSA signing key you can use the latest 
      available Connect2id server JWK set generator, see 
      https://connect2id.com/products/server/docs/config/jwk-set#generation
      
* /WEB-INF/authzStore.properties

    * authzStore.accessToken.jwsAlgorithm -- Adds support for signing issued  
      JWT-encoded access tokens with the "EdDSA" JWS algorithm (RFC 8037). 
      Requires the Connect2id server JWK set to be provisioned with a signing 
      Ed25519 key. The default JWS algorithm for signing remains "RS256" with 
      an 2048-bit RSA key due to the ubiquitous JWT library support for RS256.
  

### Resolved issues

* Calls to the ClaimsSource from a TokenIntrospectionResponseComposer SPI 
  implementation should automatically include any "claims_data" if available 
  for the introspected access token (issue server/561).
  
* Fixes a bug which prevented persistence of client registrations into an SQL
  database where the client_id contains a colon (`:`) character in combination 
  with some non-alphanumeric characters preceding it. Affected the 
  single-tenant edition of the Connect2id server (issue server/563).


### Dependency changes

* Upgrades to com.nimbusds:oauth2-authz-store:14.7

* Updates to com.nimbusds:nimbus-jose-jwt:8.17

* Updates to BouncyCastle 1.65

* Updates to OpenSAML 3.4.5

* Updates to com.nimbusds:lang-tag:1.5

* Updates to com.amazonaws:aws-java-sdk-bundle:1.11.782

* Updates to com.zaxxer:HikariCP:3.4.5

* Updates to org.mariadb.jdbc:mariadb-java-client:2.6.0

* Updates to org.postgresql:postgresql:42.2.12

* Updates to com.microsoft.sqlserver:mssql-jdbc:8.2.2.jre11

* Updates DropWizard Metrics to 4.1.8

* Updates Prometheus to 0.9.0

* Updates Log4j to 2.13.3



## 9.3 (2020-05-12)

### Configuration

* /WEB-INF/infinispan-*-{mysql|postgres95|sqlserver|h2}.xml

    * Updates the SQL store schema to v2.7 and switches to a single shared 
      database connection pool for all Infinispan map and cache structures 
      used by the Connect2id server. Support for per map / cache connection 
      pool to spread the load over multiple databases (vertical partitioning) 
      is still available.

* /WEB-INF/infinispan-*-dynamodb.xml

    * Updates the DynamoDB store schema to v1.7 and adds support for 
      configuring an optional HTTP proxy for connections to the DynamoDB 
      endpoint. The HTTP proxy is configured by setting the Java system 
      properties "dynamodb.httpProxyHost" and "dynamodb.httpProxyPort".


### Web API

* /authz-sessions/rest/v3/

    * Exposes the optional "id_token_hint" OpenID authentication request 
      parameter in the authorisation session object (under "auth_req"). 


### SPI

* Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.20

* com.nimbusds.openid.connect.provider.spi.tokens.response.CustomTokenResponseComposer

    * New SPI for composing custom token success and error responses. Can be 
      used to include additional parameters in an access token response based 
      on the authorisation (consent) "data" parameter, such as an 
      "authorization_details" parameter required in OAuth 2.0 Rich 
      Authorization Requests (draft-lodderstedt-oauth-rar-03).


### Resolved issues

* Previously consented claims appearing in the consent prompt (authorisation 
  session API) must not include language tags. Fixed a bug which prevented 
  stripping of the tags from claim names retrieved from the "clm" field in 
  authorisation records (issue server/558).
  
* Enhances the authorisation session API by automatically stripping language
  tags in the names of consented claims (issue server/559).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.20

* Upgrades to com.nimbusds:oauth2-oidc-sdk:7.5

* Upgrades to com.nimbusds:oauth2-authz-store:14.6

* Upgrades to com.nimbusds:infinispan-cachestore-sql:4.2.2

* Upgrades to com.nimbusds:infinispan-cachestore-dynamodb:3.6.1



## 9.2 (2020-04-21)

### Summary

* Updates support for "JWT Secured Authorization Request (JAR)" to 
  draft-ietf-oauth-jwsreq-21. `client_id` becomes the sole required query 
  parameter for JAR requests, in addition to the query parameter for the JWT
  itself (`request` for a JWT passed inline or `request_uri` for a JWT passed 
  by URI reference).
  
* Adds new "op.authz.oAuthRequestJWTPolicy" configuration setting for 
  specifying a policy for merging unsecured parameters in a JWT-secured
  OAuth 2.0 authorisation request (JAR) (excluding OpenID authentication 
  requests). The default policy is to accept only the JWT-secured parameters, 
  with unsecured query parameters being ignored.
  
* Adds new "op.authz.openIDRequestJWTPolicy" configuration setting for 
  specifying a policy for merging unsecured parameters in a JWT-secured OpenID
  authentication request. The default policy is merge unsecured OpenID 
  authentication request query parameters, with the JWT-secured parameters 
  having precedence.


### Configuration
      
* /WEB-INF/oidcProvider.properties

    * New "op.authz.oAuthRequestJWTPolicy" configuration setting for specifying 
      a policy for merging unsecured parameters in a JWT-secured OAuth 2.0 
      authorisation request (JAR) (excluding OpenID authentication requests).
      
      Supported policies:
      
         * STRICT -- Use only JWT-secured parameters, unsecured query 
           parameters will be ignored. This is the default policy for 
           OAuth 2.0 authorisation requests.
           
         * MERGE_UNSECURED -- Merge unsecured authorisation request query 
           parameters, with the JWT-secured parameters having precedence.

    * New "op.authz.openIDRequestJWTPolicy" configuration setting for 
      specifying a policy for merging unsecured parameters in a JWT-secured 
      OpenID authentication request.
      
      Supported policies:
      
         * STRICT -- Use only JWT-secured parameters, unsecured query 
           parameters will be ignored.
           
         * MERGE_UNSECURED -- Merge unsecured OpenID authentication request
           query parameters, with the JWT-secured parameters having precedence.
           This is the default policy for OpenID authentication requests.


### Resolved issues

* Adds missing AccessTokenKeyExternalizer and 
  AccessTokenAuthorizationExternalizer declarations for 
  "authzStore.idAccessTokenMap" in the infinispan-*.xml configs (issue 
  server/545).
  
* Fixes handling of GeneralException instances thrown from ClaimSource SPIs 
  when no error code and HTTP status code is specified. The correct response is 
  to return an HTTP status code 500 instead of an empty UserInfo (issue 
  server/547).
  
* Fixes "userInfoEndpoint.serverErrors" metering on a ClaimsSource SPI throwing
  an unchecked Exception or a GeneralException (with no parameters) instance
  (issue server/548).  

* Fixes the supply of optional claims data to ClaimsSource SPI implementations
  for OpenID claims requests for ID tokens (issue server/549).


### Dependency changes

* Upgrades to com.nimbusds:oauth2-oidc-sdk:7.4

* Updates to com.nimbusds:nimbus-jose-jwt:8.14.1



## 9.1.1 (2020-03-26)

### Resolved issues

* Fixes premature expiration of OAuth 2.0 authorisation codes resulting from 
  `prompt=none` or persisted consent authorisations in stateless Connect2id 
  server deployments (single node or cluster) with an SQL RDBMS database 
  (MySQL, PostgreSQL, Microsoft SQL server). Applies to Infinispan 
  configurations `infinispan-stateless-{mysql|postgres95|sqlserver}.xml` (where 
  Redis is _not_ used as an in-memory cache / store). Affected deployments 
  should update (issue authz-store/176). 

* Adds debug logging for authorisation grant put (AS0230) and authorisation 
  grant retrieval (AS0222) (issues authz-store/174 and 175).


### Dependency changes

* Upgrades to com.nimbusds:oauth2-authz-store:14.4.2

* Updates to com.nimbusds:nimbus-jose-jwt:8.11



## 9.1 (2020-03-24)

### Web API

* /token/introspect

    * Updates "JWT Response for OAuth Token Introspection" support to the 
      upcoming draft-ietf-oauth-jwt-introspection-response-09 version.
      
      For a client (resource server) to obtain a JWT-secured introspection 
      response it must submit an introspection request with the Accept HTTP
      request header set to "application/token-introspection+jwt". The 
      request must be authorised with the registered client authentication 
      method or with an access token.
      
      The JWT response will be JWS signed and include the following JWT claims:
      
        * "iss" -- Set to the OpenID Provider / Authorisation server issuer 
          URL.
        * "aud" -- Set to the client_id of the caller (resource server).
        
        * "iat" -- The issue timestamp.
        
        * "token_introspection" -- A JSON object containing the token 
          introspection response members, such as "active", etc.
          
      The optional op.token.introspection.jwtType configuration property that
      overrides the JWT "typ" (type) header applies.
      
      Legacy JWT-secured introspection responses (according to 
      draft-ietf-oauth-jwt-introspection-response-09) will continue to be 
      supported, for a client (resource server) to request one the Accept HTTP
      request header must be set to "application/jwt".


### SPI

Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.19

* com.nimbusds.openid.connect.provider.spi.par.ValidatorContext
  
    * Adds a `getIssuer()` method to the PAR `ValidatorContext`.  


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.19

* Updates to com.nimbusds:oauth2-oidc-sdk:7.3



## 9.0 (2020-03-18)

### Summary

* Updates support for OpenID Connect for Identity Assurance 1.0 to draft 09
  (see https://openid.net/specs/openid-connect-4-identity-assurance-1_0-09.html).
  Requested verified claims will now be automatically marked and processed as
  such in the authorisation session API. The requested "verification" and 
  individual claim "purpose" parameters will be presented in the consent 
  prompt.
  
* Updates the authorisation session API and the OAuth 2.0 grant handler SPIs to
  enable passing of additional JSON data with requests to the configured claims
  source(s). This can be used to pass claim values from the authorisation 
  handler or when implementing Identity Assurance the necessary verification 
  element for verified claims in the UserInfo response or the ID token.
  
  The included HTTP-based claims source SPI implementation is updated to 
  include a "claims_data" parameter (of type JSON object) in the request to 
  represent the optional claims data.
  
* Upgrades the Infinispan and backend database schemas for the identifier-based
  access tokens and the long-lived (persisted) authorisation records.
   
  On startup the Connect2id server will automatically create the new required 
  "id_access_tokens" and "long_lived_authorizations" table columns for a
  relational MySQL, PostgreSQL or Microsoft SQL Server database. 
  
  Connect2id server deployments with an LDAP v3 backend database (such as 
  OpenLDAP or OpenDJ) need to update the LDAP schema manually to version 1.12 
  see https://bitbucket.org/connect2id/server-ldap-schemas/src/1.10/ , the 
  OpenLDAP schema diff
  https://bitbucket.org/connect2id/server-ldap-schemas/diff/src/main/resources/
  oidc-authz-schema-openldap.ldif?at=1.12
  &diff1=49115daf531b48c2d9fd0f766721d84c28576eae
  &diff2=4c78a00734bfeb9abdd4c9dec76d9fbc51216faa  
  and the OpenDJ schema diff
  https://bitbucket.org/connect2id/server-ldap-schemas/diff/src/main/resources/
  oidc-authz-schema-opendj.ldif?at=1.12
  &diff1=49115daf531b48c2d9fd0f766721d84c28576eae
  &diff2=4c78a00734bfeb9abdd4c9dec76d9fbc51216faa
  
  Connect2id server deployments with a DynamoDB database are essentially 
  schema-less and no specific action is required.
  

### Configuration
      
* /WEB-INF/oidcProvider.properties

    * New "op.token.introspection.jwtType" configuration property. Sets the 
      "typ" (type) header of JWT introspection responses. The default value is 
      "token-introspection+jwt". This configuration allows the type header to 
      be set to "JWT" for non-compliant clients and JWT libraries which cannot 
      handle header values other than "JWT".
      
* /WEB-INF/log4j.xml

    * Adds an optional console (SYSTEM_OUT) appender. Setting the Java system
      property `log4j.loggers.root.appender` to `console` switches logging 
      from the `rolling-file` appender to the standard output. Can be useful 
      in container deployments.


### Web API

* /authz-sessions/rest/v3/

    * The consent prompt identifies requested verified OpenID claims by 
      prefixing their name with "verified:", for example "verified:given_name", 
      "verified:family_name" or "verified:address". When submitting consent to 
      the Connect2id server the names of the verified claims must also be 
      prefixed with "verified:".
      
      To process verified OpenID claims OpenID Connect for Identity Assurance 
      1.0 must be enabled (`op.assurance.supportsVerifiedClaims=true`).   
      
    * The consent prompt is updated to include the optional "purpose" attribute 
      of requested verified OpenID claims (OpenID Connect for Identity 
      Assurance 1.0) as well as regular claims. If the attribute is set for one 
      or more claims the purpose strings will appear in a new `claims.purposes` 
      JSON object containing the claim name / purpose string pairs.
      
      The accepted purpose string length is between 3 and 300 characters, 
      according to the Assurance specification. The Relying Party may use the 
      "ui_locales" OpenID authentication request parameter to set the 
      preferred language for the purpose strings.
      
      In order to prevent injection attacks all special characters in a 
      purpose string must be escaped before shown in a user interface.
      
    * The consent prompt is updated to include the optional "verification" JSON
      object for a requested verified claims set (OpenID Connect for Identity 
      Assurance 1.0). If the verification element is set for a requested 
      verified claims set to be returned in the UserInfo response it will 
      appear in a new `claims.verification.userinfo` JSON object. Likewise, if 
      the element is set for a requested claims set to be returned with the ID 
      token it will appear in a `claims.verifiction.id_token` JSON object.
      
      To include the "verification" element in the consent prompt OpenID 
      Connect for Identity Assurance 1.0 must be enabled 
      (`op.assurance.supportsVerifiedClaims=true`).
      
    * The consent is updated to include an optional "claims_data" JSON object 
      parameter. The data will be made available in the 
      `ClaimsSourceRequestContext.getClaimsData` method when the configured 
      claims source(s) get called at the UserInfo endpoint or when feeding the 
      consented claims into the ID token.
      
      The "claims_data" can be used to provision entire claims from the 
      authorisation session and the front-channel. It can also be used in 
      Identity Assurance to construct the "verification" element in the 
      authorisation session and then have it included in the UserInfo response, 
      for example in remote in-person proofing scenarios.
      
      The "claims_data" will be included in the issued access token and in 
      long-lived (persisted) authorisations in a new "cld" (claims data) JSON 
      object field. To keep the claims data confidential from the relying party 
      (client) either an identifier access token encoding must be chosen 
      (`access_token.encoding = IDENTIFIER` in the consent) or if a 
      self-contained (JWT) encoding is chosen the JWT must be additionally 
      encrypted (`access_token.encrypt = true`). 
      
      An AdvancedClaimsSource SPI implementation can retrieve the claims data
      JSON object by a call to the `ClaimsSourceRequestContext.getClaimsData`
      method.
      
* /authz-store/rest/v2/authorizations

    * The OAuth 2.0 / OpenID Connect authorisations includes a new optional 
      "cld" (claims data) JSON object field to represent claims data to be
      passed to the OpenID claims source(s) with access tokens consumed at the
      UserInfo endpoint.
      
* /token/introspect

    * The access token introspection response includes a new optional "cld" 
      (claims data) JSON object field to represent claims data to be passed to 
      the OpenID claims source(s) with access tokens consumed at the UserInfo 
      endpoint.


### SPI

Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.18

* com.nimbusds.openid.connect.provider.spi.claims.ClaimsSource, 
  AdvancedClaimsSource
  
    * The names of verified OpenID claims passed via the "claims" argument of
      the "getClaims" method will be prefixed with "verified:" by the 
      Connect2id server (OpenID Connect for Identity Assurance 1.0).  

* com.nimbusds.openid.connect.provider.spi.claims.ClaimsSourceRequestContext

    * Adds new "getClaimsData" method to obtain optional data set by an 
      authorisation handler to fulfill OpenID claims provision, for example 
      to construct the "verification" element for a verified claims set (OpenID
      Connect for Identity Assurance 1.0).
      
* com.nimbusds.openid.connect.provider.spi.tokens.AccessTokenAuthorization
    
    * Adds new "getClaimsData" method to the interface to represent OpenID 
      claims fulfillment data. The default implementation returns null.

* com.nimbusds.openid.connect.provider.spi.grants.BasicClaimsSpec

    * Adds new constructor and "getData" method for passing optional claims
      fulfillment data to the configured OpenID claims source(s).


### Resolved issues

* Fixes a security bug which caused the Connect2id server to redirect to a 
  supplied invalid redirect_uri in a OAuth 2.0 authorisation request when the 
  request includes a request object that doesn't parse to a valid JWT and the 
  "redirect_uri" is present as top-level parameter. The redirection will occur 
  with the error code and description from the failed request. The correct 
  behaviour is to not redirect back to the client with an error unless the 
  client_id and the redirect_uri are valid. Deployments are advised to update 
  to prevent potential misuse of such redirections (issue server/537).  

* Fixes a bug introduced in Connect2id server 8.1 which prevented output of the
  "verification" element in the OpenID "claims" authentication parameter output
  in /authz-sessions/rest/v3/ GET responses. The bug was caused by a faulty
  consent-all keyword sanitization (issue server/532).
  
* Removes an erroneous standard output print (issue server/535).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.18

* Upgrades to com.nimbusds:oauth2-oidc-sdk:7.2

* Updates to com.nimbusds:nimbus-jose-jwt:8.10

* Upgrades to com.nimbusds:oauth2-authz-store:14.4.1

* Updates to com.nimbusds:oidc-session-store:13.4.1

* Upgrades to com.nimbusds:oidc-claims-source-http:2.0

* Updates to commons-codec:commons-codec:1.14

* Updates to io.dropwizard.metrics:*:4.0.7

* Updates to io.prometheus:*:0.8.1

* Updates to org.apache.logging.log4j:*:2.13.1

* Updates to org.slf4j:slf4j-api:1.7.30

* Updates to com.amazonaws:aws-java-sdk-bundle:1.11.728

* Updates to Infinispan 9.4.18.Final.

* Updates to com.zaxxer:HikariCP:3.4.2

* Updates to com.h2database:h2:1.4.200

* Updates to org.mariadb.jdbc:mariadb-java-client:2.5.4

* Updates to org.postgresql:postgresql:42.2.10

* Updates to com.unboundid:unboundid-ldapsdk:5.0.1

* Updates to org.opensaml:*:3.4.3

* Upgrades to com.nimbusds:c2id-server-ldap-schemas:1.12



## 8.2 (2020-02-25)

### Configuration
      
* /WEB-INF/oidcProvider.properties

    * "op.token.authMethods" -- Basic authentication ("client_secret_basic") is 
      no longer required when configuring the enabled client authentication
      methods. Connect2id server deployments can now be configured with a 
      single enabled client authentication method other than Basic, for example 
      with "self_signed_tls_client_auth" only for client X.509 certificate
      authentication.   
      
      The first authentication method (ignoring "none") in the list will now 
      specify the default method for clients which don't set one explicitly 
      during registration.

* /WEB-INF/authzStore.properties

    * New "authzStore.refreshToken.defaultLifetime" configuration property.
      Specifies a default refresh token lifetime in seconds. Can be overridden 
      by individual authorisations. If zero or omitted defaults to permanent 
      (no expiration). Must be zero or a positive integer. The default value is 
      zero (no expiration).
      
* /WEB-INF/cors.properties

    * Any property in the configuration file can be overridden with a Java 
      system property, e.g. by setting the optional -D argument at JVM startup
      `-Dcors.allowOrigin=https://example.com`
      

### Web API

* /authz-sessions/rest/v3/

    * Includes the OpenID authentication request "purpose" parameter (from 
      OpenID Connect for Identity Assurance 1.0) in the "auth_req" JSON object
      which exposes selected request parameters when the authorisation session 
      is queried with a GET. Normally the "purpose" parameter is only provided
      during the consent step. With this the logic page can access it at any
      one time during the authorisation session.


### Resolved issues

* Fixes the URL encoding of the query parameters in front-channel logout 
  notification URIs. The query parameters were receiving a double URL encoding
  (issue server/520).
  
* Fixes an OpenID "claims" request parameter sanitisation bug which prevented 
  output of the parameter in the consent prompt when 
  op.authz.includeRawClaimsRequestInPrompt is enabled (issue server/523).
  
* Updates the UserInfo endpoint to log (INFO level, log line with ID OP7301) 
  the missing token scope if a bearer token error "insufficient_scope" is 
  returned (issue server/517). 


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.14

* Upgrades to com.nimbusds:oauth2-oidc-sdk:7.1

* Updates to com.nimbusds:nimbus-jose-jwt:8.8

* Upgrades to com.nimbusds:oauth2-authz-store:14.2

* Updates to org.bouncycastle:bcprov-jdk15on:1.64

* Updates to org.bouncycastle:bcpkix-jdk15on:1.64

* Upgrades to com.thetransactioncompany:cors-filter:2.9



## 8.1 (2020-02-03)

### Summary

* Updates mTLS client authentication by accepting client X.509 certificates 
  with additional URL-encoding on top of the PEM encoding when the certificate
  is received from a TLS termination proxy via the HTTP security header 
  configured by "op.tls.clientX509CertHeader".
  
* Adds a new "authzStore.options.issueLegacyRefreshTokens" configuration 
  property to facilitate seamless rolling cluster upgrades from Connect2id 
  server 7.x and earlier versions to 8.x. When this setting is enabled 
  Connect2id server 8.1 instances will issue refresh tokens in the old encoding
  supported and recognised in 7.x (instead of the new refresh token encoding 
  with additional encryption of metadata, introduced in 8.0). After the rolling 
  upgrade is completed and all instances are 8.1 the setting can be disabled 
  (on a subsequent upgrade) to start issuing new refresh tokens with the new 
  encoding.
 

### Configuration

* /WEB-INF/oidcProvider.properties

    * Client X.509 certificates received with the HTTP security header 
      configured by "op.tls.clientX509CertHeader" can have an optional 
      additional URL-encoding (also called percent encoding) of the PEM-encoded
      string. The presence of additional URL-encoding is automatically 
      detected.
      
* /WEB-INF/authzStore.properties

    * New "authzStore.options.issueLegacyRefreshTokens" configuration property.
      If `true` the Connect2id server will issue refresh tokens in the legacy 
      format supported up to v7.x. Intended to facilitate seamless rolling 
      cluster upgrades to v8.x and later without producing `invalid_grant`  
      errors when a new 8.x refresh token is used at a Connect2id server 7.x 
      instance. The default value is `false` (no issue of legacy refresh 
      tokens).


### Resolved issues

* Fixes parse exception reporting when reading DynamoDB items from 
  "pending_codes" and "id_access_tokens", logs JSON with error message (issue 
  authz-store/169).
  
* Fixes loading of OAuth 2.0 grant handler SPIs when multiple implementations 
  are available (broken in Connect2id server 8.0, issue server/515).
  
* Logs the names of the OAuth 2.0 grant handler SPI classes when multiple are 
  enabled for a given grant type (issue server/515).

* Logs the names of an SPI class when a single must be loaded and multiple are 
  available (issue common/60).
  
* Disables the setting of the "jsessionid" cookie in the Connect2id server 
  banner page (index.jsp) as no cookie or session is required by the page
  (issue server/516).  


### Dependency changes

* Upgrades to com.nimbusds:oauth2-authz-store:14.1

* Upgrades to com.nimbusds:nimbus-jose-jwt:8.5

* Upgrades to com.nimbusds:common:2.36

* Adds new dependency to io.github.cemiltokatli.uricomponent:uri-component:1.0



## 8.0 (2020-01-01)

### Summary

* Supports OAuth 2.0 Pushed Authorization Requests. See 
  https://tools.ietf.org/html/draft-ietf-oauth-par-00
  
* Supports OpenID Connect for Identity Assurance 1.0 (draft 08). See
  https://openid.net/specs/openid-connect-4-identity-assurance-1_0.html
  
* The default self-contained (JWT) access token codec implementing the 
  SelfContainedAccessTokenClaimsCodec SPI now supports multiple JWT profiles:
  
    * c2id-1.1 -- Connect2id server specific profile, available since v7.17, 
      with the JWT "typ" (type) header set to "at+jwt".
      
    * c2id-1.0 -- Connect2id server specific profile, available since v1.0, 
      outputs identical JWT claims as c2id-1.1 but doesn't set the JWT "typ" 
      (type) header.
      
    * oauth-1.0 -- Standard profile in development at the OAuth working group, 
      see draft-ietf-oauth-access-token-jwt-03
        
  The active JWT profile for access tokens is selected by a new optional 
  "authzStore.accessToken.codec.jwt.profile" configuration property.
  
* Consented OpenID claims can now be fed into the access token by prefixing 
  their name with "access_token:". For example, "access_token:email" will cause
  the "email" claim to be retrieved from the configured OpenID claims source 
  and fed into each access token for the given OAuth 2.0 grant. If the access 
  token is self-contained (JWT) the "email" claim will be added as a top-level 
  claim with the same name. If the access token is identifier-based the "email" 
  claim will appear as top-level claim in the token introspection response.  
  
  Two additional prefixes are supported:
  
    * "access_token:uip:" -- Causes the OpenID claim to be merged into the 
      top-level "uip" (optional preset UserInfo claims) JSON object claim.
        
    * "access_token:dat:" -- Causes the OpenID claim to be merged into the 
      top-level "dat" (optional data) JSON object claim.
        
  If the claim name clashes with an existing top-level access token claim it 
  will be ignored and not fed into the access token. 
  
* Identifier-based (or key-based) access tokens are now stored in hashed form 
  (SHA-256 truncated to 128 bits) to prevent token leakage if the database or a
  database backup is exposed and the HMAC SHA-256 key (the Connect2id server 
  JWK with key ID "hmac") to attach an authenticity and integrity code to each 
  token identifier is also compromised. Previously the only protection against 
  undetected token identifier leaks from the database or a backup was the HMAC 
  SHA-256 key remaining secret.
  
  When upgrading to Connect2id server 8.0 if there are existing 
  identifier-based access tokens in the database issued by a previous 
  Connect2id server version the  
  "authzStore.options.legacyPlainKeysInStorage" configuration property can be 
  set to `true` to enable their successful introspection, otherwise they will 
  be deemed invalid.
  
* Upgrades the Infinispan and backend database schemas for the identifier-based
  access tokens.
   
  On startup the Connect2id server will automatically create the new required 
  "id_access_tokens" table columns for a relational MySQL, PostgreSQL and 
  Microsoft SQL Server databases. 
  
  Connect2id server deployments with an LDAP v3 backend database (such as 
  OpenLDAP or OpenDJ) need to update the LDAP schema manually to version 1.10 
  see https://bitbucket.org/connect2id/server-ldap-schemas/src/1.10/ , the 
  OpenLDAP schema diff
  https://bitbucket.org/connect2id/server-ldap-schemas/diff/src/main/resources/
  oidc-authz-schema-openldap.ldif?at=1.10
  &diff1=d9599b598753ec3037b784b29baeac0e28bf7615
  &diff2=49115daf531b48c2d9fd0f766721d84c28576eae  
  and the OpenDJ schema diff
  https://bitbucket.org/connect2id/server-ldap-schemas/diff/src/main/resources/
  oidc-authz-schema-opendj.ldif?at=1.10
  &diff1=d9599b598753ec3037b784b29baeac0e28bf7615
  &diff2=49115daf531b48c2d9fd0f766721d84c28576eae
  
  Connect2id server deployments with a DynamoDB database are essentially 
  schema-less.
  
  Connect2id server deployments with Redis as the primary in-memory and caching 
  store use a new bucket (database) with number 14 to store tokens in the new
  format. Cached tokens in the old format in Redis bucket 4 will remain there
  until they expire.
  
* Introduces a new AES JSON Web Key (JWK) in the Connect2id server JWK set for
  encrypting and authenticating refresh tokens without the need for a database
  lookup. The encryption will also enable the inclusion of additional metadata 
  and the implementation of advanced security measures in future Connect2id 
  server versions. Existing refresh tokens without the additional encryption 
  layer will continue to be valid and will be handled transparently. 


### Configuration

* /WEB-INF/jwkSet.json

    * Introduces a new AES 256 bit octet sequence JWK with use "enc" 
      (encryption) and ID "refresh-token-encrypt" for encrypting issued refresh 
      tokens. This key is required, starting from Connect2id server 8.0.

* /WEB-INF/oidcProvider.properties

    * New "op.par.lifetime" configuration property which sets the lifetime of 
      the pushed authorisation requests (PAR), in seconds. Must not be shorter 
      than 10 seconds. The default value is 60 seconds.

    * New "op.authz.includeRawClaimsRequestInPrompt" configuration property 
      which enables / disables inclusion of the raw OpenID "claims" request 
      parameter in the consent prompts of the authorisation session web API, 
      under "claims" -> "raw_request". Access to the raw "claims" request 
      parameter may be required when processing requests for verified claims 
      (OpenID Connect for Identity Assurance 1.0). The default values is 
      `false` (disabled).

    * New "op.assurance.supportsVerifiedClaims" configuration property which 
      enables / disables advertisement of OpenID Connect for Identity Assurance 
      1.0 support in OpenID provider metadata and inclusion of the optional 
      transaction specific "purpose" OpenID authentication parameter in the 
      consent prompts of the authorisation session web API. Corresponds to the 
      "verified_claims_supported" OpenID provider metadata parameter. The 
      default values is `false` (disabled).
      
    * New "op.assurance.supportedTrustFrameworks" configuration property. Lists 
      the supported trust frameworks if OpenID Connect for Identity Assurance 
      1.0 support is enabled. Corresponds to the "trust_frameworks_supported" 
      OpenID provider metadata parameter.
      
    * New "op.assurance.supportedIdentityEvidenceTypes" configuration property. 
      Lists the supported identity evidence types if OpenID Connect for 
      Identity Assurance 1.0 support is enabled. Corresponds to the 
      "evidence_supported" OpenID provider metadata parameter.
      
    * New "op.assurance.supportedIDDocumentTypes" configuration property. Lists 
      the supported ID document types if OpenID Connect for Identity Assurance 
      1.0 support is enabled. Corresponds to the "id_documents_supported" 
      OpenID provider metadata parameter.
      
    * New "op.assurance.supportedIdentityVerificationMethods" configuration 
      property. Lists the supported identity verification methods if OpenID 
      Connect for Identity Assurance 1.0 support is enabled. Corresponds to the 
      "id_documents_verification_methods_supported" OpenID provider metadata 
      parameter.
      
    * New "op.assurance.supportedVerifiedClaims" configuration property. Lists 
      the supported verified claims if OpenID Connect for Identity Assurance 
      1.0 support is enabled. Corresponds to the 
      "claims_in_verified_claims_supported" OpenID provider metadata parameter.
      
* /WEB-INF/authzStore.properties

    * New "authzStore.accessToken.codec.jwt.profile" configuration property.
      Sets the JWT profile to use for access tokens minted by the default 
      self-contained access token codec. See the 
      SelfContainedAccessTokenClaimsCodec SPI JavaDoc for more information.
      
      Supported profiles:
      
        * c2id-1.1 -- Connect2id server specific profile, available since 
          v7.17, with the JWT "typ" (type) header set to "at+jwt".
          
        * c2id-1.0 -- Connect2id server specific profile, available since v1.0, 
          outputs identical JWT claims as c2id-1.1 but doesn't set the JWT 
          "typ" (type) header.
          
        * oauth-1.0 -- Standard profile in development at the OAuth working 
          group, see draft-ietf-oauth-access-token-jwt-03
          
      The default value is `c2id-1.1`.

    * New "authzStore.options.legacyPlainKeysInStorage" configuration property.
      If `true` the Connect2id server will support retrieval of legacy plain
      keys for identifier-based access tokens from storage after upgrading to 
      Connect2id server 8.x, `false` to ignore such keys which will cause the 
      introspection of the linked access tokens to flag them as invalid. Note 
      that enabling this configuration causes additional database reads and a 
      performance penalty during introspection. The default value is `false` 
      (no support for legacy plain access token keys).
      
* /WEB-INF/log4j.xml

    * The root logger level ("INFO") can now be overridden by setting the 
      "log4j.level" Java system property.
      
* /WEB-INF/infinispan-*.xml

    * Updates the data structure for the identifier-based access tokens, which 
      is now named "authzStore.idAccessTokenMap".
      
* /WEB-INF/infinispan-*-redis-*.xml

    * Deployments using Redis as primary caching and in-memory store will use
      a new bucket with number 14 for the identifier-based access tokens.
      Cached tokens in the old format in Redis bucket 4 will remain there until 
      they expire.
      

### Web API

* /par

    * New endpoint for receiving back-channel OAuth 2.0 authorisation and 
      OpenID authentication requests, also called Pushed Authorization 
      Requests (PAR). Confidential clients must authenticate with the same 
      registered method as for the token endpoint. The complete specification 
      is at https://tools.ietf.org/html/draft-ietf-oauth-par-00

* /authz-sessions/rest/v3/

    * If `op.authz.includeRawClaimsRequestInPrompt=true` the raw OpenID
      "claims" request parameter will be included in the consent prompt API 
      responses (as JSON object member "claims" -> "raw_request").

    * If OpenID Connect for Identity Assurance 1.0 is enabled 
      (`op.assurance.supportsVerifiedClaims=true`) and the OpenID 
      authentication request includes the transaction specific `purpose` 
      parameter, the parameter will be included in the consent prompt API 
      responses (as JSON object member `purpose`).
    
    * The "claims" parameter of the consent objects enables OpenID claims to be 
      fed into the access token by prefixing their name with "access_token:". 
      For example, "access_token:email" will cause the "email" claim to be 
      retrieved from the configured OpenID claims source and fed into each 
      access token for the given OAuth 2.0 grant. If the access token is 
      self-contained (JWT) the "email" claim will be added as a top-level claim 
      with the same name. If the access token is identifier-based the "email" 
      claim will appear as top-level claim in the token introspection response.  
      
      Two additional prefixes are supported:
      
        * "access_token:uip:" -- Causes the OpenID claim to be merged into the 
          top-level "uip" (optional preset UserInfo claims) JSON object claim.
            
        * "access_token:dat:" -- Causes the OpenID claim to be merged into the 
          top-level "dat" (optional data) JSON object claim.
            
      If the claim name clashes with an existing top-level access token claim 
      it will be ignored and not fed into the access token.
    
* /monitor/v1/metrics

    * Adds new meters for the Pushed Authorisation Request (PAR) endpoint:
    
        * "parEndpoint.successfulRequests" -- Meters successful PAR requests.
        
        * "parEndpoint.invalidRequests" -- Meters invalid PAR requests.
         
        * "parEndpoint.invalidClientErrors" -- Meters "invalid_client" errors
          at the PAR endpoint.
          
        * "parEndpoint.serverErrors", serverErrors -- Meters server errors
          (HTTP 500) at the PAR endpoint.


### SPI

Upgrades the Connect2id server SDK to com.nimbusds:c2id-server-sdk:4.12

* com.nimbusds.openid.connect.provider.spi.par.PARValidator

    * New SPI for performing additional validation of received Pushed 
      Authorisation Requests (PAR). See 
      https://www.javadoc.io/doc/com.nimbusds/c2id-server-sdk/4.12/
      com/nimbusds/openid/connect/provider/spi/par/PARValidator.html 

* com.nimbusds.openid.connect.provider.spi.tokens
      
    * The self-contained and identifier-based access token codecs can now be
      configured via Connect2id server properties which are obtained by 
      calling the `TokenCodecContext.getCodecProperties` method, for example to 
      enable support of multiple token profiles.
      
    * The self-contained access token codec interface 
      (`SelfContainedAccessTokenClaimsCodec`) is extended with new 
      `advancedEncode` and `advancedDecode` methods to enable setting and 
      validation of the JWT "typ" (type) header.
      
    * The `AccessTokenAuthorization` interface adds support for custom 
      top-level access token claims via the new `getOtherTopLevelParameters`
      method.


### Resolved issues

* Fixes the expiration logic for refresh tokens when the Connect2id server is
  configured with `authzStore.refreshToken.alwaysUpdate=true` (issue 
  authz-store/166).

* Fixes "authzStore.accessTokenIssues" metering on successful refresh token 
  usage (issue authz-store/167). 

* Fixes persistence of client_id metadata for cached request_uri claims in 
  multi-tenant Connect2id server deployments (issue server/507).
  
* Updates AWS SDK to 1.11.632 to support IAM role for Amazon EKS (issue 
  server/503).


### Dependency changes

* Upgrades to com.nimbusds:c2id-server-sdk:4.12
  
* Upgrades to com.nimbusds:oauth2-oidc-sdk:6.21.2

* Upgrades to com.nimbusds:nimbus-jose-jwt:8.4

* Updates to org.cryptomator:siv-mode:1.2.2

* Updates to com.nimbusds:oidc-session-store:13.4

* Upgrades to com.nimbusds:oauth2-authz-store:14.0.6

* Updates to com.nimbusds:infinispan-cachestore-dynamodb:3.5.2

* Upgrades to com.nimbusds:infinispan-cachestore-sql:4.1.1

* Updates to org.postgresql:postgresql:42.2.8

* Updates to com.unboundid:unboundid-ldapsdk:4.0.14

* Updates to com.amazonaws:aws-java-sdk-bundle:1.11.632

* Upgrades to com.nimbusds:c2id-server-ldap-schemas:1.10

* Updates to Infinispan 9.4.17.Final.

* Updates to com.nimbusds:jgroups-dynamodb-ping:1.2.3
