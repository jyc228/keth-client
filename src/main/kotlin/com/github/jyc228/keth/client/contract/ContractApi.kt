package com.github.jyc228.keth.client.contract

/**
 * A class for interacting with implementations of [Contract].
 *
 * Implementations of [Contract] are automatically generated through a Gradle plugin.
 * One interface and one class are generated automatically, and you must use the [ContractApi] through the interface.
 * The automatically generated interface itself is a callable function.
 * When called, a [Contract.Instance] object is created and you can interact with it using the [get] function.
 *
 * Additionally, all RPCs used through the contract can be batch processed.
 * ```kotlin
 * val client = EthereumClient("https://... or wss://...")
 * val usdt: Contract.Instance<ERC20> = ERC20("0x....")
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
    operator fun <T : Contract<*>> get(contract: Contract.Instance<T>): T
}
