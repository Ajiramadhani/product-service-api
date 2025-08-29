pipeline {
    agent any

    stages {
        // STAGE 1: Download code dari GitHub
        stage('Checkout Code') {
            steps {
                git branch: 'master',
                url: 'https://github.com/Ajiramadhani/product-service-api.git'
            }
        }

        // STAGE 2: Build aplikasi dengan Maven
        stage('Build Application') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        // STAGE 3: Stop container lama (jika ada)
        stage('Stop Old Containers') {
            steps {
                sh '''
                    docker-compose down || true
                    docker rm -f product-api myredis myrabbitmq mysqlserver1 || true
                '''
            }
        }

        // STAGE 4: Build dan jalankan dengan Docker Compose
        stage('Docker Compose Up') {
            steps {
                sh 'docker-compose up -d --build'
            }
        }

        // STAGE 5: Tunggu dan test aplikasi
        stage('Health Check') {
            steps {
                sleep 30  // tunggu 30 detik
                sh 'curl -f http://localhost:9014/actuator/health || exit 1'
                echo '✅ Application is running successfully!'
            }
        }
    }

    post {
        always {
            echo '🧹 Cleaning up workspace...'
            cleanWs()
        }
        success {
            echo '🎉 CI/CD Pipeline Success!'
        }
        failure {
            echo '❌ Pipeline Failed!'
            sh 'docker-compose down || true'
        }
    }
}