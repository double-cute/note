# 1. tools
int(size):
    sCard k
int(1/0):
    sIsMember k test_e

# 2. add & del
int(add_num):
    sAdd k e1 e2 ...
int(del_num):
    sRem k e1 e2 ...

# 3. get
str[]:
    sMembers k

# 4. op
str[]:
    s{Diff|Inter|Union} op_k1 op_k2 ...
int(dst_size):
    s{Diff|Inter|Union}Store res_k   op_k1 op_k2 ...

# 5. random
str[]:
    sRandMember k [count] # +:diff, -:may same
str:
    sPop k
