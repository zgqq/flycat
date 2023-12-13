from conf import *
import time
import os
import shutil
from pathlib import Path
# AUTH_USERS='{AUTH_USERS}'
# GATEWAY_DOMAIN={GATEWAY_DOMAIN}


if (target == None):
   target = "all"

start_all = target == "all"
start_traefik = (target == "traefik" or start_all)
start_mysql = (target == "mysql" or start_all)
start_sba = (target == "sba" or start_all)
start_nacos = (target == "nacos" or start_all)
start_redis = (target == "redis" or start_all)
start_registry = (target == "registry" or start_all)

deploy_dir = home_dir+'/deploy'
if not os.path.exists(deploy_dir):
   os.makedirs(deploy_dir)

if 'infra_mysql' in config_data.keys() and start_mysql:
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
#    log_execute_system("{DOCKER_COMPOSE_CMD} -f docker-compose.mysql.yml up -d")

if "web_traefik" not in containers and start_traefik:
   cmd=f"GATEWAY_DOMAIN={GATEWAY_DOMAIN} AUTH_USERS='{AUTH_USERS}' {DOCKER_COMPOSE_CMD} -f "+env+"/docker-compose.traefik.yml up -d"
   log_execute_system(cmd)
   print('Waiting traefik started...')
   time.sleep(5)
   print('Started traefik')

if 'infra_redis' in config_data.keys() and start_redis:
    enable = get_bool_value(config_data['infra_redis'], 'enable', env)
    if enable:
#         if "db_redis" not in all_networks:
#            log_execute("docker network create -d bridge db_redis")
        if "db-redis" not in containers:
           app_port = get_config_value(config_data['infra_redis'], 'port', env)
           password = get_config_value(config_data['infra_redis'], 'password', env)
           log_execute_system(f"REDIS_PORT={app_port} REDIS_PASSWORD={password} {DOCKER_COMPOSE_CMD} -f common/docker-compose.redis.yml up -d")


def infra_enabled(infra):
    enable = get_config_value(config_data[infra], 'enable', env)
    return enable

def execute_sql(docker_id, user, password):
      grantsql = f"""'echo "CREATE USER IF NOT EXISTS '"'"'{user}'"'"'@'"'"'%'"'"' IDENTIFIED BY '"'"'{password}'"'"'; GRANT ALL PRIVILEGES ON *.* TO '"'"'{user}'"'"'@'"'"'%'"'"' WITH GRANT OPTION;" > /tmp/grant.sql'"""
      log_execute_system(f"docker exec {docker_id} /bin/sh -c {grantsql}")
      log_execute_system(f"docker exec {docker_id} /bin/sh -c 'mysql -u root -p{root_password} < /tmp/grant.sql'")

def execute_sql_files(base_path, sql_files, user, password, root_password, need_wait, executed):
   if not os.path.exists(home_dir+'/deploy/cache/init-mysql'):
      os.makedirs(home_dir+ '/deploy/cache/init-mysql')
   docker_id = check_output('docker ps -f name=%s -q' % 'db-mysql', shell=True).decode().strip()
   granted = executed
   if executed:
      need_wait = False
   for file in sql_files:
       basename = os.path.basename(file)
       dst = home_dir+'/deploy/cache/init-mysql/'+basename
       if not os.path.exists(dst):
           if need_wait:
              print('Waiting mysql started...')
              time.sleep(20)
              need_wait = False
           if not granted:
              grantsql = f"""'echo "CREATE USER IF NOT EXISTS '"'"'{user}'"'"'@'"'"'%'"'"' IDENTIFIED BY '"'"'{password}'"'"'; GRANT ALL PRIVILEGES ON *.* TO '"'"'{user}'"'"'@'"'"'%'"'"' WITH GRANT OPTION;" > /tmp/grant.sql'"""
              log_execute_system(f"docker exec {docker_id} /bin/sh -c {grantsql}")
              log_execute_system(f"docker exec {docker_id} /bin/sh -c 'mysql -u root -p{root_password} < /tmp/grant.sql'")
              granted = True
