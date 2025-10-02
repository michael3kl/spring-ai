pipeline {
    agent any

    environment {
        OCP_TOKEN = credentials('OCP-TOKEN')  
        OCP_SERVER = 'https://api.rm1.0a51.p1.openshiftapps.com:6443'
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Login to OCP') {
            steps {
                sh """
                oc login --token=$OCP_TOKEN --server=$OCP_SERVER
                oc project $OCP_PROJECT
                """
            }
        }

        stage('Build & Deploy to OCP') {
            steps {
                sh """
                oc new-build --binary --name=spring-ai --strategy=source || true
                oc start-build spring-ai --from-dir=. --wait
                oc new-app spring-ai || true
                oc expose svc/spring-ai || true
                """
            }
        }
    }
}
