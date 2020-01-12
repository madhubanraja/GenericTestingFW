#!/usr/bin/env
#bash build.sh
# PARAMETER DEFINITION - BEGINS
##############################################################
#### ----------->>> EDIT THEM BASED ON NEED <<<-----------####
##############################################################
# yarn / local
export strSparkMode=local
export intTestCaseRecToShow=25
# yes \ no
export strTestCaseResToFile=yes

##############################################################
###----------->>> DO NOT EDIT BEYOND THIS POINT <<<-----------
##############################################################
export start_time=$(date +%s)
export strTestCaseResFileLoc=""
export txtLogFileName="TestSummary.log"
export jsnLogFileName="TestSummary.json"
export strParamProjectName=""
export strParamDate=""
export strParamDateFormat=""
export strGenericConfigFileName=""

if [ $# -eq 2 ] ;
then
    strTestCaseResFileLoc=./TestCaseLogs
    txtLogFileName=$1.log
    jsnLogFileName=$1.json
    echo > $txtLogFileName
    echo > $jsnLogFileName
    strParamProjectName=$1
    strParamDate=$2
    strParamDateFormat=MMDDYYYY
    strGenericConfigFileName=../config/$strParamProjectName.properties
    bash ../config/$strParamProjectName.sh
    cat $txtLogFileName
    cat $jsnLogFileName
else
    echo $#
    echo -e "\n\n In-sufficient / more than two arguments found. please pass the project name. please pass the project name as argument. Arguments can be \n\n\tProject Name\t\t ------> AllInoneConfig / \n\t\tDATE\t\t-----> 20190930 " >> $txtLogFileName
    echo -e "bash Exec.sh MyDBProject 20191230" >> $txtLogFileName
fi #end of parameter check

####################### Setting up the end timing

export end_time=$(date +%s)
export time_diff=$(( $end_time - $start_time ))


# function definition

diff_time()
{
    num=$1
    min=0
    hour=0
    day=0
    if((num>59));then
        ((sec=num%60))
        ((num=num/60))
        if((num>59));then
            ((min=num%60))
            ((num=num/60))
            if((num>23));then
                ((hour=num%24))
                ((day=num/24))
            else
                ((hour=num))
            fi
        else
            ((min=num))
        fi
    else
        ((sec=num))
    fi
    echo "---------------------------------------------------------" >> $txtLogFileName
    echo "Total Time Taken: $day day, $hour hour, $min min $sec sec" >> $txtLogFileName
    echo "---------------------------------------------------------" >> $txtLogFileName
}

diff_time $time_diff


