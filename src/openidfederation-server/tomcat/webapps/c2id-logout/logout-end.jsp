<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.URI" %>
<%@ page import="java.util.List" %>
<%@ page import="com.nimbusds.openid.connect.provider.logoutpage.CookieRemoval" %>
<%@ page import="com.nimbusds.openid.connect.provider.logoutpage.LogoutAPIClient" %>
<%@ page import="com.nimbusds.openid.connect.provider.logoutpage.LogoutEnd" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Connect2id server logout</title>
<link rel="stylesheet" type="text/css" href="app/content/css/bootstrap.css"/>
<link rel="stylesheet" type="text/css" href="app/content/css/main.css"/>
</head>
<body>

    <div class="navbar"></div>

    <div class="container voffset3">
        <div class="well">
            <%
                LogoutAPIClient client = (LogoutAPIClient)application.getAttribute(LogoutAPIClient.SERVLET_CTX_NAME);

                if ("true".equals(request.getParameter("op_logout"))) {
                    // Remove cookie if user chose to also log out of the Connect2id server
                    CookieRemoval.removeCookie(client.getConfiguration().getCookieName(), response);

                    %><h2>You have been logged out</h2><%
                }

                // Process the logout confirmation with the Connect2id server
                LogoutEnd logoutEnd = client.putConfirmation(request);

                // Check if front-channel logout GETs should be delivered to registered RPs
                List<URI> frontChannelLogoutURLs = logoutEnd.getFrontChannelLogoutURIs();

                if (frontChannelLogoutURLs != null) {
                    for (URI url: frontChannelLogoutURLs) {
                        // Render in hidden iframe
                        %><iframe src="<%= url.toString() %>" style="width:0;height:0;border:0;border:none;"></iframe><%
                    }
                }

                // Check if a post-logout redirection is requested
                URI redirectURL = logoutEnd.getPostLogoutRedirectionURI() != null ?
                        logoutEnd.getPostLogoutRedirectionURI() :
                        client.getConfiguration().getDefaultPostLogoutRedirectionURI();

                if (redirectURL != null) {
                    // Redirect to another page after 3 seconds
                    %><script type="application/javascript">setTimeout(function () {
                        window.location.replace("<%= redirectURL %>");
                    }, 3000)</script>
                    <p class="pad">You are being redirected to <a href="<%= redirectURL %>"><%= redirectURL %></a></p>
                    <%
                } else {
                    %><p class="pad">You can close this window now</p><%
                }
            %>
        </div>
    </div>

    <jsp:include page="footer.jsp"/>
</body>
</html>