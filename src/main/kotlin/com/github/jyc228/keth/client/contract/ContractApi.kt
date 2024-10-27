package com.github.jyc228.keth.client.contract

/**
 * A class for interacting with implementations of [Contract].
 *
 * Implementations of [Contract] are automatically generated through a Gradle plugin.
 * One interface and one class are generated automatically, and you must use the [ContractApi] through the interface.
 * The automatically generated interface itself is a callable function.
 * When called, a [ContractAccessor] object is created and you can interact with it using the [get] function.
 *
 * The [com.github.jyc228.keth.client.contract.library] package provides default wrappers for commonly used contracts.
 * The contracts in this package can be used without Gradle plugin configuration.
 *
 * Additionally, all RPCs used through the contract can be batch processed.
 * ```kotlin
 * val client = EthereumClient("https://... or wss://...")
 * val usdt: ContractAccessor<ERC20> = ERC20("0x....")
 *
 * client.contract[usdt].name().call {}.awaitOrThrow()
 *
 * client.batch2(
 *      { contract[usdt].name().call {} },
 *      { contract[usdt].symbol().call {} }
 * )
 * ```
 */
interface ContractApi {
    operator fun <T : Contract<*>> get(contract: ContractAccessor<T>): T
}
