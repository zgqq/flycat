#!/bin/bash
echo "Executing build ${@:1}"

#docker buildx build --build-arg CLASH_ENV_FILE=$1 --platform linux/amd64,linux/arm64 -t flycat-jdk17 .
#docker buildx build --build-arg CLASH_ENV_FILE=$1 --push --platform linux/amd64,linux/arm64 -t zgqq/flycat-jdk17 .


# doesn't work
#if [ -n "$2" ]; then
#    echo "Second argument is not empty: $2"
#    docker buildx create --use --name host-builder --driver-opt network=host --buildkitd-flags '--allow-insecure-entitlement network.host'
#    docker buildx build --allow network.host --network host --build-arg HTTP_PROXY=$2 --build-arg HTTPS_PROXY=$2 --platform linux/amd64,linux/arm64 -t $1 .
#    docker buildx build --allow network.host --network host --build-arg HTTP_PROXY=$2 --build-arg HTTPS_PROXY=$2 --push --platform linux/amd64,linux/arm64 -t $1 .
#else
#fi

echo "Second argument is empty or does not exist."
docker buildx build --platform linux/amd64,linux/arm64 -t $1 .


if [[ "$1" == *"internal"* ]] || [[ "$1" == *"localhost"* ]]; then
  echo "The variable contains 'internal' or 'localhost'"
  docker buildx build --push --platform linux/amd64,linux/arm64 -t $1 .
else
  echo "The variable does not contain 'internal' or 'localhost'"
  docker buildx build --push --platform linux/amd64,linux/arm64 -t $1 .
fi

