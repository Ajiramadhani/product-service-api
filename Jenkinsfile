pipeline {
    agent any

    environment {
        DOCKER_COMPOSE = '/usr/local/bin/docker-compose'
        DOCKER_IMAGE = 'product-service:latest'
    }

    stages {
        stage('🔄 Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('🏗️ Build Docker Image') {
            steps {
                script {
                    // Build image menggunakan Dockerfile
                    docker.build("${DOCKER_IMAGE}")
                }
            }
        }

        stage('🐳 Docker Compose Up') {
            steps {
                sh '''
                    ${DOCKER_COMPOSE} down --remove-orphans || true
                    ${DOCKER_COMPOSE} up -d --force-recreate

                    # Tunggu services ready
                    sleep 30
                '''
            }
        }

        stage('✅ Health Check') {
            steps {
                sh '''
                    # Health check dengan retry
                    for i in {1..10}; do
                        if curl -s -f http://localhost:9014/actuator/health > /dev/null; then
                            echo "✅ Health check passed!"
                            exit 0
                        fi
                        echo "⏳ Waiting for services... ($i/10)"
                        sleep 6
                    done
                    echo "❌ Health check failed after 60 seconds"
                    exit 1
                '''
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo '🎉 Pipeline Success!'
            sh '''
                echo "Docker images:"
                docker images | grep product-service
            '''
        }
        failure {
            echo '❌ Pipeline Failed! Cleaning up...'
            sh '''
                ${DOCKER_COMPOSE} down --remove-orphans --volumes || true
                docker rmi -f ${DOCKER_IMAGE} || true
                docker system prune -f || true
            '''
        }
    }
}