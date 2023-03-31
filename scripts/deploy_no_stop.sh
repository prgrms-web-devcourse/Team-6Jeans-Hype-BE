REPOSITORY=/home/ubuntu/build/build/libs
JAR_NAME=$(ls ${REPOSITORY} | grep '.jar' | tail -n 1)
SUCCESS_HEALTH=테스트 health
WAS_IP=$(cat /home/ubuntu/build/was_ip.env)
NGINX_IP=$(cat /home/ubuntu/build/nginx_ip.env)
cd ${REPOSITORY}

CURRENT_PORT=$(sudo ssh -i ${REPOSITORY}/hype-ec2-key.pem ubuntu@${NGINX_IP} sudo cat /etc/nginx/conf.d/service_url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0
if [ ${CURRENT_PORT} -eq 8080 ]; then
  TARGET_PORT=8081
elif [ ${CURRENT_PORT} -eq 8081 ]; then
  TARGET_PORT=8080
else
  echo "> No WAS is connected to nginx"
  exit 1
fi

TARGET_PID=$(sudo lsof -ti tcp:${TARGET_PORT})
if [ ! -z ${TARGET_PID} ]; then
  echo "> Kill WAS running at ${TARGET_PORT}"
  sudo kill ${TARGET_PID}
fi

echo "> New WAS runs at ${TARGET_PORT}"
nohup java -jar -Dserver.port=${TARGET_PORT} -Dspring.profiles.active=prod -Dspring.config.import=optional:file:/home/ubuntu/build/prod_info.env[.properties] ${REPOSITORY}/${JAR_NAME} &

for RETRY in {1..10}
do
  HEALTH=$(curl -s http://127.0.0.1:${TARGET_PORT}/health)
  if [ ${HEALTH} -eq ${SUCCESS_HEALTH} ]
  then
    echo "> Health Check Success"
    echo "set \$service_url http://${WAS_IP}:${TARGET_PORT};" | sudo ssh -i ${REPOSITORY}/hype-ec2-key.pem ubuntu@${NGINX_IP} sudo tee /etc/nginx/conf.d/service_url.inc
    sudo ssh -i ${REPOSITORY}/hype-ec2-key.pem ubuntu@${NGINX_IP} sudo service nginx reload
    break
  elif [ ${RETRY} -eq 10 ]
  then
    echo "> Health Check Fail"
    exit 1
  fi
  echo "> Health Check Retry..."
  sleep 10
done

CURRENT_PID=$(sudo lsof -ti tcp:${CURRENT_PORT})
if [ -z ${CURRENT_PID} ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 ${CURRENT_PID}"
  sudo kill -15 ${CURRENT_PID}
  sleep 5
fi
