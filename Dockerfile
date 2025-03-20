FROM quay.io/wildfly/wildfly:latest-jdk21
COPY ./war/target/*.war /opt/jboss/wildfly/standalone/deployments/springbootwildfly.war
COPY ./jar/target/*.jar /opt/jboss/wildfly/modules/jar/main/jar.jar
COPY ./jar/libs/* /opt/jboss/wildfly/modules/jar/main/
COPY ./jar/module.xml /opt/jboss/wildfly/modules/jar/main/
ENV JAVA_TOOL_OPTIONS="-XX:UseSVE=0"
EXPOSE 8080
EXPOSE 8787
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "--debug", "*:8787"]