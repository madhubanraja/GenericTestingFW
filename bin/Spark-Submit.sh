#!/usr/bin/env bash
if [[ "$OSTYPE" == "msys" ]]; then
  echo -e "all params shell"  >> $txtLogFileName
  echo "strSparkMode "$strSparkMode  >> $txtLogFileName
  echo "strParamProjName "$strParamProjectName >> $txtLogFileName
  echo "strParamDate "$strParamDate >> $txtLogFileName
  echo "strDateFormat "$strParamDateFormat >> $txtLogFileName
  echo "strGenericConfigFileName "$strGenericConfigFileName >> $txtLogFileName
  echo "intTstCaseRecToShow "$intTstCaseRecToShow >> $txtLogFileName
  echo "strTestCaseResToFile "$strTestCaseResToFile >> $txtLogFileName
  echo "strTestCaseResFileLoc "$strTestCaseResFileLoc >> $txtLogFileName

  echo $@ >> $txtLogFileName
  `"$JAVA_HOME\bin\java" -cp "$SPARK_HOME\conf\;$SPARK_HOME\jars\*;../lib/*" -Xmx1g org.apache.spark.deploy.SparkSubmit --class com.madhubanraja.bhrammasoft.sparkdatacompare.TheGenericCompareApp --num-executors 10 --executor-cores 6 --driver-memory 1G --conf spark.dynamicAllocation.enabled=true --conf spark.shuffle.spill=false ../lib/sparkdatacompare-1.0-SNAPSHOT-jar-with-dependencies.jar $strSparkMode $strParamProjectName $strParamDate $strParamDateFormat $strGenericConfigFileName $intTestCaseRecToShow $strTestCaseResToFile $strTestCaseResFileLoc $@ >> $txtLogFileName`
else
    echo 'SparkSubmit --driver-class-path "../lib/*" --class com.madhubanraja.bhrammasoft.sparkdatacompare.TheGenericCompareApp --num-executors 10 --executor-cores 6 --driver-memory 1G --conf spark.dynamicAllocation.enabled=true --conf spark.shuffle.spill=false sparkdatacompare-1.0-SNAPSHOT-jar-with-dependencies.jar $strSparkMode $strParamProjName $strParamDate $strDateFormat $strGenericConfigFileName $intTstCaseRecToShow $strTestCaseResToFile $strTestCaseResFileLoc $@ >> $txtLogFileName'
         `SparkSubmit --driver-class-path "../lib/*" --class com.madhubanraja.bhrammasoft.sparkdatacompare.TheGenericCompareApp --num-executors 10 --executor-cores 6 --driver-memory 1G --conf spark.dynamicAllocation.enabled=true --conf spark.shuffle.spill=false ../lib/sparkdatacompare-1.0-SNAPSHOT-jar-with-dependencies.jar $strSparkMode $strParamProjName $strParamDate strParamDateFormat $strGenericConfigFileName $intTstCaseRecToShow $strTestCaseResToFile $strTestCaseResFileLoc $@ >> $txtLogFileName`
fi


#strSparkMode : yarn
#strPROJECTNAME : MyDBProject
#strFIC_MIS_DATE : 678
#strFIC_MIS_DATE_FORMAT : strParamDateFormat
#strGENERIC_CONFIG_LOC : ../config/MyDBProject.properties
#intTestCaseRecToShow : 25
#strTestCaseResToFile : NO
#strTestCaseResFileLoc : ./TestCaseLogs

# local MyDBProject 08292019 MMDDYYYY ../config/MyDBProject.properties 25 NO ./TestCaseLogs