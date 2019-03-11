FROM ibmjava:8-jdk
MAINTAINER Jose Juan escudero jjescudero@ontimebt.com
LABEL description=""

# Install curl
RUN export DEBIAN_FRONTEND=noninteractive && \
    export LC_ALL=en_US.UTF-8 && \
    apt-get update && \
    apt-get install -y curl gosu

# SOAP UI Version to download

ARG JAR_VERSION
ENV JAR_VERSION ${JAR_VERSION:-1.0.0}



# Create user path
ENV PROJECT_PATH /var/soapui

RUN mkdir -p ${PROJECT_PATH}

VOLUME ${PROJECT_PATH}


# Set environment
ENV PATH ${PATH}:/opt/ramso/bin

ENV PROJECT null

ENV OPTIONS ''

COPY docker-entrypoint.sh /usr/local/bin/
run chmod a+x /usr/local/bin/docker-entrypoint.sh

ENTRYPOINT ["docker-entrypoint.sh"]

WORKDIR /opt/ramso/data

CMD []
