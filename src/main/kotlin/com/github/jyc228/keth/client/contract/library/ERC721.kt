package com.github.jyc228.keth.client.contract.library

import com.github.jyc228.keth.client.contract.Contract
import com.github.jyc228.keth.client.contract.ContractEvent
import com.github.jyc228.keth.client.contract.ContractEventFactory
import com.github.jyc228.keth.client.contract.ContractFactory
import com.github.jyc228.keth.client.contract.ContractFunctionP0
import com.github.jyc228.keth.client.contract.ContractFunctionP1
import com.github.jyc228.keth.client.contract.ContractFunctionP2
import com.github.jyc228.keth.client.contract.ContractFunctionP3
import com.github.jyc228.keth.client.contract.ContractFunctionP4
import com.github.jyc228.keth.client.contract.ContractFunctionRequest
import com.github.jyc228.keth.client.eth.Topics
import com.github.jyc228.keth.type.Address
import java.math.BigInteger

interface ERC721 : Contract<ERC721.Event> {
    fun approve(to: Address, tokenId: BigInteger): ContractFunctionRequest<Unit>
    fun balanceOf(owner: Address): ContractFunctionRequest<BigInteger>
    fun getApproved(tokenId: BigInteger): ContractFunctionRequest<Address>
    fun isApprovedForAll(owner: Address, operator: Address): ContractFunctionRequest<Boolean>
    fun name(): ContractFunctionRequest<String>
    fun ownerOf(tokenId: BigInteger): ContractFunctionRequest<Address>
    fun safeTransferFrom(
        from: Address,
        to: Address,
        tokenId: BigInteger
    ): ContractFunctionRequest<Unit>

    fun safeTransferFrom(
        from: Address,
        to: Address,
        tokenId: BigInteger,
        data: ByteArray
    ): ContractFunctionRequest<Unit>

    fun setApprovalForAll(operator: Address, _approved: Boolean): ContractFunctionRequest<Unit>
    fun supportsInterface(interfaceId: ByteArray): ContractFunctionRequest<Boolean>
    fun symbol(): ContractFunctionRequest<String>
    fun tokenByIndex(index: BigInteger): ContractFunctionRequest<BigInteger>
    fun tokenOfOwnerByIndex(owner: Address, index: BigInteger): ContractFunctionRequest<BigInteger>
    fun tokenURI(tokenId: BigInteger): ContractFunctionRequest<String>
    fun totalSupply(): ContractFunctionRequest<BigInteger>
    fun transferFrom(
        from: Address,
        to: Address,
        tokenId: BigInteger
    ): ContractFunctionRequest<Unit>

    sealed interface Event : ContractEvent

    data class Approval(
        val owner: Address,
        val approved: Address,
        val tokenId: BigInteger
    ) : Event {
        companion object : ContractEventFactory<Approval>(
            Approval::class,
            "0x8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925",
            { """{"anonymous":false,"inputs":[{"name":"owner","type":"address","internalType":"address","indexed":true},{"name":"approved","type":"address","internalType":"address","indexed":true},{"name":"tokenId","type":"uint256","internalType":"uint256","indexed":true}],"name":"Approval","type":"event"}""" }
        ) {
            fun Topics.filterByOwner(vararg owner: Address) = apply { filterByAddress(1, *owner) }
            fun Topics.filterByApproved(vararg approved: Address) = apply { filterByAddress(2, *approved) }
            fun Topics.filterByTokenId(vararg tokenId: BigInteger) = apply { filterByBigInteger(3, *tokenId) }
        }
    }

    data class ApprovalForAll(
        val owner: Address,
        val operator: Address,
        val approved: Boolean
    ) : Event {
        companion object : ContractEventFactory<ApprovalForAll>(
            ApprovalForAll::class,
            "0x17307eab39ab6107e8899845ad3d59bd9653f200f220920489ca2b5937696c31",
            { """{"anonymous":false,"inputs":[{"name":"owner","type":"address","internalType":"address","indexed":true},{"name":"operator","type":"address","internalType":"address","indexed":true},{"name":"approved","type":"bool","internalType":"bool","indexed":false}],"name":"ApprovalForAll","type":"event"}""" }
        ) {
            fun Topics.filterByOwner(vararg owner: Address) = apply { filterByAddress(1, *owner) }
            fun Topics.filterByOperator(vararg operator: Address) = apply { filterByAddress(2, *operator) }
        }
    }

