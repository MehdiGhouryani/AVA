package org.junit.Assert
fun fail(m: String?): Nothing = throw AssertionError(m)
fun assertEquals(a: Any?, b: Any?) { if (a != b) fail("expected:<$a> but was:<$b>") }
fun assertEquals(m: String?, a: Any?, b: Any?) { if (a != b) fail("$m expected:<$a> but was:<$b>") }
fun assertEquals(a: Double, b: Double, d: Double) { if (Math.abs(a - b) > d) fail("expected:<$a> but was:<$b>") }
fun assertNotEquals(a: Any?, b: Any?) { if (a == b) fail("values are equal: $a") }
fun assertTrue(c: Boolean) { if (!c) fail("expected true") }
fun assertTrue(m: String?, c: Boolean) { if (!c) fail(m) }
fun assertFalse(c: Boolean) { if (c) fail("expected false") }
fun assertFalse(m: String?, c: Boolean) { if (c) fail(m) }
fun assertNull(o: Any?) { if (o != null) fail("expected null but was:<$o>") }
fun assertNotNull(o: Any?) { if (o == null) fail("expected not null") }
