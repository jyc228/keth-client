package com.github.jyc228.keth.solidity.compile

import com.github.jyc228.keth.solidity.AbiComponent
import com.github.jyc228.keth.solidity.AbiItem
import com.github.jyc228.keth.solidity.AbiOutput
import com.github.jyc228.keth.solidity.AbiType

class CompileResult(
    val contractName: String,
    val abi: List<AbiItem>,
    val bin: String
) {
    val overloadingFunctions = mutableMapOf<String, MutableSet<Int>>()
    private val functionIndex = mutableSetOf<Int>()
    private val eventIndex = mutableSetOf<Int>()
    private val topLevelTuples = mutableMapOf<String, AbiComponent>()
    private val internalTuples = mutableSetOf<AbiComponent>()
    private val externalTuples = mutableSetOf<AbiComponent>()

    init {
        val functionNames = mutableSetOf<String>()
        abi.forEachIndexed { index, item ->
            item.ioAsSequence().filter { it.type == "tuple" || it.type == "tuple[]" }.forEach {
                val struct = it.resolveStruct()
                when (struct.ownerName.isBlank()) {
                    true -> topLevelTuples[struct.name] = it
                    false -> when (contractName == struct.ownerName) {
                        true -> internalTuples += it
                        false -> externalTuples += it
                    }
                }
            }
            if (item.outputs.size >= 4) {
                internalTuples += AbiOutput(
                    name = "",
                    type = "tuple",
                    internalType = "struct $contractName.${item.name?.replaceFirstChar { it.titlecase() }}Output",
                    components = item.outputs
                )
            }
            when (item.type) {
                AbiType.function -> {
                    functionIndex += index
                    val functionName = item.name!!
                    when (functionName in functionNames) {
                        true -> overloadingFunctions.getOrPut(functionName) {
                            mutableSetOf(functions().indexOfFirst { it.name == functionName })
                        } += index

                        false -> functionNames += functionName
                    }
                }

                AbiType.event -> eventIndex += index

                AbiType.constructor,
                AbiType.fallback,
                AbiType.receive,
                AbiType.error,
                null -> Unit
            }
        }
    }

    fun constructor() = abi.firstOrNull { it.type == AbiType.constructor }
    fun topLevelStructures(): Set<AbiComponent> = topLevelTuples.values.toSet()
    fun internalStructures(): Set<AbiComponent> = internalTuples
    fun externalStructures(): Set<AbiComponent> = externalTuples
    fun functions() = functionIndex.asSequence().map { abi[it] }
    fun events() = eventIndex.asSequence().map { abi[it] }
}
