# kafka-flink-es-app
echo -e "10.10.10.88 node88\n10.10.10.89 node89\n10.10.10.99 node99\n" >>/etc/hosts



/home/apache-zookeeper-3.8.0-bin/data


echo "1" > /home/apache-zookeeper-3.8.0-bin/data/myid

echo "2" > /home/apache-zookeeper-3.8.0-bin/data/myid


echo "3" > /home/apache-zookeeper-3.8.0-bin/data/myid



export ZOOKEEPER=/home/apache-zookeeper-3.8.0-bin/data
export PATH=$PATH:$JAVA_HOME/bin:$JAVA_HOME/jre/bin


/home/apache-zookeeper-3.8.0-bin/bin/zkServer.sh restart

/home/hadoop-3.3.4/bin/hdfs namenode -format


rm -rf /home/hadoop-3.3.4/tmp/data/*
rm -rf /home/hadoop-3.3.4/tmp/name/*
rm -rf /home/hadoop-3.3.4/tmp/jn/*
rm -rf /home/hadoop-3.3.4/tmp/dfs/data/*
rm -rf /home/hadoop-3.3.4/tmp/dfs/dn/*
rm -rf /home/hadoop-3.3.4/tmp/dfs/name/*
rm -rf /home/hadoop-3.3.4/tmp/dfs/jn/*
rm -rf /home/hadoop-3.3.4/tmp/dfs/nn/*


echo "# hadoop3-spark3-flink-yarn-app" >> README.md
git init
git add README.md
git commit -m "first commit"
git branch -M main
git remote add origin git@github.com:turboic/hadoop3-spark3-flink-yarn-app.git
git push -u origin main


Exception in thread "Thread-5" java.lang.IllegalStateException: Trying to access closed classloader. Please check if you store classloaders directly or indirectly in static fields. If the stacktrace suggests that the leak occurs in a third party library and cannot be fixed immediately, you can disable this check with the configuration 'classloader.check-leaked-classloader'.
配置环境变量


flink的提交方式
Flink Cluster on YARN.
https://nightlies.apache.org/flink/flink-docs-release-1.15/docs/deployment/resource-providers/yarn/


bin/yarn-session.sh –help
Usage:
   Optional
     -at,--applicationType <arg>     Set a custom application type for the application on YARN
     -D <property=value>             use value for given property
     -d,--detached                   If present, runs the job in detached mode
     -h,--help                       Help for the Yarn session CLI.
     -id,--applicationId <arg>       Attach to running YARN session
     -j,--jar <arg>                  Path to Flink jar file
     -jm,--jobManagerMemory <arg>    Memory for JobManager Container with optional unit (default: MB)
     -m,--jobmanager <arg>           Set to yarn-cluster to use YARN execution mode.
     -nl,--nodeLabel <arg>           Specify YARN node label for the YARN application
     -nm,--name <arg>                Set a custom name for the application on YARN
     -q,--query                      Display available YARN resources (memory, cores)
     -qu,--queue <arg>               Specify YARN queue.
     -s,--slots <arg>                Number of slots per TaskManager
     -t,--ship <arg>                 Ship files in the specified directory (t for transfer)
     -tm,--taskManagerMemory <arg>   Memory per TaskManager Container with optional unit (default: MB)
     -yd,--yarndetached              If present, runs the job in detached mode (deprecated; use non-YARN specific option instead)
     -z,--zookeeperNamespace <arg>   Namespace to create the Zookeeper sub-paths for high availability mode


Deployment Modes Supported by Flink on YARN #
For production use, we recommend deploying Flink Applications in Application Mode as it provides a better isolation between applications.

Application Mode #
For high-level intuition behind the application mode, please refer to the deployment mode overview.
Application Mode will launch a Flink cluster on YARN, where the main() method of the application jar gets executed on the JobManager in YARN. The cluster will shut down as soon as the application has finished. You can manually stop the cluster using yarn application -kill <ApplicationId> or by cancelling the Flink job.

./bin/flink run-application -t yarn-application ./examples/streaming/TopSpeedWindowing.jar
Once an Application Mode cluster is deployed, you can interact with it for operations like cancelling or taking a savepoint.

# List running job on the cluster
./bin/flink list -t yarn-application -Dyarn.application.id=application_XXXX_YY
# Cancel running job
./bin/flink cancel -t yarn-application -Dyarn.application.id=application_XXXX_YY <jobId>
Note that cancelling your job on an Application Cluster will stop the cluster.

To unlock the full potential of the application mode, consider using it with the yarn.provided.lib.dirs configuration option and pre-upload your application jar to a location accessible by all nodes in your cluster. In this case, the command could look like:

./bin/flink run-application -t yarn-application \
	-Dyarn.provided.lib.dirs="hdfs://myhdfs/my-remote-flink-dist-dir" \
	hdfs://myhdfs/jars/my-application.jar
The above will allow the job submission to be extra lightweight as the needed Flink jars and the application jar are going to be picked up by the specified remote locations rather than be shipped to the cluster by the client.

Session Mode #
For high-level intuition behind the session mode, please refer to the deployment mode overview.
We describe deployment with the Session Mode in the Getting Started guide at the top of the page.

The Session Mode has two operation modes:

attached mode (default): The yarn-session.sh client submits the Flink cluster to YARN, but the client keeps running, tracking the state of the cluster. If the cluster fails, the client will show the error. If the client gets terminated, it will signal the cluster to shut down as well.
detached mode (-d or --detached): The yarn-session.sh client submits the Flink cluster to YARN, then the client returns. Another invocation of the client, or YARN tools is needed to stop the Flink cluster.
The session mode will create a hidden YARN properties file in /tmp/.yarn-properties-<username>, which will be picked up for cluster discovery by the command line interface when submitting a job.

You can also manually specify the target YARN cluster in the command line interface when submitting a Flink job. Here’s an example:

./bin/flink run -t yarn-session \
  -Dyarn.application.id=application_XXXX_YY \
  ./examples/streaming/TopSpeedWindowing.jar
You can re-attach to a YARN session using the following command:

./bin/yarn-session.sh -id application_XXXX_YY
Besides passing configuration via the conf/flink-conf.yaml file, you can also pass any configuration at submission time to the ./bin/yarn-session.sh client using -Dkey=value arguments.

The YARN session client also has a few “shortcut arguments” for commonly used settings. They can be listed with ./bin/yarn-session.sh -h.

Back to top

Per-Job Mode (deprecated) #
Per-job mode is only supported by YARN and has been deprecated in Flink 1.15. It will be dropped in FLINK-26000. Please consider application mode to launch a dedicated cluster per-job on YARN.
For high-level intuition behind the per-job mode, please refer to the deployment mode overview.
The Per-job Cluster mode will launch a Flink cluster on YARN, then run the provided application jar locally and finally submit the JobGraph to the JobManager on YARN. If you pass the --detached argument, the client will stop once the submission is accepted.

The YARN cluster will stop once the job has stopped.

./bin/flink run -t yarn-per-job --detached ./examples/streaming/TopSpeedWindowing.jar
Once a Per-Job Cluster is deployed, you can interact with it for operations like cancelling or taking a savepoint.

# List running job on the cluster
./bin/flink list -t yarn-per-job -Dyarn.application.id=application_XXXX_YY
# Cancel running job
./bin/flink cancel -t yarn-per-job -Dyarn.application.id=application_XXXX_YY <jobId>
Note that cancelling your job on an Per-Job Cluster will stop the cluster.



git init
git add *
git commit -m "first commit"
git branch -M main
git remote add origin git@github.com:turboic/kafka-flink-es-app.git
git push -u origin main
