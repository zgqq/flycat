import subprocess
from subprocess import check_output
import urllib.request
import json
from urllib.parse import urlparse
import time

FETCH_COUNT = 60

start_time = time.time()

green = 'green'
blue = "blue"


def get_container_ip(blue_id):
    blue_info = check_output('docker inspect ' + blue_id, shell=True).decode().rstrip()
    blue_json_obj = json.loads(blue_info)
    obj_ = blue_json_obj[0]
    ip_address_ = obj_['NetworkSettings']['Networks']['traefik_webgateway']['IPAddress']
    return ip_address_, obj_


def execute(cmd):
    return check_output(cmd, shell=True).decode().strip()


def has_down_server():
    resp, server_ok = get_service_info()
    loaded_json = json.loads(resp)
    server_status = loaded_json['serverStatus']
    if server_status is not None:
        for x in server_status:
            if server_status[x] == 'DOWN':
                print('Server starting, name %s,ip %s' % (boot_project, ip_address_))
                return True
    return False


blue_id = check_output('docker ps -f name=%s -q' % blue, shell=True).decode().strip()
green_id = check_output('docker ps -f name=%s -q' % green, shell=True).decode().strip()

print('Got blue id %s, green id %s' % (blue_id, green_id))

ip_id = {}
id_name = {}

if blue_id:
    ip_address_, inspect = get_container_ip(blue_id)
    id_name[blue_id] = blue
    print('Map container, name %s, id %s, ip %s' % (blue, blue_id, ip_address_))
    ip_id[ip_address_] = blue_id

if green_id:
    ip_address_, inspect = get_container_ip(green_id)
    id_name[green_id] = green
    print('Map container, name %s, id %s, ip %s' % (green, green_id, ip_address_))
    ip_id[ip_address_] = green_id


def get_service_info():
    try:
        first_check = urllib.request \
            .urlopen("http://localhost:8080/api/http/services/rounter0-service@docker").read().decode()
    except urllib.error.HTTPError as e:
        print('Server error, %s' % str(e))
        return "", False
    return first_check, True


first_check, service_ok = get_service_info()
print("Service detail %s" % first_check)

if not service_ok:
    print('Service is unavailable, need close all containers')
    if blue_id:
        execute('docker stop ' + blue_id)
        blue_id = ''
    if green_id:
        execute('docker stop ' + green_id)
        green_id = ''

if first_check:
    loaded_json = json.loads(first_check)
    server_status = loaded_json['serverStatus']

    for x in server_status:
        if "DOWN" in server_status[x]:
            parsed_uri = urlparse(x)
            container_id = ip_id[parsed_uri.hostname]
            name = id_name[container_id]
            if name == green:
                print('Reset green id %s' % container_id)
                green_id = ''
            if name == blue:
                print('Reset blue id %s' % container_id)
                blue_id = ''
            print('Found down server, ip %s, force stop %s' % (x, name))
            stop_result = check_output('docker stop ' + container_id, shell=True).decode().strip()
            print('Stop container, %s' % stop_result)

boot_project = ''
close_container_id = ''
close_project = ''

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
    print('Starting %s container' % boot_project)
    execute('docker-compose -f docker-compose.app.yml --project-name=%s up -d' % boot_project)
    boot_id = check_output('docker ps -f name=%s -q' % boot_project, shell=True).decode().strip()
    ip_address_, inspect = get_container_ip(boot_id)
    if boot_id:
        try:
            execute('docker exec -it ' + boot_id + ' rm /app/stop')
        except subprocess.CalledProcessError as e:
            print('Ignored, fail to delete file %s' % str(e))

print('Waiting...')
time.sleep(5)

if boot_id and need_start:
    start = time.time()
    check_count = 0
    while True:
        check_count += 1
        if (check_count > FETCH_COUNT):
            break
        next = has_down_server()
        if not next:
            break
        time.sleep(1)
    end = time.time()
    if (check_count < FETCH_COUNT):
        print('Server started, cost %s s' % (end - start))
    else:
        print('Too many fail tries, dont close container, count %d' % check_count)
        need_close = False

if need_close:
    print("Removing %s" % close_project)
    execute('docker exec -it ' + close_container_id + ' touch /app/stop')
    stime = time.time()
    while not has_down_server():
        print('Detecting %s server status' % (close_project))
        time.sleep(1)
    etime = time.time()
    print('Removed server, cost %s' % (etime - stime))
    execute('docker exec -it ' + close_container_id + ' rm /app/stop')
    execute('docker stop ' + close_container_id)

end_time = time.time()
print('Deployed, cost %s s' % (end_time - start_time))
