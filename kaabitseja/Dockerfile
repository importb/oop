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
RUN mkdir /app/data 
RUN javac -encoding UTF-8 Main.java

ADD crontab /etc/cron.d/java-cron
RUN chmod 0644 /etc/cron.d/java-cron
RUN chmod 0777 /app
RUN crontab /etc/cron.d/java-cron

CMD ["cron", "-f"]
