FROM openjdk:13-alpine

COPY /target/*-SNAPSHOT.jar /localega-tsd-proxy.jar

CMD ["java", "-jar", "/localega-tsd-proxy.jar"]