    data class Transfer(
        val from: Address,
        val to: Address,
        val tokenId: BigInteger
    ) : Event {
        companion object : ContractEventFactory<Transfer>(
            Transfer::class,
            "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
            { """{"anonymous":false,"inputs":[{"name":"from","type":"address","internalType":"address","indexed":true},{"name":"to","type":"address","internalType":"address","indexed":true},{"name":"tokenId","type":"uint256","internalType":"uint256","indexed":true}],"name":"Transfer","type":"event"}""" }
        ) {
            fun Topics.filterByFrom(vararg from: Address) = apply { filterByAddress(1, *from) }
            fun Topics.filterByTo(vararg to: Address) = apply { filterByAddress(2, *to) }
            fun Topics.filterByTokenId(vararg tokenId: BigInteger) = apply { filterByBigInteger(3, *tokenId) }
        }
    }

    companion object : ContractFactory<ERC721>(::ERC721Impl) {
        val approve = ContractFunctionP2(
            ERC721::approve,
            "0x095ea7b334ae44009aa867bfb386f5c3b4b443ac6f0ee573fa91c4608fbadfba"
        ) { """{"inputs":[{"name":"to","type":"address","internalType":"address"},{"name":"tokenId","type":"uint256","internalType":"uint256"}],"name":"approve","stateMutability":"nonpayable","type":"function"}""" }
        val balanceOf = ContractFunctionP1(
            ERC721::balanceOf,
            "0x70a08231b98ef4ca268c9cc3f6b4590e4bfec28280db06bb5d45e689f2a360be"
        ) { """{"inputs":[{"name":"owner","type":"address","internalType":"address"}],"name":"balanceOf","outputs":[{"name":"balance","type":"uint256","internalType":"uint256"}],"stateMutability":"view","type":"function"}""" }
        val getApproved = ContractFunctionP1(
            ERC721::getApproved,
            "0x081812fc55e34fdc7cf5d8b5cf4e3621fa6423fde952ec6ab24afdc0d85c0b2e"
        ) { """{"inputs":[{"name":"tokenId","type":"uint256","internalType":"uint256"}],"name":"getApproved","outputs":[{"name":"operator","type":"address","internalType":"address"}],"stateMutability":"view","type":"function"}""" }
        val isApprovedForAll = ContractFunctionP2(
            ERC721::isApprovedForAll,
            "0xe985e9c5c6636c6879256001057b28ccac7718ef0ac56553ff9b926452cab8a3"
        ) { """{"inputs":[{"name":"owner","type":"address","internalType":"address"},{"name":"operator","type":"address","internalType":"address"}],"name":"isApprovedForAll","outputs":[{"name":"","type":"bool","internalType":"bool"}],"stateMutability":"view","type":"function"}""" }
        val name = ContractFunctionP0(
            ERC721::name,
            "0x06fdde0383f15d582d1a74511486c9ddf862a882fb7904b3d9fe9b8b8e58a796"
        ) { """{"name":"name","outputs":[{"name":"","type":"string","internalType":"string"}],"stateMutability":"view","type":"function"}""" }
        val ownerOf = ContractFunctionP1(
            ERC721::ownerOf,
            "0x6352211e6566aa027e75ac9dbf2423197fbd9b82b9d981a3ab367d355866aa1c"
        ) { """{"inputs":[{"name":"tokenId","type":"uint256","internalType":"uint256"}],"name":"ownerOf","outputs":[{"name":"owner","type":"address","internalType":"address"}],"stateMutability":"view","type":"function"}""" }
        val safeTransferFrom_address_address_uint256 = ContractFunctionP3(
            ERC721::safeTransferFrom,
            "0x42842e0eb38857a7775b4e7364b2775df7325074d088e7fb39590cd6281184ed"
        ) { """{"inputs":[{"name":"from","type":"address","internalType":"address"},{"name":"to","type":"address","internalType":"address"},{"name":"tokenId","type":"uint256","internalType":"uint256"}],"name":"safeTransferFrom","stateMutability":"nonpayable","type":"function"}""" }
        val safeTransferFrom_address_address_uint256_bytes = ContractFunctionP4(
            ERC721::safeTransferFrom,
            "0xb88d4fde60196325a28bb7f99a2582e0b46de55b18761e960c14ad7a32099465"
        ) { """{"inputs":[{"name":"from","type":"address","internalType":"address"},{"name":"to","type":"address","internalType":"address"},{"name":"tokenId","type":"uint256","internalType":"uint256"},{"name":"data","type":"bytes","internalType":"bytes"}],"name":"safeTransferFrom","stateMutability":"nonpayable","type":"function"}""" }
        val setApprovalForAll = ContractFunctionP2(
            ERC721::setApprovalForAll,
            "0xa22cb4651ab9570f89bb516380c40ce76762284fb1f21337ceaf6adab99e7d4a"
        ) { """{"inputs":[{"name":"operator","type":"address","internalType":"address"},{"name":"_approved","type":"bool","internalType":"bool"}],"name":"setApprovalForAll","stateMutability":"nonpayable","type":"function"}""" }
        val supportsInterface = ContractFunctionP1(
            ERC721::supportsInterface,
            "0x01ffc9a7a5cef8baa21ed3c5c0d7e23accb804b619e9333b597f47a0d84076e2"
        ) { """{"inputs":[{"name":"interfaceId","type":"bytes4","internalType":"bytes4"}],"name":"supportsInterface","outputs":[{"name":"","type":"bool","internalType":"bool"}],"stateMutability":"view","type":"function"}""" }
        val symbol = ContractFunctionP0(
            ERC721::symbol,
            "0x95d89b41e2f5f391a79ec54e9d87c79d6e777c63e32c28da95b4e9e4a79250ec"
        ) { """{"name":"symbol","outputs":[{"name":"","type":"string","internalType":"string"}],"stateMutability":"view","type":"function"}""" }
        val tokenByIndex = ContractFunctionP1(
            ERC721::tokenByIndex,
            "0x4f6ccce7c41aed90ec1f1887c4a821594c0f73758d8941d0ccaa2cde813b7298"
        ) { """{"inputs":[{"name":"index","type":"uint256","internalType":"uint256"}],"name":"tokenByIndex","outputs":[{"name":"","type":"uint256","internalType":"uint256"}],"stateMutability":"view","type":"function"}""" }
        val tokenOfOwnerByIndex = ContractFunctionP2(
            ERC721::tokenOfOwnerByIndex,
            "0x2f745c59a57ba1667616e5a9707eeaa36ec97c283ee24190b75d9c8d14bcb215"
        ) { """{"inputs":[{"name":"owner","type":"address","internalType":"address"},{"name":"index","type":"uint256","internalType":"uint256"}],"name":"tokenOfOwnerByIndex","outputs":[{"name":"tokenId","type":"uint256","internalType":"uint256"}],"stateMutability":"view","type":"function"}""" }
        val tokenURI = ContractFunctionP1(
            ERC721::tokenURI,
            "0xc87b56dda752230262935940d907f047a9f86bb5ee6aa33511fc86db33fea6cc"
        ) { """{"inputs":[{"name":"tokenId","type":"uint256","internalType":"uint256"}],"name":"tokenURI","outputs":[{"name":"","type":"string","internalType":"string"}],"stateMutability":"view","type":"function"}""" }
        val totalSupply = ContractFunctionP0(
            ERC721::totalSupply,
            "0x18160ddd7f15c72528c2f94fd8dfe3c8d5aa26e2c50c7d81f4bc7bee8d4b7932"
        ) { """{"name":"totalSupply","outputs":[{"name":"","type":"uint256","internalType":"uint256"}],"stateMutability":"view","type":"function"}""" }
        val transferFrom = ContractFunctionP3(
            ERC721::transferFrom,
            "0x23b872dd7302113369cda2901243429419bec145408fa8b352b3dd92b66c680b"
        ) { """{"inputs":[{"name":"from","type":"address","internalType":"address"},{"name":"to","type":"address","internalType":"address"},{"name":"tokenId","type":"uint256","internalType":"uint256"}],"name":"transferFrom","stateMutability":"nonpayable","type":"function"}""" }

        fun bin(): String {
            return ""
        }

        fun encodeDeploymentCallData(): String {
            return "0x" + bin()
        }
    }
}
