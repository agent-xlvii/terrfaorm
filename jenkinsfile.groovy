pipeline {

    parameters {
        booleanParam(name: 'autoApprove', defaultValue: false, description: 'Automatically run apply after generating plan?')
        choice(name: 'action', choices: ['apply', 'destroy'], description: 'Select the action to perform')
    }

    environment {
        AWS_ACCESS_KEY_ID     = credentials('AKIAU5LH5ZRNJRTKNQGS')
        AWS_SECRET_ACCESS_KEY = credentials('aiT1p8BOfpqlKQVmNykaLWwpldcfQOubludbLqrBJ')
        AWS_DEFAULT_REGION    = 'us-east-1'
        
    }
    stages {
        stage('Checkout') {
            steps {
                // Use credentialsId 'JENKINS' to authenticate with Bitbucket
                git branch: 'Unit_Test', url: 'https://github.com/agent-xlvii/terrfaorm.git', credentialsId: 'JENKINS'
            }
        }
        stage('Terraform init') {
            steps {
                sh 'terraform init'
            }
        }
        stage('Plan') {
            steps {
                sh 'terraform plan -out=tfplan'_
                sh 'terraform show -no-color tfplan > tfplan.txt'
            }
        }
        stage('Apply / Destroy') {
            steps {
                script {
                    if (params.action == 'apply') {
                        if (!params.autoApprove) {
                            def plan = readFile 'tfplan.txt'
                            input message: "Do you want to apply the plan?",
                            parameters: [text(name: 'Plan', description: 'Please review the plan', defaultValue: plan)]
                        }
                        sh 'terraform apply -input=false tfplan'
                    } else if (params.action == 'destroy') {
                        sh 'terraform destroy --auto-approve'
                    } else {
                        error "Invalid action selected. Please choose either 'apply' or 'destroy'."
                    }
                }
            }
        }
    }
}