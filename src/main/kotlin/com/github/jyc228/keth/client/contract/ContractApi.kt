package com.github.jyc228.keth.client.contract

import com.github.jyc228.keth.contract.Contract
import com.github.jyc228.keth.type.Address

interface ContractApi {
    operator fun <T : Contract<*>> set(address: Address, factory: Contract.Factory<T>)
    operator fun <T : Contract<*>> get(address: Address): T
    fun <T : Contract<*>> create(address: Address, factory: Contract.Factory<T>): T
}
