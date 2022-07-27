#ARG ELK_VERSION

# https://github.com/elastic/elasticsearch-docker
FROM landoop/schema-registry-ui:0.9.5

COPY --chown=1000:1000 wait-for-it.sh wait-for-it.sh
COPY --chown=1000:1000 wait-for-it.sh /wait-for-it.sh

RUN whoami

RUN ls / -la
