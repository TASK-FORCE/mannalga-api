#!/bin/bash
port=8080
while getopts p:f:j:n opt
do
  case $opt in
    p)
      port=$OPTARG
      ;;
    *)
      help
      exit 0
      ;;
  esac
done

SERVER_PID=$(lsof -i TCP:"$port" | awk 'NR == 2 {print $2}');

if [ -z "$SERVER_PID" ]; then
    echo "[info] No running PID detected"
else
    echo "kill -15 $CURRENT_PID"
    kill -15 "$SERVER_PID"
    echo "[info] Spring Boot Project Shutdown Success"
fi