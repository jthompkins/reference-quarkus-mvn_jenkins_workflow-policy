#NOTE: maybe replace this image with a TSSC openjdk image if such a thing ever needs to exist
FROM registry.redhat.io/ubi8/openjdk-8

USER 0

##############################
# vulenerability remediation #
##############################

# do not update filesystem package as it requires that buildah be run as root
# and even buildah unshare doesn't fix the problem.
# see: https://bugzilla.redhat.com/show_bug.cgi?id=1708249#c31
RUN dnf update -y --allowerasing --exclude=filesystem && \
    dnf clean all

##########################
# compliance remediation #
##########################
# NOTE / WARNING / IMPORTANT:
#   This is NOT the right way to do this.
#   The RIGHT way would be to not use Dockerfile and use a real buildah build where we can
#   run oscap remediation against the mounted file system and then close up the file system
#   into an image.
#   But.....right now.....just trying to get a reference working....
COPY compliance/ /var/lib/compliance/
RUN /var/lib/compliance/xccdf_org.ssgproject.content_rule_no_empty_passwords.sh \
    && /var/lib/compliance/xccdf_org.ssgproject.content_rule_rpm_verify_permissions.sh

# NOTE / WARNING / IMPORTANT:
#   work around for https://bugzilla.redhat.com/show_bug.cgi?id=1798685
RUN rm -f /var/log/lastlog

###############
# install app #
###############
RUN mkdir /app
ADD target/*.jar /app/app.jar
RUN chown -R 1001:0 /app && chmod -R 774 /app
EXPOSE 8080

###########
# run app #
###########
USER 1001
ENTRYPOINT ["java", "-jar"]
CMD ["/app/app.jar"]
