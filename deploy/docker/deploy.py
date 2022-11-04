import json
import subprocess
import time
import urllib.request
# import urllib2.request
from subprocess import check_output
from urllib.parse import urlparse
from conf import *
import os
from pathlib import Path
import shutil

APP_STOP = '/tmp/app_stop'

home = str(Path.home())

FETCH_COUNT = 60
start_time = time.time()

green = APP_GREEN
blue = APP_BLUE


def get_container_ip(blue_id):
    blue_info = check_output('docker inspect ' + blue_id, shell=True).decode().rstrip()
    blue_json_obj = json.loads(blue_info)
    obj_ = blue_json_obj[0]
    ip_address_ = obj_['NetworkSettings']['Networks'][APP_TRAEFIK_NETWORK]['IPAddress']
    return ip_address_, obj_


def execute(cmd):
    return check_output(cmd, shell=True).decode().strip()


def execute_and_catch(cmd):
    try:
        execute(cmd)
    except subprocess.CalledProcessError as e:
        print('Failed to execute %s' % str(e))


def execute_and_return_json(cmd):
    out = execute(cmd)
    json_obj = json.loads(out)
    return json_obj


def get_image_id(id):
    json = execute_and_return_json('docker inspect ' + id)
    return json[0]['Image'][7:20]


def get_last_img_id():
    with open(LAST_DEPLOY_ID, 'r') as file:
        id = file.read().replace('\n', '')
    return id


def get_cur_img_id():
    with open(CURRENT_DEPLOY_ID, 'r') as file:
        id = file.read().replace('\n', '')
    return id


def has_down_server():
    resp, server_ok = get_service_info()
    loaded_json = json.loads(resp)
    if 'serverStatus' in loaded_json:
        server_status = loaded_json['serverStatus']
        if server_status is not None:
            for x in server_status:
                if server_status[x] == 'DOWN':
                    print('Server starting, name %s,ip %s' % (boot_project, ip_address_))
                    return True
    return False


def is_down(id_address):
    resp, server_ok = get_service_info()
    if server_ok:
        loaded_json = json.loads(resp)
        if 'serverStatus' in loaded_json:
            server_status = loaded_json['serverStatus']
            if server_status is not None:
                for x in server_status:
                    parsed_uri = urlparse(x)
                    if parsed_uri.hostname == id_address:
                        if server_status[x] == 'DOWN':
                            return True
                        else:
                            return False
    return True


blue_id = check_output('docker ps -f name=%s -q' % blue, shell=True).decode().strip()
green_id = check_output('docker ps -f name=%s -q' % green, shell=True).decode().strip()

if '\n' in blue_id:
    print('%s has multiple containers %s, aborted' % (blue, blue_id))
    exit()

if '\n' in green_id:
    print('%s has multiple containers %s, aborted' % (green, green_id))
    exit()

if env == 'test' or env == 'local':
    if blue_id:
        print('Stop blue container %s' % (blue_id))
        check_output('docker stop ' + blue_id, shell=True).decode().strip()
        blue_id = ''
    if green_id:
        print('Stop green container %s' % (green_id))
        check_output('docker stop ' + green_id, shell=True).decode().strip()
        green_id = ''

print('Got blue id %s, green id %s' % (blue_id, green_id))

ip_id = {}
id_name = {}
name_id = {
    green: '',
    blue: ''
}

if blue_id:
    ip_address_, inspect = get_container_ip(blue_id)
    id_name[blue_id] = blue
    name_id[blue] = blue_id
    print('Map container, name %s, id %s, ip %s' % (blue, blue_id, ip_address_))
    ip_id[ip_address_] = blue_id

if green_id:
    ip_address_, inspect = get_container_ip(green_id)
    id_name[green_id] = green
    name_id[green] = green_id
    print('Map container, name %s, id %s, ip %s' % (green, green_id, ip_address_))
    ip_id[ip_address_] = green_id


# def credentials(url, username, password):
#     p = urllib2.HTTPPasswordMgrWithDefaultRealm()
#     p.add_password(None, url, username, password)
#     handler = urllib2.HTTPBasicAuthHandler(p)
#     opener = urllib2.build_opener(handler)
#     urllib2.install_opener(opener)

