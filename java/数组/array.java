int length;
boolean equals(Object o);

/* init */

// C style
int arr[] = new int[] { 1, 3, 3 }

/* static */

// 1. normal
int[] arr = new int[] { 1, 2, 3, 4 };
int[] arr = new int[4] { 1, 2, 3, 4 };  // w
int[] arr = new Object[] { 1, 3.4, "abc" };
// 2. brief
int[] arr = { 1, 2, 3 };
Integer[] iarr; iarr = { 1, 2, 3, 4 };  // w

/* dynamic */
int[] arr = new int[10];
int len = scan; int[] arr = new int[len];

/* mul dimension */
int[][] a = new int[][] { new int[3], new int[] { 1, 2, 3 }, { 3, 4, 5 } };
int[][] b = { new int[3], new int[] { 1, 2, 3 }, { 3, 4, 5 } };
int[][] c = new int[2][];
int[][] d = new int[][3]; // w, high dimension first
int[][] e = new int[2][3];
int[][] f = new int[2][] { new int[3], new int[4] }; // w, mixed
