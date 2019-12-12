import os
from conf import *

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
    os.system('python3 ./deploy.py %s' % env)
else:
    print('Not found previous container')
