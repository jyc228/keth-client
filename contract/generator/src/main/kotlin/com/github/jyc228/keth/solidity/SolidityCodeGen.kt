package com.github.jyc228.keth.solidity

abstract class SolidityCodeGen {
    protected val String.importPackagePath
        get() = when (this) {
            "BigInteger" -> "java.math.BigInteger"

            "Address" -> "com.github.jyc228.keth.type.Address"
            "Hash" -> "com.github.jyc228.keth.type.Hash"
            "HexInt" -> "com.github.jyc228.keth.type.HexInt"
            "HexULong" -> "com.github.jyc228.keth.type.HexULong"
            "HexBigInt" -> "com.github.jyc228.keth.type.HexBigInt"

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

    protected fun AbiItem.outputToKotlinType(): String? {
        if (outputs.isEmpty()) return null
        return when (outputs.size) {
            1 -> outputs[0].typeToKotlin
            2 -> "Pair<${outputs.joinToString(", ") { it.typeToKotlin }}>"
            3 -> "Triple<${outputs.joinToString(", ") { it.typeToKotlin }}>"
            else -> "${name?.replaceFirstChar { it.titlecase() }}Output"
        }
    }

    protected val AbiComponent.typeToKotlin: String
        get() {
            val arrayStartIndex = type.indexOf('[')
            val type = if (arrayStartIndex == -1) type else type.take(arrayStartIndex)
            val kotlinType = when (type) {
                "tuple" -> requireNotNull(internalType) { "invalid abi" }.split(" ")[1]
                "bool" -> "Boolean"
                "address" -> "Address"
                "string" -> "String"
                else -> when {
                    type.startsWith("bytes") -> "ByteArray"
                    type.startsWith("int") -> "BigInteger"
                    type.startsWith("uint") -> "BigInteger"
                    else -> error("unsupported type $this")
                }
            }
            if (arrayStartIndex == -1) {
                return kotlinType
            }
            return "List<${kotlinType.removeSuffix("[]")}>"
        }
}
