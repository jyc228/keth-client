package erc

import com.github.jyc228.keth.client.eth.EthApi
import com.github.jyc228.keth.contract.AbstractContract
import com.github.jyc228.keth.contract.ContractFunctionRequest
import com.github.jyc228.keth.type.Address
import java.math.BigInteger

class ERC20Impl(address: Address, api: EthApi) : ERC20, AbstractContract<ERC20.Event>(address, api) {
    override fun allowance(key0: Address, key1: Address): ContractFunctionRequest<BigInteger> {
        return ERC20.allowance(key0, key1)
    }

    override fun approve(spender: Address, amount: BigInteger): ContractFunctionRequest<Boolean> {
        return ERC20.approve(spender, amount)
    }

    override fun balanceOf(key0: Address): ContractFunctionRequest<BigInteger> {
        return ERC20.balanceOf(key0)
    }

    override fun burn(from: Address, amount: BigInteger): ContractFunctionRequest<Unit> {
        return ERC20.burn(from, amount)
    }

    override fun decimals(): ContractFunctionRequest<BigInteger> {
        return ERC20.decimals()
    }

    override fun mint(to: Address, amount: BigInteger): ContractFunctionRequest<Unit> {
        return ERC20.mint(to, amount)
    }

    override fun name(): ContractFunctionRequest<String> {
        return ERC20.name()
    }

    override fun symbol(): ContractFunctionRequest<String> {
        return ERC20.symbol()
    }

    override fun totalSupply(): ContractFunctionRequest<BigInteger> {
        return ERC20.totalSupply()
    }

    override fun transfer(recipient: Address, amount: BigInteger): ContractFunctionRequest<Boolean> {
        return ERC20.transfer(recipient, amount)
    }

    override fun transferFrom(
        sender: Address,
        recipient: Address,
        amount: BigInteger
    ): ContractFunctionRequest<Boolean> {
        return ERC20.transferFrom(sender, recipient, amount)
    }

}
