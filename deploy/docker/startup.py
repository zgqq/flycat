from subprocess import check_output
import sys
import os
from conf import *
import time
import re


def execute(cmd):
    print('Executing command %s' % cmd)
    return check_output(cmd, shell=True).decode().strip()

# env = "local"
# if len(sys.argv) > 1:
#     env = sys.argv[1]

ROUTER0 =  APP_ROUTER0
ROUTER1 =  APP_ROUTER1

# docker_env = f"""# AUTO GENERATED
# APP_NAME={APP_NAME}
# APP_DOCKER_IMAGE={APP_DOCKER_REPO}
# APP_PORT={APP_PORT}
# APP_DEBUG_PORT={APP_DEBUG_PORT}
# ROUTER0={APP_ROUTER0}
# ROUTER1={APP_ROUTER1}
# """
# f = open(env+"/.env", "w")
# f.write(docker_env)
# f.close()

op = "deploy"
if len(sys.argv) > 4:
    op = sys.argv[4]

print('Executing operation, op:%s, env:%s' %(op, env))

router_domain = get_main_config_value("router_domain", env)
router_path = get_main_config_value("router_path", env)

image_type = get_main_config_value("image_type", env)




jmx_port = get_main_config_value('jmx_port', env, 0)
jmx_port_map = ""
jvm_args = []
PORTS = ""
if jmx_port and jmx_port > 0:
   PORTS = "ports:"
   jmx_port_map = f"""- {jmx_port}:{jmx_port}"""
JMX_PORT_MAP = jmx_port_map

app_port = get_main_config_value('app_port', env, 0)
app_port_map = ""
if app_port and app_port > 0 and isLocalEnv():
   PORTS = "ports:"
   app_port_map = f"""- {app_port}:{app_port}"""
APP_PORT_MAP = app_port_map

ROUTER_DOMAIN=router_domain
ROUTER_PATH=router_path

router_label = ""
if router_domain:
   router_label=f"""
       - traefik.http.routers.{ROUTER0}.rule=Host(`{ROUTER_DOMAIN}`)
       - traefik.http.routers.{ROUTER1}.rule=Host(`{ROUTER_DOMAIN}`)
                    """
if router_path:
   router_label=f"""
       - traefik.http.routers.{ROUTER0}.rule=PathPrefix(`{ROUTER_PATH}`)
       - traefik.http.routers.{ROUTER1}.rule=PathPrefix(`{ROUTER_PATH}`)
                    """

if router_domain and router_path:
   router_label=f"""
      - traefik.http.routers.{ROUTER0}.rule=Host(`{ROUTER_DOMAIN}`) && PathPrefix(`{ROUTER_PATH}`)
      - traefik.http.routers.{ROUTER1}.rule=Host(`{ROUTER_DOMAIN}`) && PathPrefix(`{ROUTER_PATH}`)
                    """
ROUTER_LABEL = router_label


ENVS = ""
docker_envs = get_main_config_value("docker_envs", env, [])
if docker_envs:
   for env_string in docker_envs:
       match = re.match(r"(\w+)=([^ ]+)", env_string)
       env_name = match.group(1)
       env_value = match.group(2)
       ENVS = ENVS + f"{env_name}: {env_value}\n"

docker_volumes = get_main_config_value("volumes", env, [])
VOLUMES = ""
if docker_volumes:
   for volume in docker_volumes:
       VOLUMES = VOLUMES + '- ' + volume + '\n'



def create_docker_compose(docker_file, app_image, volumes, envs, ports, app_port_map, router_labels, commands, router0, router1):
    volumes_str = 'volumes:\n' + '\n'.join([f'          - {volume}' for volume in volumes]) if volumes else ''
    ports_str = 'ports:\n' + '\n'.join([f'          - {port}' for port in ports]) if ports else ''
    envs_str = 'environment:\n' + '\n'.join([f'          - {env}' for env in envs]) if envs else ''
    router_labels_str = ''
    if router_labels:
        router_labels_str = f'- {router_labels[0]}' + '\n' + '\n'.join([f'          - {label}' for label in router_labels[1:]]) if len(router_labels) > 1 else f'- {router_labels[0]}'

    if commands:
        commands_str = 'command:\n' + '\n'.join([f'          - {cmd}' for cmd in commands])
    else:
        commands_str = ''

    template = f"""
    version: '3'
    services:
      web:
        image: {app_image}
        {volumes_str}
        {envs_str}
        {ports_str}
        deploy:
          restart_policy:
            condition: on-failure
            delay: 5s
            max_attempts: 2
        labels:
          - traefik.enable=true
          {router_labels_str}
          - traefik.http.middlewares.https-redirect.redirectscheme.scheme=https
          - traefik.http.routers.{router0}.entrypoints=https
          - traefik.http.routers.{router0}.tls=true
          - traefik.http.routers.{router0}.tls.certResolver=certer
          - traefik.http.routers.{router0}.service={router0}-service
          - traefik.http.services.{router0}-service.loadbalancer.server.port={app_port_map}
          - traefik.http.routers.{router1}.middlewares=https-redirect,compress
          - traefik.http.routers.{router1}.entrypoints=http
          - traefik.http.routers.{router1}.service={router0}-service
          - traefik.docker.network=flycat_infra
          - traefik.http.middlewares.compress.compress=true
        {commands_str}
        networks:
          - infra

    networks:
      infra:
        external:
          name: flycat_infra
    """

    with open(docker_file, 'w') as file:
        file.write(template)


