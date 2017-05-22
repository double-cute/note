stat:
    WATCH k1 k2 ...
stat:
    MULTI
str[]:
    EXEC
stat:
    UNWATCH


WATCH k1 k2 ...
# watcher change eliminate EXEC
if ...
    # ...
    MULTI
        ...
        # watcher change normally EXEC
        ...
    EXEC
else
    UNWATCH  # watch-multi-exec
