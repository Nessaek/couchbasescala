FROM openjdk:8-jre-alpine
RUN mkdir -p /opt/app
WORKDIR /opt/app
COPY target/scala-2.12/app-assembly.jar app-assembly.jar

CMD java -jar app-assembly.jar