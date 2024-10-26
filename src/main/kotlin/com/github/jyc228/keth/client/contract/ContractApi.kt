package com.github.jyc228.keth.client.contract

interface ContractApi {
    operator fun <T : Contract<*>> get(contract: Contract.Instance<T>): T
}
