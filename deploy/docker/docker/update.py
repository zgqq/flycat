import os
from conf import *

os.system('docker pull %s' % (APP_DOCKER_REPO))
os.system('python3 ./deploy.py %s' % env)
