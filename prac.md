```Java
// 1. cons
String();
String(String|StringBuilder|StringBuffer s);
String(byte[] b[, int offset, int len][, Charset charset | String charsetName]);

// 2. tools
int length();
boolean isEmpty();
String toUpperCase([Locale l]);
String toLowerCase([Locale l]);

// 3. comp
boolean equals(Object o);
boolean contentEquals(CharSequence cs | StringBuffer sb);
boolean equalsIngoreCaes(String s);
boolean compareTo[IngnoreCase](String s);

// 4. i2thing
char charAt(int index);
String substring(int beg[, int end]);
String subSequence(int beg, int end);

// 5. th2i
int indexOf|lastIndexOf(int ch | String s[, int beg]);

// 6. match
boolean contains(CharSequence cs);
boolean startsWith(String prefix[, int from]);
boolean endsWith(String suffix);
boolean matches(String regx);
boolean regionMatches(
  [boolean ignoreCase,]
  int mfrom,
  String oth, int from, int len
);

// 7. proc
String trim();
String[] split(String deliRegx[, int limit]);
static String join(CharSequence deli, CharSequence... | Iterable<? extends CharSequence> args);
String replace(char oldc, char newc);
String replace(CharSequence old, CharSequence news);
String replaceAll|replaceFirst(String regx, String news);

// 8. +
+=
String concat(String s);
StringBuilder StringBuilder.append(String|StringBuilder|StringBuffer s);

// 9. 2bytes&chars
byte[] getBytes([Charset charset | String charsetName]);
char[] toCharArray();
void getBytes(int beg, int end, byte[] dst, int dstBeg);
void getChars(int beg, int end, char[] dst, int dstBeg);

// 10. format
static String format([Locale l, ]String format, Object... args);

// 11. oth2str
static String valueOf(type t);
static String valueOf(Object o);
static String valueOf(char[] s[, int offset, int len]);

// 12.
String intern();
int hashCode();
```


```Java
// 1. cons
String();
String(byte[] b[, int offset, int len][, Charset cs | String csName]);
String(char[] cs[, int offset, int len]);
static String valueOf(char[] cs[, int offset, int len]);

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
String subSequence(int beg, int end);

// 5. th2i
int indexOf|lastIndexOf(int ch | String s[, int beg]);

// 6. match
boolean contains(CharSequence cs);
boolean startsWith(String prefix[, int beg]);
boolean endsWith(String suffix);
boolean matches(String regx);
boolean regionMatches(
  [boolean ignoreCase,]
  int mbeg,
  String oth, int offset, int len
);

// 7. proc
String trim();
String[] split(String dliRegx[, int limit]);
static String join(String dili, CharSequence... | Iterable<? extends CharSequence> args);
String replace(char oc, char nc);
String replace(CharSequence os, CharSequence ns);
String replaceAll|replaceFirst(String regx, String ns);

// 8. +
+=
String concat(String s);
StringBuilder StringBuilder.append(String|StringBuilder|StringBuffer s);
// StringBuffer

// 9. fromat
static String format([Locale l, ]String format, Object... args);

// 10. 2bytes&chars
byte[] getBytes([Charset cs | String csName]);
char[] toCharArray();
void getBytes(int beg, int end, byte[] dst, int dstBeg);
void getChars(int beg, int end, char[] dst, int dstBeg);

// 11. 2oth
static String valueOf(type t);
static String valueOf(Object o);
static String valueOf(char[] cs[, int offset, int len]);

// 12.
String intern();
int hashCode();
```


```Java
// 1. cons
Scanner(InputStream is | File f[, String csName]);
Scanner(String source);
// 2. close
void close();

// 3. attr
Pattern delimiter(); Scanner useDelimiter(Pattern|String pattern);
int radix(); Scanner useRadix(int radix);
Scanner reset(); // OS Locale、radix10、deli-space

// 4. skip a pattern
Scanner skip(Pattern|String pattern);

// 5. match
//word
boolean hasNext(); String next();
//pattern
boolean hasNext(Pattern|String pattern); String next(Pattern|String regx);
//line:
boolean hasNextLine(); String nextLine();
//int：byte、short、int、long
boolean hasNextType([int radix]); type nextType([int radix]);
int radix(); Scanner useRadix(int radix); // 2-16
//float:float、double、BigDecimal
boolean hasNextType(); type nextType();
//find pattern:
String findWithinHorizon(Pattern|String pattern, int horizon);
String findInLine(Pattern|String pattern);
```


