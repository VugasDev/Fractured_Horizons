import gzip, io
import zipfile

z = zipfile.ZipFile(r'C:\Users\larss\.gradle\caches\forge_gradle\minecraft_user_repo\net\minecraft\client\extra-mapped\1.21.1\extra-mapped-1.21.1.jar')
with z.open('net/minecraft/server/MinecraftServer.java') as f:
    text = f.read().decode('utf-8')
    for line in text.splitlines():
        if 'new ServerLevel' in line:
            print(line.strip())
