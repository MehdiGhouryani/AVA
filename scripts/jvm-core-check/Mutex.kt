package kotlinx.coroutines.sync
class Mutex { val lock = java.util.concurrent.locks.ReentrantLock() }
suspend inline fun <T> Mutex.withLock(owner: Any? = null, action: () -> T): T { lock.lock(); try { return action() } finally { lock.unlock() } }
