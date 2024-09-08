package com.github.jyc228.keth.solidity

import java.math.BigInteger
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bouncycastle.jcajce.provider.digest.Keccak
import org.bouncycastle.util.encoders.Hex

interface AbiComponent {
    val name: String
    val type: String
    val components: List<AbiComponent>
    val internalType: String?

    fun resolveStruct(): Struct = requireNotNull(internalType)
        .split(" ")[1]
        .split(".")
        .let {
            if (it.size == 1) Struct("", it[0].removeSuffix("[]"))
            else Struct(it[0], it[1].removeSuffix("[]"))
        }

    fun resolveName() = when (name.all { it == '_' }) {
        true -> "`${name}`"
        false -> name
    }

    fun encodeType(): String {
        if (type == "tuple" && components.isNotEmpty()) {
            return components.joinToString(",", prefix = "(", postfix = ")") { it.encodeType() }
        }
        return type
    }

    data class Struct(val ownerName: String, val name: String)
}

@Serializable
data class AbiInput(
    override val name: String,
    override val type: String,
    override val components: List<AbiInput> = emptyList(),
    override val internalType: String? = null,
    val indexed: Boolean? = null,
) : AbiComponent

@Serializable
data class AbiOutput(
    override val name: String,
    override val type: String,
    override val components: List<AbiOutput> = emptyList(),
    override val internalType: String? = null,
) : AbiComponent

@Serializable
data class AbiItem(
    val anonymous: Boolean? = null,
    val constant: Boolean? = null,
    val inputs: List<AbiInput> = emptyList(),
    val name: String? = null,
    val outputs: List<AbiOutput> = emptyList(),
    val payable: Boolean? = null,
    val stateMutability: StateMutabilityType? = null,
    val type: AbiType? = null,
    @Contextual
    val gas: BigInteger? = null,
) {
    fun ioAsSequence(): Sequence<AbiComponent> = inputs.asSequence() + outputs.asSequence()
    fun computeSig(): String = "${name}(${inputs.joinToString(",") { it.type }})".keccak256Hash()
}

enum class StateMutabilityType {
    pure, view, nonpayable, payable
}

enum class AbiType {
    function, `constructor`, event, fallback, receive, error
}

private fun String.keccak256Hash(): String {
    val bytes = with(Keccak.Digest256()) {
        forEach { update(it.code.toByte()) }
        digest()
    }
    return Hex.encode(bytes).decodeToString()
}