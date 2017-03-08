！！从上面可以推断出Queue鉴别元素相同的依据和List一样也是equals方法！因为LinkedList属于Queue，而LinkedList也属于List，而List鉴别元素相同的依据是equals，因此Queue鉴别元素相同的依据肯定也是equals！不信可以试一下哦！！！
方法：
     i. 类别A（包装了一个int a）的equals返回true；
     ii. 连续插入1、2、3、4；
     iii. 然后删除一个7，结果发现序列变成了2、3、4；
     iv. 因为它的依据是equals方法，删除7的是否发现第一个1和7equals返回true，因此删除了第一个1！
！！所以equals一定要实现地合理！
