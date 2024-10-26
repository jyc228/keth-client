package com.github.jyc228.keth.client.contract

import com.github.jyc228.keth.PrivateAccount
import com.github.jyc228.keth.client.ApiResult
import com.github.jyc228.keth.client.eth.Access
import com.github.jyc228.keth.client.eth.BlockReference
import com.github.jyc228.keth.client.eth.CallRequest
import com.github.jyc228.keth.client.eth.EthApi
import com.github.jyc228.keth.type.Address
import com.github.jyc228.keth.type.Hash
import com.github.jyc228.keth.type.HexBigInt
import com.github.jyc228.keth.type.HexData
import com.github.jyc228.keth.type.HexULong

interface ContractFunctionRequest<R> {
    suspend fun call(build: suspend CallBuilder.() -> Unit): ApiResult<R>
    suspend fun transaction(
        account: PrivateAccount,
        build: suspend TransactionBuilder.() -> Unit
    ): ApiResult<Hash>

    interface CallBuilder {
        var from: Address?
        var gasPrice: HexBigInt?
        var gasLimit: HexBigInt?
        var value: HexBigInt?
        var targetBlock: BlockReference
    }

    interface TransactionBuilder {
        var chainId: HexULong?
        var nonce: HexULong?
        var gasPrice: HexBigInt?
        var gasLimit: HexBigInt?
        var value: HexBigInt?
        var accessList: List<Access>
        var maxFeePerGas: HexBigInt?
        var maxPriorityFeePerGas: HexBigInt?
    }
}

class EthContractFunctionRequest<R>(
    private val contractAddress: Address,
    private val eth: EthApi,
    private val data: String,
    private val convertCallResult: (HexData?) -> R
) : ContractFunctionRequest<R>,
    ContractFunctionRequest.CallBuilder,
    ContractFunctionRequest.TransactionBuilder {
    override var from: Address? = null
    override var gasLimit: HexBigInt? = null
    override var gasPrice: HexBigInt? = null
    override var value: HexBigInt? = null
    override var targetBlock: BlockReference = BlockReference.latest
    override var chainId: HexULong? = null
    override var nonce: HexULong? = null
    override var accessList: List<Access> = emptyList()
    override var maxFeePerGas: HexBigInt? = null
    override var maxPriorityFeePerGas: HexBigInt? = null

    override suspend fun call(build: suspend ContractFunctionRequest.CallBuilder.() -> Unit): ApiResult<R> {
        val builder: ContractFunctionRequest.CallBuilder = this.apply { build() }
        return eth.call(
            CallRequest(
                from = builder.from,
                to = contractAddress,
                gas = builder.gasLimit,
                gasPrice = builder.gasPrice,
                value = builder.value,
                data = data,
            ),
            targetBlock
        ).map(convertCallResult)
    }

    override suspend fun transaction(
        account: PrivateAccount,
        build: suspend ContractFunctionRequest.TransactionBuilder.() -> Unit
    ): ApiResult<Hash> {
        val builder: ContractFunctionRequest.TransactionBuilder = this.apply { build() }
        return eth.sendTransaction(account) {
            this.input = data
            this.to = contractAddress
            this.nonce = builder.nonce ?: HexULong(0u)
            this.chainId = builder.chainId
            this.gasPrice = builder.gasPrice ?: HexBigInt.ZERO
            this.gasLimit = builder.gasLimit ?: HexBigInt.ZERO
            this.value = builder.value ?: HexBigInt.ZERO
            this.accessList += builder.accessList
            this.maxFeePerGas = builder.maxFeePerGas ?: HexBigInt.ZERO
            this.maxPriorityFeePerGas = builder.maxPriorityFeePerGas ?: HexBigInt.ZERO
        }
    }
}
