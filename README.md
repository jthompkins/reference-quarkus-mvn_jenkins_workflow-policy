Source: https://github.com/quarkusio/quarkus-quickstarts/tree/master/rest-json-quickstart

## Jenkins Config

The jenkins service account must be added to the privileged scc in order for the buildah pod to be provisioned:
```
oc adm policy add-scc-to-user privileged -z jenkins -n jenkins
```

Create a secret that can be used to push images to quay:
```
oc create secret docker-registry quay-basic-auth --docker-server=quay.io/tssc \
   --docker-username='YOUR-REGISTRY-USERNAME' --docker-password='YOUR-REGISTRY-PASSWORD' \
   -n <jenkins project>
```

Note the secret name is `quay-basic-auth` which matches the secret mounted in the Jenkins pipeline
## Tekton Config

The tekton pipeline service account must be added to the privileged scc in order for the buildah pod to be provisioned:
```
oc adm policy add-scc-to-user privileged -z pipeline -n <tekton project>
```

Create a secret that can be used to push images to quay:
```
oc create secret docker-registry quay-basic-auth --docker-server=quay.io/tssc \
   --docker-username='YOUR-REGISTRY-USERNAME' --docker-password='YOUR-REGISTRY-PASSWORD' \
   -n <tekton project>
```

Patch the `pipeline` service account so that it has access to the secret and can use it for pushes:
```
oc patch sa/pipeline -p '{"secrets" : [{"name" : "quay-basic-auth"}]}' -n <tekton project>
```

Create a secret to contain the git credentials
```
oc create secret generic git-secret --from-literal=GIT_USERNAME=supersecret --from-literal=GIT_PASSWORD=topsecret -n <tekton project>
```

The GIT_USERNAME should be the name of the application access token (http://gitea.apps.tssc.rht-set.com/user/settings/applications)

The GIT_PASSWORD should be the string that is returned when you generated the application access token.

Create a secret to contain the ArgoCD Credentials
```
oc create secret generic git-secret --from-literal=ARGOCD_USERNAME=supersecret --from-literal=ARGOCD_PASSWORD=topsecret -n <tekton project>
```

Create a secret to contain the Artifactory Credentials
```
oc create secret generic artifactgory-secret --from-literal=ARTIFACTORY_USERNAME=supersecret --from-literal=ARTIFACTORY_PASSWORD=topsecret -n <tekton project>
```

Create a secret to contain the SonarQube Credentials
```
oc create secret generic sonar-secret --from-literal=SONAR_USERNAME=supersecret --from-literal=SONAR_PASSWORD=topsecret -n <tekton project>
```

## Tests

### Unit Tests
Run the unit tests:
```
mvn test
```

### UAT Tests
Run the user acceptance tests:
```
mvn -Pintegration-test test -Dselenium.hub.url=SELENIUM_HUB_URL -Dtarget.base.url=TARGET_BASE_URL
```

SELENIUM_HUB_URL is the URL where the Selenium Hub is available and listening.

TARGET_BASE_URL is the URL of this running application.
