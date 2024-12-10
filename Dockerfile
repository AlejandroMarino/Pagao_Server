FROM maven:3.9-sapmachine-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests




FROM openjdk:17-jdk-oracle

COPY --from=build /app/target/spring_Pagao_docker.jar /spring_Pagao_docker.jar
ENTRYPOINT ["java","-jar","/spring_Pagao_docker.jar"]
