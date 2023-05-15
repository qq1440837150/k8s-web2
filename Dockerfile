FROM openjdk:8u302-jdk
ADD target/k8s-web-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]