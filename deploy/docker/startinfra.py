from conf import *
import time
import os
import shutil
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
   cmd=f"GATEWAY_DOMAIN={GATEWAY_DOMAIN} AUTH_USERS='{AUTH_USERS}' docker-compose -f "+env+"/docker-compose.traefik.yml up -d"
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
       jmx_port = get_config_value(config_data['infra_sba'], 'jmx_port', env)
       docker_image = docker_repo + ':' + tag

       JMX_PORT_MAP = ""
       if jmx_port and jmx_port > 0:
           JMX_PORT_MAP = f"- {jmx_port}:{jmx_port}"

       SBA_DOCKER_IMAGE=docker_image
       SBA_APP_PORT=app_port

       ROUTER_SBA0='flycat-sba0'
       ROUTER_SBA1='flycat-sba1'
       template = f"""
version: '3'
services:
  monitor:
    image: {SBA_DOCKER_IMAGE}
    container_name: "web-sba"
    #    restart: always # prevent other service unavailable yet
    ports:
      - {SBA_APP_PORT}:{SBA_APP_PORT}
      {JMX_PORT_MAP}
    deploy:
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 2
    labels:
      - traefik.enable=true
      - traefik.http.routers.{ROUTER_SBA0}.rule=PathPrefix(`/sba-admin`)
      - traefik.http.routers.{ROUTER_SBA1}.rule=PathPrefix(`/sba-admin`)
      - traefik.http.routers.{ROUTER_SBA0}.entrypoints=https
      - traefik.http.routers.{ROUTER_SBA0}.tls=true
      - traefik.http.services.{ROUTER_SBA0}-service.loadbalancer.server.port={SBA_APP_PORT}
      - traefik.http.routers.{ROUTER_SBA0}.service={ROUTER_SBA0}-service
      - traefik.http.routers.{ROUTER_SBA1}.entrypoints=http
      - traefik.http.routers.{ROUTER_SBA1}.service={ROUTER_SBA0}-service
      - traefik.docker.network=flycat_infra
    networks:
#      - traefik
      - infra

networks:
  infra:
    external:
      name: flycat_infra
      """

       text_file = open("./target/docker-compose.sba.yml", "w")
       #write string to file
       text_file.write(template)
       #close file
       text_file.close()

       cmd=f"ROUTER_SBA0=flycat-sba0 ROUTER_SBA1=flycat-sba1 SBA_DOCKER_IMAGE={docker_image} SBA_APP_PORT={app_port} docker-compose -f target/docker-compose.sba.yml up -d"
       print('Executing system command: %s' % cmd)
       log_execute_system(cmd)
       time.sleep(2)
       print('Started sba')

if 'infra_mysql' in config_data.keys():
    enable = get_config_value(config_data['infra_mysql'], 'enable', env)
    if enable:
       need_wait = False
       app_port = get_config_value(config_data['infra_mysql'], 'port', env)
       user = get_config_value(config_data['infra_mysql'], 'user', env)
       password = get_config_value(config_data['infra_mysql'], 'password', env)
       root_password = get_config_value(config_data['infra_mysql'], 'root_password', env)
       database = get_config_value(config_data['infra_mysql'], 'database', env)
       if "db-mysql" not in containers:
           if user == "root":
             print("Unable to set mysql user as root, it was created by default!")
             exit(0)
           log_execute_system(f"MYSQL_PORT={app_port} MYSQL_DATABASE={database} MYSQL_USER={user} "\
           "MYSQL_PASSWORD={password} MYSQL_ROOT_PASSWORD={root_password} docker-compose -f common/docker-compose.mysql.yml up -d""")
           need_wait = True

#        template = f"""CREATE USER IF NOT EXISTS 'user'@ IDENTIFIED BY 'password';"""
       sql_files = get_config_value(config_data['infra_mysql'], 'initSQL_files', env)
       if not os.path.exists(home_dir+'/deploy/cache/init-mysql'):
          os.makedirs(home_dir+ '/deploy/cache/init-mysql')
       docker_id = check_output('docker ps -f name=%s -q' % 'db-mysql', shell=True).decode().strip()
       granted = False
       for file in sql_files:
           basename = os.path.basename(file)
           dst = home_dir+'/deploy/cache/init-mysql/'+basename
           if not os.path.exists(dst):
               if need_wait:
                  time.sleep(5)
                  need_wait = False
               if not granted:
                  grantsql = f"""'echo "CREATE USER IF NOT EXISTS '"'"'{user}'"'"'@'"'"'%'"'"' IDENTIFIED BY '"'"'{password}'"'"'; GRANT ALL PRIVILEGES ON *.* TO '"'"'{user}'"'"'@'"'"'%'"'"' WITH GRANT OPTION;" > /tmp/grant.sql'"""
                  log_execute_system(f"docker exec {docker_id} /bin/sh -c {grantsql}")
                  log_execute_system(f"docker exec {docker_id} /bin/sh -c 'mysql -u root -p{root_password} < /tmp/grant.sql'")
                  granted = True
#               if file.startswith('/'):
#                  raise Exception("Sorry, file path cannot start with /")
    #            log_execute_system(f"docker exec {docker_id} /bin/sh -c 'mysql -u {user} -p{password} <{dst}'")
               src = config_dir + '/' + file
               log_execute_system(f"docker cp {src} {docker_id}:/tmp/{basename}")
#                log_execute_system(f"docker exec {docker_id} /bin/sh -c 'mysql -u root -p{root_password} </tmp/{basename}'")
               log_execute_system(f"docker exec {docker_id} /bin/sh -c 'mysql -u {user} -p{password} </tmp/{basename}'")
               shutil.copyfile(src, dst)

