<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         session="false"
         import="com.nimbusds.tenants.manager.*,
                 com.nimbusds.oauth2.sdk.as.*,
                 com.nimbusds.openid.connect.sdk.op.*,
                 com.nimbusds.openid.connect.provider.*,
                 com.nimbusds.openid.connect.provider.config.*,
                 com.nimbusds.openid.connect.provider.splash.*" %><%

    OIDCProviderMetadata opMetadata = null;
    boolean invalidTenantError = false;
    try {
        TenantContext tenantCtx = ProviderContext.from(application)
                .getTenantResolver()
                .resolveTenantContext(request.getHeader("Issuer"), request.getHeader("Tenant-ID"));

        SplashPage splashPage = new Configuration(tenantCtx.getTenantProperties()).splashPage;

        if (SplashPage.DEFAULT.equals(splashPage)) {
            // continue with default page
            opMetadata = ProviderContext.from(application).getProviderMetadataFactory().compose(tenantCtx);
        } else if (SplashPage.BLANK.equals(splashPage)) {
            response.setStatus(200);
            return;
        } else if (SplashPage.OP_METADATA.equals(splashPage)) {
            response.setStatus(301);
            response.setHeader("Location", tenantCtx.getIssuer() + OIDCProviderConfigurationRequest.OPENID_PROVIDER_WELL_KNOWN_PATH);
            return;
        } else {
            response.setStatus(301);
            response.setHeader("Location", splashPage.getURI().toString());
            return;
        }

    } catch (TenantUnavailableException e) {
        response.setStatus(400); // Bad Request
        invalidTenantError = true;
    }
