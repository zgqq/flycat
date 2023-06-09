import sys
import json
from subprocess import check_output
import os
from pathlib import Path
import argparse

parser = argparse.ArgumentParser()
parser.add_argument("--env", default=None, help="", required=True)
parser.add_argument("--conf",
                      default=None,
                      help="", required=True)
parser.add_argument("--module", default=None, help="",  required=False)
parser.add_argument("--target", default=None, help="",  required=False)
conf_args = parser.parse_args()
env = conf_args.env
conf_path = conf_args.conf
module_name = conf_args.module
target = conf_args.target

print('Got args, args:%s' % (conf_args))
home_dir = str(Path.home())

# env = "local"
# conf_path = "../../../config.json"
# if len(sys.argv) > 1:
#     print('Command args %s' % sys.argv)
#     env = sys.argv[1]
#     conf_path = sys.argv[2]
#
# module_name = None
# target =  None
# if len(sys.argv) > 3:
#     if sys.argv[3]!= "all":
#        module_name = sys.argv[3]
#     target = sys.argv[3]




tag = env
# if env == "local":
#    tag = "dockerlocal"
# else:
#    tag = "dockerprod"

def isLocalEnv():
    return env == "local"

def isProdEnv():
    return env == "prod"

def get_bool_value(data, key, env):
    config_value = get_config_value(data, key, env)
    return config_value != None

isDebug = True

def get_config_value(data, key, env, default_value = None):
    global module_name,isDebug
    if 'modules' in data.keys() and len(data['modules'].keys())>0:
        config_name = module_name
        if not module_name or module_name == "app":
           config_name = list(data['modules'].keys())[0]
        if isDebug:
           print('modules: %s, module_name:%s, read config module:%s, key:%s, env:%s' % (data['modules'].keys(), module_name,
           config_name,
           key, env))
        module_config = data['modules'][config_name]
        conf_value = get_config_value(module_config, key, env, None)
        if conf_value != None:
           return conf_value

    if 'env_overwrite' in data.keys() and env in data['env_overwrite']:
        if key in data['env_overwrite'][env].keys():
           return data['env_overwrite'][env][key]
    if key in data.keys():
       return data[key]
    return default_value

f = open(conf_path)
data = json.load(f)
# APP_DOMAIN = data['app_domain']
APP_NAME = get_config_value(data, 'app_name', env)
APP_PORT = get_config_value(data, 'app_port', env)
APP_DEBUG_PORT = get_config_value(data, 'debug_port', env)
HEALTHCHECK_PATH = get_config_value(data, 'healthcheck_path', env)

GATEWAY_USER = get_config_value(data, 'gateway_user', env)
GATEWAY_PASS = get_config_value(data, 'gateway_pass', env)
AUTH_USERS = get_config_value(data, 'gateway_auths', env)

GATEWAY_DOMAIN = get_config_value(data, 'gateway_domain', env)
config_data = data


def get_sub_config_value(main_key, key, env):
    if main_key in data.keys():
       return get_config_value(data[main_key], key, env)
    return None

def get_main_config_value(key, env, default_value = None):
    return get_config_value(data, key, env, default_value)

# APP_DOCKER_REPO = "zgqq/flycat-price:"+tag
# APP_NAME = "flycat-price"
# APP_DOMAIN = "price.zhenvip.wang"
# APP_PORT = 9020
# APP_DEBUG_PORT = 5006
# APP_TRAEFIK_SERVICE_URL = "http://localhost:8080/api/http/services/"+APP_ROUTER0+"-service@docker"


APP_BLUE = "blue-"+APP_NAME
APP_GREEN = "green-"+APP_NAME

APP_TRAEFIK_NETWORK = "flycat_infra"

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

def write_template(file_path, template):
   text_file = open(file_path, "w")
   #write string to file
   text_file.write(template)
   #close file
   text_file.close()

config_dir = os.path.dirname(os.path.abspath(conf_path)) ## directory of file


APP_ROUTER0 =  APP_NAME + '0'
APP_ROUTER1 =  APP_NAME + '1'
if (isProdEnv()):
   APP_TRAEFIK_SERVICE_URL = "https://"+GATEWAY_DOMAIN+"/api/http/services/"+APP_ROUTER0+"-service@docker"
else:
   APP_TRAEFIK_SERVICE_URL = "http://"+GATEWAY_DOMAIN+"/api/http/services/"+APP_ROUTER0+"-service@docker"
# APP_TRAEFIK_SERVICE_URL = "http://"+GATEWAY_DOMAIN+"/api/http/services/"+APP_ROUTER0+"-service@docker"


docker_repo_conf = get_main_config_value("docker_repo", env)

if ':' not in docker_repo_conf:
   image_type = get_main_config_value("image_type", env)
   if not image_type:
      APP_DOCKER_IMAGE = docker_repo_conf +":"+tag
   else:
      APP_DOCKER_IMAGE = docker_repo_conf
else:
   APP_DOCKER_IMAGE = docker_repo_conf

DOCKER_COMPOSE_CMD="docker compose"

# if not os.path.exists(home_dir+'/deploy'):
#    os.makedirs(home_dir+'/deploy')

# status_dir = f'{home_dir}/deploy/docker-userapp/{APP_NAME}/{env}'
# status_dir = f'{home_dir}/.{APP_NAME}/env'
status_dir = f'{home_dir}/deploy/cache/{APP_NAME}/{env}'
if not os.path.exists(status_dir):
   os.makedirs(status_dir)

TARGET_DIR = status_dir + '/target'
if not os.path.exists(TARGET_DIR):
  os.makedirs(TARGET_DIR)

LAST_DEPLOY_ID = f'{status_dir}/last_deploy_id'
CURRENT_DEPLOY_ID = f'{status_dir}/current_deploy_id'

DOCKER_COMPOSE_APP_YML = f'{TARGET_DIR}/docker-compose.app.yml'