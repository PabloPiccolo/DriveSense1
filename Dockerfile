FROM eclipse-temurin:25-jdk

WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

CMD ["sh", "-c", "java -jar target/*.jar"]
