pipeline {
    agent any

    environment {
        APP_NAME = "springai"
        PROJECT = "michael3kl-dev"
        BASE_IMAGE = "registry.access.redhat.com/ubi8/openjdk-17"
        ACCESS_TOKEN_OCP = credentials('OCP-TOKEN') 
    }

    stages {
        stage('Build Spring Boot') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Login OCP') {
            steps {
                bat '''
                oc login --token=%ACCESS_TOKEN_OCP% --server=https://api.rm1.0a51.p1.openshiftapps.com:6443 --insecure-skip-tls-verify=true
                oc project %PROJECT%
                '''
            }
        }

        stage('BuildConfig & Build Image') {
            steps {
                bat '''
                oc get bc %APP_NAME% 1>nul 2>nul
                if %ERRORLEVEL% NEQ 0 (
                  oc new-build %BASE_IMAGE% --name=%APP_NAME% --binary=true
                )
                oc start-build %APP_NAME% --from-file=target\\*.jar --follow --wait
                '''
            }
        }

        stage('Deploy to OCP') {
            steps {
                bat '''
                oc get deploy %APP_NAME% 1>nul 2>nul
                if %ERRORLEVEL% NEQ 0 (
                  oc new-app %APP_NAME%:latest --name=%APP_NAME%
                  oc expose svc/%APP_NAME%
                ) else (
                  oc rollout restart deploy/%APP_NAME%
                )
                oc rollout status deploy/%APP_NAME% --timeout=120s
                '''
            }
        }
    }
}
