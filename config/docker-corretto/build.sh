docker buildx build --platform linux/amd64,linux/arm64 -t flycat-jdk17 .
docker buildx build --push --platform linux/amd64,linux/arm64 -t zgqq/flycat-jdk17 .