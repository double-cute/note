# 1. tools
int(size):
    zCard k
int(del_num):
    zRem k e1 e2 ...

# 2. score op: score: float, ±inf
# add
int(new_add_num):
    zAdd k   sc1 e1   sc2 e2   ...
# get score
str:
    zScore k ele
# score range
int:
    zCount k min_sc max_sc
int(del_num):
    zRemRangeByScore k min_sc max_sc
str[] # offset: ref by output
    zRangeByScore k min_sc max_sc [WITHSCORES] [LIMIT offset count]
    zRevRangeByScore k min_sc max_sc [WITHSCORES] [LIMIT offset count]

# 3. rank op: rank: 0, -
# get rank
int:
    z[Rev]Rank k e
# rank range
int(del_num):
    zRemRangeByRank k beg end
str[]:
    z[Rev]Range k beg end [WITHSCORES]

# 4. set op: w: float
int(dst_size):
    zInter|UnionStore dst_k
        op_k_count  op_k1   op_k2   ...
        [WEIGHTS    w1      w2      ...]
        [AGGREGATE SUM|MIN|MAX]
