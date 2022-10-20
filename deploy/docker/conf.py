import sys
import json
from subprocess import check_output
import os
from pathlib import Path

home_dir = str(Path.home())

env = "local"
conf_path = "../../../config.json"
if len(sys.argv) > 1:
    print('Command args %s' % sys.argv)
    env = sys.argv[1]
    conf_path = sys.argv[2]

tag = env
# if env == "local":
#    tag = "dockerlocal"
# else:
#    tag = "dockerprod"


def get_bool_value(data, key, env):
    config_value = get_config_value(data, key, env)
    return config_value != None

def get_config_value(data, key, env):
    if 'env_overwrite' in data.keys() and env in data['env_overwrite']:
        if key in data['env_overwrite'][env].keys():
           return data['env_overwrite'][env][key]
    if key in data.keys():
       return data[key]
    return None

f = open(conf_path)
data = json.load(f)
APP_DOCKER_REPO = data['docker_repo']+":"+tag
APP_NAME = data['app_name']
APP_DOMAIN = data['app_domain']
APP_PORT = data['app_port']
APP_DEBUG_PORT = data['app_debug_port']

GATEWAY_USER = data['gateway_user']
GATEWAY_PASS = data['gateway_pass']
AUTH_USERS = data['gateway_auths']

APP_ROUTER0 =  APP_NAME + '0'
APP_ROUTER1 =  APP_NAME + '1'

GATEWAY_DOMAIN = get_config_value(data, 'gateway_domain', env)
config_data = data


def get_sub_config_value(main_key, key, env):
    if main_key in data.keys():
       return get_config_value(data[main_key], key, env)
    return None

# APP_DOCKER_REPO = "zgqq/flycat-price:"+tag
# APP_NAME = "flycat-price"
# APP_DOMAIN = "price.zhenvip.wang"
# APP_PORT = 9020
# APP_DEBUG_PORT = 5006
# APP_TRAEFIK_SERVICE_URL = "http://localhost:8080/api/http/services/"+APP_ROUTER0+"-service@docker"
APP_TRAEFIK_SERVICE_URL = "http://"+GATEWAY_DOMAIN+"/api/http/services/"+APP_ROUTER0+"-service@docker"
# APP_TRAEFIK_SERVICE_URL = "http://"+GATEWAY_DOMAIN+"/api/http/services/"+APP_ROUTER0+"-service@docker"
print('Get service url %s' % (APP_TRAEFIK_SERVICE_URL))

DOCKER_COMPOSE_APP_YML = 'docker-compose.app.yml'

APP_BLUE = "blue-"+APP_NAME
APP_GREEN = "green-"+APP_NAME

APP_TRAEFIK_NETWORK = "flycat_infra"

LAST_DEPLOY_ID = env + '/server/last_deploy_id'
CURRENT_DEPLOY_ID = env + '/server/current_deploy_id'
print('Using %s env' % env)


def execute(cmd):
    return check_output(cmd, shell=True).decode().strip()

def log_execute(command):
   print("Executing system command: %s" % (command))
   os.system(command)

def log_execute_system(command):
   print("Executing system command: %s" % (command))
   code = os.system(command)
   if code > 0:
      sys.exit(code)

config_dir = os.path.dirname(os.path.abspath(conf_path)) ## directory of file