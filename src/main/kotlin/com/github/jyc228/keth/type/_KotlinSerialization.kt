package com.github.jyc228.keth.type

import com.github.jyc228.keth.client.eth.Block
import com.github.jyc228.keth.client.eth.RpcTransaction
import com.github.jyc228.keth.client.eth.Transaction
import com.github.jyc228.keth.client.eth.TransactionStatus
import com.github.jyc228.keth.client.eth.TransactionType
import kotlinx.datetime.Instant
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal abstract class HexStringSerializer<T : HexString>(val toObject: (String) -> T) : KSerializer<T> {
    override val descriptor = PrimitiveSerialDescriptor(this::class.qualifiedName ?: error(""), PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(value.with0x)
    override fun deserialize(decoder: Decoder) = toObject(decoder.decodeString().lowercase())
}

internal object HashSerializer : HexStringSerializer<Hash>(Hash::invoke)
internal object AddressSerializer : HexStringSerializer<Address>(Address::invoke)
internal object HexIntSerializer : HexStringSerializer<HexInt>(::HexInt)
internal object HexULongSerializer : HexStringSerializer<HexULong>(::HexULong)
internal object HexBigIntSerializer : HexStringSerializer<HexBigInt>(::HexBigInt)
internal object HexDataSerializer : HexStringSerializer<HexData>(HexData::invoke)

internal object InstantSerializer : KSerializer<Instant> {
    override val descriptor = PrimitiveSerialDescriptor("com.github.jyc228.keth.Instant", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Instant) =
        encoder.encodeString("0x${value.epochSeconds.toString(16)}")

    override fun deserialize(decoder: Decoder) =
        Instant.fromEpochSeconds(decoder.decodeString().removePrefix("0x").toLong(16))
}

internal abstract class NullSerializer<T>(
    private val serializer: KSerializer<T>,
    private val default: T
) : KSerializer<T> by serializer {
    override fun deserialize(decoder: Decoder): T = decoder.decodeNullableSerializableValue(serializer) ?: default
}

internal object TransactionHashSerializer : KSerializer<Block.TransactionHash> {
    private val serializer = HashSerializer
    override val descriptor: SerialDescriptor get() = serializer.descriptor

    override fun deserialize(decoder: Decoder): Block.TransactionHash {
        return Block.TransactionHash(serializer.deserialize(decoder))
    }

    override fun serialize(encoder: Encoder, value: Block.TransactionHash) {
        serializer.serialize(encoder, value.hash)
    }
}

internal object TransactionObjectSerializer : KSerializer<Block.TransactionObject> {
    private val defaultSerializer = RpcTransaction.serializer()
    override val descriptor: SerialDescriptor get() = defaultSerializer.descriptor

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): Block.TransactionObject {
        val customSerializer = decoder.serializersModule.getPolymorphic(Transaction::class, null)
        return Block.TransactionObject((customSerializer ?: defaultSerializer).deserialize(decoder))
    }

    override fun serialize(encoder: Encoder, value: Block.TransactionObject) {
        throw SerializationException("Block.TransactionObject serialize unsupported")
    }
}

class UnknownTransactionSerializer(
    val unknownTransactionSerializer: (TransactionType) -> KSerializer<out Transaction>
) : JsonContentPolymorphicSerializer<Transaction>(Transaction::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Transaction> {
        val type = element.jsonObject["type"]?.jsonPrimitive?.content ?: error("")
        return when (val t = TransactionType.from(type)) {
            is TransactionType.Unknown -> unknownTransactionSerializer(t)
            else -> RpcTransaction.serializer()
        }
    }
}

internal object TransactionTypeSerializer : HexStringSerializer<TransactionType>(TransactionType.Companion::from)
internal object TransactionStatusSerializer : HexStringSerializer<TransactionStatus>(TransactionStatus.Companion::from)
internal class NullBlockHash : NullSerializer<Hash>(Hash.serializer(), Transaction.pendingBlockHash)
internal class NullBlockNumber : NullSerializer<HexULong>(HexULong.serializer(), Transaction.pendingBlockNumber)
internal class NullTxIndex : NullSerializer<HexInt>(HexInt.serializer(), HexInt(-1))
internal class NullGas : NullSerializer<HexBigInt>(HexBigInt.serializer(), HexBigInt("0"))
internal class NullList<E>(element: KSerializer<E>) : NullSerializer<List<E>>(ListSerializer(element), emptyList())
