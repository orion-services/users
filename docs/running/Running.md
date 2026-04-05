---
layout: default
title: Running the Service
nav_order: 2
---

# Running the Service

This document provides comprehensive instructions for running the Orion Users service in different environments: development mode, production with JVM containers, and production with native compilation containers.

## Development

### Prerequisites

- **Java 21** or higher
- **Maven** (or use the included `mvnw` wrapper)
- **Database**: PostgreSQL
- **Node.js 18+** (for Playground frontend development)

### Running in Dev Mode

The easiest way to run the service during development is using Quarkus dev mode, which enables live coding and automatic reloading:

```bash
./mvnw compile quarkus:dev
```

The service will be available at:
- **API**: `http://localhost:8080`
- **Playground**: `http://localhost:8080/test`
- **Dev UI**: `http://localhost:8080/q/dev/` (dev mode only)

**Features of Dev Mode:**
- Automatic code reloading on changes
- Dev UI for configuration and monitoring
- Hot reload for Kotlin/Java code
- Automatic database schema updates (if configured)

### Configuration

The main configuration file is located at:
```
src/main/resources/application.properties
```

**Important Configuration Properties:**

```properties
# Database Configuration
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=your-username
quarkus.datasource.password=your-password
quarkus.datasource.reactive.url=postgresql://localhost:5432/users

# JWT Configuration
mp.jwt.verify.publickey.location=classpath:publicKey.pem
mp.jwt.verify.issuer=orion-users

# Mail Configuration (for email validation and password recovery)
quarkus.mailer.host=smtp.example.com
quarkus.mailer.port=587
quarkus.mailer.username=your-email@example.com
quarkus.mailer.password=your-password
quarkus.mailer.from=your-email@example.com

# Social Auth Configuration
social.auth.google.client-id=your-client-id.apps.googleusercontent.com

# Application Properties
quarkus.http.host=0.0.0.0
quarkus.http.port=8080
```

**Environment-Specific Configuration:**

You can use Quarkus profiles for different environments:

```properties
# Development
%dev.quarkus.datasource.reactive.url=postgresql://localhost:5432/users_dev

# Test
%test.quarkus.datasource.reactive.url=postgresql://localhost:5433/users_test

# Production
%prod.quarkus.datasource.reactive.url=postgresql://production-host:5432/users_prod
```

## Production with Containers

The service can be deployed using Docker containers in two modes:
1. **JVM Mode**: Traditional Java Virtual Machine execution
2. **Native Mode**: Native compilation using GraalVM (smaller, faster startup)

### JVM Container

#### Building the JVM Container

1. **Package the application**:
```bash
./mvnw package
```

This creates the application JAR and dependencies in `target/quarkus-app/`.

2. **Build the Docker image**:
```bash
docker build -f src/main/docker/Dockerfile.jvm -t orion-users:jvm .
```

#### Running the JVM Container

```bash
docker run -i --rm -p 8080:8080 \
  -e QUARKUS_DATASOURCE_REACTIVE_URL=postgresql://host.docker.internal:5432/users \
  -e QUARKUS_DATASOURCE_USERNAME=your-username \
  -e QUARKUS_DATASOURCE_PASSWORD=your-password \
  orion-users:jvm
```

**Environment Variables:**

You can override configuration using environment variables:

```bash
docker run -i --rm -p 8080:8080 \
  -e QUARKUS_DATASOURCE_REACTIVE_URL=postgresql://db-host:5432/users \
  -e QUARKUS_DATASOURCE_USERNAME=dbuser \
  -e QUARKUS_DATASOURCE_PASSWORD=dbpass \
  -e QUARKUS_MAILER_HOST=smtp.example.com \
  -e QUARKUS_MAILER_PORT=587 \
  -e QUARKUS_MAILER_USERNAME=email@example.com \
  -e QUARKUS_MAILER_PASSWORD=emailpass \
  -e QUARKUS_MAILER_FROM=email@example.com \
  -e SOCIAL_AUTH_GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com \
  orion-users:jvm
```

**JVM Memory Configuration:**

The JVM container uses the `run-java.sh` script which automatically configures memory based on container limits. You can customize this:

```bash
docker run -i --rm -p 8080:8080 \
  -e JAVA_OPTS="-Xmx512m -Xms256m" \
  -e JAVA_MAX_MEM_RATIO=50 \
  orion-users:jvm
```

