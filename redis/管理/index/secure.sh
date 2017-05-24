# 1. bind IP: config
bind 127.0.0.1

# 2. passwd
    # config
requirepass 123
    # set
stat:
    config set requirepass 123
    # unset
stat:
    config set requirepass ""
    # authen
stat:
    auth passwd

# 3. cmd alias: config
rename-command FLUSHALL ""
