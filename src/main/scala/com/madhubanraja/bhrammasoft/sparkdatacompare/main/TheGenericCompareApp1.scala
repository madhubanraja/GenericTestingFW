package com.madhubanraja.bhrammasoft.sparkdatacompare

import java.io.{File, FileInputStream}
import java.util.{Calendar, Properties}

object TheGenericCompareApp1
{
  def main(args: Array[String]): Unit = {

    System.out.println("args.length : "+args.length)
    var strSparkMode=args(0)
    var strProjectName=args(1)
    var strDate=args(2)
    var strGenericConfigFiles=args(3)
    var intTestCaseRecordsToShow=args(4)
    var strTestCaseResultToFile=args(5)
    var strTestCaseResultFileLoc=args(6)
    var strJsonLogFileName=args(7)

    var strJSON="["
    var intTotalPassScenarios=0
    var intTotalFailScenarios=0
    var intTotalNumberOfTestCases=0

    val strStartTime = Calendar.getInstance.getTime
    var test_strat_time_json=Calendar.getInstance.getTimeInMillis
    var test_end_time_json=Calendar.getInstance.getTimeInMillis
    var duration_json=test_end_time_json-test_strat_time_json

    val PASSED="PASSED"
    val FAILED="FAILED"
    val strResult=FAILED
    var AllOverStatus=PASSED
    try{
      // general properties
      var generalProperties: Properties = null
      if(strGenericConfigFiles != null) {
        var f= new File(strGenericConfigFiles)
        if(!f.isDirectory)
          {
            if(f.exists)
              {
                generalProperties=new Properties()
                generalProperties.load(new FileInputStream(strGenericConfigFiles))
              }else
              {
                println(" General Configuration Properties Not found. Correct the path in the parameter!")
                System.exit(1)
              }
          }else
          {

          }
      }
    }catch{
      case ex:Exception=>
        println("Exception")
        println(ex.toString)
    }

  }
}