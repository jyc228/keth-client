package com.github.jyc228.keth.solidity

import com.github.jyc228.kotlin.codegen.GenerationContext

internal fun GenerationContext.Companion.new() = GenerationContext {
    when (it) {
        "BigInteger" -> "java.math.BigInteger"

        "Address" -> "com.github.jyc228.keth.type.Address"
        "Hash" -> "com.github.jyc228.keth.type.Hash"
        "HexInt" -> "com.github.jyc228.keth.type.HexInt"
        "HexULong" -> "com.github.jyc228.keth.type.HexULong"
        "HexBigInt" -> "com.github.jyc228.keth.type.HexBigInt"
        "Topics" -> "com.github.jyc228.keth.type.Topics"

        "Indexed" -> "com.github.jyc228.keth.contract.Indexed"
        "@Indexed" -> "com.github.jyc228.keth.contract.Indexed"
        "AbstractContract" -> "com.github.jyc228.keth.contract.AbstractContract"
        "Contract" -> "com.github.jyc228.keth.contract.Contract"
        "ContractEvent" -> "com.github.jyc228.keth.contract.ContractEvent"
        "ContractEventFactory" -> "com.github.jyc228.keth.contract.ContractEventFactory"
        "ContractFunctionP0" -> "com.github.jyc228.keth.contract.ContractFunctionP0"
        "ContractFunctionP1" -> "com.github.jyc228.keth.contract.ContractFunctionP1"
        "ContractFunctionP2" -> "com.github.jyc228.keth.contract.ContractFunctionP2"
        "ContractFunctionP3" -> "com.github.jyc228.keth.contract.ContractFunctionP3"
        "ContractFunctionP4" -> "com.github.jyc228.keth.contract.ContractFunctionP4"
        "ContractFunctionP5" -> "com.github.jyc228.keth.contract.ContractFunctionP5"
        "ContractFunctionP6" -> "com.github.jyc228.keth.contract.ContractFunctionP6"
        "ContractFunctionP7" -> "com.github.jyc228.keth.contract.ContractFunctionP7"
        "ContractFunctionP8" -> "com.github.jyc228.keth.contract.ContractFunctionP8"
        "ContractFunctionP9" -> "com.github.jyc228.keth.contract.ContractFunctionP9"
        "ContractFunctionRequest" -> "com.github.jyc228.keth.contract.ContractFunctionRequest"

        "ApiResult" -> "com.github.jyc228.keth.client.ApiResult"
        "EthApi" -> "com.github.jyc228.keth.client.eth.EthApi"
        else -> ""
    }
}