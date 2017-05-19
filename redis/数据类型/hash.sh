# 1. tools
int(field_count) hlen k
int(1/0) hexists k f

# 2. set & del
int(1/0 insert/update) hset k f fv
int(1/0 stat) hsetnx k f fv
stat hmset k f fv [f fv ...]

# 3. del
int(del count) hdel k f [f ...]

# 4. get
str hget k f
str[] hmget k f [f ...]
str[] hkeys k
str[] hvals k
str_fvpair[] hgetall k

# 5. num
int(new_val) hincrby k f delta
