// 1. tools
int size();
boolean isEmpty();

// 2. add
boolean add(E e);
boolean addAll(Collection<? extends E> c);
static <T> boolean addAll(Collection<? super T> c, T... eles);

// 3. remove
boolean remove(Object o);
default boolean removeIf(Predicate<? super E> filter);
void clear();

// 4. op
boolean retainAll(Collection<?> c);
	static boolean disjoint(Collection<?> c1, Collection<?> c2);
boolean addAll(Collection<? extends E> c);
boolean removeAll(Collection<?> c);

// 5. contain
boolean contains(Object o);
boolean containsAll(Collection<?> c);

// 6. 2arr
Object[] toArray();
<T> T[] toArray(T[] a);

// 7. stat
static int frequency(Collection<?> c, Object o);
static <T> T max|min(Collection<T> c[, Comparator<T> cmp]);

// 8. iter
Iterator<E> iterator();
boolean hasNext(); E next(); default void forEachRemaining(Consumer<? super E> action);
void remove();
