sort key [LIMIT offset count] # [offset, offset+count], offset: -
    [STORE dstKey]
    [BY pattern_refKey]
        [ALPHA] [ASC|DESC] # unsafe resolve str2float
[
GET pattern1
GET pattern2
...
]


sort blogs:id LIMIT 4 5
    STORE res_list
    BY "blog:*:info->view.count"
        [ALPHA] DESC
GET #
GET "blog:*:info->title"
GET "blog:*:info->publish.time"
GET "blog:*:info->view.count"
