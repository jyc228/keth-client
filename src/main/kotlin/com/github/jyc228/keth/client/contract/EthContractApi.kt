package com.github.jyc228.keth.client.contract

import com.github.jyc228.keth.client.eth.EthApi
import com.github.jyc228.keth.type.Address

class EthContractApi(
    private val eth: EthApi
) : ContractApi {
    private val cache = mutableMapOf<Address, Contract<*>>()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Contract<*>> get(contract: ContractAccessor<T>): T {
        return cache[contract.address] as? T?
            ?: contract.factory.create(contract.address, eth).also { cache[contract.address] = it }
    }
}