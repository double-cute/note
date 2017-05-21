# 1. tools
int:
    Strlen k

# 2. set
stat:
    Set k v
stat:
    mSet   k1 v1   k2 v2  ...

# 3. get
str:
    Get k
str[]:
    mGet k1 k2 ...

# 4. append
int(k_new_strlen):
    Append k v

# 5. num
int(v_new):
    Incr|Decr k
int(v_new):
    IncrBy|DecrBy k delta
str(v_new):
    IncrByFloat k delta

# 6. bit: 0, [start, end], r -1
    # get
int(bit_val):
    GetBit k index
int(index):
    BitPos k test_bit_val [start_byte [end_byte]]
int(1 count):
    BitCount k [start_byte [end_byte]]
    # set
int(old_bit_val):
    SetBit k index bit_val
int(res_strlen): # not-only-1-op, not nil == nil, others, nil == 0
    BitOp NOT|AND|OR|XOR
        res_k   op_k1 op_k2 ...
