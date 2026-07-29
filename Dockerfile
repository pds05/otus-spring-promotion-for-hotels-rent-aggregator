FROM openjdk:27-ea-jdk-slim
ADD target/*.jar ha-promotion.jar
ENTRYPOINT ["java", "-jar", "/ha-promotion.jar"]