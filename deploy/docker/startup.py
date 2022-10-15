from subprocess import check_output
import sys
import os
from conf import *
import time

# env = "local"
# if len(sys.argv) > 1:
#     env = sys.argv[1]

docker_env = f"""# AUTO GENERATED
APP_NAME={APP_NAME}
APP_DOMAIN={APP_DOMAIN}
APP_DOCKER_IMAGE={APP_DOCKER_REPO}
APP_PORT={APP_PORT}
APP_DEBUG_PORT={APP_DEBUG_PORT}
ROUTER0={APP_ROUTER0}
ROUTER1={APP_ROUTER1}
AUTH_USERS='{AUTH_USERS}'
GATEWAY_DOMAIN={GATEWAY_DOMAIN}
"""
f = open(env+"/.env", "w")
f.write(docker_env)
f.close()

op = "deploy"
if len(sys.argv) > 3:
    op = sys.argv[3]

print('Executing operation, op:%s, env:%s' %(op, env))

def execute(cmd):
    return check_output(cmd, shell=True).decode().strip()

def log_execute(command):
   print("Executing system command: %s" % (command))
   os.system(command)

all_networks = execute("docker network ls")
if "webgateway_traefik" not in all_networks:
   os.system("docker network create -d bridge webgateway_traefik")

if "db_mysql" not in all_networks:
   os.system("docker network create -d bridge db_mysql")


containers = execute("docker container ls")
if "db_mysql" not in containers:
   os.system("docker-compose -f docker-compose.mysql.yml up -d")

if "web_traefik" not in containers:
   cmd="docker-compose -f "+env+"/docker-compose.traefik.yml up -d"
   print('Executing system command: %s' % cmd)
   os.system(cmd)
   time.sleep(2)
   print('Started traefik')

if 'infra_redis' in config_data.keys():
    enable = get_bool_value(config_data['infra_redis'], 'enable', env)
    if enable:
        if "db_redis" not in all_networks:
           log_execute("docker network create -d bridge db_redis")
        if "db-redis" not in containers:
           app_port = get_config_value(config_data['infra_redis'], 'port', env)
           password = get_config_value(config_data['infra_redis'], 'password', env)
           log_execute(f"REDIS_PORT={app_port} REDIS_PASSWORD={password} docker-compose -f common/docker-compose.redis.yml up -d")

if 'infra_sba' in config_data.keys():
    enable = get_config_value(config_data['infra_sba'], 'enable', env)
    if enable and "web-sba" not in containers:
       app_port = get_config_value(config_data['infra_sba'], 'app_port', env)
       docker_repo = get_config_value(config_data['infra_sba'], 'docker_repo', env)
       docker_image = docker_repo + ':' + tag
       cmd=f"ROUTER_SBA0=flycat-sba0 ROUTER_SBA1=flycat-sba1 SBA_DOCKER_IMAGE={docker_image} SBA_APP_PORT={app_port} docker-compose -f common/docker-compose.sba.yml up -d"
       print('Executing system command: %s' % cmd)
       os.system(cmd)
       time.sleep(2)
       print('Started sba')

# if op == "update":
#     os.system("git fetch")
#     os.system("git reset --hard remotes/origin/master")
#     os.system('docker pull %s' % (APP_DOCKER_REPO))


if op == "rollback":
   id = ''
   try:
       with open(LAST_DEPLOY_ID, 'r') as file:
           id = file.read().replace('\n', '')
   except FileNotFoundError:
       pass
   except BaseException as e:
       print('Fail to read last deploy image id, %s' % str(e))

   print('Trying to rollback previous container %s' % id)
   if id:
       os.system('docker tag %s %s' % (id, APP_DOCKER_REPO))
       os.system('python3 ./deploy.py %s' % (env, conf_path))
   else:
       print('Not found previous container')
else:
    os.system('python3 ./deploy.py %s %s' % (env, conf_path))