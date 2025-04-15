#!/bin/bash

export IGNITE_HOME="$PWD/ignite"

export JDK_JAVA_OPTIONS="-Dloader.path=WEB-INF/lib-provided,WEB-INF/lib,WEB-INF/classes,file:lib/ \
-Djava.net.preferIPv4Stack=true \
-Djava.security.egd=file:/dev/./urandom \
-DIGNITE_UPDATE_NOTIFIER=false \
-DIGNITE_QUIET=true \
-Dio.netty.tmpdir=$PWD \
-Djava.io.tmpdir=$PWD \
-Dio.netty.native.workdir=$PWD \
-Djdk.tls.client.protocols=TLSv1.2 \
--add-opens=java.base/sun.security.pkcs=ALL-UNNAMED \
--add-opens=java.base/sun.security.pkcs10=ALL-UNNAMED \
--add-opens=java.base/sun.security.util=ALL-UNNAMED \
--add-opens=java.base/sun.security.x509=ALL-UNNAMED \
--add-opens=java.base/sun.security.tools.keytool=ALL-UNNAMED \
--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED \
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
--add-opens=java.management/com.sun.jmx.mbeanserver=ALL-UNNAMED \
--add-opens=jdk.internal.jvmstat/sun.jvmstat.monitor=ALL-UNNAMED \
--add-opens=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED \
--add-opens=jdk.management/com.sun.management.internal=ALL-UNNAMED \
--add-opens=java.base/java.io=ALL-UNNAMED \
--add-opens=java.base/java.nio=ALL-UNNAMED \
--add-opens=java.base/java.util=ALL-UNNAMED \
--add-opens=java.base/java.util.concurrent=ALL-UNNAMED \
--add-opens=java.base/java.util.concurrent.locks=ALL-UNNAMED \
--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED \
--add-opens=java.base/java.lang=ALL-UNNAMED \
--add-opens=java.base/java.lang.invoke=ALL-UNNAMED \
--add-opens=java.base/java.math=ALL-UNNAMED \
--add-opens=java.sql/java.sql=ALL-UNNAMED \
--add-opens=java.base/javax.net.ssl=ALL-UNNAMED \
--add-opens=java.base/java.net=ALL-UNNAMED \
--add-opens=jdk.unsupported/sun.misc=ALL-UNNAMED \
--add-opens=java.base/java.lang.ref=ALL-UNNAMED \
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
--add-opens=java.base/java.security=ALL-UNNAMED \
--add-opens=java.base/java.security.ssl=ALL-UNNAMED \
--add-opens=java.base/java.security.cert=ALL-UNNAMED \
--add-opens=java.base/sun.security.rsa=ALL-UNNAMED \
--add-opens=java.base/sun.security.ssl=ALL-UNNAMED \
--add-opens=java.base/sun.security.x500=ALL-UNNAMED \
--add-opens=java.base/sun.security.pkcs12=ALL-UNNAMED \
--add-opens=java.base/sun.security.provider=ALL-UNNAMED \
--add-opens=java.base/javax.security.auth.x500=ALL-UNNAMED"

# Run the configuration microservice in background and redirect output
java -server \
-XX:+AlwaysPreTouch \
-XX:+UseG1GC \
-XX:+ScavengeBeforeFullGC \
-XX:+DisableExplicitGC \
-Xmx2g \
-Dspring.profiles.active=config,duplicatelogs \
-jar ../build/libs/takserver-core-5.2-RELEASE-16.war > takserver_config.log 2>&1 &

# Save the process ID of configuration microservice
echo $! > takserver_config.pid
echo "TAK Server configuration microservice started with PID $(cat takserver_config.pid)"

# Run the messaging microservice in background and redirect output
java -server \
-XX:+AlwaysPreTouch \
-XX:+UseG1GC \
-XX:+ScavengeBeforeFullGC \
-XX:+DisableExplicitGC \
-Xmx2g \
-Dspring.profiles.active=messaging,duplicatelogs \
-jar ../build/libs/takserver-core-5.2-RELEASE-16.war > takserver_messaging.log 2>&1 &

# Save the process ID of messaging microservice
echo $! > takserver_messaging.pid
echo "TAK Server messaging microservice started with PID $(cat takserver_messaging.pid)"

# Run the API microservice in background and redirect output
java -server \
-XX:+AlwaysPreTouch \
-XX:+UseG1GC \
-XX:+ScavengeBeforeFullGC \
-XX:+DisableExplicitGC \
-Xmx2g \
-Dspring.profiles.active=api,duplicatelogs \
-Dkeystore.pkcs12.legacy \
-jar ../build/libs/takserver-core-5.2-RELEASE-16.war > takserver_api.log 2>&1 &

# Save the process ID of API microservice
echo $! > takserver_api.pid
echo "TAK Server API microservice started with PID $(cat takserver_api.pid)"

# Print the takserver-messaging.log
tail -f logs/takserver-messaging.log
