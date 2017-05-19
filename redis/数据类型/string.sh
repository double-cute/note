# 1. tools
int strlen k

# 2. set
stat set k v
stat mset k v [k v ...]

# 3. get
str get k
str[] mget k [k ...]

# 4. append
int(k_new_strlen) append k v

# 5. num
int(v_new) incr|decr k
int(v_new) incrby|decrby k delta
str(v_new) incrbyfloat k delta

# 6. bit: 0, [start, end], r -1
    # get
int(bit_val) getbit k index
int(index) bitpos k test_bit_val [start_byte[ end_byte]]
int(1 count) bitcount k [start_byte[ end_byte]]
    # set
int(old_bit_val) setbit k index bit_val
int(res_strlen) bitop not|and|or|xor res_k op_k [op_k ...]
        # not-only-1-op, not nil == nil
        # others, nil == 0