#               if file.startswith('/'):
#                  raise Exception("Sorry, file path cannot start with /")
#            log_execute_system(f"docker exec {docker_id} /bin/sh -c 'mysql -u {user} -p{password} <{dst}'")
           src = base_path + '/' + file
           log_execute_system(f"docker cp {src} {docker_id}:/tmp/{basename}")
#                log_execute_system(f"docker exec {docker_id} /bin/sh -c 'mysql -u root -p{root_password} </tmp/{basename}'")
           log_execute_system(f"docker exec {docker_id} /bin/sh -c 'mysql -u {user} -p{password} </tmp/{basename}'")
           shutil.copyfile(src, dst)
           executed = True
   return executed

if 'infra_mysql' in config_data.keys() and start_mysql:
    enable = get_config_value(config_data['infra_mysql'], 'enable', env)
    if enable:
       need_wait = False
       app_port = get_config_value(config_data['infra_mysql'], 'port', env)
       user = get_config_value(config_data['infra_mysql'], 'user', env)
       password = get_config_value(config_data['infra_mysql'], 'password', env)
       root_password = get_config_value(config_data['infra_mysql'], 'root_password', env)
       database = get_config_value(config_data['infra_mysql'], 'database', env)
       backup_save_days = get_config_value(config_data['infra_mysql'], 'backup_save_days', env, 7)
       time_zone = get_config_value(config_data['infra_mysql'], 'time_zone', env, 'Asia/Shanghai')

       if "db-mysql" not in containers:
           if user == "root":
             print("Unable to set mysql user as root, it was created by default!")
             exit(0)
           log_execute_system(f"TIME_ZONE={time_zone} MYSQL_PORT={app_port} MYSQL_DATABASE={database} MYSQL_USER={user} "\
           f"MYSQL_PASSWORD={password} MYSQL_ROOT_PASSWORD={root_password} {DOCKER_COMPOSE_CMD} -f common/docker-compose.mysql.yml up -d")
           need_wait = True

#        template = f"""CREATE USER IF NOT EXISTS 'user'@ IDENTIFIED BY 'password';"""
       sql_files = get_config_value(config_data['infra_mysql'], 'initSQL_files', env, [])
       executed = execute_sql_files(config_dir, sql_files, user, password, root_password,need_wait, False)
       if infra_enabled('infra_nacos'):
          database = get_config_value(config_data['infra_nacos'], 'mysql_database', env)
          nacos_user = get_config_value(config_data['infra_nacos'], 'nacos_user', env)
          nacos_password = get_config_value(config_data['infra_nacos'], 'nacos_password', env)
          sql_content = Path('./common/mysql-initdb/nacos.sql').read_text()
          sql_content = sql_content.replace('{DATABASE}', database)
          sql_content = sql_content.replace('{NACOS_USER}', nacos_user)
          sql_content = sql_content.replace('{NACOS_PASSWORD}', nacos_password)
          write_template(f'{TARGET_DIR}/nacos.sql', sql_content)
          sql_dir = os.path.abspath(f'{TARGET_DIR}')
          internal_files = ['nacos.sql']
          execute_sql_files(sql_dir, internal_files, user, password, root_password, need_wait, executed)

       backup_databases = get_config_value(config_data['infra_mysql'], 'backup_databases', env, [])
       if len(backup_databases) > 0:
          print('Scheduling backup job')
          backup_dir = home_dir + '/backup/databases'
          if not os.path.exists(backup_dir):
             os.makedirs(backup_dir)
          backup_cron = get_config_value(config_data['infra_mysql'], 'backup_cron', env, '')
#           date_time = now.strftime("%Y%m%d-%H%M%S")
          jobs_str = ""
          try:
             jobs_str = execute('crontab -l')
          except Exception as e:
             pass

          job_list = jobs_str.split("\n")
          new_list = []
          cmd_list  = []

#           cmd_list.append(f"find {backup_dir} -type f -mtime + {backup_save_days} -name '*.gz' -execdir rm -- '{{}}' \;")
          # find /home/zgqq/backup/databases -name "*.sql.gz" -type f -mtime +15 -delete
          cmd_list.append(f"find {backup_dir} -name '*.sql.gz' -type f -mtime +{backup_save_days} -delete")

          for backup_database in backup_databases:
