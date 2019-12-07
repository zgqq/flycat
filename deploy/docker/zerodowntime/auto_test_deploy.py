import os
import time

count = 0
while True:
    count += 1
    print('Deploying %d count' % count)
    os.system('python3 ./deploy.py')
    print('Auto deployed %d count' % count)
    time.sleep(3)
