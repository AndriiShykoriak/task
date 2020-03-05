#!/usr/bin/env bash
mvn clean package

echo 'Copy files...'



scp -i ~/.ssh/key_london_aws.pem \
   target/Reserv-1.0-SNAPSHOT.jar \
ubuntu@35.176.165.108:/home/ubuntu/
      echo 'Restart server...'

ssh -i ~/.ssh/key_london_aws.pem ubuntu@35.176.165.108 << EOF

pgrep java | xargs kill -9

nohup java -jar Reserv-1.0-SNAPSHOT.jar > log.txt &
EOF

echo 'bye'







