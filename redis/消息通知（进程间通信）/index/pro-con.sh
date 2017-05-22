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
