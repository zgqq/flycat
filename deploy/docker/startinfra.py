from conf import *
import time
import os
# AUTH_USERS='{AUTH_USERS}'
# GATEWAY_DOMAIN={GATEWAY_DOMAIN}

if 'infra_mysql' in config_data.keys():
    if not os.path.exists('./common/mysql-initdb'):
       os.makedirs('./common/mysql-initdb')

all_networks = execute("docker network ls")
if "flycat_infra" not in all_networks:
   log_execute_system("docker network create -d bridge flycat_infra")

# if "webgateway_traefik" not in all_networks:
#    log_execute_system("docker network create -d bridge webgateway_traefik")

# if "db_mysql" not in all_networks:
#    os.system("docker network create -d bridge db_mysql")

containers = execute("docker container ls")
# if "db_mysql" not in containers:
#    log_execute_system("docker-compose -f docker-compose.mysql.yml up -d")

if "web_traefik" not in containers:
   cmd=f"GATEWAY_DOMAIN={GATEWAY_DOMAIN} AUTH_USERS={AUTH_USERS} docker-compose -f "+env+"/docker-compose.traefik.yml up -d"
   print('Executing system command: %s' % cmd)
   log_execute_system(cmd)
   time.sleep(2)
   print('Started traefik')

if 'infra_redis' in config_data.keys():
    enable = get_bool_value(config_data['infra_redis'], 'enable', env)
    if enable:
#         if "db_redis" not in all_networks:
#            log_execute("docker network create -d bridge db_redis")
        if "db-redis" not in containers:
           app_port = get_config_value(config_data['infra_redis'], 'port', env)
           password = get_config_value(config_data['infra_redis'], 'password', env)
           log_execute_system(f"REDIS_PORT={app_port} REDIS_PASSWORD={password} docker-compose -f common/docker-compose.redis.yml up -d")

if 'infra_sba' in config_data.keys():
    enable = get_config_value(config_data['infra_sba'], 'enable', env)
    if enable and "web-sba" not in containers:
       app_port = get_config_value(config_data['infra_sba'], 'app_port', env)
       docker_repo = get_config_value(config_data['infra_sba'], 'docker_repo', env)
       docker_image = docker_repo + ':' + tag
       cmd=f"ROUTER_SBA0=flycat-sba0 ROUTER_SBA1=flycat-sba1 SBA_DOCKER_IMAGE={docker_image} SBA_APP_PORT={app_port} docker-compose -f common/docker-compose.sba.yml up -d"
       print('Executing system command: %s' % cmd)
       log_execute_system(cmd)
       time.sleep(2)
       print('Started sba')

if 'infra_mysql' in config_data.keys():
    enable = get_config_value(config_data['infra_mysql'], 'enable', env)
    if enable and "db-mysql" not in containers:
       app_port = get_config_value(config_data['infra_mysql'], 'port', env)
       user = get_config_value(config_data['infra_mysql'], 'user', env)
       password = get_config_value(config_data['infra_mysql'], 'password', env)
       root_password = get_config_value(config_data['infra_mysql'], 'root_password', env)
       database = get_config_value(config_data['infra_mysql'], 'database', env)
       if user == "root":
         print("Unable to set mysql user as root, it was created by default!")
         exit(0)
       log_execute_system(f"""MYSQL_PORT={app_port} MYSQL_DATABASE={database} MYSQL_USER={user} MYSQL_PASSWORD={password} MYSQL_ROOT_PASSWORD={root_password} docker-compose -f common/docker-compose.mysql.yml up -d""")
# if 'infra_nacos' in config_data.keys():
#     enable = get_config_value(config_data['infra_nacos'], 'enable', env)
#     if enable and "config-nacos" not in containers:

# if op == "update":
#     os.system("git fetch")
#     os.system("git reset --hard remotes/origin/master")
#     os.system('docker pull %s' % (APP_DOCKER_REPO))
