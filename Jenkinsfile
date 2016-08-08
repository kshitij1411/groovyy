node('master'){
	
	stage 'checkout'

	//properties ([pipelineTriggers([]), buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '10', numToKeepStr: '')), [$class: 'GitLabConnectionProperty', gitLabConnection: ''], parameters([string(defaultValue: 'master', description: 'qweez branch', name: 'qweez_branch'), booleanParam(defaultValue: true, description: 'force build the packages even if there is no change/commit to the spark repository', name: 'force')]), [$class: 'ThrottleJobProperty', categories: [], limitOneJobWithMatchingParams: false, maxConcurrentPerNode: 2, maxConcurrentTotal: 2, paramsToUseForLimit: '', throttleEnabled: true, throttleOption: 'project']])

	checkout([$class: 'GitSCM', branches: [[name: 'q-spark-1.5.1']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'spark'], [$class: 'LocalBranch', localBranch: 'q-spark-1.5.1'], [$class: 'CloneOption', depth: 0, noTags: true, reference: '', shallow: true], [$class: 'CleanBeforeCheckout']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '6bd8525c-0e2c-4b0e-a88c-36642e0485d5', url: 'ssh://git@bitbucket.org/qubole/spark.git']]])
	checkout([$class: 'GitSCM', branches: [[name: 'master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'chef-repo'], [$class: 'CloneOption', depth: 0, noTags: true, reference: '', shallow: true], [$class: 'LocalBranch', localBranch: 'master'], [$class: 'CleanBeforeCheckout']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '6bd8525c-0e2c-4b0e-a88c-36642e0485d5', url: 'ssh://git@bitbucket.org/qubole/chef-repo.git']]])

	wrap([$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: '8f887bd9-1968-4928-8869-34f2d1b31d6a', replaceTokens: false, targetLocation: '', variable: '']]])
	{

		stage concurrency: 1, name: 'Hadoop Build'

		sh'''
			workspace=$(pwd)
			rm -rf $workspace/venv
			mkdir -p $workspace/venv
			virtualenv $workspace/venv
			. $workspace/venv/bin/activate
			mkdir -p /media/ephemeral0/packages
			rm -fr $workspace/qweez
			git clone -b master git@bitbucket.org:qubole/qweez.git $workspace/qweez
			cd $workspace/qweez/ && python setup.py install
			rm -f $workspace/*.log
			#qweez -d hadoop2 create -f -s $workspace -b master
		'''
		stage concurrency: 2, name: 'Spark and Zeppelin Parallel Build'

		parallel(
			phase1: {
				/*parallel(*/
						/*phase1: {*/
							sh '''
								workspace=$(pwd)
								$workspace/venv/bin/qweez -d spark create -f -s $workspace -b q-spark-1.5.1 --archive-spark-version 1.5.1 --archive-branch master
							'''			
						/*},
						phase2: {
							cleanup_spark_repo
							sh '''
								workspace=$(pwd)
								$workspace/venv/bin/qweez -d spark create -f -s $workspace -b q-spark-1.6.0 --archive-spark-version 1.6.0 --archive-branch master
							'''
						},
						phase3: {
							cleanup_spark_repo
							sh '''
								workspace=$(pwd)
								$workspace/venv/bin/qweez -d spark create -f -s $workspace -b q-spark-1.6.1 --archive-spark-version 1.6.1 --archive-branch master
							'''
						}
						phase4: {
							cleanup_spark_repo
							sh '''
								workspace=$(pwd)
								$workspace/venv/bin/qweez -d spark create -f -s workspace -b q-spark-1.6.2 --archive-spark-version 1.6.2 --archive-branch master
							'''
						}
						phase5: {
							cleanup_spark_repo
							sh '''
								workspace=$(pwd)
								$workspace/venv/bin/qweez -d spark create -f -s $workspace -b q-spark-2.0.0-rc6 --archive-spark-version 2.0.0 --archive-branch master
							'''
						}
					)*/
				},
			phase2: {
					//sh 'rm -f $workspace/*.log'
					
					//ZEP_BRANCH=q-zep-0.6.0

					/*parallel (
						phase1: {*/
							sh '''
								workspace=$(pwd)
								rm -f $workspace/*.log
								$workspace/venv/bin/qweez -d zeppelin create -f -s $workspace -b q-zep-0.6.0 --spark-version 1.5 --archive-spark-version 1.5.1 --archive-branch master
							'''
						/*}
						phase2: {
							sh '''
								workspace=$(pwd)
								$workspace/venv/bin/qweez -d zeppelin create -f -s $workspace -b $ZEP_BRANCH --spark-version 1.6 --archive-spark-version 1.6.0 --archive-branch master
							'''
						}
						phase3: {
							sh '''
								workspace=$(pwd)
								$workspace/venv/bin/qweez -d zeppelin create -f -s $workspace -b $ZEP_BRANCH --spark-version 1.6 --archive-spark-version 1.6.1 --archive-branch master
							'''
						}
					)*/
			}
		)
	}
	archive 'qweez/qweez*.log'
}

