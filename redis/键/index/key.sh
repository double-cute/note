# 1. tools
int(1/0):
    exists k
stat(string/hash/list/set/zset/none):
    type k

# 2. get
str[]:
    keys pattern_k # ? * [a-cxy] \

# 3. del
int(del_num):
    del k1 k2 ...
    # bat del
redis-cli keys "pattern_k" | xargs redis-cli del
redis-cli del `redis-cli keys "pattern_k"` # recommand

# 4. expire
    # expire
int(1/0):
    [p]Expire k time_len
int(1/0):
    [p]ExpireAt k unix_time_stamp
    # ttl
int(+:n/-1, -:-2):
    [p]TTL k
    # persist: string set
int(1/0):
    persist k
