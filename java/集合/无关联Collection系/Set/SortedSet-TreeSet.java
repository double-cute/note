/* SortedSet */

// 1. get cmp
Comparator<? super E> comparator();

// 2. peek: excp
E first();
E last();

// 3. range:
SortedSet<E> subSet(E low, E high);
SortedSet<E> headSet(E to);
SortedSet<E> tailSet(E from);

/* TreeSet */

// 1. cons
TreeSet([Comparator<? super E> cmp]);
TreeSet(Collection<? extends E> c|SortedSet<E> s);

// 2. poll: safe
E pollFirst();
E pollLast();

// 3. find closest th
E lower|floor(E e);
E higher|ceiling(E e);
