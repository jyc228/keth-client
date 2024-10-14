package com.github.jyc228.keth.type

import com.github.jyc228.keth.client.eth.RpcTransaction
import com.github.jyc228.keth.client.eth.Transaction
import com.github.jyc228.keth.client.eth.TransactionHashes
import com.github.jyc228.keth.client.eth.TransactionObjects
import com.github.jyc228.keth.client.eth.TransactionStatus
import com.github.jyc228.keth.client.eth.TransactionType
import kotlinx.datetime.Instant
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
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
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer

internal abstract class HexStringSerializer<T : HexString>(val toObject: (String) -> T) : KSerializer<T> {
    override val descriptor = PrimitiveSerialDescriptor(this::class.qualifiedName ?: error(""), PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(value.with0x)
    override fun deserialize(decoder: Decoder) = toObject(decoder.decodeString().lowercase())
}

internal object HashSerializer : HexStringSerializer<Hash>(Hash::fromHexString)
internal object AddressSerializer : HexStringSerializer<Address>(Address::fromHexString)
internal object HexIntSerializer : HexStringSerializer<HexInt>(::HexInt)
internal object HexULongSerializer : HexStringSerializer<HexULong>(::HexULong)
internal object HexBigIntSerializer : HexStringSerializer<HexBigInt>(::HexBigInt)
internal object HexDataSerializer : HexStringSerializer<HexData>(HexData::fromHexString)

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

internal object TransactionHashesSerializer : KSerializer<TransactionHashes> {
    private val serializer = ListSerializer(HashSerializer)
    override val descriptor: SerialDescriptor get() = serializer.descriptor

    override fun deserialize(decoder: Decoder): TransactionHashes {
        return TransactionHashes(serializer.deserialize(decoder))
    }

    override fun serialize(encoder: Encoder, value: TransactionHashes) {
        serializer.serialize(encoder, value)
    }
}

internal object TransactionObjectsSerializer : KSerializer<TransactionObjects> {
    private val serializer = ListSerializer(serializer<Transaction>())
    override val descriptor: SerialDescriptor get() = serializer.descriptor

    override fun deserialize(decoder: Decoder): TransactionObjects {
        return TransactionObjects(serializer.deserialize(decoder))
    }

    override fun serialize(encoder: Encoder, value: TransactionObjects) {
        serializer.serialize(encoder, value)
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

fun createEthSerializersModule(serializer: KSerializer<Transaction>) = SerializersModule {
    polymorphic(Transaction::class) { defaultDeserializer { serializer } }
}