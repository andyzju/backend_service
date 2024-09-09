FROM openjdk:11.0.14.1-jdk-buster

ARG USER_HOME_DIR="/root"
ARG SHA=f790857f3b1f90ae8d16281f902c689e4f136ebe584aba45e4b1fa66c80cba826d3e0e52fdd04ed44b4c66f6d3fe3584a057c26dfcac544a60b301e6d0f91c26

WORKDIR /app
RUN mkdir -p /app/sample/target
COPY sample/target/sample-1.10.0.jar /app/sample/target/sample-1.10.0.jar
# 确保在构建上下文中有maven压缩包并将其复制到镜像中
COPY maven/ ./maven/

RUN echo "${SHA}  ./maven/apache-maven.tar.gz" | sha512sum -c -

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && echo "${SHA}  ./maven/apache-maven.tar.gz" | sha512sum -c - \
  && cp -r ./maven/apache-maven-3.8.6/* /usr/share/maven/ \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn




ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"


# RUN mvn clean package -Dmaven.test.skip=true


ENTRYPOINT ["sh", "-c", "java -jar /app/sample/target/sample-1.10.0.jar"]