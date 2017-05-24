# redis-cli
str[]:
    info
mode:
    slowlog get
# config
slowlog-log-slower-than micro_sec
slowlog-max-len log_num

# Instagram Monitor
git clone https://github.com/facebookarchive/redis-faina
redis-cli monitor | head -n num_cmd_2_analyze | ./redis-faina.py

# rdb2json
    # install
git clone https://github.com/sripathikrishnan/redis-rdb-tools
cd redis-rdb-tools
sudo python setup.py install
    # rdb2json
rdb --command json rdb_path > output.json
    # keys info
rdb -c memory rdb_path > output.csv
