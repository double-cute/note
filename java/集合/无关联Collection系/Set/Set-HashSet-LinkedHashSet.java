/* Set */

// 1. cons: immu
static <T> Set<T> emptySet();
static <T> Set<T> singletonSet();

// 2. equals & hashCode
hashCode: sum e.hashCode


/* HashSet & LinkedHashSet */

[Linked]HashSet([int intialCapacity]); // def 16
[Linked]HashSet(Collection<? extends E> c);

boolean -> 1/0
byte/short/char/int -> val
long -> val ^ (val>>>32)
float -> Float.floatToIntBits(val)
double -> val=Double.doubleToLongBits(val); val ^ (val>>>32)
ref -> ref.hashCode()
obj -> sum: obj.field.hashCode() * prime
