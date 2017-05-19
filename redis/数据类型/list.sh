# 1. tools
int llen k

# 2. stack
int(new_list_len) lpush|rpush k v [v ...]
str lpop|rpop k

# 3. modify
int(new_list_len) linsert k before|after pivot_ele ins_ele
int(rem count) lrem k count rem_ele
str(trans_ele) rpoplpush k_src k_dst

# 4. index: - right
str lindex k index
stat lset k index new_val  # range err

str[] lrange k beg end
stat ltrim k beg end
