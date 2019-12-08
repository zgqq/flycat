import os
from conf import *

id = ''
with open('last_deploy_id', 'r') as file:
    id = file.read().replace('\n', '')
print('Trying to rollback previous container %s' % id)
if id:
    os.system('docker tag %s %s' % (id, APP_DOCKER_REPO))
    os.system('python3 ./deploy.py')
else:
    print('Not found previous container')
