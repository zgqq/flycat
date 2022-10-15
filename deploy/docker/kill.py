from conf import *
from subprocess import check_output

def execute(cmd):
    print('Executing command %s' % (cmd))
    return check_output(cmd, shell=True).decode().strip()

app_id = check_output('docker ps -f name='+APP_NAME+' -q', shell=True).decode().strip()
if app_id:
   execute('docker stop ' + app_id)

sba_id = check_output('docker ps -f name=web-sba -q', shell=True).decode().strip()
if sba_id:
   execute('docker stop ' + sba_id)
   execute('docker rm web-sba')

web_traefik_id = check_output('docker ps -f name=web_traefik -q', shell=True).decode().strip()
if web_traefik_id:
   execute('docker stop ' + web_traefik_id)
   execute('docker rm web_traefik')


db_redis = check_output('docker ps -f name=db-redis -q', shell=True).decode().strip()
if db_redis:
   execute('docker stop ' + db_redis)
   execute('docker rm db-redis')

