// 1. cons:
ArrayList([int initialCapacity]);  // def 10
ArrayList(Collection<? extends E> c);
static <T> List<T> Arrays.asList(T... eles);  // immu

// 2. capacity
void ensureCapacity(int minCapacity);
void trimToSize();