#                  /usr/bin/mysqldump -h $mysql_host -u $mysql_username -p$mysql_password $mysql_database | gzip -9 -c > $backup_path/$today/$mysql_database-`date +%H%M`.sql.gz
              time_var="backup_time=`date +'%Y-%m-%d_%H%M%S'`"
              backup_cmd = f"docker exec db-mysql sh -c 'exec mysqldump -u{user} -p{password} {backup_database} "\
              f"| gzip -9 -c' > {backup_dir}/{backup_database}-$backup_time.sql.gz"
              cmd_list.append(time_var)
              cmd_list.append(backup_cmd)
              cmd_list.append(f"rm {backup_dir}/{backup_database}-latest.sql.gz")
              cmd_list.append(f"ln -s {backup_dir}/{backup_database}-$backup_time.sql.gz {backup_dir}/{backup_database}-latest.sql.gz")
#               backup = f"{backup_cron} {backup_cmd}"
#               new_list.append(backup)
#               for job in job_list:
#                   if backup_cmd in job:
#                      job_list.remove(job)

          with open(home_dir + '/deploy/backup_mysql.sh', 'w') as f:
            for line in cmd_list:
                f.write(f"{line}\n")
          script_path = home_dir + '/deploy/backup_mysql.sh'
          log_execute_system('chmod +x '+script_path)

          for job in job_list:
              if not job:
                continue
              if script_path in job:
                 new_list.append(f"{backup_cron} {script_path}")
              else:
                 new_list.append(job)

          print('crontab list size %s' % len(new_list))
          if len(new_list) == 0:
              new_list.append(f"{backup_cron} {script_path}")

#           for job in job_list:
#               if job not in new_list:
#                  new_list.append(job)

          with open('/tmp/new_crontab', 'w') as f:
            for line in new_list:
                f.write(f"{line}\n")
          log_execute_system('crontab /tmp/new_crontab')

          with open('/tmp/new_crontab') as f:
            contents = f.read()
            print(contents)

#           log_execute_system('rm /tmp/new_crontab')
          print('Scheduled backup job' )
#            crontab