def credentials(url, username, password):
    # create a password manager
    password_mgr = urllib.request.HTTPPasswordMgrWithDefaultRealm()
    # Add the username and password.
    # If we knew the realm, we could use it instead of None.
    password_mgr.add_password(None, url, username, password)
    handler = urllib.request.HTTPBasicAuthHandler(password_mgr)
    # create "opener" (OpenerDirector instance)
    opener = urllib.request.build_opener(handler)
    # Install the opener.
    # Now all calls to urllib.request.urlopen use our opener.
    urllib.request.install_opener(opener)


def get_service_info():
    try:
        credentials(APP_TRAEFIK_SERVICE_URL, GATEWAY_USER, GATEWAY_PASS)
        first_check = urllib.request \
            .urlopen(APP_TRAEFIK_SERVICE_URL).read().decode()
    except urllib.error.HTTPError as e:
        print('Server error, %s' % str(e))
        return "", False
    return first_check, True


def get_image_tags(image_id):
    _info = check_output('docker inspect ' + image_id, shell=True).decode().rstrip()
    _json_obj = json.loads(_info)
    obj_ = _json_obj[0]
    tags = obj_['RepoTags']
    return tags


def contain_tag(image_id, str):
    tags = get_image_tags(image_id)
    for x in tags:
        if str in x:
            return True
    return False


def stop_down_servers():
    first_check, service_ok = get_service_info()
    print("Service detail %s" % first_check)

    closed_servers = {}
    if first_check:
        loaded_json = json.loads(first_check)
        if 'serverStatus' in loaded_json:
            server_status = loaded_json['serverStatus']
            for x in server_status:
                if "DOWN" in server_status[x]:
                    parsed_uri = urlparse(x)
                    if parsed_uri.hostname in ip_id.keys():
                        container_id = ip_id[parsed_uri.hostname]
                        name = id_name[container_id]
                        closed_servers[name] = container_id
                        print('Found down server, ip %s, force stop %s' % (x, name))
                        stop_result = check_output('docker stop ' + container_id, shell=True).decode().strip()
                        print('Stop container, %s' % stop_result)
                    else:
                        print('Not found container id by %s' % (parsed_uri.hostname))
        else:
            service_ok = False
    return closed_servers, service_ok


closed_servers, service_ok = stop_down_servers()

for x in closed_servers:
    name = x
    container_id = closed_servers[x]
    if name == green:
        print('Reset green id %s' % container_id)
        green_id = ''
    if name == blue:
        print('Reset blue id %s' % container_id)
        blue_id = ''

if not service_ok:
    print('Service is unavailable, need close all containers')
    if blue_id:
        execute('docker stop ' + blue_id)
        blue_id = ''
    if green_id:
        execute('docker stop ' + green_id)
        green_id = ''

boot_project = ''
close_container_id = ''
close_project = ''
boot_status = ''

if blue_id:
    boot_project = green
    close_project = blue
    close_container_id = blue_id

if green_id:
    boot_project = blue
    close_project = green
    close_container_id = green_id

need_start = True
need_close = True

if green_id and boot_project == green:
    print('Server previously started, project %s, id %s' % (green, green_id))
    need_start = False

if blue_id and boot_project == blue:
    print('Server previously started, project %s, id %s' % (blue, blue_id))
    need_start = False

if not green_id and not blue_id:
    need_start = True
    boot_project = blue
    need_close = False

boot_id = ''
ip_address_ = ''
if need_start:
    start = time.time()
    if boot_project == blue:
        boot_status = 'blue'
    else:
        boot_status = 'green'

    print('Starting %s container' % boot_project)
    cp_container_id = execute('docker create %s' % APP_DOCKER_IMAGE)

    directory = "%s/deploy/docker-container/%s/%s" % (Path.home(), APP_NAME, boot_status)
    current_deploy = "%s/deploy/docker-container/%s/current" % (Path.home(), APP_NAME)
    if not os.path.exists(directory):
        os.makedirs(directory)
    else:
        shutil.rmtree(directory, ignore_errors=True)
        os.makedirs(directory)
    deployed_directory = directory
    app_directory = directory + '/app'
    execute('docker cp %s:/app %s' % (cp_container_id, directory))
    deploy_image_id = get_image_id(cp_container_id)
    deploy_tags = get_image_tags(deploy_image_id)
    print('Preparing deploy, image id %s, app directory %s, tags %s' % (deploy_image_id, app_directory, deploy_tags))
    yml = DOCKER_COMPOSE_APP_YML
