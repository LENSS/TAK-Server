Connect2id Server 18.0


README


This package contains a sample Connect2id server deployment for testing, 
evaluation and development purposes.


System requirements:

* 2+ GBytes of system RAM.

* Java 11+.
  
* Backend SQL or LDAP database (embedded H2 database included in this package).

* Java Servlet 3.0 Container (included in this package).


Quick start:

1. Start the Apache Tomcat web server, which has preinstalled and configured
   instances of the Connect2id server and an H2 database for persisting its 
   data, the Connect2id login page, an LdapAuth service for authenticating 
   users against an included in-memory LDAP directory, and an OpenID Connect 
   relying party client.
   
   To start Apache Tomcat:
   
       connect2id-server-18.0/tomcat/bin/startup.sh
   
   To access the Tomcat management UI point your browser to
   
       http://127.0.0.1:8080/manager
   
   The Apache Tomcat admin credentials are:
   
       * Username: admin
       * Password: secret
   
   
2. Point your favourite web browser to the URL of the example OpenID Connect 
   client to test authentication and UserInfo retrieval with the Connect2id 
   server:
   
   http://127.0.0.1:8080/oidc-client



Questions or comments? Email us at support@connect2id.com


2025-04-02
