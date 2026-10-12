pipeline {
    agent any

    tools {
        maven 'Maven3'
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
                bat 'mvn test'
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging application into WAR artifact...'
                bat 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.war', allowEmptyArchive: true
            }
        }
    }

    post {
        success {
            echo '====================================================='
            echo ' CI Pipeline completed successfully!                '
            echo ' WAR artifact generated and archived in target/     '
            echo '====================================================='
        }

        failure {
            echo '====================================================='
            echo ' CI Pipeline failed! Check logs for details.        '
            echo '====================================================='
        }
    }
}
