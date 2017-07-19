
```

## 1. 注释：
行注释：## 这是一个行注释
块注释：
#**
    这是一个块注释
*#

## 2. 访问变量

##普通变量
$foo
## array/list
$foo[3]、$foo[$i]
## map
$foo["bar"]
## 访问形式
$foo ## 简约
${foo}  ## 正规，可以应付${foo}bar，没有歧义
$!foo、$!{foo}  ## 空值时为空串

## 3. 变量赋值

#set($foo = "hello!")
#set($foo.age = $bar + 1*3/2)  ## 调用setter设置
#set($foo["Peter"][3] = $bar + 1)
#set($list = [1, 2, 3])
#set($map = {1:1, "2":"2"})

## 4. 变量取值和调用方法

## getter解析
$obj.name  ## 调用的是obj.getName/obj.get("name")等
## 显式调用Java方法
$obj.getName()
$obj.setName("Peter")
$obj.valueOf(15)
$obj.getList()
$str.format("%d%d!", 15, 20)

## 5. 转义
#[[
    里面的内容全部视为纯字符串文本
]]

## 6. 控制流：

## condition:
> < == !=    && || !

## if-else

#if(cond)
    ...
#elseif(cond)
    ...
#elseif(cond)
    ...
#else
    ...
#end

## foreach

#foreach($item in $arrayOrList)
    $foreach.index
    $foreach.count

    #break
    #continue
    ...
#end

#foreach($key in $map.keySet())
    $map.get($key)
    ...
#end

## 7. 包含

#include("a.txt")  ## 包含纯文本（里面的HTML标签都转译）
#parse("a.html") ## 包含并解析

## 8. 函数

#macro(funcName, $arg1, $arg2)
    ...
#end

#funcName(1, 2)  ## 调用
```
