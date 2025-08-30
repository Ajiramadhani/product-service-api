pipeline {
    agent any

    environment {
        DOCKER_COMPOSE = '/usr/local/bin/docker-compose'
        COMPOSE_PROJECT_NAME = 'product-service-api'
    }

    stages {
        stage('Build and Deploy') {
            steps {
                cleanWs()
                checkout scm

                sh '''
                    mvn clean package -DskipTests
                    docker-compose build
                    docker-compose up -d
                '''
            }
        }

    }

    post {
        always {
            cleanWs()
        }
        failure {
            echo '❌ Pipeline failed! Running cleanup for NEW containers...'

            sh '''
                cd product-service-api && docker-compose down --remove-orphans || true

                docker rm -f product-api-new mysqlserver1-new myredis-new myrabbitmq-new || true

                docker network rm product-service-api_app-network || true

                docker volume rm product-service-api_mysql_data_new || true
                docker volume rm product-service-api_redis_data_new || true
                docker volume rm product-service-api_rabbitmq_data_new || true

                echo "✅ Cleaned up: product-api-new, mysqlserver1-new, myredis-new, myrabbitmq-new"
            '''
        }
    }
}