```Java
// 1. cons
Scanner(InputStream is | File f[, String csName]);
Scanner(String s);

// 2. close
void close()；

// 3. attr
Pattern delimiter(); int radix();
Scanner useDelimiter(Pattern|String dli);
Scanner useRadix(int radix);
Scanner reset(); // OSLOCAL RADIX10 DELI-SPACE

// 4. skip
Scanner skip(Pattern|String pattern);

// 5. match
//word
boolean hasNext(); String next();
//pattern
boolean hasNext(Pattern|String pattern); String next(Pattern|String pattern);
//line
boolean hasNextLine(); String nextLine();
//int:byte short int long BigInteger
boolean hasNextType([int radix]); type nextType([int radix]);
//float:float double BigDecimal
boolean hasNextType(); type nextType();
//find
String findWithinHorizon(Pattern|String pattern, int horizon);
String findInLine(Pattern|String pattern);
```


```Java
// 1. cons
PrintStream|PrintWriter(File|String f[, String csName]);
PrintStream(OutputStream os[, boolean autoFlush[, String encoding]]);
PrintWriter(OutputStream|OutputWriter out[, boolean autoFlush]);

// 2. print[ln]
void println(type t); // boolean char int long float double
void println(String|char[] s);
void println(Object o);
void println();

// 3. append
PrintStream append(char c);
PrintStream append(CharSequence cs[, int beg, int end]);

// 4. format
PrintStream format([Locale l, ]String format, Object... args);
%b %c %s %d
%o %#o %x %#x
%f %e %g
%n \t %% %tc

.5
10d
-10d
+23d
05d
,+5d
```


```Java
// 1. cons
PrintStream|PrintWriter(File|String f[, String csName]);
PrintStream(OutputStream os[, boolean autoFlush[, String encoding]]);
PrintWriter(OutputStream|Writer out[, autoFlush]);

// 2. println
void println(type t); // boolean char int long float double
void println(String|char[] s);
void println(Object o);
void println();

// 3. append
PrintStream|PrintWriter append(char c);
PrintStream|PrintWriter append(CharSequence cs[, int start, int end]);

// 4. flush
void flush(); // \n byte[] println

// 5. format
PrintStream|PrintWriter printf|format([Locale l, ]String format, Object... args);

%b %c %s %d
%o %#o %x %#x
%f %e %g
%n \t %% %tc

+0d
.5f
-,020d
```



```Java
/* read */
//InputStream
int read([byte[] b[, int offset, int len]]);
//Reader
int read([char[] s[, int offset, int len]]);

/* write */
//OutputStream
void write(int b);
void write(byte[] b[, int offset, int len]);
//Writer
void write(int c);
void write(String|char[] s[, int offset, int len]);

//
long skip(long n); // I&R
int available(); // I

// O&W
void flush();

//Writer
Writer append(char c);
Writer append(CharSequence cs[, int start, int end]);
```

```Java
FileInputStream|FileReader(File|String f);
FileOutputStream|FileWriter(File|String f[, boolean append]);

StringReader(String node);
StringWriter([int iniSize]); // StringBuffer
StringBuffer getBuffer();
```






```Java
// 1. cons
String();
String(String|StringBuidler|StringBuffer s);
String(byte[] b[, int offset, int len][, Charset|String cs]);
String(char[] c[, int offset, int len]);

// 2. tools
int length();
boolean isEmpty();
String toUpperCase([Locale l]);
String toLowerCase([Locale l]);

// 3. cmp
boolean equals(Object o);
boolean contentEquals(CharSequence|StringBuffer s);  // thread safe
boolean equalsIgnoreCase(String s);
int compareTo[IgnoreCase](String s);

// 4. i2th
char charAt(int index);
String substring(int beg[, int end]);
CharSequence subSequence(int beg, int end);

// 5. th2i
int indexOf|lastIndexOf(int|String th[, int beg]);

// 6. match
boolean contains(CharSequence s);
boolean startsWith(String prefix[, int beg]);
boolean endsWith(String suffix);
boolean matches(String regx);
boolean regionMatches(
  [boolean IgnoreCase, ]
  int beg,
  String other, int offset, int len
);

// 7. proc
String trim();
String split(String regDeli[, int limit]);
static String join(CharSequence deli, CharSequence...|Iterable<? extends CharSequence> eles);
String replace(char|CharSequence: oldd, neww);
String replaceAll|replaceFirst(String regx, String replacement);

// 8. concat
+=
String concat(String s);
StringBuilder|StringBuffer append(String|StringBuilder|StringBuffer s);

// 9. 2bs/cs
byte[] getBytes([Charset|String cs]);
char[] toCharArray();

void getBytes(int beg, int end, byte[] dst, int dstBeg); // C style without encoder
void getChars(int beg, int end, char[] dst, int dstBeg);

// 10. 2str
// type: boolean char int long float double
static String valueOf(type t|Object o);
static String valueOf(char[] c[, int offset, int len]);

// 11. other
String intern();
int hashCode(); a[0] * 31^(n-1) ... a[n-1] * 1

// 12. format
static String format([Locale l, ]String format, Object... args);
%b %c %s %d
%o %#o %x %#x
%f %e %g
%n \t %% %tc

.5
10d
-10d
+23d
05d
,+5d
```
