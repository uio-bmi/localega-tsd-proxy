FROM maven:3.9.2-eclipse-temurin-17-alpine as builder

COPY pom.xml .

RUN mkdir -p /root/.m2 && \
    mkdir /root/.m2/repository

COPY settings.xml /root/.m2

RUN mvn dependency:go-offline --no-transfer-progress

COPY src/ /src/

RUN mvn clean install -DskipTests --no-transfer-progress

FROM eclipse-temurin:17-jre-alpine

RUN apk add --no-cache ca-certificates

COPY --from=builder /target/localega-*.jar /localega-tsd-proxy.jar

RUN addgroup -g 1000 lega && \
    adduser -D -u 1000 -G lega lega

USER 1000

CMD ["java", "-jar", "/localega-tsd-proxy.jar"]
