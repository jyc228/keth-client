package com.github.jyc228.keth.client.contract

import com.github.jyc228.keth.solidity.AbiItem
import com.github.jyc228.keth.type.HexData
import com.github.jyc228.keth.type.HexString
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction10
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3
import kotlin.reflect.KFunction4
import kotlin.reflect.KFunction5
import kotlin.reflect.KFunction6
import kotlin.reflect.KFunction7
import kotlin.reflect.KFunction8
import kotlin.reflect.KFunction9
import kotlin.reflect.KType

abstract class AbstractContractFunction<R>(
    private val returnType: KType,
    private val sig: String,
    jsonAbi: () -> String,
) {
    val abi by lazy(LazyThreadSafetyMode.NONE) { AbiItem.fromJson(jsonAbi()) }

    protected fun encodeFunctionCall(vararg parameters: Any?): String {
        if (parameters.isEmpty()) return sig
        return "${sig.take(10)}${abiCodec.encode(abi.inputs, parameters.map { (it as? HexString)?.hex ?: it })}"
    }

    @Suppress("UNCHECKED_CAST")
    fun decodeResult(result: HexData?): R {
        if (result == null) return null as R
        val result = abiCodec.decode(abi.outputs, result.hex.removePrefix("0x"))
        return result.getValue(abi.outputs[0].name) as R
    }

    @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
    protected inline fun <E> Any.c(): E = this as E
}

class ContractFunctionP0<R>(
    private val kFunction: KFunction1<*, ContractFunctionRequest<R>>,
    sig: String,
    jsonAbi: () -> String,
) : AbstractContractFunction<R>(kFunction.returnType, sig, jsonAbi) {
    fun encodeFunctionCall() = super.encodeFunctionCall()
}

class ContractFunctionP1<P1, R>(
    private val kFunction: KFunction2<*, P1, ContractFunctionRequest<R>>,
    sig: String,
    jsonAbi: () -> String,
) : AbstractContractFunction<R>(kFunction.returnType, sig, jsonAbi) {
    fun encodeFunctionCall(p1: P1) = super.encodeFunctionCall(p1)

    fun <R> decodeFunctionCall(input: String, callParameter: (P1) -> R): R {
        val p = abiCodec.decodeFunctionCall(abi.inputs, kFunction.parameters, input)
        return callParameter(p[0].c())
    }
}

class ContractFunctionP2<P1, P2, R>(
    private val kFunction: KFunction3<*, P1, P2, ContractFunctionRequest<R>>,
    sig: String,
    jsonAbi: () -> String,
) : AbstractContractFunction<R>(kFunction.returnType, sig, jsonAbi) {
    fun encodeFunctionCall(p1: P1, p2: P2) = super.encodeFunctionCall(p1, p2)

    fun <R> decodeFunctionCall(input: String, callParameter: (P1, P2) -> R): R {
        val p = abiCodec.decodeFunctionCall(abi.inputs, kFunction.parameters, input)
        return callParameter(p[0].c(), p[1].c())
    }
}

class ContractFunctionP3<P1, P2, P3, R>(
    private val kFunction: KFunction4<*, P1, P2, P3, ContractFunctionRequest<R>>,
    sig: String,
    jsonAbi: () -> String,
) : AbstractContractFunction<R>(kFunction.returnType, sig, jsonAbi) {
    fun encodeFunctionCall(p1: P1, p2: P2, p3: P3) = super.encodeFunctionCall(p1, p2, p3)

    fun <R> decodeFunctionCall(input: String, callParameter: (P1, P2, P3) -> R): R {
        val p = abiCodec.decodeFunctionCall(abi.inputs, kFunction.parameters, input)
        return callParameter(p[0].c(), p[1].c(), p[2].c())
    }
}

class ContractFunctionP4<P1, P2, P3, P4, R>(
    private val kFunction: KFunction5<*, P1, P2, P3, P4, ContractFunctionRequest<R>>,
    sig: String,
    jsonAbi: () -> String,
) : AbstractContractFunction<R>(kFunction.returnType, sig, jsonAbi) {
    fun encodeFunctionCall(p1: P1, p2: P2, p3: P3, p4: P4) = super.encodeFunctionCall(p1, p2, p3, p4)

    fun <R> decodeFunctionCall(input: String, callParameter: (P1, P2, P3, P4) -> R): R {
        val p = abiCodec.decodeFunctionCall(abi.inputs, kFunction.parameters, input)
        return callParameter(p[0].c(), p[1].c(), p[2].c(), p[3].c())
    }
}

