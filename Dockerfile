#Stage 1
FROM maven:3.8.4-openjdk-17-slim as stage1
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY ./src ./src
RUN mvn clean install -Dmaven.test.skip=true

#Stage 2
FROM openjdk:17-slim
WORKDIR /app

# Cài font cần thiết (freetype + dejavu + msttcorefonts)
RUN apt-get update && \
    apt-get install -y libfreetype6 fonts-dejavu-core ttf-mscorefonts-installer fontconfig && \
    fc-cache -f -v && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy file JAR với tên mong muốn
ARG JAR_FILE=target/*.jar
COPY --from=stage1 /app/${JAR_FILE} /site_informativo.jar

EXPOSE 8080

# ENTRYPOINT để chạy app
ENTRYPOINT ["java","-jar","/site_informativo.jar"]
