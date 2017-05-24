# RDB

# basic config
dir $dumpfile_aof_dir
dbfilename $dumpfile_rename
appendfilename $aof_rename
rdbcompression yes|no

# 1. syn
stat:
    save

# 2. asyn
    # 1. conf
    save window_time_len(s) num_key_change
    # 2. manual
    stat(message):
        bgSave
    # 3. flushall
    stat:
        flushAll
    # 4. replication
    # 5. OS mem apply
    $ sudo vim /etc/sysctl.conf # vm.overcommit_memory = 1
    $ reboot | $ sudo sysctl vm.overcommit_memory=1

# 3. get
int(unix_time_stamp):
    lastSave


# AOF

# 1. manual rewrite: opti .aof
stat(message):
    bgRewriteAOF
# 2. config
auto-aof-rewrite-percentage  120
auto-aof-rewrite-min-size 64mb
# 3. OS cache sync
appendfsync always|everysec|no
