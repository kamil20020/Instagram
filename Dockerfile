FROM rabbitmq:4.0.0-rc.1-management-alpine
COPY queue-definitions.json /etc/rabbitmq/definitions.json
COPY queue-config.conf /etc/rabbitmq/rabbitmq.conf