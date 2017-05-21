# 1. tools
int(size) scard k
int(1/0) sismember k test_ele

# 2. add & rem
int(add num) sadd k ele [ele ...]
int(rem num) srem k ele [ele ...]

# 3. get
str[] smembers k

# 4. op
str[] sdiff|sinter|sunion op_k [op_k ...]
int(res_size) s[diff|inter|union]store res res_k op_k [op_k ...]

# 5. random
str[] srandmember k [count] # +:diff, -:may same
str spop k
