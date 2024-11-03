FROM docker.io/maven:3.9.8-amazoncorretto-21 AS builder

ARG SERVICE=http

WORKDIR /usr/src/app

COPY . .

RUN mvn --batch-mode --no-transfer-progress clean package \
    && cp target/app.jar /

FROM quay.io/kaiserpfalzedv/java-runner:21-latest AS runner

COPY --from=builder --chown=root:root --chmod=0555 /app.jar /deployments