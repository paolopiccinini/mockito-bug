FROM alpine/java:21-jdk as build
COPY mvnw /code/mockito-bug/mvnw
COPY .mvn /code/mockito-bug/.mvn
COPY pom.xml /code/mockito-bug/

COPY ./war/pom.xml /code/mockito-bug/war/
COPY ./war/src /code/mockito-bug/war/src/
COPY ./jar/pom.xml /code/mockito-bug/jar/
COPY ./jar/src /code/mockito-bug/jar/src/

WORKDIR /code/mockito-bug
RUN ./mvnw -B org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline
RUN ./mvnw package
RUN ./mvnw -pl jar dependency:copy-dependencies



## Stage 2 : create the docker final image
FROM quay.io/wildfly/wildfly:latest-jdk21
COPY --from=build /code/mockito-bug/war/target/*.war /opt/jboss/wildfly/standalone/deployments/springbootwildfly.war
COPY --from=build /code/mockito-bug/jar/target/*.jar /opt/jboss/wildfly/modules/jar/main/jar.jar
COPY --from=build /code/mockito-bug/jar/libs/* /opt/jboss/wildfly/modules/jar/main/
COPY ./jar/module.xml /opt/jboss/wildfly/modules/jar/main/
ENV JAVA_TOOL_OPTIONS="-XX:UseSVE=0"
EXPOSE 8080
EXPOSE 8787
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "--debug", "*:8787"]
