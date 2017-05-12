// 1. cons
String([String|StringBuilder|StringBuffer s]);
String(byte[] b[, int offset, int len][, Charset|String cs]);
String(char[] c[, int offset, int len]);
String(int[] codePoints[, int offset, int len]);

// 2. tools
int length();
boolean isEmpty();
String toUpperCase([Locale l]);
String toLowerCase([Locale l]);

// 3. cmp
boolean equals(Object o);
boolean contentEquals(CharSequence|StringBuffer s);
boolean equalsIgnoreCase(String s);
boolean compareTo[IgnoreCase](String s);

// 4. i2th
char charAt(int index);
String substring(int beg[, int end]);
CharSequence subSequence(int beg, int end);

// 5. th2i
int indexOf|lastIndexOf(int|String th[, int beg]);

// 6. matches
boolean contains(CharSequence s);
boolean startsWith(String prefix[, int beg]);
boolean endsWith(String suffix);
boolean matches(String regx);
boolean regionMatches(
	[boolean ignoreCase, ]
	int beg,
	String other, int offset, int len
);

// 7. proc
String trim();
String[] split(String regDeli[, int limit]);
static String join(CharSequence deli, CharSequence...|Iterable<? extends CharSequence> eles);
String replace(char|String: oldd, neww);
String replaceAll|replaceFirst(String regx, String replacement);

// 8. concat
+=
String concat(String s);
StringBuilder|StringBuffer append(String|StringBuilder|StringBuffer s);

// 9. 2bs/cs
byte[] getBytes([Charset|String cs]);
char[] toCharArray();
void getBytes(int beg, int end, byte[] dst, int dstBeg);
void getChars(int beg, int end, char[] dst, int dstBeg);

// 10. 2str
static String valueOf(type t|Object o);
static String valueOf(char[] c[, int offset, int len]);

// 11. format
static String format([Locale l, ]String format, Object... args);
%b %c %d %s
%o %#o %x %#x
%f %e %g
%n \t %% %tc

// 12. other
String intern();
int hashCode();  // s[0]*31^(n-1) + ... s[n-1]*1

// 13. code point
String(int[] codePoints, int offset, int len);
int codePointAt|codePointBefore(int index);
int codePointCount(int beg, int end);
int offsetByCodePoints(int index, int codePointOffset);
