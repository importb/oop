# Use an official Python runtime as a parent image
FROM python:3.9-slim

WORKDIR /app

COPY main.py /app/main.py
COPY .env /app/.env

RUN apt-get update && apt-get -y install cron nano

RUN python3 -m pip install requests

ADD crontab /etc/cron.d/crontab

RUN chmod 0644 /etc/cron.d/crontab

RUN crontab /etc/cron.d/crontab

RUN mkdir /app/data

RUN touch /var/log/cron.log

CMD cron && tail -f /var/log/cron.log
