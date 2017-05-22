# 1. task queue
str[k_list, pop_e]|nil4timeout:
    b[l|r]Pop
        k_list1
        k_list2
        ...
        timeout(s)  # 0 for infinite

# consumer
infinite_loop:
    task = brPop
            task_queue_important
            task_queue_normal
            task_queue_low
            0;
    excute($task)


# 2. publish-subscribe
    # publish
int(rec_cli_num):
    PUBLISH ch message
    # subscribe
enter_mode:
    SUBSCRIBE ch1 ch2 ...
str[]:
    UNSUBSCRIBE [ch1 ch2 ...]
    # psubscribe
enter_mode:
    PSUBSCRIBE pattern_ch1 pattern_ch2 ...
str[]:
    PUNSUBSCRIBE [pattern_ch1 pattern_ch2 ...]