if 'infra_nacos' in config_data.keys():
    enable = get_config_value(config_data['infra_nacos'], 'enable', env)
    if enable and "config-nacos" not in containers:

       nacos_port = get_config_value(config_data['infra_nacos'], 'port', env, 8848)
       if nacos_port and nacos_port > 0:
           nacos_port_map = f"- {nacos_port}:{nacos_port}"

       host = get_config_value(config_data['infra_nacos'], 'mysql_host', env)
       port = get_config_value(config_data['infra_nacos'], 'mysql_port', env)
       user = get_config_value(config_data['infra_nacos'], 'mysql_user', env)
       password = get_config_value(config_data['infra_nacos'], 'mysql_password', env)
       database = get_config_value(config_data['infra_nacos'], 'mysql_database', env)

       jvm_xmx = get_config_value(config_data['infra_nacos'], 'jvm_xmx', env)
       jvm_xms = get_config_value(config_data['infra_nacos'], 'jvm_xms', env)
       jvm_xmn = get_config_value(config_data['infra_nacos'], 'jvm_xmn', env)

       jmx_port = get_config_value(config_data['infra_nacos'], 'jmx_port', env)
       jmx_port_map = ""
       jvm_args = []
       if jmx_port and jmx_port > 0:
           jmx_port_map = f"- \"{jmx_port}:{jmx_port}\""
           jvm_args.extend([
                                                    "-Djava.rmi.server.hostname=localhost",
                                                    "-Dcom.sun.management.jmxremote.local.only=false",
                                                    "-Dcom.sun.management.jmxremote=true",
                                                    "-Dcom.sun.management.jmxremote.port="+str(jmx_port),
                                                    "-Dcom.sun.management.jmxremote.rmi.port="+str(jmx_port),
                                                    "-Dcom.sun.management.jmxremote.authenticate=false",
                                                    "-Dcom.sun.management.jmxremote.ssl=false"])
       java_opt =  ' '.join(jvm_args)
       if not os.path.exists('./target/'):
          os.makedirs('./target/')

       MYSQL_PORT=port
       MYSQL_DATABASE=database
       MYSQL_USER=user
       MYSQL_PASSWORD=password
       MYSQL_HOST=host
       JVM_XMX = jvm_xmx
       JVM_XMS = jvm_xms
       JVM_XMN = jvm_xmn

       JAVA_OPT=java_opt
       NACOS_PORT = nacos_port

       ROUTER_NACOS0='flycat-nacos0'
       ROUTER_NACOS1='flycat-nacos1'

       template = f"""
version: '3'
services:
  config:
    container_name: "config-nacos"
    image: nacos/nacos-server:v2.1.1-slim
    restart: always
    volumes:
      - ~/deploy/logs/nacos/standalone-logs/:/home/nacos/logs
    networks:
      - infra
    environment:
      - PREFER_HOST_MODE=hostname
      - MODE=standalone
      - SPRING_DATASOURCE_PLATFORM=mysql
      - NACOS_AUTH_ENABLE=true
      - MYSQL_SERVICE_HOST={MYSQL_HOST}
      - MYSQL_SERVICE_DB_NAME={MYSQL_DATABASE}
      - MYSQL_SERVICE_PORT={MYSQL_PORT}
      - MYSQL_SERVICE_USER={MYSQL_USER}
      - MYSQL_SERVICE_PASSWORD={MYSQL_PASSWORD}
      - JVM_XMS={JVM_XMS}
      - JVM_XMX={JVM_XMX}
      - JVM_XMN={JVM_XMN}
      - MYSQL_SERVICE_DB_PARAM=characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true
      - JAVA_OPT={JAVA_OPT}
      - NACOS_APPLICATION_PORT={NACOS_PORT}
    labels:
      - traefik.enable=true
      - traefik.http.routers.{ROUTER_NACOS0}.rule=PathPrefix(`/nacos`)
      - traefik.http.routers.{ROUTER_NACOS1}.rule=PathPrefix(`/nacos`)
      - traefik.http.routers.{ROUTER_NACOS0}.entrypoints=https
      - traefik.http.routers.{ROUTER_NACOS0}.tls=true
      - traefik.http.services.{ROUTER_NACOS0}-service.loadbalancer.server.port={NACOS_PORT}
      - traefik.http.routers.{ROUTER_NACOS0}.service={ROUTER_NACOS0}-service
      - traefik.http.routers.{ROUTER_NACOS1}.entrypoints=http
      - traefik.http.routers.{ROUTER_NACOS1}.service={ROUTER_NACOS0}-service
      - traefik.docker.network=flycat_infra
    ports:
      - "9848:9848"
      - "9555:9555"
      {jmx_port_map}
      {nacos_port_map}

networks:
  infra:
    external:
      name: flycat_infra
"""
       text_file = open("./target/docker-compose.nacos.yml", "w")
       #write string to file
       text_file.write(template)
       #close file
       text_file.close()

       log_execute_system(f"MYSQL_PORT={port} MYSQL_DATABASE={database} MYSQL_USER={user}" \
        f" MYSQL_PASSWORD={password} MYSQL_HOST={host} JAVA_OPT=\"{java_opt}\" docker-compose -f ./target/docker-compose.nacos.yml up -d")


# if op == "update":
#     os.system("git fetch")
#     os.system("git reset --hard remotes/origin/master")
#     os.system('docker pull %s' % (APP_DOCKER_REPO))
