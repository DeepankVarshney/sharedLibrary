def call(Map params) {

    pipeline {
        agent any

        tools {
            maven "maven_3.6"
            docker "docker"
        }

        stages {

            stage ('Git checkout') {
                steps {
                    git branch: params.branch, url: params.url
                }
            }

            stage ('Maven build') {
                steps {
                    sh 'mvn clean package'
                }
            }

            stage ('Test build') {
                steps {
                    parallel (
                         "unit tests": { sh 'mvn test' },
                         "integration tests": { sh 'mvn integration-test' }
                }
            }

            stage ('Upload artifact to s3') {
                steps {
                    sh 'aws s3 cp /var/lib/jenkins/workspace/java-build/target/*.war s3://jenkins-artifact-war'
                }
            }

            stage('Create Docker image') {
                steps {
                    // sh 'docker build -f nginx.Dockerfile -t nginx .'
                    docker.build(params.imageName)
                }
            }

            stage('Push Docker image to ECR') {
                steps {
                    docker.withRegistry('159714198409.dkr.ecr.us-east-1.amazonaws.com/nginx-test') {
                        docker.image(params.imageName).push('latest')
                    }
                }
            }

        //     stage('Create ECS using Terraform') {
        //
        //     }
        }
    }
}
    
