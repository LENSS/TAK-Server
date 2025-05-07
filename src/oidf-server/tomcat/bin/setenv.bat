# Set min heap memory
set "CATALINA_OPTS=%CATALINA_OPTS% -Xms512M"

# Set max heap memory
set "CATALINA_OPTS=%CATALINA_OPTS% -Xmx4096M"

# Halve thread stack size to allow more threads
set "CATALINA_OPTS=%CATALINA_OPTS% -Xss512k"

# Use server-specific optimisations
set "CATALINA_OPTS=%CATALINA_OPTS% -server"

# Make UTF-8 the default encoding for file operations (Windows)
set "CATALINA_OPTS=%CATALINA_OPTS% -Dfile.encoding=utf-8"

# Connect2id server login page settings
set "CATALINA_OPTS=%CATALINA_OPTS% -Dc2id.endpoint=http://127.0.0.1:8080/c2id"
set "CATALINA_OPTS=%CATALINA_OPTS% -Dc2id.ldapAuth.endpoint=http://127.0.0.1:8080/ldapauth/"

echo "Using CATALINA_OPTS: %CATALINA_OPTS%"
echo "Using JAVA_OPTS: %JAVA_OPTS%"
