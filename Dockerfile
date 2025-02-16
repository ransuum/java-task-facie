FROM ubuntu:latest
LABEL authors="dmitr"

ENTRYPOINT ["top", "-b"]