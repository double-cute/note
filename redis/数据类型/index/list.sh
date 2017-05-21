# 1. tools
int:
    lLen k

# 2. stack op
int(new_list_len):
    lPush|rPush k v1 v2 ...
str:
    lPop|rPop k

# 3. modify
int(new_list_len):
    lInsert k BEFORE|AFTER pivot_e ins_e
int(del_num):
    lRem k count del_e
str(trans_ele):
    RpopLpush k_src k_dst

# 4. index: - right
    # single
str:
    lIndex k index
stat:
    lSet k index new_e  # range err
    # range
str[]:
    lRange k beg end
stat:
    lTrim k beg end
