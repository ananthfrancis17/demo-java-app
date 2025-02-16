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
        
        stage('Deploy to Artifactory') {
            steps {
                script {
                    sh '''
                        mvn deploy \
                        -e -X \
                        -s /var/lib/jenkins/.m2/settings.xml \
                        -Dmaven.test.skip=true \
                        -Dartifactory.username=${ARTIFACTORY_CREDENTIALS_USR} \
                        -Dartifactory.password=${ARTIFACTORY_CREDENTIALS_PSW} \
                        -DaltDeploymentRepository=artifactory::default::http://devops-1548912442.ap-southeast-1.elb.amazonaws.com:82/artifactory/libs-snapshot-local
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
