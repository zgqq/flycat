from subprocess import check_output
import sys
import os
from conf import *
import time

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
if not os.path.exists(home_dir+'/deploy'):
   os.makedirs(home_dir+'/deploy')

op = "deploy"
if len(sys.argv) > 3:
    op = sys.argv[3]

print('Executing operation, op:%s, env:%s' %(op, env))

router_domain = get_main_config_value("router_domain", env)
router_path = get_main_config_value("router_path", env)



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

template = f"""
version: '3'
services:
  web:
    image: {APP_DOCKER_IMAGE}
#    restart: always # prevent other service unavailable yet
    volumes:
      - ~/deploy/docker-userapp/{APP_NAME}/data:/userapp/data
      - ~/deploy/docker-userapp/{APP_NAME}/logs:/userapp/logs
      - ${{app_volume}}:/app
    environment:
      DEPLOY_IMAGE_ID: ${{deploy_image_id}}
      DEPLOY_APP_DIR: ${{app_volume}}
      DEPLOY_TAGS: ${{deploy_tags}}
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
write_template("./target/docker-compose.app.yml", template)

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
       os.system('docker tag %s %s' % (id, APP_DOCKER_REPO))
       os.system('python3 ./deploy.py %s' % (env, conf_path))
   else:
       print('Not found previous container')
else:
    os.system('python3 ./deploy.py %s %s' % (env, conf_path))