FROM python:3.8
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1
WORKDIR /django
COPY Pipfile Pipfile.lock /django
RUN pip install pipenv && pipenv install --system
RUN pip install psycopg2-binary==2.8.6
COPY . /django

