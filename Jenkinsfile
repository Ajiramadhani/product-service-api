pipeline {
    agent any

    environment {
        DOCKER_COMPOSE = '/usr/local/bin/docker-compose'
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'master',
                url: 'https://github.com/Ajiramadhani/product-service-api.git'
            }
        }

        stage('Build Application') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Stop Old Containers') {
            steps {
                sh '''
                    $DOCKER_COMPOSE down || true
                    docker rm -f product-api myredis myrabbitmq mysqlserver1 || true
                '''
            }
        }

        stage('Docker Compose Up') {
            steps {
                sh '$DOCKER_COMPOSE up -d --build'
            }
        }

        stage('Health Check') {
            steps {
                sleep 30
                sh 'curl -f http://localhost:9014/actuator/health || exit 1'
                echo '✅ Application is running successfully!'
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo '🎉 CI/CD Pipeline Success!'
        }
        failure {
            echo '❌ Pipeline Failed!'
            sh '$DOCKER_COMPOSE down || true'
        }
    }
}