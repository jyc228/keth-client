package com.github.jyc228.keth.client.contract

import com.github.jyc228.keth.client.ApiResult
import com.github.jyc228.keth.client.eth.EthApi
import com.github.jyc228.keth.client.eth.GetLogsRequest
import com.github.jyc228.keth.client.eth.Log
import com.github.jyc228.keth.type.Address
import com.github.jyc228.keth.type.HexData
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses

abstract class AbstractContract<ROOT_EVENT : ContractEvent>(
    private val address: Address,
    private val api: EthApi
) : Contract<ROOT_EVENT> {

    override suspend fun getLogs(options: (GetLogsRequest.() -> Unit)?): ApiResult<List<Pair<ROOT_EVENT, Log>>> {
        val contractInterface = this::class.superclasses.first { it.isSubclassOf(Contract::class) }
        val eventFactoryByHash = contractInterface
            .nestedClasses
            .mapNotNull { it.companionObjectInstance as? ContractEventFactory<ROOT_EVENT> }
            .associateBy { it.eventSig.hex }

        val request = GetLogsRequest(address = mutableSetOf(address)).apply { options?.invoke(this) }
        return api.getLogs(request).map { logs ->
            logs.mapNotNull { log ->
                eventFactoryByHash[log.topics[0].hex]?.decodeIf(log)?.let { e -> e to log }
            }
        }
    }

    override suspend fun getLogs(
        vararg requests: Contract.GetEventRequest<ROOT_EVENT>,
        options: (GetLogsRequest.() -> Unit)?
    ): ApiResult<List<Pair<ROOT_EVENT, Log>>> {
        val request = GetLogsRequest(address = mutableSetOf(address)).apply {
            requests.forEach { it.buildTopic(topics) }
            options?.invoke(this)
        }
        return api.getLogs(request).map { logs ->
            val eventRequestByHash = requests.associateBy { it.factory.eventSig.hex }
            logs.map { log ->
                val eventRequest = requireNotNull(eventRequestByHash[log.topics.first().hex])
                val decoded = eventRequest.factory.decode(log)
                eventRequest.subscribe?.invoke(decoded)
                decoded to log
            }
        }
    }

    protected fun <R> newRequest(convertCallResult: (HexData?) -> R, data: String): ContractFunctionRequest<R> {
        return EthContractFunctionRequest(address, api, data, convertCallResult)
    }
}
