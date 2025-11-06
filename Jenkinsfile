pipeline {
    agent {
        docker {
            image 'maven:3.9.9-eclipse-temurin-21'
            args '-v /var/lib/jenkins/caches/maven:/.m2'
        }
    }

    environment {
        APP_NAME = "simple-java-maven-app"
    }
    stages {
        stage('Build') {
            steps {
                echo "Build inside docker container"
                sh 'ls -la'              // pour vérifier le contenu du workspace
                sh 'mvn -B clean package'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Archive_Artifacts') {
            steps {
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
    }

    post {
        always { 
           echo "Nettoyage du workspace..."
           cleanWs() }
        success { echo ' Build and test completed successfully! ' }
        failure { echo ' Build failed.' }
    }
}
