#!/usr/bin/env bash
#if [[  "$OSTYPE" == "msys" ]]; then
#    export srcFileLoc1=../SampleDataFiles/src
#    export tgtFileLoc1=../SampleDataFiles/tgt
#else
#    export srcFileLoc1=file:////SampleDataFiles/src
#    export tgtFileLoc1=file:////SampleDataFiles/tgt
#fi
echo "bash ./Spark-Submit.sh"
bash ./Spark-Submit.sh