**Available JVM Environment Variables:**
- `JAVA_OPTS`: JVM options (e.g., `-verbose:class`)
- `JAVA_MAX_MEM_RATIO`: Max heap memory ratio (default: 50%)
- `JAVA_INITIAL_MEM_RATIO`: Initial heap memory ratio (default: 25%)
- `JAVA_DEBUG`: Enable remote debugging (default: false)
- `JAVA_DEBUG_PORT`: Remote debugging port (default: 5005)

### Native Container

#### Building the Native Container

**Prerequisites:**
- GraalVM installed, OR
- Docker (for container-based native build)

**Option 1: Local Native Build**

1. **Install GraalVM** and set `GRAALVM_HOME`
2. **Package with native profile**:
```bash
./mvnw package -Pnative
```

3. **Build the Docker image**:
```bash
docker build -f src/main/docker/Dockerfile.native -t orion-users:native .
```

**Option 2: Container-Based Native Build**

If you don't have GraalVM installed locally, you can build natively using Docker:

```bash
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

Then build the Docker image:
```bash
docker build -f src/main/docker/Dockerfile.native -t orion-users:native .
```

#### Running the Native Container

```bash
docker run -i --rm -p 8080:8080 \
  -e QUARKUS_DATASOURCE_REACTIVE_URL=postgresql://host.docker.internal:5432/users \
  -e QUARKUS_DATASOURCE_USERNAME=your-username \
  -e QUARKUS_DATASOURCE_PASSWORD=your-password \
  orion-users:native
```

**Native Container Benefits:**
- **Faster startup**: Typically starts in milliseconds instead of seconds
- **Lower memory footprint**: Uses less memory than JVM
- **Smaller image size**: No JVM runtime required
- **Better for serverless**: Ideal for FaaS platforms

**Native Container Limitations:**
- Longer build time
- Some Java features may not be available (reflection limitations)
- Requires container-based build if GraalVM is not installed locally

### Available Dockerfiles

The project includes several Dockerfiles in `src/main/docker/`:

- **Dockerfile.jvm**: JVM-based container (recommended for most cases)
- **Dockerfile.native**: Native compilation container (best performance)
- **Dockerfile.legacy-jar**: Legacy JAR packaging (not recommended)
- **Dockerfile.native-micro**: MicroProfile native container

### Docker Compose

The project includes a ready-to-use `docker-compose.yml` file in the root directory that automatically provisions a PostgreSQL database and builds the Orion Users service.

This is the easiest way to launch the complete environment (including the API, Playground, and Admin Dashboard).

**Running the environment:**
```bash
docker compose up --build
```

**Features provided by this compose file:**
- Automatically builds the API service from the root `Dockerfile`
- Deploys a `postgres:17-alpine` database with health checks
- Injects necessary database credentials natively
- Mocks out the email backend (`QUARKUS_MAILER_MOCK=true`) so the server boots smoothly without real SMTP credentials

Once it finishes booting, access the services:
- **API**: `http://localhost:8080`
- **Playground**: `http://localhost:8080/test`
- **Admin Dashboard**: `http://localhost:8080/dashboard`

*(Note: Configuration values can be securely overridden by placing a `.env` file in the root directory)*

## Packaging Options

### Standard JAR

Package the application as a standard JAR:

```bash
./mvnw package
```

The application will be in `target/quarkus-app/quarkus-run.jar`.

Run with:
```bash
java -jar target/quarkus-app/quarkus-run.jar
```

### Uber JAR

Package as a single executable JAR:

```bash
./mvnw package -Dquarkus.package.type=uber-jar
```

The application will be in `target/users-0.0.6-runner.jar`.

Run with:
```bash
java -jar target/users-0.0.6-runner.jar
```

### Native Executable

Create a native executable:

```bash
./mvnw package -Pnative
```

The executable will be in `target/users-0.0.6-runner`.

Run with:
```bash
./target/users-0.0.6-runner
```

**Note**: Native compilation requires GraalVM. Use `-Dquarkus.native.container-build=true` for container-based builds.

## Configuration

### Application Properties

Key configuration properties in `application.properties`:

**Database:**
- `quarkus.datasource.db-kind`: Database type (mysql, postgresql, etc.)
- `quarkus.datasource.reactive.url`: Database connection URL
- `quarkus.datasource.username`: Database username
- `quarkus.datasource.password`: Database password

**JWT:**
- `mp.jwt.verify.publickey.location`: Location of public key for JWT verification
- `mp.jwt.verify.issuer`: JWT issuer name

