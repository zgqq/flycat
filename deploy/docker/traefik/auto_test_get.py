import time
import urllib.request

SITE_API = "http://localhost/v1/status"

def http_get(url):
    response = ""
    try:
        response = urllib.request \
            .urlopen(url).read().decode()
    except urllib.error.HTTPError as e:
        print('Server error ' + str(e))
    return response


succ = 0
fail = 0

while True:
    status_ok = True
    try:
        urlopen = urllib.request.urlopen(SITE_API)
        resp = urlopen.read().decode()
    except urllib.error.HTTPError as e:
        status_ok = False
    if status_ok:
        succ += 1
        if succ % 50 == 0:
            print('Running... succ:%d, fail:%d' % (succ, fail))
        time.sleep(0.1)
        continue
    fail += 1
    print('response %s' % resp)
    time.sleep(0.1)
