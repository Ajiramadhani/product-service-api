pipeline {
    agent any

    environment {
        DOCKER_COMPOSE = '/usr/local/bin/docker-compose'
    }

    stages {
        stage('🔄 Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('🏗️ Build Application') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('🐳 Docker Compose Up') {
            steps {
                sh '${DOCKER_COMPOSE} up -d --build --force-recreate'
            }
        }

    }

    post {
        always {
            cleanWs()
        }
        success {
            echo '🎉 Pipeline Success!'
        }
        failure {
            echo '❌ Pipeline Failed! Cleaning up...'
            sh '''
                ${DOCKER_COMPOSE} down --remove-orphans --volumes || true
                docker system prune -f || true
            '''
        }
    }
}