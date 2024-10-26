package erc

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
    fun allowance(key0: Address, key1: Address): ContractFunctionRequest<BigInteger>
    fun approve(spender: Address, amount: BigInteger): ContractFunctionRequest<Boolean>
    fun balanceOf(key0: Address): ContractFunctionRequest<BigInteger>
    fun burn(from: Address, amount: BigInteger): ContractFunctionRequest<Unit>
    fun decimals(): ContractFunctionRequest<BigInteger>
    fun mint(to: Address, amount: BigInteger): ContractFunctionRequest<Unit>
    fun name(): ContractFunctionRequest<String>
    fun symbol(): ContractFunctionRequest<String>
    fun totalSupply(): ContractFunctionRequest<BigInteger>
    fun transfer(recipient: Address, amount: BigInteger): ContractFunctionRequest<Boolean>
    fun transferFrom(
        sender: Address,
        recipient: Address,
        amount: BigInteger
    ): ContractFunctionRequest<Boolean>

    sealed interface Event : ContractEvent

    data class Approval(
        val owner: Address,
        val spender: Address,
        val value: BigInteger
    ) : Event {
        companion object : ContractEventFactory<Approval>(
            Approval::class,
            "0x8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925",
            { """{"anonymous":false,"inputs":[{"name":"owner","type":"address","internalType":"address","indexed":true},{"name":"spender","type":"address","internalType":"address","indexed":true},{"name":"value","type":"uint256","internalType":"uint256","indexed":false}],"name":"Approval","type":"event"}""" }
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
            { """{"anonymous":false,"inputs":[{"name":"from","type":"address","internalType":"address","indexed":true},{"name":"to","type":"address","internalType":"address","indexed":true},{"name":"value","type":"uint256","internalType":"uint256","indexed":false}],"name":"Transfer","type":"event"}""" }
        ) {
            fun Topics.filterByFrom(vararg from: Address) = apply { filterByAddress(1, *from) }
            fun Topics.filterByTo(vararg to: Address) = apply { filterByAddress(2, *to) }
        }
    }

    companion object : ContractFactory<ERC20>(::ERC20Impl) {
        val allowance = ContractFunctionP2(
            ERC20::allowance,
            "0xdd62ed3e90e97b3d417db9c0c7522647811bafca5afc6694f143588d255fdfb4"
        ) { """{"inputs":[{"name":"","type":"address","internalType":"address"},{"name":"","type":"address","internalType":"address"}],"name":"allowance","outputs":[{"name":"","type":"uint256","internalType":"uint256"}],"stateMutability":"view","type":"function"}""" }
        val approve = ContractFunctionP2(
            ERC20::approve,
            "0x095ea7b334ae44009aa867bfb386f5c3b4b443ac6f0ee573fa91c4608fbadfba"
        ) { """{"inputs":[{"name":"spender","type":"address","internalType":"address"},{"name":"amount","type":"uint256","internalType":"uint256"}],"name":"approve","outputs":[{"name":"","type":"bool","internalType":"bool"}],"stateMutability":"nonpayable","type":"function"}""" }
        val balanceOf = ContractFunctionP1(
            ERC20::balanceOf,
            "0x70a08231b98ef4ca268c9cc3f6b4590e4bfec28280db06bb5d45e689f2a360be"
        ) { """{"inputs":[{"name":"","type":"address","internalType":"address"}],"name":"balanceOf","outputs":[{"name":"","type":"uint256","internalType":"uint256"}],"stateMutability":"view","type":"function"}""" }
        val burn = ContractFunctionP2(
            ERC20::burn,
            "0x9dc29fac0ba6d4fc521c69c2b0c636d612e3343bc39ed934429b8876b0d12cba"
        ) { """{"inputs":[{"name":"from","type":"address","internalType":"address"},{"name":"amount","type":"uint256","internalType":"uint256"}],"name":"burn","stateMutability":"nonpayable","type":"function"}""" }
        val decimals = ContractFunctionP0(
            ERC20::decimals,
            "0x313ce567add4d438edf58b94ff345d7d38c45b17dfc0f947988d7819dca364f9"
        ) { """{"name":"decimals","outputs":[{"name":"","type":"uint8","internalType":"uint8"}],"stateMutability":"view","type":"function"}""" }
        val mint = ContractFunctionP2(
            ERC20::mint,
            "0x40c10f19c047ae7dfa66d6312b683d2ea3dfbcb4159e96b967c5f4b0a86f2842"
        ) { """{"inputs":[{"name":"to","type":"address","internalType":"address"},{"name":"amount","type":"uint256","internalType":"uint256"}],"name":"mint","stateMutability":"nonpayable","type":"function"}""" }
        val name = ContractFunctionP0(
            ERC20::name,
            "0x06fdde0383f15d582d1a74511486c9ddf862a882fb7904b3d9fe9b8b8e58a796"
        ) { """{"name":"name","outputs":[{"name":"","type":"string","internalType":"string"}],"stateMutability":"view","type":"function"}""" }
        val symbol = ContractFunctionP0(
            ERC20::symbol,
            "0x95d89b41e2f5f391a79ec54e9d87c79d6e777c63e32c28da95b4e9e4a79250ec"
        ) { """{"name":"symbol","outputs":[{"name":"","type":"string","internalType":"string"}],"stateMutability":"view","type":"function"}""" }
        val totalSupply = ContractFunctionP0(
            ERC20::totalSupply,
            "0x18160ddd7f15c72528c2f94fd8dfe3c8d5aa26e2c50c7d81f4bc7bee8d4b7932"
        ) { """{"name":"totalSupply","outputs":[{"name":"","type":"uint256","internalType":"uint256"}],"stateMutability":"view","type":"function"}""" }
        val transfer = ContractFunctionP2(
            ERC20::transfer,
            "0xa9059cbb2ab09eb219583f4a59a5d0623ade346d962bcd4e46b11da047c9049b"
        ) { """{"inputs":[{"name":"recipient","type":"address","internalType":"address"},{"name":"amount","type":"uint256","internalType":"uint256"}],"name":"transfer","outputs":[{"name":"","type":"bool","internalType":"bool"}],"stateMutability":"nonpayable","type":"function"}""" }
        val transferFrom = ContractFunctionP3(
            ERC20::transferFrom,
            "0x23b872dd7302113369cda2901243429419bec145408fa8b352b3dd92b66c680b"
        ) { """{"inputs":[{"name":"sender","type":"address","internalType":"address"},{"name":"recipient","type":"address","internalType":"address"},{"name":"amount","type":"uint256","internalType":"uint256"}],"name":"transferFrom","outputs":[{"name":"","type":"bool","internalType":"bool"}],"stateMutability":"nonpayable","type":"function"}""" }

        fun bin(): String {
            return "608060405234801561000f575f80fd5b5060405161124d38038061124d833981810160405281019061003191906101f6565b8260039081610040919061048b565b508160049081610050919061048b565b508060055f6101000a81548160ff021916908360ff16021790555050505061055a565b5f604051905090565b5f80fd5b5f80fd5b5f80fd5b5f80fd5b5f601f19601f8301169050919050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52604160045260245ffd5b6100d28261008c565b810181811067ffffffffffffffff821117156100f1576100f061009c565b5b80604052505050565b5f610103610073565b905061010f82826100c9565b919050565b5f67ffffffffffffffff82111561012e5761012d61009c565b5b6101378261008c565b9050602081019050919050565b8281835e5f83830152505050565b5f61016461015f84610114565b6100fa565b9050828152602081018484840111156101805761017f610088565b5b61018b848285610144565b509392505050565b5f82601f8301126101a7576101a6610084565b5b81516101b7848260208601610152565b91505092915050565b5f60ff82169050919050565b6101d5816101c0565b81146101df575f80fd5b50565b5f815190506101f0816101cc565b92915050565b5f805f6060848603121561020d5761020c61007c565b5b5f84015167ffffffffffffffff81111561022a57610229610080565b5b61023686828701610193565b935050602084015167ffffffffffffffff81111561025757610256610080565b5b61026386828701610193565b9250506040610274868287016101e2565b9150509250925092565b5f81519050919050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52602260045260245ffd5b5f60028204905060018216806102cc57607f821691505b6020821081036102df576102de610288565b5b50919050565b5f819050815f5260205f209050919050565b5f6020601f8301049050919050565b5f82821b905092915050565b5f600883026103417fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff82610306565b61034b8683610306565b95508019841693508086168417925050509392505050565b5f819050919050565b5f819050919050565b5f61038f61038a61038584610363565b61036c565b610363565b9050919050565b5f819050919050565b6103a883610375565b6103bc6103b482610396565b848454610312565b825550505050565b5f90565b6103d06103c4565b6103db81848461039f565b505050565b5b818110156103fe576103f35f826103c8565b6001810190506103e1565b5050565b601f82111561044357610414816102e5565b61041d846102f7565b8101602085101561042c578190505b610440610438856102f7565b8301826103e0565b50505b505050565b5f82821c905092915050565b5f6104635f1984600802610448565b1980831691505092915050565b5f61047b8383610454565b9150826002028217905092915050565b6104948261027e565b67ffffffffffffffff8111156104ad576104ac61009c565b5b6104b782546102b5565b6104c2828285610402565b5f60209050601f8311600181146104f3575f84156104e1578287015190505b6104eb8582610470565b865550610552565b601f198416610501866102e5565b5f5b8281101561052857848901518255600182019150602085019450602081019050610503565b868310156105455784890151610541601f891682610454565b8355505b6001600288020188555050505b505050505050565b610ce6806105675f395ff3fe608060405234801561000f575f80fd5b50600436106100a7575f3560e01c806340c10f191161006f57806340c10f191461016557806370a082311461018157806395d89b41146101b15780639dc29fac146101cf578063a9059cbb146101eb578063dd62ed3e1461021b576100a7565b806306fdde03146100ab578063095ea7b3146100c957806318160ddd146100f957806323b872dd14610117578063313ce56714610147575b5f80fd5b6100b361024b565b6040516100c09190610989565b60405180910390f35b6100e360048036038101906100de9190610a3a565b6102d7565b6040516100f09190610a92565b60405180910390f35b6101016103c4565b60405161010e9190610aba565b60405180910390f35b610131600480360381019061012c9190610ad3565b6103c9565b60405161013e9190610a92565b60405180910390f35b61014f61056e565b60405161015c9190610b3e565b60405180910390f35b61017f600480360381019061017a9190610a3a565b610580565b005b61019b60048036038101906101969190610b57565b61058e565b6040516101a89190610aba565b60405180910390f35b6101b96105a3565b6040516101c69190610989565b60405180910390f35b6101e960048036038101906101e49190610a3a565b61062f565b005b61020560048036038101906102009190610a3a565b61063d565b6040516102129190610a92565b60405180910390f35b61023560048036038101906102309190610b82565b610753565b6040516102429190610aba565b60405180910390f35b6003805461025890610bed565b80601f016020809104026020016040519081016040528092919081815260200182805461028490610bed565b80156102cf5780601f106102a6576101008083540402835291602001916102cf565b820191905f5260205f20905b8154815290600101906020018083116102b257829003601f168201915b505050505081565b5f8160025f3373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f8573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f20819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925846040516103b29190610aba565b60405180910390a36001905092915050565b5f5481565b5f8160025f8673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f3373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f8282546104519190610c4a565b925050819055508160015f8673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f8282546104a49190610c4a565b925050819055508160015f8573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f8282546104f79190610c7d565b925050819055508273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8460405161055b9190610aba565b60405180910390a3600190509392505050565b60055f9054906101000a900460ff1681565b61058a8282610773565b5050565b6001602052805f5260405f205f915090505481565b600480546105b090610bed565b80601f01602080910402602001604051908101604052809291908181526020018280546105dc90610bed565b80156106275780601f106105fe57610100808354040283529160200191610627565b820191905f5260205f20905b81548152906001019060200180831161060a57829003601f168201915b505050505081565b6106398282610846565b5050565b5f8160015f3373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f82825461068a9190610c4a565b925050819055508160015f8573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f8282546106dd9190610c7d565b925050819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040516107419190610aba565b60405180910390a36001905092915050565b6002602052815f5260405f20602052805f5260405f205f91509150505481565b8060015f8473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f8282546107bf9190610c7d565b92505081905550805f808282546107d69190610c7d565b925050819055508173ffffffffffffffffffffffffffffffffffffffff165f73ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8360405161083a9190610aba565b60405180910390a35050565b8060015f8473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f8282546108929190610c4a565b92505081905550805f808282546108a99190610c4a565b925050819055505f73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8360405161090d9190610aba565b60405180910390a35050565b5f81519050919050565b5f82825260208201905092915050565b8281835e5f83830152505050565b5f601f19601f8301169050919050565b5f61095b82610919565b6109658185610923565b9350610975818560208601610933565b61097e81610941565b840191505092915050565b5f6020820190508181035f8301526109a18184610951565b905092915050565b5f80fd5b5f73ffffffffffffffffffffffffffffffffffffffff82169050919050565b5f6109d6826109ad565b9050919050565b6109e6816109cc565b81146109f0575f80fd5b50565b5f81359050610a01816109dd565b92915050565b5f819050919050565b610a1981610a07565b8114610a23575f80fd5b50565b5f81359050610a3481610a10565b92915050565b5f8060408385031215610a5057610a4f6109a9565b5b5f610a5d858286016109f3565b9250506020610a6e85828601610a26565b9150509250929050565b5f8115159050919050565b610a8c81610a78565b82525050565b5f602082019050610aa55f830184610a83565b92915050565b610ab481610a07565b82525050565b5f602082019050610acd5f830184610aab565b92915050565b5f805f60608486031215610aea57610ae96109a9565b5b5f610af7868287016109f3565b9350506020610b08868287016109f3565b9250506040610b1986828701610a26565b9150509250925092565b5f60ff82169050919050565b610b3881610b23565b82525050565b5f602082019050610b515f830184610b2f565b92915050565b5f60208284031215610b6c57610b6b6109a9565b5b5f610b79848285016109f3565b91505092915050565b5f8060408385031215610b9857610b976109a9565b5b5f610ba5858286016109f3565b9250506020610bb6858286016109f3565b9150509250929050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52602260045260245ffd5b5f6002820490506001821680610c0457607f821691505b602082108103610c1757610c16610bc0565b5b50919050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52601160045260245ffd5b5f610c5482610a07565b9150610c5f83610a07565b9250828203905081811115610c7757610c76610c1d565b5b92915050565b5f610c8782610a07565b9150610c9283610a07565b9250828201905080821115610caa57610ca9610c1d565b5b9291505056fea26469706673582212206921fdf9aec38fb82fe0b82d61840f413cef5812f0f39dd74e5cb0abdf509d5864736f6c63430008190033"
        }

        fun encodeDeploymentCallData(
            _name: String,
            _symbol: String,
            _decimals: BigInteger
        ): String {
            return "0x" + bin() + encodeParameters(
                """{"inputs":[{"name":"_name","type":"string","internalType":"string"},{"name":"_symbol","type":"string","internalType":"string"},{"name":"_decimals","type":"uint8","internalType":"uint8"}],"stateMutability":"nonpayable","type":"constructor"}""",
                _name, _symbol, _decimals
            )
        }
    }
}