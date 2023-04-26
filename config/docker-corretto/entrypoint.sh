#!/bin/sh

touch /tmp/start_ep
cd $HOME/clash-for-linux/
#mv .env /tmp
echo "" > .env
sh start.sh
source /etc/profile.d/clash.sh
proxy_on
touch /tmp/end_ep
