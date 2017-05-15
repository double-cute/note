// 1. cons: immu
static <K, V> Map<K, V> emptyMap();
static <K, V> SortedMap<K, V> emptySortedsMap();
static <K, V> Map<K, V> singletonMap();

// 2. tools
int size();
boolean isEmpty();
boolean equals(Object o);
int hashCode(); // sum: hash(entry) = hash(key)^hash(value)

/* 3. modify */

// 1. add

/* force */
V put(K k, V v);
void putAll(Map<? extends K, ? extends V> m);
/* no asso ins, r ov */
default V putIfAbsent(K k, V v);
/* no asso ins r nv, else r ov */
default V computeIfAbsent(K k, Function<? super K, ? extends V> k2v);

// 2. remove: has key
V remove(Object k);
default boolean remove(Object k, Object ov);
void clear();

// 3. replace
/* has key force */
default V replace(K k, V nv);
default boolean replace(K k, V ov, V nv);
/* value not null */
default void replaceAll(BiFunction<? super K, ? super V, ? extends V> kv2v);
/* force: kv2v -> null-delete else ins; r kv2v */
default V compute(K k, BiFunction<? super K, ? super V, ? extends V> kv2v);
/* force: vv2v = !ov?defv:f(ov,defv); vv2v -> null-delete else ins; r vv2v */
default V merge(K k, V defv, BiFunction<? super V, ? super V, ? super V> vv2v);
/* asso: kv2v -> null-delete else ins, r kv2v; no-asso r null */
default V computeIfPresent(K k, BiFunction<? super K, ? super V, ? extends V> kv2v);


/* 4. find */

// 1. contains: has
boolean containsKey(Object k);
boolean containsValue(Object v);

// 2. k2v
V get(Object k);
default getOrDefault(Object k, V defVal); // no null

// 3. set
Set<K> keySet();
Collection<V> values();
Set<Map.Entry<K, V>> entrySet();

// 4. entry set
K getKey();
V getValue();
V setValue(V v);
static <K, V> Comparator<Map.Entry<K, V> comparingByKey|Value([Comparator<? super K|V> cmp]);

// 5. iter
default void forEach(BiConsumer<? super K, ? super V> action);