router_labels = ""
if router_domain:
   router_labels= [f"traefik.http.routers.{ROUTER0}.rule=Host(`{ROUTER_DOMAIN}`)",
        f"traefik.http.routers.{ROUTER1}.rule=Host(`{ROUTER_DOMAIN}`)"]

if router_path:
   router_labels=[f"traefik.http.routers.{ROUTER0}.rule=PathPrefix(`{ROUTER_PATH}`)",
                   f"traefik.http.routers.{ROUTER1}.rule=PathPrefix(`{ROUTER_PATH}`)"]

if router_domain and router_path:
   router_labels = [f"traefik.http.routers.{ROUTER0}.rule=Host(`{ROUTER_DOMAIN}`) && PathPrefix(`{ROUTER_PATH}`)",
      f"traefik.http.routers.{ROUTER1}.rule=Host(`{ROUTER_DOMAIN}`) && PathPrefix(`{ROUTER_PATH}`)"]


docker_commands = get_main_config_value("docker_commands", env, [])
if image_type == "external":
    # 将变量替换为实际的值
    app_image = APP_DOCKER_IMAGE
    volumes = docker_volumes
    envs = docker_envs
    ports = [f"{app_port}:{app_port}"]
    app_port_map = app_port
    router_labels = router_labels
    commands = docker_commands
    create_docker_compose(DOCKER_COMPOSE_APP_YML, app_image, volumes, envs, ports, app_port_map, router_labels, commands, ROUTER0, ROUTER1)
    execute(
        "%s -f %s --project-name=%s up -d"
        % (DOCKER_COMPOSE_CMD, DOCKER_COMPOSE_APP_YML, APP_NAME)
    )
    exit()


template = f"""
version: '3'
services:
  web:
    image: {APP_DOCKER_IMAGE}
#    restart: always # prevent other service unavailable yet
    volumes:
#       - ~/deploy/docker-userapp/{APP_NAME}/data:/userapp/data
#       - ~/deploy/docker-userapp/{APP_NAME}/logs:/userapp/logs
      - ~/deploy/docker-userapp/{APP_NAME}:/userapp/
      - ${{app_volume}}:/app
      {VOLUMES}
    environment:
      DEPLOY_IMAGE_ID: ${{deploy_image_id}}
      DEPLOY_APP_DIR: ${{app_volume}}
      DEPLOY_TAGS: ${{deploy_tags}}
      {ENVS}
    {PORTS}
      {JMX_PORT_MAP}
      {APP_PORT_MAP}
    deploy:
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 2
    labels:
      - traefik.enable=true
      {ROUTER_LABEL}
      - traefik.http.middlewares.https-redirect.redirectscheme.scheme=https
      - traefik.http.routers.{ROUTER0}.entrypoints=https
      - traefik.http.routers.{ROUTER0}.tls=true
      - traefik.http.routers.{ROUTER0}.tls.certResolver=certer
      - traefik.http.routers.{ROUTER0}.service={ROUTER0}-service
      - traefik.http.services.{ROUTER0}-service.loadbalancer.server.port={APP_PORT}
      - traefik.http.services.{ROUTER0}-service.loadbalancer.healthcheck.path={HEALTHCHECK_PATH}
      - traefik.http.services.{ROUTER0}-service.loadbalancer.healthcheck.interval=5s
      - traefik.http.routers.{ROUTER1}.middlewares=https-redirect,compress
      - traefik.http.routers.{ROUTER1}.entrypoints=http
      - traefik.http.routers.{ROUTER1}.service={ROUTER0}-service
      - traefik.docker.network=flycat_infra
      - traefik.http.middlewares.compress.compress=true

    networks:
#      - traefik
      - infra

networks:
  infra:
    external:
      name: flycat_infra
"""
print("Writing docker compose app template")
write_template(f"{DOCKER_COMPOSE_APP_YML}", template)

if isProdEnv():
   log_execute_system(f'docker pull {APP_DOCKER_IMAGE}')

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
       os.system('docker tag %s %s' % (id, APP_DOCKER_IMAGE))
       os.system('python3 ./deploy.py --env %s --conf %s --module %s' % (env, conf_path, module_name))
   else:
       print('Not found previous container')
else:
#     os.system('python3 ./deploy.py %s %s' % (env, conf_path))
    os.system('python3 ./deploy.py --env %s --conf %s --module %s' % (env, conf_path, module_name))
