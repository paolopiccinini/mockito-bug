# mockito-bug
mockito-bug
```bash
docker build -t mockito-bug .  

docker run -p 8080:8080 -p 8787:8787 -it mockito-bug

GET to [http](http://localhost:8080/springbootwildfly/Paolo/hello)
```
if u want to use maven local run

```bash
./mvnw package
./mvnw -pl jar dependency:copy-dependencies
```

and use this docker file
```docker
FROM quay.io/wildfly/wildfly:latest-jdk21
COPY --from=build /code/mockito-bug/war/target/*.war /opt/jboss/wildfly/standalone/deployments/springbootwildfly.war
COPY --from=build /code/mockito-bug/jar/target/*.jar /opt/jboss/wildfly/modules/jar/main/jar.jar
COPY --from=build /code/mockito-bug/jar/libs/* /opt/jboss/wildfly/modules/jar/main/
COPY ./jar/module.xml /opt/jboss/wildfly/modules/jar/main/
ENV JAVA_TOOL_OPTIONS="-XX:UseSVE=0"
EXPOSE 8080
EXPOSE 8787
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "--debug", "*:8787"]
```
