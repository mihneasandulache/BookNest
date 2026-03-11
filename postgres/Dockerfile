FROM postgres:16-alpine

# Any *.sql or *.sh scripts placed in this directory are run once
# on first container start (in alphabetical order).
COPY init/ /docker-entrypoint-initdb.d/

EXPOSE 5432
