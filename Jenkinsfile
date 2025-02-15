pipeline {
    agent any
    
    tools {
        maven 'Maven'
        jdk 'JDK11'
    }
    
    environment {
        SONAR_CREDENTIALS = credentials('sonar-token')
        ARTIFACTORY_CREDENTIALS = credentials('artifactory-credentials')
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.projectKey=demo-app \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.login=${SONAR_CREDENTIALS}
                    '''
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        
        stage('Deploy to Artifactory') {
            steps {
                script {
                    sh '''
                        mvn deploy \
                        -Dmaven.test.skip=true \
                        -Dartifactory.username=${ARTIFACTORY_CREDENTIALS_USR} \
                        -Dartifactory.password=${ARTIFACTORY_CREDENTIALS_PSW}
                    '''
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
    }
}