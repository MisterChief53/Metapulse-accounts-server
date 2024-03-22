FROM bellsoft/liberica-openjdk-debian:21

# RUN apt-get update && apt-get install -y curl unzip procps libxext libxrender libxtst libxi freetype gcompat

RUN apt-get update && apt-get install -y curl unzip procps

ENV PORT 8080
# # set hostname to localhost
ENV HOSTNAME "0.0.0.0"
