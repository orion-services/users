####
# Imagem JVM (Java 25) com o frontend admin (Vite/Vue) incluído.
#
# O admin em src/main/resources/META-INF/resources/admin gera artefatos em
# META-INF/resources/dashboard/ (URL /dashboard), servidos pelo Quarkus.
#
# A partir da raiz do repositório:
#
#   docker build -t quarkus/users-jvm-admin .
#
#   docker run --rm -p 8080:8080 quarkus/users-jvm-admin
#
# Requisitos: apenas Docker (Node e Maven correm dentro do build).
####

# --- 1) Build do admin (npm)
FROM node:22-alpine AS admin-build
WORKDIR /app/admin
COPY src/main/resources/META-INF/resources/admin/package.json \
     src/main/resources/META-INF/resources/admin/package-lock.json ./
RUN npm ci
COPY src/main/resources/META-INF/resources/admin/ ./
RUN npm run build
# vite.config.js: outDir ../dashboard -> /app/dashboard

# --- 2) Build Quarkus (fast-jar), alinhado a maven.compiler.release=25
# Imagem Maven oficial (evita depender do wrapper: maven-wrapper.jar está em .gitignore)
FROM maven:3-eclipse-temurin-25 AS maven-build
WORKDIR /build
COPY pom.xml .
COPY src ./src
COPY --from=admin-build /app/dashboard ./src/main/resources/META-INF/resources/dashboard
RUN mvn -B -DskipTests -Dexec.skip=true package

# --- 3) Runtime (JRE 25)
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