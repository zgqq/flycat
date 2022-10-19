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
"""
f = open(env+"/.env", "w")
f.write(docker_env)
f.close()

op = "deploy"
if len(sys.argv) > 3:
    op = sys.argv[3]

print('Executing operation, op:%s, env:%s' %(op, env))

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