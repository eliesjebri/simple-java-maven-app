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
                echo "Build dans docker container"
                sh 'ls -la'              // pour vérifier le contenu du workspace
                sh 'mvn -B -DskipTests clean package'
            }
        }

        stage('Test and Coverage') {
            steps {
				echo 'Exécution des tests JUnit...'
				// Génère les rapports dans target/surefire-reports
                sh 'mvn -B test'
            }
            post {
                always {
                    echo 'Publication des rapports...'
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

                    // Publication HTML du rapport JaCoCo
                    publishHTML([
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Code Coverage',
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true
                    ])
                }
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
