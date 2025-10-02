pipeline {
  agent any
  tools {
        maven "Maven3"
  }
  environment {
    OCP_TOKEN   = credentials('ocp-token')
    OCP_SERVER  = 'https://api.rm1.0a51.p1.openshiftapps.com:6443'
    OCP_PROJECT = 'michael3kl-dev'
    APP_NAME    = 'springai'
  }
  stages {
    stage('Build Spring Boot') {
      steps {
        bat 'mvn clean package -DskipTests'
      }
    }

    stage('Login to OpenShift') {
      steps {
        bat """
        oc login --token=%OCP_TOKEN% --server=%OCP_SERVER% --insecure-skip-tls-verify=true
        oc project %OCP_PROJECT%
        """
      }
    }

    stage('Build & Deploy') {
      steps {
        bat """
        oc new-build registry.access.redhat.com/ubi8/openjdk-17 --name=%APP_NAME% --binary=true || echo "BuildConfig exists"
        oc start-build %APP_NAME% --from-file=target\\BelajarAI-0.0.1-SNAPSHOT.jar --follow --wait
        oc new-app %APP_NAME%:latest --name=%APP_NAME% || echo "App exists"
        oc expose svc/%APP_NAME% || echo "Route exists"
        oc rollout status deploy/%APP_NAME% --timeout=120s
        oc get all -n %OCP_PROJECT%
        """
      }
    }
  }
}
