node('master'){
	stage 'checkout'

//	properties [pipelineTriggers([]), buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '10', numToKeepStr: '')), [$class: 'GitLabConnectionProperty', gitLabConnection: ''], parameters([string(defaultValue: 'master', description: 'qweez branch', name: 'qweez_branch'), booleanParam(defaultValue: false, description: '', name: 'force')]), [$class: 'ThrottleJobProperty', categories: [], limitOneJobWithMatchingParams: false, maxConcurrentPerNode: 2, maxConcurrentTotal: 2, paramsToUseForLimit: '', throttleEnabled: true, throttleOption: 'project']]

	//checkout([$class: 'GitSCM', branches: [[name: 'q-spark-1.5.1']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'spark'], [$class: 'LocalBranch', localBranch: 'q-spark-1.5.1'], [$class: 'CloneOption', depth: 0, noTags: true, reference: '', shallow: true], [$class: 'CleanBeforeCheckout']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '7fb26b9e-9e2b-400d-b72d-4c874cba8e76', url: 'ssh://git@bitbucket.org/qubole/spark.git']]])
	//checkout([$class: 'GitSCM', branches: [[name: 'master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'chef-repo'], [$class: 'CloneOption', depth: 0, noTags: true, reference: '', shallow: true], [$class: 'CleanBeforeCheckout'], [$class: 'LocalBranch', localBranch: 'master']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '7fb26b9e-9e2b-400d-b72d-4c874cba8e76', url: 'ssh://git@bitbucket.org/qubole/chef-repo.git']]])

	wrap([$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: '8f887bd9-1968-4928-8869-34f2d1b31d6a', replaceTokens: false, targetLocation: '$HOME/.m2/settings.xml', variable: '']]]) {
    // some block


		stage concurrency: 1, name: 'Hadoop Build'

		sh'''
			workspace=$(pwd)
			rm -rf $workspace/venv
			mkdir -p $workspace/venv
			virtualenv $workspace/venv
			. $workspace/venv/bin/activate
			mkdir -p /media/ephemeral0/packages
			rm -fr $workspace/qweez
			#git clone -b master git@bitbucket.org:qubole/qweez.git $workspace/qweez
			#cd $workspace/qweez/ && python setup.py install
			#rm -f $workspace/*.log
			#qweez -d hadoop2 create -f -s $workspace -b master
		'''
		stage concurrency: 2, name: 'Spark and Zeppelin Parallel Build'

		parallel(
			phase1: {
			sh'''
				workspace=$(pwd)
				rm -f $workspace/*.log
				
				cleanup_spark_repo () {
					rm -rf $workspace/spark
	 			   git clone git@bitbucket.org:qubole/spark.git $workspace/spark
				}

			#	$workspace/venv/bin/qweez -d spark create -f -s $workspace -b q-spark-1.5.1 --archive-spark-version 1.5.1 --archive-branch master
			'''
			},
			phase2: {
				sh'''
				workspace=$(pwd)
				rm -f $workspace/*.log
				
				ZEP_BRANCH=q-zep-0.6.0

				#qweez -d zeppelin create -f -s $workspace -b $ZEP_BRANCH --spark-version 1.5 --archive-spark-version 1.5.1 --archive-branch master
				#qweez -d zeppelin create -f -s $workspace -b $ZEP_BRANCH --spark-version 1.6 --archive-spark-version 1.6.0 --archive-branch master
				#qweez -d zeppelin create -f -s $workspace -b $ZEP_BRANCH --spark-version 1.6 --archive-spark-version 1.6.1 --archive-branch master

			'''	
			}
		)
	}
	archive 'qweez/qweez*.log'
}

