package com.github.jyc228.keth.contract

import com.github.jyc228.keth.abi.Abi
import com.github.jyc228.keth.client.ApiResult
import com.github.jyc228.keth.client.eth.EthApi
import com.github.jyc228.keth.solidity.AbiItem
import com.github.jyc228.keth.type.Address
import com.github.jyc228.keth.type.GetLogsRequest
import com.github.jyc228.keth.type.Log
import kotlinx.serialization.json.Json
import org.intellij.lang.annotations.Language

interface Contract<ROOT_EVENT : ContractEvent> {
    suspend fun getLogs(options: (GetLogsRequest.() -> Unit)? = null): ApiResult<List<Pair<ROOT_EVENT, Log>>>

    suspend fun <EVENT : ROOT_EVENT, INDEXED, FACTORY : ContractEventFactory<EVENT, INDEXED>> getLogs(
        factory: FACTORY,
        filterParameter: (GetLogsRequest.(indexedParam: INDEXED) -> Unit)? = null
    ): ApiResult<List<Pair<EVENT, Log>>>

    abstract class Factory<T : Contract<*>>(val create: (Address, EthApi) -> T) {
        protected fun encodeParameters(@Language("json") jsonAbi: String, vararg args: Any?): String {
            val abi: AbiItem = Json.decodeFromString(jsonAbi)
            return Abi.encodeParameters(abi.inputs.map { it.type }, args.toList()).removePrefix("0x")
        }
    }
}

interface ContractEvent
