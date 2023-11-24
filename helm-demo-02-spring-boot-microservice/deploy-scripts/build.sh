#!/bin/bash
# docker build --build-arg JAR_FILE=target/spring-boot-microservice.jar -t myorg/myapp .

az acr build --image spring-boot-employee:v1  --registry jax2helmacr --file ./Dockerfile .



popd

