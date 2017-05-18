// 1. cons: immu
static <K, V> Map<K, V> emptyMap();
static <K, V> SortedMap<K, V> emptySortedMap();
static <K, V> Map<K, V> singletonMap(K k, V v);

// 2. tools
int size();
boolean isEmpty();
boolean equals(Object o);
int hashCode(); // sum: hash(key) ^ hash(value)


// 3. add & modify

/* force add */
	// i. ov put(k, nv)
	V put(K k, V v);
	// ii. kv2v put(k, kv2v): null>del
	V compute(K k, BiFunction<? super K, ? super V, ? extends V> kv2v);
	// iii. vv2v put(k, vv2v): null>del, vv2v = !ov:dv:f(ov,dv)
	V merge(K k, V dv, BiFunction<? super V, ? super V, ? extends V> vv2v);

/* asso replace */
	// i. ov replace(k, nv), no-asso none r ov(null)
	default V replace(K k, V nv);
	// ii. boolean replace(k, ov, nv)
	default boolean replace(K k, V ov, V nv);
	// iii. kv2v replace(k, kv2v): null>del, no-asso none r ov(null)
	default V putIfPresent(K k, BiFunction<? super K, ? super V, ? extends V> kv2v);
	// iv. force: change the world
	default void replaceAll(BiFunction<? super K, ? super V, ? extends V> kv2v);

/* no-asso replace */
	// i. ov put(k, v), asso none r ov
	default V putIfAbsent(K k, V v);
	// ii. k2v put(k, k2v), asso none r ov
	default V computeIfAbsent(K k, Function<? super K, ? extends V> k2v);


// 4. remove
V remove(Object k);
default boolean remove(Object k, Object ov);
void clear();

// 5. contain
boolean containsKey(Object k);
boolean containsValue(Object v);

// 6. k2v
V get(K k);
V getOrDefault(K k, V dv); // k must not in then r dv

// 7. set
Set<K> keySet();
Collection<V> values();
Set<Map.Entry<K, V>> entrySet();

// 8. entry
K getKey();
V getValue();
V setValue(V v);
static <K, V> Comparator<Map.Entry<K, V>>
	comparingByKey|Value([Comparator<? super K|V>]);

// 9. iter
default void forEach(BiConsumer<? super K, ? super V> action);
