pipeline {
    agent any
    
    tools {
        maven 'Maven'
        jdk 'JDK11'
        jfrog 'jfrog-cli'
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
                        -Dsonar.projectKey=devops \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.login=${SONAR_CREDENTIALS}
                    '''
                }
            }
        }
        
        // stage('Quality Gate') {
        //     steps {
        //         timeout(time: 5, unit: 'MINUTES') {
        //             waitForQualityGate abortPipeline: true
        //         }
        //     }
        // }

        stage('Publish Artifacts'){
            steps {
                jf 'rt u target/*.jar libs-release-local/'
            }
        }
        
    }
    
    post {
        always {
            cleanWs()
        }
    }
}
