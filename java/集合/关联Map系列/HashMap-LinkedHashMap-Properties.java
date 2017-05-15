/* HashMap & LinkedHashMap */
[Linked]HashMap([int initialCapacity[, float loadFactor]]);
[Linked]HashMap(Map<? extends K, ? extends V> m);

/* Properties */

// 1. cons
Properties([Properties p]);

// 2. prop names
Enumeration<?>	propertyNames();
Set<String>	stringPropertyNames();

// 3. get&set
String getProperty(String key[, String defaultValue]);
Object setProperty(String key, String value);

// 4. IO
/* load: merge */
void load(InputStream|Reader in);
void loadFromXML(InputStream in);
/* store: cover */
void store|list(OutputStream|Writer out, String comments);
void storeToXML(OutputStream os, String comment[, String encoding]);