if 'infra_nacos' in config_data.keys() and start_nacos:
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

       ROUTER_NACOS0='flycat_nacos0'
       ROUTER_NACOS1='flycat_nacos1'

       HTTPS_TEMPLATE = ""

       if isProdEnv():
           HTTPS_TEMPLATE = f"""
      - traefik.http.routers.{ROUTER_NACOS0}.rule=Host(`{GATEWAY_DOMAIN}`) && PathPrefix(`/nacos`)
      - traefik.http.routers.{ROUTER_NACOS0}.entrypoints=https
      - traefik.http.routers.{ROUTER_NACOS0}.tls=true
      - traefik.http.routers.{ROUTER_NACOS0}.tls.certResolver=certer
      - traefik.http.routers.{ROUTER_NACOS0}.service={ROUTER_NACOS0}-service
      - traefik.http.middlewares.https-redirect.redirectscheme.scheme=https
      - traefik.http.routers.{ROUTER_NACOS1}.middlewares=https-redirect
              """

       template = f"""
version: '3'
services:
  config:
    container_name: "config-nacos"
    image: nacos/nacos-server:v2.2.0-slim
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
      {HTTPS_TEMPLATE}
      - traefik.http.services.{ROUTER_NACOS0}-service.loadbalancer.server.port={NACOS_PORT}
      - traefik.http.routers.{ROUTER_NACOS1}.rule=Host(`{GATEWAY_DOMAIN}`) && PathPrefix(`/nacos`)
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
       text_file = open(f"{TARGET_DIR}/docker-compose.nacos.yml", "w")
       #write string to file
       text_file.write(template)
       #close file
       text_file.close()

       log_execute_system(f"MYSQL_PORT={port} MYSQL_DATABASE={database} MYSQL_USER={user}" \
        f" MYSQL_PASSWORD={password} MYSQL_HOST={host} JAVA_OPT=\"{java_opt}\" {DOCKER_COMPOSE_CMD} -f {TARGET_DIR}/docker-compose.nacos.yml up -d")
       print('Waiting nacos started...')
       time.sleep(15)

if "registry" not in containers and isProdEnv() and start_registry:
   docker_dir = home_dir+'/deploy/docker'
   if not os.path.exists(docker_dir):
      os.makedirs(docker_dir)
      os.makedirs(docker_dir+'/auth')

   registry_user = get_config_value(config_data, 'docker_registry_user', env)
   registry_password = get_config_value(config_data, 'docker_registry_password', env)
   log_execute_system(f"docker run \
      --entrypoint htpasswd \
      httpd:2 -Bbn {registry_user} {registry_password} > {docker_dir}/auth/htpasswd")

   log_execute_system(f"docker rm registry")
   log_execute_system(f'docker run -d \
      -p 5000:5000 \
      --restart=always \
      --name registry \
      -v /mnt/registry:/var/lib/registry \
      -v {docker_dir}/auth:/auth \
      -e "REGISTRY_AUTH=htpasswd" \
      -e "REGISTRY_AUTH_HTPASSWD_REALM=Registry Realm" \
      -e REGISTRY_AUTH_HTPASSWD_PATH=/auth/htpasswd \
      -v {deploy_dir}/data/traefik/letsencrypt/certs:/certs \
      -e REGISTRY_HTTP_TLS_CERTIFICATE=/certs/certs/{DOCKER_REGISTRY_DOMAIN}.crt \
      -e REGISTRY_HTTP_TLS_KEY=/certs/private/{DOCKER_REGISTRY_DOMAIN}.key \
      registry:2')
   time.sleep(2)
   log_execute_system(f'echo {registry_password}  | docker login {DOCKER_REGISTRY_DOMAIN}:5000 --username {registry_user} --password-stdin')

# if op == "update":
#     os.system("git fetch")
#     os.system("git reset --hard remotes/origin/master")
#     os.system('docker pull %s' % (APP_DOCKER_REPO))


if 'infra_sba' in config_data.keys() and start_sba:
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

       ROUTER_SBA0='flycat_sba0'
       ROUTER_SBA1='flycat_sba1'

       HTTPS_TEMPLATE = ""
       if isProdEnv():
          HTTPS_TEMPLATE = f"""
      - traefik.http.routers.{ROUTER_SBA0}.rule=Host(`{GATEWAY_DOMAIN}`) && PathPrefix(`/sba-admin`)
      - traefik.http.routers.{ROUTER_SBA0}.entrypoints=https
      - traefik.http.routers.{ROUTER_SBA0}.tls=true
      - traefik.http.routers.{ROUTER_SBA0}.tls.certResolver=certer
      - traefik.http.routers.{ROUTER_SBA0}.service={ROUTER_SBA0}-service
      - traefik.http.middlewares.https-redirect.redirectscheme.scheme=https
      - traefik.http.routers.{ROUTER_SBA1}.middlewares=https-redirect
          """


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
      {HTTPS_TEMPLATE}
      - traefik.http.services.{ROUTER_SBA0}-service.loadbalancer.server.port={SBA_APP_PORT}
      - traefik.http.routers.{ROUTER_SBA1}.rule=Host(`{GATEWAY_DOMAIN}`) && PathPrefix(`/sba-admin`)
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

       text_file = open(f"{TARGET_DIR}/docker-compose.sba.yml", "w")
       #write string to file
       text_file.write(template)
       #close file
       text_file.close()
       if isProdEnv():
          os.system('docker pull %s' % (SBA_DOCKER_IMAGE))
       cmd=f"ROUTER_SBA0=flycat-sba0 ROUTER_SBA1=flycat-sba1 SBA_DOCKER_IMAGE={docker_image} SBA_APP_PORT={app_port} {DOCKER_COMPOSE_CMD} -f {TARGET_DIR}/docker-compose.sba.yml up -d"
       print('Executing system command: %s' % cmd)
       log_execute_system(cmd)
       time.sleep(2)
       print('Started sba')
