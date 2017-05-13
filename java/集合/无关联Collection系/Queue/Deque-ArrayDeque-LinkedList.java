/* Deque */

void addFirst(E e); boolean offerFirst(E e);
E removeFirst(); E pollFirst();
E getFirst(); E peekFirst();

void addLast(E e); boolean offerLast(E e);
E removeLast(); E pollLast();
E getLast(); E peekLast();

void push(E e);
E pop();
E peek();

// others
boolean removeFirst|LastOccurrence(E e);
Iterator<E> descendingIterator();


/* ArrayDeque & LinkedList */

ArrayDeque([int initialCapacity]); // def 16
ArrayDeque(Collection<? extends E> c);
LinkedList([Collection<? extends E> c);
