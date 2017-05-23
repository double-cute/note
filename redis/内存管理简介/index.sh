# 1. cache
maxmemory size  # unenable embstr
maxmemory-policy
    volatile|allkeys-lru
    volatile|allkeys-random
    volatile-ttl
    noeviction

# 2. type-encoding

str:
string(raw, embstr, int)
hash(hashtable, ziplist)
list(linkedlist, quicklist, ziplist)
set(hashtable, intset)
zset(skiplist, ziplist)
    object encoding k

# config

# 1. string
maxmemory unenables embstr
# 2. hash、list、zset
hash|list|zset-max-ziplist-entries num
hash|list|zset-max-ziplist-value size
# 3. set
set-max-intset-entries num
