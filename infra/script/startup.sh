#!/bin/bash
help() {
  echo "
  [help] CLI 파라미터
      -p <포트번호>
      -f <스프링 프로파일>
      -j <실행할 jar 파일>
      -n <앱 이름>
  "
}

profile="dev"               # Spring Profile 디폴트
port=8080                   # Spring 기본 포트
runnableJarName="app.jar"   # 실행할 jar 파일
appName="Super Invertion Server"

while getopts p:f:j:n opt
do
  case $opt in
    n)
      appName=$OPTARG
      ;;
    p)
      port=$OPTARG
      ;;
    f)
      profile=$OPTARG
      ;;
    j)
      runnableJarName=$OPTARG
      ;;
    *)
      help
      exit 0
      ;;
  esac
done

nohup java -jar "$runnableJarName"   \
-Dserver.port="$port"                \
-Dspring.profiles.active="$profile" &

if [ "$?" =  "0" ]; then
  echo "($appName) Run Successfully"
else
  echo "($appName) Run Fail"
fi;

