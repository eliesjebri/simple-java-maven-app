pipeline {
    agent any
    environment {
        REGISTRY_URL = "local"
        IMAGE_NAME   = "simple-java-maven-app"
    }
    stages {

        stage('Build') {
            agent {
                docker {
                    image 'maven:3.9.9-eclipse-temurin-21'
                    args '-v /var/lib/jenkins/caches/maven:/.m2'
                }
            }
            steps {
                echo 'Compilation du projet Maven...'
                sh 'mvn clean package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                stash includes: 'target/*.jar', name: 'built-jar'
            }
        }

        stage('Parallel Tasks') {
            parallel {
                stage('Unit Tests') {
                    agent {
                        docker {
                            image 'maven:3.9.9-eclipse-temurin-21'
                            args '-v /var/lib/jenkins/caches/maven:/.m2'
                        }
                    }
                    steps {
                        echo 'Exécution des tests unitaires...'
                        sh 'mvn test'
                    }
                    post {
                        always {
                            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                        }
                    }
                }

                stage('Static Analysis') {
                    agent {
                        docker {
                            image 'maven:3.9.9-eclipse-temurin-21'
                            args '-v /var/lib/jenkins/caches/maven:/.m2'
                        }
                    }
                    steps {
                        echo 'Analyse statique avec Checkstyle...'
                        sh 'mvn checkstyle:check || true'
                    }
                    post {
                        always {
                            echo 'Analyse terminée (voir logs Jenkins).'
                        }
                    }
                }

                stage('Code Coverage') {
                    agent {
                        docker {
                            image 'maven:3.9.9-eclipse-temurin-21'
                            args '-v /var/lib/jenkins/caches/maven:/.m2'
                        }
                    }
                    steps {
                        echo 'Génération du rapport de couverture JaCoCo...'
                        sh 'mvn verify'
                    }
                    post {
                        always {
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
            }
        }

        stage('Docker Image') {
            agent any
            steps {
                echo 'Construction de l’image Docker à partir du JAR archivé (stash)...'
                unstash 'built-jar'

                sh '''
                echo "Diagnostic avant build Docker"
                echo "Utilisateur Jenkins: $(whoami)"
                echo "Répertoire courant: $(pwd)"
                which sh || echo "Shell introuvable"
                ls -lah
                '''

                sh '''
                echo "Construction de l image avec tags : latest et build-$BUILD_NUMBER"
                docker build -t ${REGISTRY_URL}/${IMAGE_NAME}:latest -t ${REGISTRY_URL}/${IMAGE_NAME}:"build-$BUILD_NUMBER" .
                docker images | grep ${IMAGE_NAME}
                '''
            }
        }
    }

    post {
        always {
            echo "Nettoyage du workspace..."
            cleanWs()
        }
        success {
            echo "Build, tests et analyses parallèles terminés avec succès !"
        }
        failure {
            echo "Une ou plusieurs tâches ont échoué."
        }
    }
}
