# 1. tools
int(field_num):
    hLen k
int(1/0):
    hExists k f

# 2. set & del
int(1/0 insert/update):
    hSet k f fv
int(1/0 stat):
    hSetNx k f fv
stat:
    hmSet k   f1 fv1   f2 fv2   ...
int(del_num):
    hDel k f1 f2 ...

# 3. get
str:
    hGet k f
str[]:
    hmGet k f1 f2 ...
str[]:
    hKeys k
str[]:
    hVals k
str_f-fv_pair[]:
    hGetAll k

# 4. num op
int(new_val):
    hIncrBy k f delta