%><!DOCTYPE html>
<html lang="en">
	<head>
		<title>Connect2id Server</title>
        <% response.setHeader("Content-Security-Policy", "default-src 'self'"); %>
		<meta charset="utf-8">
        <meta content="no-cache" http-equiv="cache-control">
        <link rel="stylesheet" type="text/css" href="css/font-ptsans.css">
        <link rel="stylesheet" type="text/css" href="css/normalize.css">
        <link rel="stylesheet" type="text/css" href="css/main.css">
	</head>
	<body>

		<h1>Connect2id Server <%= ProviderSpec.SOFTWARE_VERSION %></h1>

		<%

        if (invalidTenantError) {

            %><h2>Error: Invalid tenant or tenant disabled</h2><%

	    } else {

            %><h2>Standard OAuth 2.0 / OpenID Connect 1.0 endpoints:</h2>

            <table>
                <colgroup>
                    <col class="col-1"/>
                    <col class="col-2"/>
                    <col class="col-3"/>
                </colgroup>
                <tr>
                    <td>Server metadata</td>
                    <%
                        String opMetadataURLString = opMetadata.getIssuer() + OIDCProviderConfigurationRequest.OPENID_PROVIDER_WELL_KNOWN_PATH;
                        String asMetadataURLString = opMetadata.getIssuer() + AuthorizationServerConfigurationRequest.OAUTH_SERVER_WELL_KNOWN_PATH;
                    %>
                    <td><a href="<%= opMetadataURLString %>"><%= opMetadataURLString %></a><br/>
                        <a href="<%= asMetadataURLString %>"><%= asMetadataURLString %></a></td>
                    <td><a href="https://connect2id.com/products/server/docs/api/discovery">docs</a></td>
                </tr>
                <tr>
                    <td>Federation metadata</td>
                    <% if (opMetadata.getClientRegistrationTypes() != null) {
                        %><td><a href="<%= opMetadata.getIssuer() %>/.well-known/openid-federation"><%= opMetadata.getIssuer() %>/.well-known/openid-federation</a></td><%
                    } else {
                        %><td>disabled</td><%
                    } %>
                    <td><a href="https://connect2id.com/products/server/docs/api/federation-entity-configuration">docs</a></td>
                </tr>
                <tr>
                    <td>JWK set</td>
                    <td><a href="<%= opMetadata.getJWKSetURI() %>"><%= opMetadata.getJWKSetURI() %></a></td>
                    <td><a href="https://connect2id.com/products/server/docs/api/jwk-set">docs</a></td>
                </tr>
                <tr>
                    <td>Signed JWK set</td>
                    <% if (opMetadata.getSignedJWKSetURI() != null) {
			            %><td><a href="<%= opMetadata.getSignedJWKSetURI() %>"><%= opMetadata.getSignedJWKSetURI() %></a></td><%
                    } else {
                        %><td>disabled</td><%
                    } %>
                    <td><a href="https://connect2id.com/products/server/docs/api/jwk-set">docs</a></td>
                </tr>
                <tr>
                    <td>Client registration</td>
                    <td><a href="<%= opMetadata.getRegistrationEndpointURI() %>"><%= opMetadata.getRegistrationEndpointURI() %></a></td>
                    <td><a href="https://connect2id.com/products/server/docs/api/client-registration">docs</a></td>
                </tr>
                <tr>
                    <td>Federation client registration</td>
                    <% if (opMetadata.getFederationRegistrationEndpointURI() != null) {
                        %><td><a href="<%= opMetadata.getFederationRegistrationEndpointURI() %>"><%= opMetadata.getFederationRegistrationEndpointURI() %></a></td><%
                    } else {
                        %><td>disabled</td><%
                    } %>
                    <td><a href="https://connect2id.com/products/server/docs/api/federation-client-registration">docs</a></td>
                </tr>
                <tr>
                    <td>PAR</td>
                    <td><a href="<%= opMetadata.getPushedAuthorizationRequestEndpointURI() %>"><%= opMetadata.getPushedAuthorizationRequestEndpointURI() %></a></td>
                    <td><a href="https://connect2id.com/products/server/docs/api/par">docs</a></td>
                </tr>
                <tr>
                    <td>Authorization</td>
                    <td><a href="<%= opMetadata.getAuthorizationEndpointURI() %>"><%= opMetadata.getAuthorizationEndpointURI() %></a></td>
                    <td><a href="https://connect2id.com/products/server/docs/api/authorization">docs</a></td>
                </tr>
                <tr>
                    <td>CIBA</td>
                    <td><a href="<%= opMetadata.getBackChannelAuthenticationEndpointURI() %>"><%= opMetadata.getBackChannelAuthenticationEndpointURI() %></a></td>
                    <td><a href="https://connect2id.com/products/server/docs/api/ciba">docs</a></td>
                </tr>
                <tr>
                    <td>Token</td>
                    <td><a href="<%= opMetadata.getTokenEndpointURI() %>"><%= opMetadata.getTokenEndpointURI() %></a></td>
                    <td><a href="https://connect2id.com/products/server/docs/api/token">docs</a></td>
                </tr>
                <tr>
                    <td>Token introspection</td>
                    <td><a href="<%= opMetadata.getIntrospectionEndpointURI() %>"><%= opMetadata.getIntrospectionEndpointURI() %></a></td>
                    <td><a href="https://connect2id.com/products/server/docs/api/token-introspection">docs</a></td>
                </tr>
                <tr>
                    <td>Token revocation</td>
                    <td><a href="<%= opMetadata.getRevocationEndpointURI() %>"><%= opMetadata.getRevocationEndpointURI() %></a></td>
                    <td><a href="https://connect2id.com/products/server/docs/api/token-revocation">docs</a></td>
                </tr>
                <tr>
                    <td>UserInfo</td>
                    <td><a href="<%= opMetadata.getUserInfoEndpointURI() %>"><%= opMetadata.getUserInfoEndpointURI() %></a></td>
                    <td><a href="https://connect2id.com/products/server/docs/api/userinfo">docs</a></td>
                </tr>
                <tr>
                    <td>Check session</td>
                    <% if (opMetadata.getCheckSessionIframeURI() != null) {
                        %><td><a href="<%= opMetadata.getCheckSessionIframeURI() %>"><%= opMetadata.getCheckSessionIframeURI() %></a></td><%
                    } else {
                        %><td>disabled</td><%
                    } %>
                    <td><a href="https://connect2id.com/products/server/docs/api/check-session">docs</a></td>
                </tr>
                <tr>
                    <td>End session</td>
                    <% if (opMetadata.getEndSessionEndpointURI() != null) {
                        %><td><a href="<%= opMetadata.getEndSessionEndpointURI() %>"><%= opMetadata.getEndSessionEndpointURI() %></a></td><%
                    } else {
                        %><td>disabled</td><%
                    } %>
                    <td><a href="https://connect2id.com/products/server/docs/api/logout">docs</a></td>
                </tr>
            </table>

            <h2>Integration endpoints:</h2>

            <% String baseURL = opMetadata.getIssuer() + "/"; %>

            <table>
                <colgroup>
                    <col class="col-1"/>
                    <col class="col-2"/>
                    <col class="col-3"/>
                </colgroup>
                <tr>
                    <td>Authorization session</td>
                    <td><a href="<%= baseURL %>authz-sessions/rest/v3"><%= baseURL %>authz-sessions/rest/v3</a></td>
                    <td><a href="https://connect2id.com/products/server/docs/integration/authz-session">docs</a></td>
                </tr>
                <tr>
                    <td>Logout session</td>
                    <td><a href="<%= baseURL %>logout-sessions/rest/v1"><%= baseURL %>logout-sessions/rest/v1</a><br/>
                    <td><a href="https://connect2id.com/products/server/docs/integration/logout-session">docs</a></td>
                </tr>
                <tr>
                    <td>CIBA sessions</td>
                    <td><a href="<%= baseURL %>ciba-sessions/rest/v1"><%= baseURL %>ciba-sessions/rest/v1</a></td>
                    <td><a href="https://connect2id.com/products/server/docs/integration/ciba">docs</a></td>
                </tr>
                <tr>
                    <td>Direct authorization</td>
                    <td><a href="<%= baseURL %>direct-authz/rest/v2"><%= baseURL %>direct-authz/rest/v2</a></td>
                    <td><a href="https://connect2id.com/products/server/docs/integration/direct-authz">docs</a></td>
                </tr>
                <tr>
                    <td>Subject session store</td>
                    <td><a href="<%= baseURL %>session-store/rest/v2"><%= baseURL %>session-store/rest/v2</a></td>
                    <td><a href="https://connect2id.com/products/server/docs/integration/session-store">docs</a></td>
                </tr>
                <tr>
                    <td>Authorization store</td>
                    <td><a href="<%= baseURL %>authz-store/rest/v3"><%= baseURL %>authz-store/rest/v3</a><br/>
                        <a href="<%= baseURL %>authz-store/rest/v2"><%= baseURL %>authz-store/rest/v2</a></td>
                    <td><a href="https://connect2id.com/products/server/docs/integration/authz-store">docs</a><br/>
                        <a href="https://connect2id.com/products/server/docs/archive/v9/integration/authz-store">docs</a></td>
                </tr>
                <tr>
                    <td>Key store</td>
                    <td><a href="<%= baseURL %>key-store/rest/v1"><%= baseURL %>key-store/rest/v1</a></td>
                    <td><a href="https://connect2id.com/products/server/docs/integration/key-store">docs</a></td>
                </tr>
                <tr>
                    <td>Security Token Service (STS)</td>
                    <td><a href="<%= baseURL %>sts/rest/v1"><%= baseURL %>sts/rest/v1</a><br/>
                    <td><a href="https://connect2id.com/products/server/docs/integration/sts">docs</a></td>
                </tr>
                <tr>
                    <td>Monitoring</td>
                    <td><a href="<%= baseURL %>monitor/v1"><%= baseURL %>monitor/v1</a></td>
                    <td><a href="https://connect2id.com/products/server/docs/integration/monitoring">docs</a></td>
                </tr>
                <tr>
                    <td>Configuration check</td>
                    <td><a href="<%= baseURL %>config/check"><%= baseURL %>config</a></td>
                    <td><a href="https://connect2id.com/products/server/docs/integration/config">docs</a></td>
                </tr>
            </table>

            <h2>Notes:</h2>

            <table style="width: 45em">
                <tr>
                    <td>Use the dedicated <a href="https://connect2id.com/products/server/docs/integration/monitoring#healthcheck">resource</a>
                        for Connect2id server health checks, not this URL.</td>
                </tr>
            </table>

        <%
	    } // if (invalidTenantError)
	    %>

		<hr/>

		<p><a href="https://connect2id.com"><img id="logo" src="css/connect2id-logo.png" alt="Connect2id"/></a>
		  &copy; 2012 - 2025, <a href="https://connect2id.com">Connect2id Ltd.</a></p>

	</body>
</html>
