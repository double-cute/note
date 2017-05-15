/* SortedMap */

// 1. comprator
Comparator<? super K> comparator();

// 2. peek: unsafe
K firstKey|lastKey();

// 3. sub
SortedMap<K, V> subMap(K low, K high);
SortedMap<K, V> headMap(K to);
SortedMap<K, V> tailMap(K from);


/* TreeMap */

// 1. cons
TreeMap([Comparator<? super K> cmp]);
TreeMap(Map|SortedMap<? extends K, ? extends V> m);

// 2. poll
Map.Entry<K, V> pollFirstEntry|pollLastEntry();

// 3. find ab
K lowerKey|floorKey(K k);
Map.Entry<K, V> lowerEntry|floorEntry(K k);
K higherKey|ceilingKey(K k);
Map.Entry<K, V> higherEntry|ceilingEntry(K k);