**Mail:**
- `quarkus.mailer.host`: SMTP server host
- `quarkus.mailer.port`: SMTP server port
- `quarkus.mailer.username`: SMTP username
- `quarkus.mailer.password`: SMTP password
- `quarkus.mailer.from`: Default sender email

**Social Auth:**
- `social.auth.google.client-id`: Google OAuth2 Client ID

**HTTP:**
- `quarkus.http.host`: HTTP host (default: 0.0.0.0)
- `quarkus.http.port`: HTTP port (default: 8080)

### Environment Variables

All configuration properties can be overridden using environment variables. Convert property names to uppercase and replace dots with underscores:

- `quarkus.datasource.username` → `QUARKUS_DATASOURCE_USERNAME`
- `social.auth.google.client-id` → `SOCIAL_AUTH_GOOGLE_CLIENT_ID`

### Externalized Configuration

For production, consider using:
- **Kubernetes ConfigMaps/Secrets**
- **HashiCorp Vault**
- **AWS Systems Manager Parameter Store**
- **Environment-specific property files**

## Troubleshooting

### Application Won't Start

- **Check database connection**: Verify database is running and accessible
- **Check port availability**: Ensure port 8080 is not in use
- **Check logs**: Review application logs for errors
- **Verify Java version**: Ensure Java 21+ is installed

### Database Connection Issues

- **Verify connection string**: Check `quarkus.datasource.reactive.url` format
- **Check credentials**: Verify username and password
- **Network connectivity**: Ensure database is reachable from application
- **Database exists**: Ensure the database exists before starting

### Memory Issues

- **JVM containers**: Adjust `JAVA_MAX_MEM_RATIO` or `JAVA_OPTS`
- **Native containers**: Generally use less memory, but check container limits
- **Monitor usage**: Use `docker stats` or monitoring tools

### Native Build Failures

- **GraalVM version**: Ensure compatible GraalVM version
- **Reflection**: Some libraries may require reflection configuration
- **Build time**: Native builds take significantly longer (10-30 minutes)
- **Container build**: Use `-Dquarkus.native.container-build=true` if GraalVM not available

### Mail Configuration Issues

- **SMTP settings**: Verify SMTP host, port, and credentials
- **Firewall**: Ensure SMTP port is not blocked
- **TLS/SSL**: Configure `quarkus.mailer.ssl` if required
- **Test connection**: Use a mail testing tool to verify SMTP settings

## Performance Tuning

### JVM Tuning

For JVM containers, optimize memory and GC:

```bash
docker run -i --rm -p 8080:8080 \
  -e JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC" \
  -e JAVA_MAX_MEM_RATIO=75 \
  orion-users:jvm
```

### Native Optimization

Native builds are optimized by default. For further optimization:

- Use `-Dquarkus.native.additional-build-args` for GraalVM options
- Profile the application to identify optimization opportunities
- Consider using `-Dquarkus.native.enable-all-security-services` if needed

## Security Considerations

### Production Checklist

- [ ] Use strong database passwords
- [ ] Enable HTTPS/TLS
- [ ] Configure CORS properly
- [ ] Use secure JWT keys (not default keys)
- [ ] Configure mail server with TLS
- [ ] Set appropriate file permissions
- [ ] Use secrets management for sensitive data
- [ ] Enable security headers
- [ ] Regular security updates
- [ ] Monitor and log security events

### Secrets Management

Never commit secrets to version control. Use:
- Environment variables
- Kubernetes Secrets
- HashiCorp Vault
- AWS Secrets Manager
- Other secure secret management solutions

## Monitoring and Logging

### Health Checks

Quarkus provides health checks at:
- `/q/health`: Basic health check
- `/q/health/live`: Liveness probe
- `/q/health/ready`: Readiness probe

### Metrics

Enable metrics (if configured):
- `/q/metrics`: Prometheus metrics endpoint

### Logging

Configure logging in `application.properties`:

```properties
quarkus.log.level=INFO
quarkus.log.category."dev.orion.users".level=DEBUG
```

## Additional Resources

- [Quarkus Documentation](https://quarkus.io/)
- [Quarkus Native Image Guide](https://quarkus.io/guides/building-native-image)
- [Quarkus Docker Guide](https://quarkus.io/guides/container-image)
- [GraalVM Documentation](https://www.graalvm.org/docs/)

