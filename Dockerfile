####
# JVM image (Java 25) with the admin frontend (Vite/Vue) included.
#
# The admin app in src/main/resources/META-INF/resources/admin generates artifacts in
# META-INF/resources/dashboard/ (URL /dashboard), served by Quarkus.
#
# From the repository root:
#
#   docker build -t quarkus/users-jvm-admin .
#
#   docker run --rm -p 8080:8080 quarkus/users-jvm-admin
#
# Requirements: only Docker (Node and Maven run inside the build).
####

# --- 1) Build Quarkus + admin (npm via exec-maven-plugin)
# Node.js is installed in the Maven stage because exec-maven-plugin runs
# "npm ci" and "npm run build" during the generate-resources phase.
FROM maven:3-eclipse-temurin-25 AS maven-build
WORKDIR /build
RUN apt-get update -q && \
    apt-get install -y --no-install-recommends nodejs npm && \
    rm -rf /var/lib/apt/lists/*
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

# --- 2) Runtime (JRE 25)
FROM eclipse-temurin:25-jre-noble
ENV LANG='en_US.UTF-8' \
    LANGUAGE='en_US:en' \
    JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

WORKDIR /deployments
COPY --from=maven-build /build/target/quarkus-app/lib/ ./lib/
COPY --from=maven-build /build/target/quarkus-app/*.jar ./
COPY --from=maven-build /build/target/quarkus-app/app/ ./app/
COPY --from=maven-build /build/target/quarkus-app/quarkus/ ./quarkus/

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS_APPEND} -jar quarkus-run.jar"]
