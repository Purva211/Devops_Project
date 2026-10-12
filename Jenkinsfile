
pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out Library Management System code'
            }
        }

        stage('Build') {
            steps {
                withMaven(maven: 'Maven3') {
                    bat 'mvn clean package'
                }
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests'
            }
        }
    }

    post {
        success {
            echo 'CI Pipeline completed successfully!'
        }

        failure {
            echo 'CI Pipeline failed!'
        }
    }
}
