package com.github.jyc228.keth.solidity

import java.io.InputStream
import java.math.BigInteger
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.bouncycastle.jcajce.provider.digest.Keccak
import org.bouncycastle.util.encoders.Hex
import org.intellij.lang.annotations.Language

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
data class IndexableAbiComponent(
    override val name: String,
    override val type: String,
    override val components: List<SimpleAbiComponent> = emptyList(),
    override val internalType: String? = null,
    val indexed: Boolean? = null,
) : AbiComponent

@Serializable
data class SimpleAbiComponent(
    override val name: String,
    override val type: String,
    override val components: List<SimpleAbiComponent> = emptyList(),
    override val internalType: String? = null,
) : AbiComponent

@Serializable
data class AbiItem(
    val anonymous: Boolean? = null,
    val constant: Boolean? = null,
    val inputs: List<IndexableAbiComponent> = emptyList(),
    val name: String? = null,
    val outputs: List<SimpleAbiComponent> = emptyList(),
    val payable: Boolean? = null,
    val stateMutability: StateMutabilityType? = null,
    val type: AbiType? = null,
    @Contextual
    val gas: BigInteger? = null,
) {
    fun ioAsSequence(): Sequence<AbiComponent> = inputs.asSequence() + outputs.asSequence()
    fun computeSig(): String = "${name}(${inputs.joinToString(",") { it.encodeType() }})".keccak256Hash()

    companion object {
        fun fromJson(@Language("json") json: String): AbiItem = Json.decodeFromString(json)
        fun listFromJson(@Language("json") json: String): List<AbiItem> = Json.decodeFromString(json)

        @OptIn(ExperimentalSerializationApi::class)
        fun listFromJsonStream(stream: InputStream): List<AbiItem> = Json.decodeFromStream(stream)
    }
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