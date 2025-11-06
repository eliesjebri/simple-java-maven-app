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

stage('Docker Image') {
    steps {
        echo 'Construction de l’image Docker à partir du JAR archivé...'

        // 1️ Récupérer le jar archivé du stage "Build"
        copyArtifacts(
            projectName: env.JOB_NAME,          // même job
            selector: specific("${env.BUILD_NUMBER}"), // depuis ce build
            filter: 'target/*.jar'
        )

        // 2️ Vérifier que le jar est bien récupéré
        sh 'ls -lh target/*.jar'

        // 3️ Construire l’image Docker
        // Construit les deux tags
        script {
            // Tag court basé sur le numéro de build
            def shortTag = "build-${env.BUILD_NUMBER}"
            
            sh """
            echo 'JAR récupéré :'
            ls -lh target/*.jar

            echo 'Construction de l’image avec tags : latest et ${shortTag}'
            docker build -t simple-java-maven-app:latest -t simple-java-maven-app:${shortTag} .

            echo 'Images construites :'
            docker images | grep simple-java-maven-app
            """
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
