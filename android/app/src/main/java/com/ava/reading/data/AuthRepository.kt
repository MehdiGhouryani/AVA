package com.ava.reading.data

import com.ava.reading.content.validPhone
import kotlinx.coroutines.delay

enum class AuthResult { Success, Wrong, Expired, Locked, Network }
interface AuthRepository {
    suspend fun request(phone: String): AuthResult
    suspend fun verify(phone: String, code: String): AuthResult
    fun reset()
}
/** Development only. The number and code never leave memory or this device. */
class MockAuthRepository(private val clock: () -> Long = { System.nanoTime()/1_000_000 }, private val latency: Long = 500) : AuthRepository {
    private var sentAt: Long? = null
    private var phone = ""
    private var wrong = 0
    private var lockUntil = 0L
    var force: AuthResult? = null
    override suspend fun request(phone: String): AuthResult {
        delay(latency)
        force?.let { return it }
        if(clock() < lockUntil) return AuthResult.Locked
        if(!validPhone(phone)) return AuthResult.Wrong
        this.phone = phone; sentAt=clock();wrong=0
        return AuthResult.Success
    }
    override suspend fun verify(phone: String, code: String): AuthResult {
        delay(latency)
        force?.let { return it }
        if(clock() < lockUntil) return AuthResult.Locked
        val sent = sentAt ?: return AuthResult.Expired
        if(this.phone != phone || clock()-sent >= 300_000) return AuthResult.Expired
        if(code=="123456") { wrong=0; return AuthResult.Success }
        wrong++
        if(wrong>=5) { lockUntil=clock()+120_000;wrong=0;return AuthResult.Locked }
        return AuthResult.Wrong
    }
    override fun reset() { sentAt=null;phone="";wrong=0;lockUntil=0 }
}
