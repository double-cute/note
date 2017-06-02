
select、insert、update、delete公用的属性

| 共有属性 | 说明 |
| --- | --- |
| id | SQL语句的ID，同时也是mapper方法名 |
| parameterType | SQL语句的参数（同时也是方法参数）|
| flushCache | 默认为false，如果为true则SQL语句被调用就会清空本地缓存和二级缓存 |
| statementType | STATEMENT、PREPARED、CALLABLE，分别对应Statement、PreparedStatement、CallableStatement，默认为PREPARED |
| timeout | 单位是秒，表示执行多久还没就过就抛出异常，默认为依赖驱动 |


- select

| useCache | select中默认为true，其余默认为false，true表示该SQL语句的结果将会被二级缓存 |