#     execute('cd %s && %s {DOCKER_COMPOSE_CMD} -f %s --project-name=%s up -d' % (
#         env,
#         'app_volume=' + app_directory+' deploy_image_id='+deploy_image_id+' deploy_tags='+(','.join(deploy_tags)),
#         yml,
#         boot_project))

    execute('%s %s -f %s --project-name=%s up -d' % (
        'app_volume=' + app_directory+' deploy_image_id='+deploy_image_id+' deploy_tags='+(','.join(deploy_tags)),
        DOCKER_COMPOSE_CMD,
        yml,
        boot_project))
    boot_id = check_output('docker ps -f name=%s -q' % boot_project, shell=True).decode().strip()
    ip_address_, inspect = get_container_ip(boot_id)
    if boot_id:
        ip_id[ip_address_] = boot_id
        id_name[boot_id] = boot_project
        name_id[boot_project] = boot_id
        cur_img_id = get_image_id(boot_id)
        print('Deployed image id %s' % cur_img_id)
        try:
            execute('docker exec ' + boot_id + ' rm ' + APP_STOP)
        except subprocess.CalledProcessError as e:
            print('Ignored, fail to delete file %s' % str(e))

print('Waiting...')
time.sleep(2)

def write_current_deploy_id():
    directory = os.path.dirname(CURRENT_DEPLOY_ID)
    if not os.path.exists(directory):
        os.makedirs(directory)
    print(cur_img_id, file=open(CURRENT_DEPLOY_ID, 'w'))

    if os.path.exists(current_deploy):
        os.remove(current_deploy)
    os.symlink(deployed_directory, current_deploy)


def write_last_deploy_id():
    directory = os.path.dirname(LAST_DEPLOY_ID)
    if not os.path.exists(directory):
        os.makedirs(directory)
    print(last_img_id, file=open(LAST_DEPLOY_ID, 'w'))


if need_start and boot_id:
    check_count = 0
    while True:
        check_count += 1
        if (check_count > FETCH_COUNT):
            break
        print('Server starting, name %s,ip %s' % (boot_project, ip_address_))
        next = is_down(ip_address_)
        if not next:
            break
        time.sleep(1)
    end = time.time()
    if (check_count < FETCH_COUNT):
        print('Server started, cost %s s' % (end - start))
        cur_img_id = get_image_id(boot_id)
        if close_project:
            old_id = name_id[close_project]
            if old_id:
                last_img_id = get_image_id(old_id)
                print('last image id %s, boot image id %s' % (last_img_id, cur_img_id))
                if last_img_id != cur_img_id:
                    write_last_deploy_id()
                else:
                    print('image id is not changed')
        write_current_deploy_id()
    else:
        resp, server_ok = get_service_info()
        print("Service detail %s" % resp)
        stop_down_servers()
        print('Too many fail tries, dont close container, count %d' % check_count)
        logs = execute('docker logs --tail=20 -t %s' % boot_id)
        print('May cause: %s' % logs)
        need_close = False
elif need_start and not boot_id:
    print('Fail to startup server, no need to close server, %s' % need_close)
    need_close = False

if need_close:
    print("Removing %s, id %s" % (close_project, close_container_id))
    execute('docker exec ' + close_container_id + ' touch ' + APP_STOP)
    stime = time.time()
    count = 0
    while not has_down_server():
        print('Detecting %s server status' % (close_project))
        count += 1
        if count > 10:
            break
        time.sleep(1)
    etime = time.time()
    print('Removed server, cost %s' % (etime - stime))
    execute_and_catch('docker exec ' + close_container_id + ' rm ' + APP_STOP)
    execute('docker stop ' + close_container_id)

end_time = time.time()
print('Deployed, cost %s s' % (end_time - start_time))
