import java.lang.reflect.InvocationTargetException
fun main(args: Array<String>) {
    var pass = 0; var fail = 0
    for (cn in args) {
        val c = Class.forName(cn)
        for (m in c.declaredMethods.sortedBy { it.name }) {
            if (m.getAnnotation(org.junit.Test::class.java) == null) continue
            try { m.invoke(c.getDeclaredConstructor().newInstance()); pass++ }
            catch (e: InvocationTargetException) { fail++; println("FAIL $cn.${m.name}: ${e.cause}") }
            catch (e: Throwable) { fail++; println("ERROR $cn.${m.name}: $e") }
        }
    }
    println("RESULT pass=$pass fail=$fail")
    System.exit(if (fail == 0) 0 else 1)
}
