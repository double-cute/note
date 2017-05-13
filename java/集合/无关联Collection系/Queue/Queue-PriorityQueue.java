/* Queue */
boolean add(E e); boolean offer(E e);
E remove(); E poll();
E element(); E peek();

/* PriorityQueue */
PriorityQueue([int initialCapacity][,][Comparator<? supr E> cmp]);  // def 11
PriorityQueue(Collection<? extends E>|SortedSet<? extends E>|PriorityQueue<? extends E> c);
