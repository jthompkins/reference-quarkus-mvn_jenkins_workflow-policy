#TODO Replace this image with a TSSC openjdk image once it is available
FROM registry.redhat.io/ubi8/openjdk-8

USER 0

RUN mkdir /app 

ADD target/*.jar /app/app.jar
RUN chown -R 1001:0 /app && chmod -R 774 /app
EXPOSE 8080

USER 1001

ENTRYPOINT ["java", "-jar"]
CMD ["/app/app.jar"]
