#!/usr/bin/env python
import paramiko
import os
import sys
import time

blip = "127.0.0.1"
bluser = "yy"
blpasswd = "yy_in_lnx"

hostname = "192.168.56.166"
username = "yy"
password = "yy_in_lnx"

port = 22
passinfo = b"'s password: "
paramiko.util.log_to_file("sshlogin.log")

ssh = paramiko.SSHClient()
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
ssh.connect(hostname=blip, username=bluser, password=blpasswd)
channel = ssh.invoke_shell()
channel.settimeout(10)

buff = b""
resp = b""
channel.send(f"ssh {username}@{hostname}\n")

while not buff.endswith(passinfo):
    try:
        resp = channel.recv(9999)
    except Exception as e:
        print(f"error connect to {hostname}:{e} connection time.")
        channel.close()
        ssh.close()
        sys.exit()

    print(resp)
    buff += resp

    if not buff.find(b"yes/no") == -1:
        channel.send("yes\n")
        buff = b""

channel.send(password+"\n")

buff = b""
while not buff.endswith(b"$ "):
    resp = channel.recv(9999)
    if not resp.find(passinfo) == -1:
        print("Error info: Authentication failed.")
        channel.close()
        ssh.close()
        sys.exit()

    print(resp)
    buff += resp

channel.send("ifconfig\n")
buff = b""

try:
    while buff.find(b"$ ") == -1:
        resp = channel.recv(9999)
        buff += resp
except Exception as e:
    print(f"error ifconfig:{e}")

print(buff)
channel.close()
ssh.close()
