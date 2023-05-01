FROM maven:3.9.1-eclipse-temurin-17 AS builder

COPY . /tmp/leaderboard/
RUN mvn jpackage:jpackage@jpackage-app-image -f /tmp/leaderboard/pom.xml

FROM debian:bullseye-20230411-slim

COPY --from=builder /tmp/leaderboard/target/RMonitorLeaderboard /opt/leaderboard/
RUN /opt/leaderboard/bin/RMonitorLeaderboard --help

ENTRYPOINT [ "/opt/leaderboard/bin/RMonitorLeaderboard"]
CMD [ "--help" ]