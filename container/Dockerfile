FROM eclipse-temurin:17-jre

COPY /leaderboard-*.jar /opt/leaderboard/leaderboard.jar
RUN java -jar /opt/leaderboard/leaderboard.jar --help

ENTRYPOINT [ "java", "-jar", "/opt/leaderboard/leaderboard.jar"]
CMD [ "--help" ]