# Reuters classification

Prerequisites:

        java8
        scala-2.11
        spark-1.6.1
        
Build:

        ./build.bash
        
Run locally:
        
        export PATH=$PATH:~/Downloads/spark-1.6.1-bin-hadoop2.4/bin
        
        ./run.bash -h
        
        ./run.bash -maxItemLabels 1
        
For running in cluster modify `--master` parameter in `run.bash` 

See output like:

        INFO DAGScheduler: Job 7 finished: countByValue at MulticlassMetrics.scala:43, took 5.492259 s
        fMeasure: 0.8093977498345467
        precision: 0.8093977498345467 recall: 0.8093977498345467
        ---------------------------
        INFO SparkContext: Invoking stop() from shutdown hook
        INFO SparkUI: Stopped Spark web UI at http://192.168.1.4:4040
        INFO MapOutputTrackerMasterEndpoint: MapOutputTrackerMasterEndpoint stopped!
        INFO MemoryStore: MemoryStore cleared
        INFO BlockManager: BlockManager stopped
        INFO BlockManagerMaster: BlockManagerMaster stopped
        INFO OutputCommitCoordinator$OutputCommitCoordinatorEndpoint: OutputCommitCoordinator stopped!
        INFO SparkContext: Successfully stopped SparkContext
        INFO ShutdownHookManager: Shutdown hook called
        INFO ShutdownHookManager: Deleting directory /private/var/folders/kz/gq5ww5_15zjc2tgvsxr6cr0w0000gn/T/spark-d53e5c0e-fb58-4fd6-ae8b-2804a9e99368
        INFO RemoteActorRefProvider$RemotingTerminator: Shutting down remote daemon.
        INFO RemoteActorRefProvider$RemotingTerminator: Remote daemon shut down; proceeding with flushing remote transports.
        INFO ShutdownHookManager: Deleting directory /private/var/folders/kz/gq5ww5_15zjc2tgvsxr6cr0w0000gn/T/spark-d53e5c0e-fb58-4fd6-ae8b-2804a9e99368/httpd-3a4c6257-8e23-47a1-9193-778ec9410c81

        
        