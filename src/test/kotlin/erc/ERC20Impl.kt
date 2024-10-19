package erc

import com.github.jyc228.keth.client.eth.EthApi
import com.github.jyc228.keth.contract.AbstractContract
import com.github.jyc228.keth.contract.ContractFunctionRequest
import com.github.jyc228.keth.type.Address
import java.math.BigInteger

class ERC20Impl(address: Address, api: EthApi) : ERC20, AbstractContract<ERC20.Event>(address, api) {
    override fun allowance(key0: Address, key1: Address): ContractFunctionRequest<BigInteger> {
        return newRequest(
            ERC20.allowance::decodeResult,
            ERC20.allowance.encodeFunctionCall(key0, key1),
        )
    }
    override fun approve(spender: Address, amount: BigInteger): ContractFunctionRequest<Boolean> {
        return newRequest(
            ERC20.approve::decodeResult,
            ERC20.approve.encodeFunctionCall(spender, amount),
        )
    }
    override fun balanceOf(key0: Address): ContractFunctionRequest<BigInteger> {
        return newRequest(
            ERC20.balanceOf::decodeResult,
            ERC20.balanceOf.encodeFunctionCall(key0),
        )
    }
    override fun burn(from: Address, amount: BigInteger): ContractFunctionRequest<Unit> {
        return newRequest(
            ERC20.burn::decodeResult,
            ERC20.burn.encodeFunctionCall(from, amount),
        )
    }
    override fun decimals(): ContractFunctionRequest<BigInteger> {
        return newRequest(
            ERC20.decimals::decodeResult,
            ERC20.decimals.encodeFunctionCall(),
        )
    }
    override fun mint(to: Address, amount: BigInteger): ContractFunctionRequest<Unit> {
        return newRequest(
            ERC20.mint::decodeResult,
            ERC20.mint.encodeFunctionCall(to, amount),
        )
    }
    override fun name(): ContractFunctionRequest<String> {
        return newRequest(
            ERC20.name::decodeResult,
            ERC20.name.encodeFunctionCall(),
        )
    }
    override fun symbol(): ContractFunctionRequest<String> {
        return newRequest(
            ERC20.symbol::decodeResult,
            ERC20.symbol.encodeFunctionCall(),
        )
    }
    override fun totalSupply(): ContractFunctionRequest<BigInteger> {
        return newRequest(
            ERC20.totalSupply::decodeResult,
            ERC20.totalSupply.encodeFunctionCall(),
        )
    }
    override fun transfer(recipient: Address, amount: BigInteger): ContractFunctionRequest<Boolean> {
        return newRequest(
            ERC20.transfer::decodeResult,
            ERC20.transfer.encodeFunctionCall(recipient, amount),
        )
    }
    override fun transferFrom(
        sender: Address,
        recipient: Address,
        amount: BigInteger
    ): ContractFunctionRequest<Boolean> {
        return newRequest(
            ERC20.transferFrom::decodeResult,
            ERC20.transferFrom.encodeFunctionCall(sender, recipient, amount),
        )
    }
}