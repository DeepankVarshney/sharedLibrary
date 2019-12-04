FROM ubuntu:18.04

RUN apt-get update
RUN apt-get install -y openjdk-8-jdk

COPY /var/lib/jenkins/workspace/test@libs/sharedLibrary /home

EXPOSE 8091

CMD ["java -jar -Dspring.profiles.active=test /home/spring-boot-rest-example-0.5.0.war"]
