// type: boolean byte short char int long float double Object

// 1. tools
static String toString(type[] a);
static boolean equals(Object o);

// 2. binary search: no boolean
static int binarySearch(type[] a[, int fromIndex, int toIndex] ,type key);
static <T> int binarySearch(T[] a[, int fromIndex, int toIndex] ,T key);

// 3. sort: no booleans
static void sort|parallelSort(type[] a[, int from, int to]);
static void sort|parallelSort(T[] a[, int from, int to], Comparator<? super T> cmp);
static void parallelSort(T[] a, int from, int to);

// 4. fill:
static void fill(type[] a[, int from, int to], type val);

static void setAll|parallelSetAll(int[] array, IntToTypeFunction generator);
static <T> void setAll|parallelSetAll(T[] array, IntFunction<? extends T> generator);

static void	parallelPrefix(type[] array[, int fromIndex, int toIndex], TypeBinaryOperator op);
static <T> void	parallelPrefix(T[] array[, int fromIndex, int toIndex], BinaryOperator<T> op);

// 5. copy
static type[] copyOf(type[] original, int len);
static T[] copyOf(T[] original, int len);

static type[] copyOf(type[] original, int from, int to);
static T[] copyOf(T[] original, int from, int to);

static native void System.arraycopy(
    Object src, int srcPos,
    Object dest, int destPos,
    int length
);
