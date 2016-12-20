FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/forum.jar /forum/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/forum/app.jar"]
