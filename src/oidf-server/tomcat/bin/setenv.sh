#!/bin/sh

# Set min heap memory
export CATALINA_OPTS="$CATALINA_OPTS -Xms512M"

# Set max heap memory
export CATALINA_OPTS="$CATALINA_OPTS -Xmx4096M"

# Halve thread stack size to allow more threads
export CATALINA_OPTS="$CATALINA_OPTS -Xss512k"

# Use server-specific optimisations
export CATALINA_OPTS="$CATALINA_OPTS -server"

# Make UTF-8 the default encoding for file operations (Windows)
export CATALINA_OPTS="$CATALINA_OPTS -Dfile.encoding=utf-8"

# Faster seeding of secure random generator
export CATALINA_OPTS="$CATALINA_OPTS -Djava.security.egd=file:/dev/./urandom"

# Connect2id server login page settings
export CATALINA_OPTS="$CATALINA_OPTS -Dc2id.endpoint=http://127.0.0.1:8080/c2id"
export CATALINA_OPTS="$CATALINA_OPTS -Dc2id.ldapAuth.endpoint=http://127.0.0.1:8080/ldapauth/"


echo "Using CATALINA_OPTS: $CATALINA_OPTS"
echo "Using JAVA_OPTS: $JAVA_OPTS"