class ContractFunctionP5<P1, P2, P3, P4, P5, R>(
    private val kFunction: KFunction6<*, P1, P2, P3, P4, P5, ContractFunctionRequest<R>>,
    sig: String,
    jsonAbi: () -> String,
) : AbstractContractFunction<R>(kFunction.returnType, sig, jsonAbi) {
    fun encodeFunctionCall(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5) = super.encodeFunctionCall(p1, p2, p3, p4, p5)

    fun <R> decodeFunctionCall(input: String, callParameter: (P1, P2, P3, P4, P5) -> R): R {
        val p = abiCodec.decodeFunctionCall(abi.inputs, kFunction.parameters, input)
        return callParameter(p[0].c(), p[1].c(), p[2].c(), p[3].c(), p[4].c())
    }
}

class ContractFunctionP6<P1, P2, P3, P4, P5, P6, R>(
    private val kFunction: KFunction7<*, P1, P2, P3, P4, P5, P6, ContractFunctionRequest<R>>,
    sig: String,
    jsonAbi: () -> String,
) : AbstractContractFunction<R>(kFunction.returnType, sig, jsonAbi) {
    fun encodeFunctionCall(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6) =
        super.encodeFunctionCall(p1, p2, p3, p4, p5, p6)

    fun <R> decodeFunctionCall(input: String, callParameter: (P1, P2, P3, P4, P5, P6) -> R): R {
        val p = abiCodec.decodeFunctionCall(abi.inputs, kFunction.parameters, input)
        return callParameter(p[0].c(), p[1].c(), p[2].c(), p[3].c(), p[4].c(), p[5].c())
    }
}

class ContractFunctionP7<P1, P2, P3, P4, P5, P6, P7, R>(
    private val kFunction: KFunction8<*, P1, P2, P3, P4, P5, P6, P7, ContractFunctionRequest<R>>,
    sig: String,
    jsonAbi: () -> String,
) : AbstractContractFunction<R>(kFunction.returnType, sig, jsonAbi) {
    fun encodeFunctionCall(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7) =
        super.encodeFunctionCall(p1, p2, p3, p4, p5, p6, p7)

    fun <R> decodeFunctionCall(input: String, callParameter: (P1, P2, P3, P4, P5, P6, P7) -> R): R {
        val p = abiCodec.decodeFunctionCall(abi.inputs, kFunction.parameters, input)
        return callParameter(p[0].c(), p[1].c(), p[2].c(), p[3].c(), p[4].c(), p[5].c(), p[6].c())
    }
}

class ContractFunctionP8<P1, P2, P3, P4, P5, P6, P7, P8, R>(
    private val kFunction: KFunction9<*, P1, P2, P3, P4, P5, P6, P7, P8, ContractFunctionRequest<R>>,
    sig: String,
    jsonAbi: () -> String,
) : AbstractContractFunction<R>(kFunction.returnType, sig, jsonAbi) {
    fun encodeFunctionCall(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8) =
        super.encodeFunctionCall(p1, p2, p3, p4, p5, p6, p7, p8)

    fun <R> decodeFunctionCall(input: String, callParameter: (P1, P2, P3, P4, P5, P6, P7, P8) -> R): R {
        val p = abiCodec.decodeFunctionCall(abi.inputs, kFunction.parameters, input)
        return callParameter(p[0].c(), p[1].c(), p[2].c(), p[3].c(), p[4].c(), p[5].c(), p[6].c(), p[7].c())
    }
}

class ContractFunctionP9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>(
    private val kFunction: KFunction10<*, P1, P2, P3, P4, P5, P6, P7, P8, P9, ContractFunctionRequest<R>>,
    sig: String,
    jsonAbi: () -> String,
) : AbstractContractFunction<R>(kFunction.returnType, sig, jsonAbi) {
    fun encodeFunctionCall(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9) =
        super.encodeFunctionCall(p1, p2, p3, p4, p5, p6, p7, p8, p9)

    fun <R> decodeFunctionCall(input: String, callParameter: (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R): R {
        val p = abiCodec.decodeFunctionCall(abi.inputs, kFunction.parameters, input)
        return callParameter(p[0].c(), p[1].c(), p[2].c(), p[3].c(), p[4].c(), p[5].c(), p[6].c(), p[7].c(), p[8].c())
    }
}
