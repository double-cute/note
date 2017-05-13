// 1. cons: immu
static <T> List<T> emptyList();
static <T> List<T> singletonList();
static <T> List<T> nCopies(int n, T o);

// 2. equals & hashCode
hashCode: hash=1; for e in list: hash += 31*hash + e.hash;

/* 3. modify */

// 1. add
void add(int index, E e);
boolean addAll(int index, Collection<? extends E> c);

// 2. remove
E remove(int index);

// 3. replace
static <T> boolean replaceAll(List<T> l, T o, T n);
default void replaceAll(UnaryOperator<E> op);

// 4. fill
static <T> void fill(List<? super T> l, T o);
static <T> void copy(List<? super T> dst, List<? extends T> src);

/* other */

// 1. i2th
E get(int index); E set(int index, E e);
List<E> subList(int beg, int end);

// 2. th2i
int indexOf|lastIndexOf(Object o);
static int indexOfSubList|lastIndexOfSubList(List<?> src, List<?> tar);
static <T> int binarySearch(List<T> l, T key[, Comparator<T> cmp]);

// 3. order
static void swap(List<?> l, int i, int j);
static void reverse(List<?> l);
static void shuffle(List<?> l[, Random rnd]);
static void rotate(List<?> l, int distance);
static <T> void sort(List<T> l[, Comparator<T> cmp]);
default void sort(Comparator<? super E> c);

// 4. iter
ListIterator<E> listIterator([int index]);
boolean hasPrevious(); E previous(); int previousIndex(); // also next
void add(E e); void remove(); void set(E e);
