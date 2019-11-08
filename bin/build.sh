
#/C/Users/madhu/Rajasekaran/Learning/Code/ScalaSparkPythonCogTraining/sparkdatacompare/bin
# triggrering build
cd ..
ls -ltr
#read -p "$*"
mvn clean package
ls -ltr
#read -p "$*"
cd bin
mv ../target/sparkdatacompare-1.0-SNAPSHOT-jar-with-dependencies.jar ../lib/
rm -r ../target
ls -ltr
#read -p "$*"