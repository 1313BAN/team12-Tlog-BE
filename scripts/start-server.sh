#!/bin/bash

echo "--------------- 서버 배포 시작 -----------------"
cd /home/ec2-user/team12-Tlog-BE
sudo fuser -k -n tcp 8080 || true
nohup java -jar Tlog-0.0.1-DEMO.jar > ./output.log 2>&1 &
echo "--------------- 서버 배포 끝 -----------------"
