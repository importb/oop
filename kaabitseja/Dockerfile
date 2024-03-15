FROM ubuntu:latest

WORKDIR /app

# Set environment variables for Java installation
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
ENV PATH $PATH:$JAVA_HOME/bin

# Install Java
RUN apt-get update && \
    apt-get install -y openjdk-11-jdk cron && \
    rm -rf /var/lib/apt/lists/*

COPY .env /app/
COPY Main.java /app/
COPY mysql-connector-j-8.0.31.jar /app/
COPY org.json-1.6-20240205.jar /app/
COPY build.sh /app/
COPY run.sh /app/
RUN mkdir /app/data 
RUN chmod +x build.sh run.sh 
RUN /app/build.sh

ADD crontab /etc/cron.d/java-cron
RUN chmod 0644 /etc/cron.d/java-cron
RUN chmod 0777 /app
RUN crontab /etc/cron.d/java-cron

RUN touch /var/log/cron.log
# vb peaks tegema RUN cron -f ja ss CMD tail -f /var/log/cron.log

CMD ["cron", "-f"]
