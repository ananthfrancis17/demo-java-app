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
        DEPLOY_SERVER = '10.0.2.76' // Replace with your server Iasa
        DEPLOY_USER = 'ssm-user'     // Replace with your server username
        DEPLOY_PATH = '/home/ssm-user/application' // Replace with your target directory
        SSH_CREDENTIALS = credentials('deploy-ssh') // Add SSH credentials in Jenkin
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
        
        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Publish Artifacts'){
            steps {
                jf 'rt u target/*.jar firstjob/'
            }
        }

        stage('Deploy to Server') {
            steps {
                script {
                    // Create target directory if it doesn't exist
                    sshagent(['deploy-ssh']) {
                        sh """
                            # Create directory if it doesn't exist
                            ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_SERVER} "mkdir -p ${DEPLOY_PATH}"
                            
                            # Copy the JAR file
                            scp -o StrictHostKeyChecking=no target/*.jar ${DEPLOY_USER}@${DEPLOY_SERVER}:${DEPLOY_PATH}/
                            
                            # Optional: Restart service if needed
                            ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_SERVER} "systemctl restart myapp"
                        """
                    }
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
