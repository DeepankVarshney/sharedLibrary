def call(Map params) {

    pipeline {
        agent any

        tools {
            maven "maven_3.6"
        }

        environment {
            dockerImage = ''
        }

        stages {

            stage ('Git checkout') {
                steps {
                    git branch: params.branch, url: params.Url
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
                    )
                }
            }

            stage('Create Docker image') {
                steps {
                    script {
                        dockerImage = docker.build(params.imagename, "/var/lib/jenkins/workspace/test@libs/sharedLibrary")
                    }
                }
            }

            stage('Push Docker image to ECR') {
                steps {
                    script {
                        docker.withRegistry("159714198409.dkr.ecr.us-east-1.amazonaws.com/test") { dockerImage.push('latest')
                        }
                    }
                }
            }

        //     stage('Create ECS using Terraform') {
        //
        //     }
        }
    }
}
