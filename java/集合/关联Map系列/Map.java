// 1. cons: immu
static <K, V> Map<K, V> emptyMap();
static <K, V> SortedMap<K, V> emptySortedMap();
static <K, V> Map<K, V> singletonMap(K k, V v);

// 2. tools
int size();
boolean isEmpty();
boolean equals(Object o);
int hashCode(); // sum: hash(k) ^ hash(v)


// 3. add & modify

/* force add */
	// i. ov put(k, v): force
V put(K k, V v);
	// ii. kv2v put(k, kv2v): force, null > del
default V compute(K k, BiFunction<? super K, ? super V, ? extends V> kv2v);
	// iii. vv2v put(k, vv2v): force, null > del, vv2v = !ov?defv:vv2v
default V merge(K k, V defv, BiFunction<? super V, ? super V, ? extends V> vv2v);

/* no asso then add */
	// i. ov put(k, v)
default V putIfAbsent(K k, V v);
	// ii. k2v put(k, k2v) else k2v = ov
default V computeIfAbsent(K k, Function<? super K, ? extends V> k2v);

/* asso then replace */
	// i. ov replace(k, v)
default V replace(K k, V v);
	// ii. boolean replace(k, ov, nv);
default boolean replace(K k, V ov, V nv); // has key then do
	// iii. kv2v replace(k, kv2v): null > del, no-asso r null
default V computeIfPresent(K k, BiFunction<? super K, ? super V, ? extends V> kv2v);
	// iv. force: change the world
default void replaceAll(BiFunction<? super K, ? super V, ? extends V> kv2v);


// 4. remove: force
V remove(Object k);
default boolean remove(Object k, Object ov, Object nv);
void clear();

// 5. contains: force
boolean containsKey(Object k);
boolean containsValue(Object v);

// 6. k2v
V get(Object k);
default getOrDefault(Object k, V defVal); // exclude:k-null

//7. set
Set<K> keySet();
Collection<V> values();
Set<Map.Entry<K, V>> entrySet();

// 8. entry
K getKey();
V getValue();
V setValue(V v);
static <K, V> Comparator<Map.Entry<K, V>>
	comparingByKey|Value([Comparator<? super K|V> cmp]);

// 9. iter
default void forEach(BiConsumer<? super K, ? super V> action);
