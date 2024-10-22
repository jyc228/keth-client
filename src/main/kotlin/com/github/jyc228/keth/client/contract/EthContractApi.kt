package com.github.jyc228.keth.client.contract

import com.github.jyc228.keth.client.eth.EthApi
import com.github.jyc228.keth.type.Address

class EthContractApi(
    private val eth: EthApi,
    private val contracts: MutableMap<Address, Contract.Factory<*>> = mutableMapOf()
) : ContractApi {
    constructor(eth: EthApi, contract: EthContractApi) : this(eth, contract.contracts)

    override fun <T : Contract<*>> set(address: Address, factory: Contract.Factory<T>) {
        contracts[address] = factory
    }

    override fun <T : Contract<*>> get(address: Address): T {
        return contracts.getValue(address).create(address, eth) as T
    }

    override fun <T : Contract<*>> create(address: Address, factory: Contract.Factory<T>): T {
        return factory.create(address, eth)
    }
}