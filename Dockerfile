FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY backend/pom.xml .
RUN mvn dependency:go-offline -q
COPY backend/src ./src
RUN mvn package -DskipTests -q

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY frontend ./frontend
EXPOSE 8080
ENV TZ=Asia/Ho_Chi_Minh
ENTRYPOINT ["java", "-jar", "app.jar"]
