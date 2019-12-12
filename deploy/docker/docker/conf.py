import sys

APP_DOCKER_REPO = "zgqq/flycat-blog"
APP_TRAEFIK_SERVICE_URL = "http://localhost:8080/api/http/services/rounter0-service@docker"

DOCKER_COMPOSE_APP_YML = 'docker-compose.app.yml'

APP_BLUE = "blue"
APP_GREEN = "green"

APP_TRAEFIK_NETWORK = "traefik_webgateway"

env = "local"
if len(sys.argv) > 1:
    env = sys.argv[1]

LAST_DEPLOY_ID = env + '/data/last_deploy_id'
CURRENT_DEPLOY_ID = env + '/data/current_deploy_id'
print('Using %s env' % env)
