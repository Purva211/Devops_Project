pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    environment {
        IMAGE_NAME = 'library-management-system'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out Library Management System source code from GitHub...'
            }
        }

        stage('Build') {
            steps {
                echo 'Compiling Java source code...'
                bat 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                echo 'Executing unit tests...'
                bat 'mvn test -DargLine="-XX:+EnableDynamicAgentLoading -Dnet.bytebuddy.experimental=true"'
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging application into WAR artifact...'
                bat 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.war', allowEmptyArchive: true
            }
        }

        stage('Docker Build') {
            steps {
                echo "Building Docker container image: ${IMAGE_NAME}:${BUILD_NUMBER} and latest..."
                bat "docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} -t ${IMAGE_NAME}:latest ."
            }
        }
    }

    post {
        success {
            echo '====================================================='
            echo ' CI/CD Pipeline completed successfully!             '
            echo " Docker Image: ${IMAGE_NAME}:${BUILD_NUMBER} created! "
            echo '====================================================='
        }

        failure {
            echo '====================================================='
            echo ' CI/CD Pipeline failed! Check logs for details.     '
            echo '====================================================='
        }
    }
}
