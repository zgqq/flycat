from fabric.connection import Connection
from fabric import task
import os
from pathlib import Path

home = str(Path.home())

@task
def createUser(ctx):
    server = input("host: ")  
    user = input("input user: ")  

    conn = Connection(host=server, user='root')
    try: 
        conn.run('ls')
    except Exception as e:
        os.system('ssh-copy-id root@'+server_ip)
        conn = Connection(host=server_ip, user='root')

def take_connection():
    server = input("connect: ")  
    return Connection(host=server)

    

@task 
def ohmyserver(ctx):
    conn = take_connection()
    install_dependancies(conn)
    install_fzf(conn)
    install_oh_my_zsh(conn)
    
def install_dependancies(conn):
    # fix Package libssl1.1:amd64 is not configured yet.
    conn.run('sudo rm -f /var/cache/debconf/*.dat || true && sudo rm -f /var/cache/apt/archives/lock && sudo rm -f /var/lib/dpkg/lock')     
    conn.run('sudo apt-get update && sudo apt-get -y install zsh openssl curl git goaccess')

def install_fzf(conn):
    conn.run('git clone --depth 1 https://github.com/junegunn/fzf.git ~/.fzf && ~/.fzf/install')

def install_oh_my_zsh(conn):
    conn.run('sh -c "$(curl -fsSL https://raw.githubusercontent.com/robbyrussell/oh-my-zsh/master/tools/install.sh)"')
    
    
