FROM ubuntu:18.04

RUN apt-get update
RUN apt-get install -y openjdk-8-jdk

EXPOSE 8091

CMD ["cp /var/lib/jenkins/workspace/test/target/spring-boot-rest-example-0.5.0.war /home/"]
CMD ["java -jar -Dspring.profiles.active=test /home/spring-boot-rest-example-0.5.0.war"]
