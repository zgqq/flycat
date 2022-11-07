from conf import *
from subprocess import check_output


def execute(cmd):
    print('Executing command %s' % (cmd))
    return check_output(cmd, shell=True).decode().strip()


app_id = check_output('docker ps -f name='+APP_NAME+' -q', shell=True).decode().strip()
if app_id:
   try:
       log_execute_system('docker exec -it  '+app_id+' /bin/bash -c "wget https://arthas.aliyun.com/arthas-boot.jar && java -jar arthas-boot.jar"')
   except:
       log_execute_system('docker exec -it  '+app_id+' /bin/bash -c "curl https://arthas.aliyun.com/arthas-boot.jar --output arthas-boot.jar && java -jar arthas-boot.jar"')



