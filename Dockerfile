FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /projeto
COPY ["Código Fonte/pom.xml", "Código Fonte/pom.xml"]
COPY ["Código Fonte/src/", "Código Fonte/src/"]
COPY ["Front-end/", "Front-end/"]
RUN mvn -B -f "Código Fonte/pom.xml" package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build ["/projeto/Código Fonte/target/cadastro-fornecedores-*.jar", "/app/app.jar"]
ENV SERVER_ADDRESS=0.0.0.0 SERVER_PORT=10000
EXPOSE 10000
USER 10001
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
