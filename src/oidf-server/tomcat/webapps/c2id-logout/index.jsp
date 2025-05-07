<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.nimbusds.oauth2.sdk.*" %>
<%@ page import="com.nimbusds.openid.connect.provider.logoutpage.*" %>
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

        // Call the Connect2id server to process the logout request
        LogoutAPIResponseMessage message = client.postRequest(request);

        if (message instanceof LogoutPrompt) {

            LogoutPrompt logoutPrompt = (LogoutPrompt)message;

            %>
            <h2>Do you wish to log out of the OpenID provider?</h2>
            <form id="logout-confirmation-form" action="logout-end.jsp" method="post">
                <input type="hidden" name="logout_sid" value="<%= logoutPrompt.getLogoutSID() %>"/>
                <div class="row">
                    <div class="col-xs-3"></div>
                    <div class="col-xs-3">
                        <button type="submit" class="btn btn-primary btn-block" name="op_logout" value="true">Yes</button>
                    </div>
                    <div class="col-xs-3">
                        <button type="submit" class="btn btn-primary btn-block" name="op_logout" value="false">No</button>
                    </div>
                    <div class="col-xs-3"></div>
                </div>
            </form>
            <%

        } else if (message instanceof LogoutError) {

            // The logout request could not be processed, display error message to the user

            LogoutError logoutError = (LogoutError)message;

            ErrorObject error = logoutError.getErrorObject();

            %><h2>Invalid logout request</h2><%
            %><a href="javascript:" id="show">Details...</a> <a href="javascript:" id="hide">Hide details</a><%
            %><p class="error pad">error: <%= error.getCode() %></p><%
            %><p class="error pad">error_description: <%= error.getDescription() %></p><%

        } else if (message instanceof LogoutEnd) {

            %><h2>Invalid or expired OpenID provider session</h2><%
            %><p class="pad">You can close this window now</p><%

        }
        %>
        </div>
    </div>

    <jsp:include page="footer.jsp"/>

    <script src="app/js/main.js" type="application/javascript"></script>
</body>
</html>