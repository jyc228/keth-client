package com.github.jyc228.keth.client.contract.library

import com.github.jyc228.keth.client.contract.AbstractContract
import com.github.jyc228.keth.client.contract.ContractFunctionRequest
import com.github.jyc228.keth.client.eth.EthApi
import com.github.jyc228.keth.type.Address
import java.math.BigInteger

class ERC721Impl(address: Address, api: EthApi) : ERC721, AbstractContract<ERC721.Event>(address, api) {
    override fun approve(to: Address, tokenId: BigInteger): ContractFunctionRequest<Unit> {
        return newRequest(
            ERC721.approve::decodeResult,
            ERC721.approve.encodeFunctionCall(to, tokenId),
        )
    }

    override fun balanceOf(owner: Address): ContractFunctionRequest<BigInteger> {
        return newRequest(
            ERC721.balanceOf::decodeResult,
            ERC721.balanceOf.encodeFunctionCall(owner),
        )
    }

    override fun getApproved(tokenId: BigInteger): ContractFunctionRequest<Address> {
        return newRequest(
            ERC721.getApproved::decodeResult,
            ERC721.getApproved.encodeFunctionCall(tokenId),
        )
    }

    override fun isApprovedForAll(owner: Address, operator: Address): ContractFunctionRequest<Boolean> {
        return newRequest(
            ERC721.isApprovedForAll::decodeResult,
            ERC721.isApprovedForAll.encodeFunctionCall(owner, operator),
        )
    }

    override fun name(): ContractFunctionRequest<String> {
        return newRequest(
            ERC721.name::decodeResult,
            ERC721.name.encodeFunctionCall(),
        )
    }

    override fun ownerOf(tokenId: BigInteger): ContractFunctionRequest<Address> {
        return newRequest(
            ERC721.ownerOf::decodeResult,
            ERC721.ownerOf.encodeFunctionCall(tokenId),
        )
    }

    override fun safeTransferFrom(
        from: Address,
        to: Address,
        tokenId: BigInteger
    ): ContractFunctionRequest<Unit> {
        return newRequest(
            ERC721.safeTransferFrom_address_address_uint256::decodeResult,
            ERC721.safeTransferFrom_address_address_uint256.encodeFunctionCall(from, to, tokenId),
        )
    }

    override fun safeTransferFrom(
        from: Address,
        to: Address,
        tokenId: BigInteger,
        data: ByteArray
    ): ContractFunctionRequest<Unit> {
        return newRequest(
            ERC721.safeTransferFrom_address_address_uint256_bytes::decodeResult,
            ERC721.safeTransferFrom_address_address_uint256_bytes.encodeFunctionCall(from, to, tokenId, data),
        )
    }

    override fun setApprovalForAll(operator: Address, _approved: Boolean): ContractFunctionRequest<Unit> {
        return newRequest(
            ERC721.setApprovalForAll::decodeResult,
            ERC721.setApprovalForAll.encodeFunctionCall(operator, _approved),
        )
    }

    override fun supportsInterface(interfaceId: ByteArray): ContractFunctionRequest<Boolean> {
        return newRequest(
            ERC721.supportsInterface::decodeResult,
            ERC721.supportsInterface.encodeFunctionCall(interfaceId),
        )
    }

    override fun symbol(): ContractFunctionRequest<String> {
        return newRequest(
            ERC721.symbol::decodeResult,
            ERC721.symbol.encodeFunctionCall(),
        )
    }

    override fun tokenByIndex(index: BigInteger): ContractFunctionRequest<BigInteger> {
        return newRequest(
            ERC721.tokenByIndex::decodeResult,
            ERC721.tokenByIndex.encodeFunctionCall(index),
        )
    }

    override fun tokenOfOwnerByIndex(owner: Address, index: BigInteger): ContractFunctionRequest<BigInteger> {
        return newRequest(
            ERC721.tokenOfOwnerByIndex::decodeResult,
            ERC721.tokenOfOwnerByIndex.encodeFunctionCall(owner, index),
        )
    }

    override fun tokenURI(tokenId: BigInteger): ContractFunctionRequest<String> {
        return newRequest(
            ERC721.tokenURI::decodeResult,
            ERC721.tokenURI.encodeFunctionCall(tokenId),
        )
    }

    override fun totalSupply(): ContractFunctionRequest<BigInteger> {
        return newRequest(
            ERC721.totalSupply::decodeResult,
            ERC721.totalSupply.encodeFunctionCall(),
        )
    }

    override fun transferFrom(
        from: Address,
        to: Address,
        tokenId: BigInteger
    ): ContractFunctionRequest<Unit> {
        return newRequest(
            ERC721.transferFrom::decodeResult,
            ERC721.transferFrom.encodeFunctionCall(from, to, tokenId),
        )
    }
}
