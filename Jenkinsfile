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
                stash includes: 'target/*.jar', name: 'built-jar'
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
            // Exécution sur l’hôte Jenkins (où Docker CLI est disponible)
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
    echo "Construction Docker..."
    docker build -t simple-java-maven-app:latest .
    '''
}
        }
    } // ← fermeture du bloc stages

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
