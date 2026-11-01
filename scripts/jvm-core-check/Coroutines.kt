package kotlinx.coroutines
import kotlin.coroutines.*
fun <T> runBlocking(block: suspend () -> T): T {
    var result: Result<T>? = null
    block.startCoroutine(Continuation(EmptyCoroutineContext) { result = it })
    return result!!.getOrThrow()
}
suspend fun delay(ms: Long) { Thread.sleep(ms) }
