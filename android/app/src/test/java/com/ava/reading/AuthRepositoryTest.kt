package com.ava.reading

import com.ava.reading.data.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class AuthRepositoryTest {
    @Test fun successRequiresRequest()=runTest {val a=MockAuthRepository(latency=0);assertEquals(AuthResult.Expired,a.verify("09123456789","123456"));assertEquals(AuthResult.Success,a.request("09123456789"));assertEquals(AuthResult.Success,a.verify("09123456789","123456"))}
    @Test fun codeBelongsToRequestedNumber()=runTest {val a=MockAuthRepository(latency=0);a.request("09123456789");assertEquals(AuthResult.Expired,a.verify("09999999999","123456"))}
    @Test fun expiryWorksAtBoundary()=runTest {var now=0L;val a=MockAuthRepository({now},0);a.request("09123456789");now=300_000;assertEquals(AuthResult.Expired,a.verify("09123456789","123456"))}
    @Test fun lockCannotBeBypassedByResendAndEventuallyRecovers()=runTest {
        var now=0L;val a=MockAuthRepository({now},0);a.request("09123456789")
        repeat(4) {assertEquals(AuthResult.Wrong,a.verify("09123456789","111111"))}
        assertEquals(AuthResult.Locked,a.verify("09123456789","111111"));assertEquals(AuthResult.Locked,a.request("09123456789"))
        now=120_001;assertEquals(AuthResult.Success,a.request("09123456789"));assertEquals(AuthResult.Success,a.verify("09123456789","123456"))
    }
    @Test fun resetClearsSessionAndNetworkIsInjectable()=runTest {val a=MockAuthRepository(latency=0);a.request("09123456789");a.reset();assertEquals(AuthResult.Expired,a.verify("09123456789","123456"));a.force=AuthResult.Network;assertEquals(AuthResult.Network,a.request("09123456789"))}
}
