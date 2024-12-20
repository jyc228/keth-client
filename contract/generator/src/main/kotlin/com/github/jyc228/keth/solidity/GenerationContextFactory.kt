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

        "Topics" -> "com.github.jyc228.keth.client.eth.Topics"
        "AbstractContract" -> "com.github.jyc228.keth.client.contract.AbstractContract"
        "Contract" -> "com.github.jyc228.keth.client.contract.Contract"
        "ContractFactory" -> "com.github.jyc228.keth.client.contract.ContractFactory"
        "ContractEvent" -> "com.github.jyc228.keth.client.contract.ContractEvent"
        "ContractEventFactory" -> "com.github.jyc228.keth.client.contract.ContractEventFactory"
        "ContractFunctionP0" -> "com.github.jyc228.keth.client.contract.ContractFunctionP0"
        "ContractFunctionP1" -> "com.github.jyc228.keth.client.contract.ContractFunctionP1"
        "ContractFunctionP2" -> "com.github.jyc228.keth.client.contract.ContractFunctionP2"
        "ContractFunctionP3" -> "com.github.jyc228.keth.client.contract.ContractFunctionP3"
        "ContractFunctionP4" -> "com.github.jyc228.keth.client.contract.ContractFunctionP4"
        "ContractFunctionP5" -> "com.github.jyc228.keth.client.contract.ContractFunctionP5"
        "ContractFunctionP6" -> "com.github.jyc228.keth.client.contract.ContractFunctionP6"
        "ContractFunctionP7" -> "com.github.jyc228.keth.client.contract.ContractFunctionP7"
        "ContractFunctionP8" -> "com.github.jyc228.keth.client.contract.ContractFunctionP8"
        "ContractFunctionP9" -> "com.github.jyc228.keth.client.contract.ContractFunctionP9"
        "ContractFunctionRequest" -> "com.github.jyc228.keth.client.contract.ContractFunctionRequest"
        "GetEventRequest" -> "com.github.jyc228.keth.client.contract.GetEventRequest"

        "ApiResult" -> "com.github.jyc228.keth.client.ApiResult"
        "EthApi" -> "com.github.jyc228.keth.client.eth.EthApi"
        else -> ""
    }
}