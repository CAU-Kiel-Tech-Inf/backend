#!/bin/bash

server=flut
ssh -M -S ssh-ctrl-socket -fnNT -L 5000:localhost:5000 $server
ssh -S ssh-ctrl-socket -O check $server || exit 1
docker login localhost:5000 || exit 1
./gradlew clean dockerImage
docker tag swc_game-server localhost:5000/swc_game-server
docker push localhost:5000/swc_game-server
ssh -S ssh-ctrl-socket -O exit $server
# this is currently done in webapp
#scp docker-compose-production.yml $server:./docker-compose.yml
ssh $server 'sudo docker pull localhost:5000/swc_game-server' && \
# NOTE This requires the other services (client-controller and gameserver) in the compose file also already deployed
ssh $server 'sudo docker service update --image localhost:5000/swc_game-server:latest --with-registry-auth deploy_gameserver'
