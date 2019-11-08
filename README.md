# Generic Automated Spark Testing Framework !
<h2>Software Requirements:</h2>
<ol>
<li><h5>Intellij IDE community Edison</h5>
<li><h5>Java 1.8_201</h5>
<li><h5>Scala 2.11</h5>
<li><h5>Spark 2.4.3 </h5>
<li><h5>Git Bash in case of windows environment </h5>
</ol>

<h2>What all are the use of this tool ?</h2>
<OL>
    <li/> Compare csv, parquet and All RDBMS which all are supports JDBC ( 2 power n combinations! )
    <li/> Works on AWS
    <li/> Works with Files @  
        <OL>
        <li/> Windows
        <li/> Linux / UNIX 
        <li/> HDFS 
        <li/> S3
        </OL>
    <li/> Can work with jenkins and any other automated pipeline PAR releases!
    <li/> Not only a Comparison tool but also a Testing tool for all your customized scenarios!
    <li/> N number of test case creations! There is no limit!
    <li/> 100% configurable ! 
    <li/> 0% Developer dependency for any customization! 
    <li/> Non Programmer can do Spark-Scala based test case implementation!
    <li/> Little Shell and SQL Knowledge is enough to use the tool!
</OL>

<h2>How to compare a CSV File</h2>
<OL>
<li/> take a copy of DejavuDemoHSQLTbl.sh and DejavuDemoHSQLTbl.properties file ( Ex. ./config/XYZ.properties )
<li/> Rename them with your new project name DejavuDemoHSQLTbl.sh and DejavuDemoHSQLTbl.properties file
<li/> Edit the renamed .properties file with the File schema details and file names. Table columns can be a comma seperated values. 
<pre>
        Example : ( Let us assume two sources and two targets )
                ## ( 1 ) ######################### Source Files Details ##############
                #CSV/PARQUET
                SOURCE_FORMAT=CSV
                SOURCE_DELIM=,
                NO_OF_SOURCES=2
                
                SOURCE_COLUMNS_0=id,permalink
                SOURCE_NAME_0=TechCrunchcontinentalUSA_SRC0
                SOURCE_COLUMNS_1=id,city,state
                SOURCE_NAME_1=TechCrunchcontinentalUSA_SRC1    
                
                ## ( 2 ) ######################### Target Files Details ##############
                #CSV/PARQUET
                TARGET_FORMAT=CSV
                # can be , ~ | N/A
                TARGET_DELIM=,
                NO_OF_TARGETS=1

                TARGET_COLUMNS_0=id,permalink,company,numEmps,category,city,state,fundedDate,raisedAmt,raisedCurrency,round
                TARGET_NAME_0=TechCrunchcontinentalUSA_TGT

                ## ( 3 ) ######################### Cleaning and joining Source and targets respectively ##############
                
                TESTING_BUSINESS_NAME=Dejavu Generic Testing Framework Demo
                FINAL_SOURCE_JOIN_QUERY=SELECT trim(SOURCE_NAME_0.id) as id,trim(permalink) as permalink,trim(city) as city,trim(state) as state FROM SOURCE_NAME_0,SOURCE_NAME_1 where SOURCE_NAME_0.id=SOURCE_NAME_0.id
                FINAL_TARGET_JOIN_QUERY=SELECT id,permalink,city,state FROM TARGET_NAME_0
                FINAL_SOURCE_NAME=final_source_tbl
                FINAL_TARGET_NAME=final_target_tbl
                
                ## ( 4 ) ######################### Defining test cases ##############
                #sample queries with DATE functions use tags with <DATE> / upper(from_unixtime(unix_timestamp(CAST(trim('<DATE>') AS STRING), 'yyyyMMdd'), 'dd-MMM-yyyy')) instead of hardcode the date value.

                NUMBER_OF_TEST_CASES=3

                TC_NAME_0=Count check
                TC_QRY_0=select count(id) from final_source_tbl minus select count(id) from final_target_tbl  
                TC_NAME_0=Target vs Source diff columns
                TC_QRY_0=select id,permalink,city,state from final_target_tbl minus SELECT id,permalink,city,state from final_source_tbl
                TC_NAME_0=Source vs Target diff columns
                TC_QRY_0=select id,permalink,city,state from final_target_tbl minus SELECT id,permalink,city,state from final_source_tbl

</pre>
<li>Example :
<pre>

        #CSV/PARQUET
        SOURCE_FORMAT=PARQUET
        SOURCE_DELIM=N/A
        NO_OF_SOURCES=1

        SOURCE_COLUMNS_0=N/A
        SOURCE_NAME_0=TechCrunchcontinentalUSA_SRC

</pre>
<li/> Edit the test cases based on need in the same properties file ( Ex. ./config/XYZ.sh )
<li/> create a sample variables for the source and target files path in the renames shell script.
<li/> create a sample variables for the source and target files path in the renames shell script.
<li/> Pass them as a parameter like source path followed by target paths
<li>Example Windows Local Files : ( Let us assume two sources and two targets )
<pre>
            
        export strBlodSourceFileLoc1=../SampleDataFiles/snap_dt=20190220/Source
        export strBlodTargetFileLoc1=../SampleDataFiles/snap_dt=20190220/TargetRefinedLR/
        export strBlodSourceFileLoc2=../SampleDataFiles/snap_dt=20190220/Source
            export strBlodTargetFileLoc2=../SampleDataFiles/snap_dt=20190220/TargetRefinedLR/
            
            bash ./Spark-Submit.sh $strBlodSourceFileLoc1 $strBlodSourceFileLoc2 $strBlodTargetFileLoc1 $strBlodTargetFileLoc2
