FROM gradle:8.10.0-jdk17

RUN ["mkdir", "-p", "/build"]

EXPOSE 8080

ENTRYPOINT ["/bin/bash", "entrypoint.sh"]