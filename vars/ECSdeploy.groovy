def call(Map params) {

    pipeline {
        agent any

        tools {
            maven "maven_3.6"
        }

        environment {
            dockerImage = ''
            ecrUrl = "https://159714198409.dkr.ecr.us-east-1.amazonaws.com/test"
            // ecrCred = $(aws ecr get-login --no-include-email --region us-east-1)
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
                        ecrCred = sh 'aws ecr get-login --no-include-email --region us-east-1'
                        docker.withRegistry(ecrUrl, ecrCred) { dockerImage.push('latest')
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
