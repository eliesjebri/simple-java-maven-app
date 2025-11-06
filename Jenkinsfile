pipeline {
    agent {
        docker {
            image 'maven:3.9.9-eclipse-temurin-21'
            args '-v /var/lib/jenkins/caches/maven:/.m2'
        }
    }

    stages {

        stage('Build') {
            steps {
                echo 'Compilation du projet Maven...'
                sh 'mvn clean package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Parallel Tasks') {
            parallel {
                stage('Unit Tests') {
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
                    steps {
                        echo '🔍 Analyse statique avec Checkstyle...'
                        sh 'mvn checkstyle:check || true'
                    }
                    post {
                        always {
                            echo 'Analyse terminée (voir logs Jenkins).'
                        }
                    }
                }

                stage('Code Coverage') {
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