</pre>
</li>
<li>Example Linux or UNIX Local Files : ( Let us assume two sources and two targets )
<pre>
        export strBlodSourceFileLoc1=file:////SampleDataFiles/snap_dt=20190220/Source
        export strBlodTargetFileLoc1=file:////SampleDataFiles/snap_dt=20190220/TargetRefinedLR/
        export strBlodSourceFileLoc2=file:////SampleDataFiles/snap_dt=20190220/Source
            export strBlodTargetFileLoc2=file:////SampleDataFiles/snap_dt=20190220/TargetRefinedLR/
            
            bash ./Spark-Submit.sh $strBlodSourceFileLoc1 $strBlodSourceFileLoc2 $strBlodTargetFileLoc1 $strBlodTargetFileLoc2
</pre>
</li>
<li>Example HDFS Local Files : ( Let us assume two sources and two targets )
<pre>
        export strBlodSourceFileLoc1=/SampleDataFiles/snap_dt=20190220/Source
        export strBlodTargetFileLoc1=/SampleDataFiles/snap_dt=20190220/TargetRefinedLR/
        export strBlodSourceFileLoc2=/SampleDataFiles/snap_dt=20190220/Source
            export strBlodTargetFileLoc2=/SampleDataFiles/snap_dt=20190220/TargetRefinedLR/
            
            bash ./Spark-Submit.sh $strBlodSourceFileLoc1 $strBlodSourceFileLoc2 $strBlodTargetFileLoc1 $strBlodTargetFileLoc2
</pre>
</li>
<li>Example s3 Local Files : ( Let us assume two sources and two targets )
<pre>
        export strBlodSourceFileLoc1=s3://SampleDataFiles/snap_dt=20190220/Source
        export strBlodTargetFileLoc1=s3://SampleDataFiles/snap_dt=20190220/TargetRefinedLR/
        export strBlodSourceFileLoc2=s3://SampleDataFiles/snap_dt=20190220/Source
            export strBlodTargetFileLoc2=s3://SampleDataFiles/snap_dt=20190220/TargetRefinedLR/
            
            bash ./Spark-Submit.sh $strBlodSourceFileLoc1 $strBlodSourceFileLoc2 $strBlodTargetFileLoc1 $strBlodTargetFileLoc2
</pre>
</li>
</OL>
<h2>How to compare a RDBMS</h2>
<OL>
<li/> take a copy of DejavuDemoHSQLTbl.sh and DejavuDemoHSQLTbl.properties file ( Ex. ./config/XYZ.properties )
<li/> Rename them with your new project name DejavuDemoHSQLTbl.sh and DejavuDemoHSQLTbl.properties file
<li/> Edit the renamed .properties file with the JDBC details like JDBC driver class, username, password, source table name and password.
<li/> Edit the test cases based on need in the same properties file
<li/> Other steps are same like the above csv example
</OL></li>

<h2>How to compare a PARQUET</h2>
<OL>
<li/> take a copy of DejavuDemoHSQLTbl.sh and DejavuDemoHSQLTbl.properties file ( Ex. ./config/XYZ.properties )
<li/> Rename them with your new project name DejavuDemoHSQLTbl.sh and DejavuDemoHSQLTbl.properties file
<li/> Edit the renamed .properties file with the JDBC details like JDBC driver class, username, password, source table name and password.
<li/> Edit the test cases based on need in the same properties file
<li/> Other steps are same like the above csv example
</OL>


<h2>Some other one time configs ?</h2>
<OL>
    <li/>Edit ./bin/Exec.sh as like below
    <li/>
    <pre>
    # yarn / local
    export strSparkMode=local
    export intTestCaseRecToShow=25
    # yes \ no
    export strTestCaseResToFile=yes
    </pre>
    <li/><b>strSparkMode=local</b> makes local mode of execution / comparing local files
    <li/><b>strSparkMode=yarn</b> makes enterprise mode of execution / comparing HDFS / S3 files
    <li/><b>intTestCaseRecToShow=25</b> Says how many records needs to be showed in the log of FAILED test cases. 
    <li/><b>strSparkMode=yarn</b> Says whether you needs to store entire failed test case in to a file for further debugging. ( it makes your process reasonably slower ) 
</OL>

<h2>How to execute ?</h2>
<OL>
    <li/>cd [tool folder location]/bin
    <li/> bash -x Exec.sh projectname DATE
    <li/>Eg : bash DejavuDemoCsv 20190909
    <li/> Find the Execution logs in 
    <OL>
    <li/>[tool folder location]/bin/DejavuATTDTestingSummary.log
    <li/>[tool folder location]/bin/DejavuATTDTestingSummary.json
    <li/> Find the failed testcase records in [tool folder location]/bin/TestCaseLogs/
    </OL>
</OL>
