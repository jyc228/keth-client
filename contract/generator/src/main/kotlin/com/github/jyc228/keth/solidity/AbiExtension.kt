package com.github.jyc228.keth.solidity

internal fun AbiItem.outputToKotlinType(): String? {
    if (outputs.isEmpty()) return null
    return when (outputs.size) {
        1 -> outputs[0].typeToKotlin
        2 -> "Pair<${outputs.joinToString(", ") { it.typeToKotlin }}>"
        3 -> "Triple<${outputs.joinToString(", ") { it.typeToKotlin }}>"
        else -> "${name?.replaceFirstChar { it.titlecase() }}Output"
    }
}

internal val AbiComponent.typeToKotlin: String
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