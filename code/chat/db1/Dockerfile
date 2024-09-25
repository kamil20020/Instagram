FROM postgres:13
# COPY ./scripts/ /docker-entrypoint-initdb.d/

ENV LANG=pl_PL.UTF-8  
ENV LC_ALL=pl_PL.UTF-8

RUN apt-get update && apt-get install -y locales && \
    sed -i -e 's/# pl_PL.UTF-8 UTF-8/pl_PL.UTF-8 UTF-8/' /etc/locale.gen && \
    dpkg-reconfigure --frontend=noninteractive locales && \
    update-locale LANG=pl_PL.UTF-8