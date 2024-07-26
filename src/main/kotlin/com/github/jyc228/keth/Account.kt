package com.github.jyc228.keth

import com.github.jyc228.keth.type.Address
import kotlin.random.Random
import org.bouncycastle.asn1.sec.SECNamedCurves
import org.bouncycastle.jcajce.provider.digest.Keccak

interface Account {
    val address: Address

    companion object {
        fun generate(entropy: ByteArray?) {
            val privateKey = keccak256(
                randomBytes32(),
                keccak256(
                    randomBytes32(),
                    entropy ?: randomBytes32()
                ),
                randomBytes32()
            )
        }

        fun fromPrivateKey(privateKey: ByteArray) {
            SECNamedCurves.getByName("secp256k1")
        }

        private fun randomBytes32() = Random.nextBytes(32)

        private fun keccak256(vararg bytes: ByteArray): ByteArray {
            return with(Keccak.Digest256()) {
                bytes.forEach { it.forEach(::update) }
                digest()
            }
        }
    }
}

fun AccountWithPrivateKey(address: Address, privateKey: String): AccountWithPrivateKey {
    return SimpleAccountWithPrivateKey(address, privateKey)
}

interface AccountWithPrivateKey : Account {
    val privateKey: String
}

private data class SimpleAccountWithPrivateKey(
    override val address: Address,
    override val privateKey: String
) : AccountWithPrivateKey
