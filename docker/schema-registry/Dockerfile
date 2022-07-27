#ARG ELK_VERSION

# https://github.com/elastic/elasticsearch-docker
FROM confluentinc/cp-schema-registry:6.2.0

COPY --chown=1000:1000 wait-for-it.sh wait-for-it.sh
COPY --chown=1000:1000 wait-for-it.sh /wait-for-it.sh

RUN whoami

RUN ls / -la
