package com.madhubanraja.bhrammasoft.sparkdatacompare

import java.io.{File, FileInputStream, PrintWriter}
import java.util.{Calendar, Properties}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types._
import org.apache.spark.storage.StorageLevel

/** * @author Rajasekaran Sundaramoorthy  */
object TheGenericCompareApp
{
  def main(args: Array[String]) {
    System.out.println("args.length : " + args.length)
    var strSparkMode = args(0)
    System.out.println("strSparkMode : " + strSparkMode)
    var strPROJECTNAME = args(1)
    System.out.println("strPROJECTNAME : " + strPROJECTNAME)
    var strFIC_MIS_DATE = args(2)
    System.out.println("strFIC_MIS_DATE : " + strFIC_MIS_DATE)
    var strFIC_MIS_DATE_FORMAT = args(3)
    System.out.println("strFIC_MIS_DATE_FORMAT : " + strFIC_MIS_DATE_FORMAT)
    var strGENERIC_CONFIG_LOC = args(4)
    System.out.println("strGENERIC_CONFIG_LOC : " + strGENERIC_CONFIG_LOC)
    var intTestCaseRecToShow = args(5)
    System.out.println("intTestCaseRecToShow : " + intTestCaseRecToShow)
    var strTestCaseResToFile = args(6)
    System.out.println("strTestCaseResToFile : " + strTestCaseResToFile)
    var strTestCaseResFileLoc = args(7)
    System.out.println("strTestCaseResFileLoc : " + strTestCaseResFileLoc)
    // testing variables dec start
    var strText = ""
    var strJSON = "["
    var intTotalPassScenarios = 0
    var intTotalFailScenarios = 0
    var intTestCasenumer = 0
    var strDejavuATTDTestingSummaryJsonFileName = "DejavuATTDTestingSummary.json";
    // var strDejavuATTDTestingSummaryTextFileName="DejavuATTDTestingSummary.log";
    val strStartTime = Calendar.getInstance().getTime()
    var test_start_time_json = Calendar.getInstance.getTimeInMillis()
    var test_end_time_json = Calendar.getInstance.getTimeInMillis()
    var duration_json = test_end_time_json - test_start_time_json
    val PASSED = "PASSED"
    val FAILED = "FAILED"
    var strResult = FAILED
    var strAllOverStatus = PASSED
    // testing variables dec ends

    try {
      /* --------------------------------------------------- General properties ---------------------------------------------------*/
      var generalProperties: Properties = null
      if (strGENERIC_CONFIG_LOC != null) {
        var f = new File(strGENERIC_CONFIG_LOC)
        if (!f.isDirectory) {
          if (f.exists) {
            generalProperties = new Properties()
            generalProperties.load(new FileInputStream(strGENERIC_CONFIG_LOC))
          } else {
            println("General Configuration properties not found. Correct the path in the parameter !")
            System
              .exit(1)
          }
        } else {
          println("General Configuration properties not found. the path is a directory correct it!")
          System
            .exit(1)
        }
      } else {
        println("General Config path is null , check the parameter !")
        System
          .exit(1)
      }
      /*--------- area to code ---------------------------
      ----- get the source file locations as list based on the number of source files
      ----- from the args (7) + number of input files -1 is the locations
      for source file
        then + number of target files will give the target files as a commandline
        arguments.
          ----- Because the locations needs to be get from the shell script dynamically by
        parsing the corresponding logs */
      val intNO_OF_SOURCE_FILES = Integer.parseInt(generalProperties.get("NO_OF_SOURCES").toString)
      val intNO_OF_TARGET_FILES = Integer.parseInt(generalProperties.get("NO_OF_TARGETS").toString)
      var intSourceFileLocStartIndex = 8
      var intSourceFileLocEndIndex = intNO_OF_SOURCE_FILES + intSourceFileLocStartIndex - 1
      var strArraySourceLoc = new Array[String](intNO_OF_SOURCE_FILES)
      var strArraySourceSchemas = new Array[StructType](intNO_OF_SOURCE_FILES)
      var strStructTypeTemp = new StructType();
      var strArraySourceNames = new Array[String](intNO_OF_SOURCE_FILES)
      var ArraySourceDFs = new Array[DataFrame](intNO_OF_SOURCE_FILES)
      var FinalSourceDF = ArraySourceDFs(intNO_OF_SOURCE_FILES - 1)
      var strDelimTemp = ""
      var strColumnNameTemp = "'"
      var strKeyTemp = ""
      var intTempIndex = 0
      var strColumnName = ""
      val strColumnType = "string"
      val spark_app_mode = strSparkMode
      val spark = SparkSession.builder.appName(generalProperties.get("TESTING_BUSINESS_NAME").toString).config("spark.sql.broadcastTimeout", "600").master(spark_app_mode).getOrCreate()
/*    val blockSize = 1024 * 1024 * 50 // 16MB
      spark.conf.set( "dfs.blocksize", blockSize )*/
      if (intNO_OF_SOURCE_FILES != 0) {
        println("intNO_OF_SOURCE_FILES : " + intNO_OF_SOURCE_FILES)
        intTempIndex = 0
        for (i <- intSourceFileLocStartIndex to intSourceFileLocEndIndex) {
          strKeyTemp = "SOURCE_NAME_" + intTempIndex
          strArraySourceNames(intTempIndex) = generalProperties.get(strKeyTemp).toString
          println(strKeyTemp + " : " + strArraySourceNames(intTempIndex))
          //----------------------- processing SOURCE -----------------------------------------------------------------------------------
          if (generalProperties.get("SOURCE_FORMAT").toString.toUpperCase().equals("JDBC")) {
            ArraySourceDFs(intTempIndex) = spark.read.format("jdbc")
              .option("url", generalProperties.get("SOURCE_DB_URL").toString)
              .option("user", generalProperties.get("SOURCE_DB_USER").toString)
              .option("password", generalProperties.get("SOURCE_DB_PASS").toString)
              .option("driver", generalProperties.get("SOURCE_JDBC_DRIVER_CLASS").toString)
              .option("fetchSize", generalProperties.get("SOURCE_FETCH_COUNT").toString)
              .option("dbtable", strArraySourceNames(intTempIndex).toString).load()

            /*
                          .option("dbtable", generalProperties.get("SOURCE_NAME_" + intTempIndex).toString).load()
            */
            ArraySourceDFs(intTempIndex).persist(StorageLevel.MEMORY_AND_DISK_SER)
            strKeyTemp = "SOURCE_NAME_" + intTempIndex
            ArraySourceDFs(intTempIndex).createOrReplaceTempView(strKeyTemp)
          } else if (generalProperties.get("SOURCE_FORMAT").toString.toUpperCase().equals("CSV")) {
            strArraySourceLoc(intTempIndex) = args(i)
            println("source file path_" + (intTempIndex) + " : " + strArraySourceLoc(intTempIndex))
            strKeyTemp = "SOURCE_DELIM"
            strDelimTemp = generalProperties.get(strKeyTemp).toString
            println("generalProperties.get(" + strKeyTemp + ") : " + strDelimTemp)
            strKeyTemp = "SOURCE_COLUMNS_" + intTempIndex
            strColumnNameTemp = generalProperties.get(strKeyTemp).toString
            println("generalProperties.get(" + strKeyTemp + ") : " + strColumnNameTemp)
            strStructTypeTemp = new StructType();
            for (intColIndex <- 0 to generalProperties.get(strKeyTemp).toString.split(",").length - 1) {
              strColumnName = generalProperties.get(strKeyTemp).toString.split(",")(intColIndex)
              strStructTypeTemp = strStructTypeTemp.add(strColumnName, strColumnType)
            }
            println("source file Schemas_" + (intTempIndex) + " : " + strStructTypeTemp)
            ArraySourceDFs(intTempIndex) = spark.read.format("csv").option("header", "false").option("delimiter", strDelimTemp).schema(strStructTypeTemp).load(strArraySourceLoc(intTempIndex).toString)
            strKeyTemp = "SOURCE_NAME_" + intTempIndex
            ArraySourceDFs(intTempIndex).createOrReplaceTempView(strKeyTemp)
          } else if (generalProperties.get("SOURCE_FORMAT").toString.toUpperCase().equals("PARQUET")) {
            strArraySourceLoc(intTempIndex) = args(i)
            println("source file path_" + (intTempIndex) + " : " + strArraySourceLoc(intTempIndex))
            ArraySourceDFs (intTempIndex) = spark.read.option("header", "true").parquet(strArraySourceLoc(intTempIndex))
            strKeyTemp = "SOURCE_NAME_" + intTempIndex
            ArraySourceDFs(intTempIndex).createOrReplaceTempView(strKeyTemp)
          }
          println("-------------------- Raw Data frame of Source " + intTempIndex + " -------------------- ")
          ArraySourceDFs(intTempIndex).show()
          intTempIndex = intTempIndex + 1
        } //end of for loop for multiple sources.
        FinalSourceDF = spark.sql(generalProperties.get("FINAL_SOURCE_JOIN_QUERY").toString)
        FinalSourceDF.createOrReplaceTempView(generalProperties.get("FINAL_SOURCE_NAME").toString)
      } else {
        println(" Error : Number of sources cannot be zero !")
        System.exit(1)
      }
      //------------- processing TARGET --------------------------------------------------------
      var intTargetFileLocStartIndex = intSourceFileLocEndIndex + 1
      var intTargetFileLocEndIndex = intNO_OF_TARGET_FILES + intTargetFileLocStartIndex - 1
      var strArrayTargetLoc = new Array[String](intNO_OF_TARGET_FILES)
      var strArrayTargetNames = new Array[String](intNO_OF_TARGET_FILES)
      var ArrayTargetDFs = new Array[DataFrame](intNO_OF_TARGET_FILES)
      var FinalTargetDF = ArrayTargetDFs(intNO_OF_TARGET_FILES - 1)
      if (intNO_OF_TARGET_FILES != 0) {
        println("intNO_OF_TARGET_FILES : " + intNO_OF_TARGET_FILES)
        intTempIndex = 0
        for (i <- intTargetFileLocStartIndex to intTargetFileLocEndIndex) {
          strKeyTemp = "TARGET_NAME_" + intTempIndex
          strArrayTargetNames (intTempIndex) = generalProperties.get(strKeyTemp).toString
          println (strKeyTemp + " : " + strArrayTargetNames(intTempIndex))
          //----------------------- processing Target -----------------------------------------------------------------------------------
          if (generalProperties.get("TARGET_FORMAT").toString.toUpperCase().equals("JDBC")) {
            ArrayTargetDFs(intTempIndex) = spark.read.format("jdbc")
              .option("url", generalProperties.get("TARGET_DB_URL").toString)
              .option("user", generalProperties.get("TARGET_DB_USER").toString)
              .option("password", generalProperties.get("TARGET_DB_PASS").toString)
              .option("driver", generalProperties.get("TARGET_JDBC_DRIVER_CLASS").toString)
              .option("fetchSize", generalProperties.get("TARGET_FETCH_COUNT").toString)
              .option("dbtable", strArrayTargetNames(intTempIndex).toString).load()

            strKeyTemp = "TARGET_NAME_" + intTempIndex
            ArrayTargetDFs (intTempIndex).createOrReplaceTempView(strKeyTemp)
          } else if (generalProperties.get("TARGET_FORMAT").toString.toUpperCase().equals("CSV")) {
            strArrayTargetLoc(intTempIndex) = args(i)
            println ("Target file path_" + (intTempIndex) + " : " + strArrayTargetLoc(intTempIndex))
            strKeyTemp = "TARGET_DELIM"
            strDelimTemp = generalProperties.get(strKeyTemp).toString
            println ("generalProperties.get(" + strKeyTemp + ") : " + strDelimTemp)
            strKeyTemp = "TARGET_COLUMNS_" + intTempIndex
            strColumnNameTemp = generalProperties.get(strKeyTemp).toString
            println ("generalProperties.get(" + strKeyTemp + ") : " + strColumnNameTemp)
            strStructTypeTemp = new StructType();
            for (intColIndex <- 0 to generalProperties.get(strKeyTemp).toString.split(",").length - 1) {
              strColumnName = generalProperties.get(strKeyTemp).toString.split(",")(intColIndex)
              strStructTypeTemp = strStructTypeTemp.add(strColumnName, strColumnType)
            }
            ArrayTargetDFs (intTempIndex) = spark.read.format("csv")
              .option("header", "false")
              .option("delimiter", strDelimTemp).schema(strStructTypeTemp).load(strArrayTargetLoc(intTempIndex).toString)
            strKeyTemp = "TARGET_NAME_" + intTempIndex
            ArrayTargetDFs (intTempIndex).createOrReplaceTempView(strKeyTemp)
          } else if (generalProperties.get("TARGET_FORMAT").toString.toUpperCase().equals("PARQUET")) {
            strArrayTargetLoc(intTempIndex) = args(i)
            println ("Target file path_" + (intTempIndex) + " : " + strArrayTargetLoc(intTempIndex))
            ArrayTargetDFs (intTempIndex) = spark.read.option("header", "true").parquet(strArrayTargetLoc(intTempIndex))
            strKeyTemp = "TARGET_NAME_" + intTempIndex
            ArrayTargetDFs (intTempIndex).createOrReplaceTempView(strKeyTemp)
          }
          println("-------------------- Raw Data frame of Target " + intTempIndex + " -------------------- ")
          ArrayTargetDFs(intTempIndex).show()
          intTempIndex = intTempIndex + 1
        } /*end of for loop.for multiple target*/
        FinalTargetDF = spark.sql(generalProperties.get("FINAL_TARGET_JOIN_QUERY").toString)
        FinalTargetDF.createOrReplaceTempView(generalProperties.get("FINAL_TARGET_NAME").toString)
        /* println("-------------------- Final Target -------------------- ") */
      } else {
        println(" Error : Number of Target cannot be zero !")
        System.exit(1)
      }
      /* ---------------------------------------------------------------------------------------------------------------------
      // ---------------------------------- test case validations started --------------------------------------------------- */
      /* ---------------------------------------------------------------------------------------------------------------------
       */
      println("")
      println ("-------------------------------------------------------------------------------------------------------------------------------------------------------")
      println ("--------\t\t\t" + generalProperties.get("TESTING_BUSINESS_NAME").toString + " @ ( " + strStartTime + " )\t\t\t---------------")
      println ("-------------------------------------------------------------------------------------------------------------------------------------------------------")

      val srcDF = spark.sql("select * from " + generalProperties.get("FINAL_SOURCE_NAME"))
      val tgtDF = spark.sql("select * from " + generalProperties.get("FINAL_TARGET_NAME"))
      val intSrcCount = srcDF.count()
      val intTgtCount = tgtDF.count()
      var tcNumber = 1
      println ("( " + tcNumber + " )------------ SOURCE AND TARGET COUNT and primary key VALIDATION ------------")
      println ("SOURCE_COUNT : " + intSrcCount)
      println ("TARGET_COUNT : " + intTgtCount)
      if (intSrcCount == intTgtCount) {
        strResult = PASSED
        intTotalPassScenarios = intTotalPassScenarios + 1
        println ("\n" + strResult + "\n")
      } else {
        strResult = FAILED
        strAllOverStatus = FAILED
        intTotalFailScenarios = intTotalFailScenarios + 1
        println ("\n" + strResult + "\n")
      }
      println ("\n")
      test_end_time_json = Calendar.getInstance.getTimeInMillis()
      duration_json = test_end_time_json - test_start_time_json
      duration_json = duration_json / 1000
      /* converting milliseconds to seconds*/
      intTestCasenumer = 1
      strJSON = strJSON +
        "\n\t{" + "\n \"storyId\": \"ATDD_Test_case_" + intTestCasenumer + "\"," +
        "\n \"name\": \"SOURCE AND TARGET COUNT and primary key VALIDATION\"," +
        "\n \"status\": \"" + strResult + "\"," +
        "\n \"duration\": " + duration_json + "\n\t},"
      val strEndTime = Calendar.getInstance().getTime()
      var intNUMBER_OF_TEST_CASES = Integer.parseInt(generalProperties.get("NUMBER_OF_TEST_CASES").toString)
      var strTestCaseName = ""
      val strTC_NME = "TC_NME_"
      val strTC_QRY = "TC_QRY_"
      /*-----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
      var tcDF = srcDF
      var tcValidationCount = 0L
      var strQry = ""
      for (intTCIndex <- 0 to intNUMBER_OF_TEST_CASES - 1) {
        tcNumber = tcNumber + 1
        intTestCasenumer = intTestCasenumer + 1;
        tcValidationCount = 0
        test_start_time_json = Calendar.getInstance.getTimeInMillis()
        println()
        strTestCaseName = generalProperties.get(strTC_NME + intTCIndex).toString
        println ("( " + tcNumber + " )------------ " + strTestCaseName + " ------------")
        strQry = generalProperties.get(strTC_QRY + intTCIndex).toString
        strQry = strQry.replaceAll("<FIC_MIS_DATE_VALUE>", "upper(from_unixtime(unix_timestamp(cast(TRIM('" + strFIC_MIS_DATE + "') as varchar(10)), 'yyyyMMdd'), 'dd-MMM-yyyy'))")
        strQry = strQry.replaceAll("<DATE>", strFIC_MIS_DATE)
        tcDF = spark.sql(strQry)
        tcValidationCount = tcDF.count()
        println (strTestCaseName + " COUNT : " + tcValidationCount)
        if (tcValidationCount == 0) {
          strResult = PASSED
          intTotalPassScenarios = intTotalPassScenarios + 1
          println ("\n" + strResult + "\n")
        } else {
          strResult = FAILED
          strAllOverStatus = FAILED
          intTotalFailScenarios = intTotalFailScenarios + 1
          println ("\n" + strResult + "\n")
          tcDF.show(Integer.parseInt(intTestCaseRecToShow), false)
          if (strTestCaseResToFile.toUpperCase.equals("YES"))
          {
            println(System.getProperty("user.dir")+"/"+strTestCaseName);
            tcDF.repartition(1).write.option("header", "true").mode("overwrite").csv(strTestCaseResFileLoc +"/"+ strTestCaseName )
          }
        }
        test_end_time_json = Calendar.getInstance.getTimeInMillis()
        duration_json = test_end_time_json - test_start_time_json
        duration_json = duration_json / 1000
        /* converting milliseconds to seconds */
        strJSON = strJSON + "\n\t{" + "\n \"storyId\": \"ATDD_Test_case_" + intTestCasenumer + "\"," + "\n \"name\": \"" + strTestCaseName + "\"," + "\n \"status\": \"" + strResult + "\"," + "\n \"duration\": " + duration_json + "\n\t},"
        println();
      }
      /*----------------------------------------------------------------------------------------------------------------------------------------------------------------- */
      strJSON = strJSON.substring(0, strJSON.length - 1)
      strJSON = strJSON + "\n]\n"
      val writer = new PrintWriter(new File(strDejavuATTDTestingSummaryJsonFileName))
      writer.write(strJSON)
      writer.close()
      println("-------------------------------------------------------------------------------------------------------------------------------------------------------")
      println ("\nTotal_Pass_Count: " + intTotalPassScenarios + "\nTotal_Fail_Count: " + intTotalFailScenarios + "\nStart_Time\t\t: " + strStartTime + "\nEndTime \t\t: " + strEndTime + "\nstatus \t\t\t: " + strAllOverStatus)
      println ("-------------------------------------------------------------------------------------------------------------------------------------------------------")
    }
    catch {
      case ex:
        Exception => println("Exception while comparing row by row ! ")
        println("ex.toString : " + ex.toString)
        println("ex.getMessage : " + ex.getMessage)
        println("ex.getClass : " + ex.getClass)
        println("ex.getStackTraceString : \n" + ex.getStackTraceString)
        System.exit(1)
    }
  }
}

