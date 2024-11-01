package com.github.jyc228.keth.client.contract.library

import com.github.jyc228.keth.client.contract.AbstractContract
import com.github.jyc228.keth.client.contract.ContractFunctionRequest
import com.github.jyc228.keth.client.eth.EthApi
import com.github.jyc228.keth.type.Address
import java.math.BigInteger

class ERC20Impl(address: Address, api: EthApi) : ERC20, AbstractContract<ERC20.Event>(address, api) {
    override fun name(): ContractFunctionRequest<String> {
        return newRequest(
            ERC20.name::decodeResult,
            ERC20.name.encodeFunctionCall(),
        )
    }
    override fun approve(_spender: Address, _value: BigInteger): ContractFunctionRequest<Boolean> {
        return newRequest(
            ERC20.approve::decodeResult,
            ERC20.approve.encodeFunctionCall(_spender, _value),
        )
    }
    override fun totalSupply(): ContractFunctionRequest<BigInteger> {
        return newRequest(
            ERC20.totalSupply::decodeResult,
            ERC20.totalSupply.encodeFunctionCall(),
        )
    }
    override fun transferFrom(
        _from: Address,
        _to: Address,
        _value: BigInteger
    ): ContractFunctionRequest<Boolean> {
        return newRequest(
            ERC20.transferFrom::decodeResult,
            ERC20.transferFrom.encodeFunctionCall(_from, _to, _value),
        )
    }
    override fun decimals(): ContractFunctionRequest<BigInteger> {
        return newRequest(
            ERC20.decimals::decodeResult,
            ERC20.decimals.encodeFunctionCall(),
        )
    }
    override fun balanceOf(_owner: Address): ContractFunctionRequest<BigInteger> {
        return newRequest(
            ERC20.balanceOf::decodeResult,
            ERC20.balanceOf.encodeFunctionCall(_owner),
        )
    }
    override fun symbol(): ContractFunctionRequest<String> {
        return newRequest(
            ERC20.symbol::decodeResult,
            ERC20.symbol.encodeFunctionCall(),
        )
    }
    override fun transfer(_to: Address, _value: BigInteger): ContractFunctionRequest<Boolean> {
        return newRequest(
            ERC20.transfer::decodeResult,
            ERC20.transfer.encodeFunctionCall(_to, _value),
        )
    }
    override fun allowance(_owner: Address, _spender: Address): ContractFunctionRequest<BigInteger> {
        return newRequest(
            ERC20.allowance::decodeResult,
            ERC20.allowance.encodeFunctionCall(_owner, _spender),
        )
    }
}
