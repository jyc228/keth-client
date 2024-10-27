package com.github.jyc228.keth.client.contract.library

import com.github.jyc228.keth.client.contract.Contract
import com.github.jyc228.keth.client.contract.ContractEvent
import com.github.jyc228.keth.client.contract.ContractEventFactory
import com.github.jyc228.keth.client.contract.ContractFactory
import com.github.jyc228.keth.client.contract.ContractFunctionP0
import com.github.jyc228.keth.client.contract.ContractFunctionP1
import com.github.jyc228.keth.client.contract.ContractFunctionP2
import com.github.jyc228.keth.client.contract.ContractFunctionP3
import com.github.jyc228.keth.client.contract.ContractFunctionRequest
import com.github.jyc228.keth.client.eth.Topics
import com.github.jyc228.keth.type.Address
import java.math.BigInteger

interface ERC20 : Contract<ERC20.Event> {
    fun name(): ContractFunctionRequest<String>
    fun approve(_spender: Address, _value: BigInteger): ContractFunctionRequest<Boolean>
    fun totalSupply(): ContractFunctionRequest<BigInteger>
    fun transferFrom(
        _from: Address,
        _to: Address,
        _value: BigInteger
    ): ContractFunctionRequest<Boolean>

    fun decimals(): ContractFunctionRequest<BigInteger>
    fun balanceOf(_owner: Address): ContractFunctionRequest<BigInteger>
    fun symbol(): ContractFunctionRequest<String>
    fun transfer(_to: Address, _value: BigInteger): ContractFunctionRequest<Boolean>
    fun allowance(_owner: Address, _spender: Address): ContractFunctionRequest<BigInteger>
    sealed interface Event : ContractEvent

    data class Approval(
        val owner: Address,
        val spender: Address,
        val value: BigInteger
    ) : Event {
        companion object : ContractEventFactory<Approval>(
            Approval::class,
            "0x8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925",
            { """{"anonymous":false,"inputs":[{"name":"owner","type":"address","indexed":true},{"name":"spender","type":"address","indexed":true},{"name":"value","type":"uint256","indexed":false}],"name":"Approval","type":"event"}""" }
        ) {
            fun Topics.filterByOwner(vararg owner: Address) = apply { filterByAddress(1, *owner) }
            fun Topics.filterBySpender(vararg spender: Address) = apply { filterByAddress(2, *spender) }
        }
    }

    data class Transfer(
        val from: Address,
        val to: Address,
        val value: BigInteger
    ) : Event {
        companion object : ContractEventFactory<Transfer>(
            Transfer::class,
            "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
            { """{"anonymous":false,"inputs":[{"name":"from","type":"address","indexed":true},{"name":"to","type":"address","indexed":true},{"name":"value","type":"uint256","indexed":false}],"name":"Transfer","type":"event"}""" }
        ) {
            fun Topics.filterByFrom(vararg from: Address) = apply { filterByAddress(1, *from) }
            fun Topics.filterByTo(vararg to: Address) = apply { filterByAddress(2, *to) }
        }
    }

    companion object : ContractFactory<ERC20>(::ERC20Impl) {
        val name = ContractFunctionP0(
            ERC20::name,
            "0x06fdde0383f15d582d1a74511486c9ddf862a882fb7904b3d9fe9b8b8e58a796"
        ) { """{"constant":true,"name":"name","outputs":[{"name":"","type":"string"}],"payable":false,"stateMutability":"view","type":"function"}""" }
        val approve = ContractFunctionP2(
            ERC20::approve,
            "0x095ea7b334ae44009aa867bfb386f5c3b4b443ac6f0ee573fa91c4608fbadfba"
        ) { """{"constant":false,"inputs":[{"name":"_spender","type":"address"},{"name":"_value","type":"uint256"}],"name":"approve","outputs":[{"name":"","type":"bool"}],"payable":false,"stateMutability":"nonpayable","type":"function"}""" }
        val totalSupply = ContractFunctionP0(
            ERC20::totalSupply,
            "0x18160ddd7f15c72528c2f94fd8dfe3c8d5aa26e2c50c7d81f4bc7bee8d4b7932"
        ) { """{"constant":true,"name":"totalSupply","outputs":[{"name":"","type":"uint256"}],"payable":false,"stateMutability":"view","type":"function"}""" }
        val transferFrom = ContractFunctionP3(
            ERC20::transferFrom,
            "0x23b872dd7302113369cda2901243429419bec145408fa8b352b3dd92b66c680b"
        ) { """{"constant":false,"inputs":[{"name":"_from","type":"address"},{"name":"_to","type":"address"},{"name":"_value","type":"uint256"}],"name":"transferFrom","outputs":[{"name":"","type":"bool"}],"payable":false,"stateMutability":"nonpayable","type":"function"}""" }
        val decimals = ContractFunctionP0(
            ERC20::decimals,
            "0x313ce567add4d438edf58b94ff345d7d38c45b17dfc0f947988d7819dca364f9"
        ) { """{"constant":true,"name":"decimals","outputs":[{"name":"","type":"uint8"}],"payable":false,"stateMutability":"view","type":"function"}""" }
        val balanceOf = ContractFunctionP1(
            ERC20::balanceOf,
            "0x70a08231b98ef4ca268c9cc3f6b4590e4bfec28280db06bb5d45e689f2a360be"
        ) { """{"constant":true,"inputs":[{"name":"_owner","type":"address"}],"name":"balanceOf","outputs":[{"name":"balance","type":"uint256"}],"payable":false,"stateMutability":"view","type":"function"}""" }
        val symbol = ContractFunctionP0(
            ERC20::symbol,
            "0x95d89b41e2f5f391a79ec54e9d87c79d6e777c63e32c28da95b4e9e4a79250ec"
        ) { """{"constant":true,"name":"symbol","outputs":[{"name":"","type":"string"}],"payable":false,"stateMutability":"view","type":"function"}""" }
        val transfer = ContractFunctionP2(
            ERC20::transfer,
            "0xa9059cbb2ab09eb219583f4a59a5d0623ade346d962bcd4e46b11da047c9049b"
        ) { """{"constant":false,"inputs":[{"name":"_to","type":"address"},{"name":"_value","type":"uint256"}],"name":"transfer","outputs":[{"name":"","type":"bool"}],"payable":false,"stateMutability":"nonpayable","type":"function"}""" }
        val allowance = ContractFunctionP2(
            ERC20::allowance,
            "0xdd62ed3e90e97b3d417db9c0c7522647811bafca5afc6694f143588d255fdfb4"
        ) { """{"constant":true,"inputs":[{"name":"_owner","type":"address"},{"name":"_spender","type":"address"}],"name":"allowance","outputs":[{"name":"","type":"uint256"}],"payable":false,"stateMutability":"view","type":"function"}""" }

        fun bin(): String {
            return ""
        }

        fun encodeDeploymentCallData(): String {
            return "0x" + bin()
        }
    }
}
