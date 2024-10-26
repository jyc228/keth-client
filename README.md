# keth-client: Kotlin Ethereum Rpc Client

keth-client is a Kotlin implementation of the
[Ethereum JSON RPC API](https://ethereum.org/ko/developers/docs/apis/json-rpc/)

***Caution!***

This project is a personal toy project. Many parts are not thoroughly tested, so use with caution.

## Features

- Supports various Ethereum RPCs via HTTP and WebSocket
- Fully coroutine-based
- Type-safe and easy batch requests
- Collects and sends RPC requests in batch mode in multi-threaded environments
- Automatically generates Kotlin contract wrappers from ABI, supporting function encoding, type-safe decoding, and chain
  interactions

## Usage

### Rpc Client

To interact with the network, you need to create an `EthereumClient`. You can create it using the `EthereumClient`
function and add additional settings via lambda parameters.
Most settings are optional, but if you set the `interval` property, it will collect requests at regular intervals and
send them in batch mode.
You can adjust the batch call size with the `batchSize` property. If set to null, all requests will be sent at once,
which may cause the node to reject the requests.

You can find configuration options
in [EthereumClientFactory.kt](src/main/kotlin/com/github/jyc228/keth/client/EthereumClientFactory.kt).

```kotlin
suspend fun main() {
  EthereumClient("https://... or wss://...") // Send all requests immediately

  EthereumClient("https://... or wss://...") {
        interval = 100.milliseconds // Collect requests and send in batch every 100 milliseconds
        batchSize = 10u // Send 10 requests at a time
    }
}
```

The batch mode, which sends multiple requests at once, is provided by the `EthereumClient.batch` function. In the
callback function, `this` is bound to `EthereumClient`.
You need to use the client bound to `this` when using the API.
For single types, the `batch` function is sufficient, but if you want to request functions with different return types
simultaneously, you can use the `batch2`, `batch3` extension functions.
The `batchSize` property also applies here.

```kotlin
suspend fun main() {
    val client = EthereumClient("https://... or wss://...")

    // 10 HTTP requests will be sent.
    client.eth.getBlocks(1uL..10uL, txHash).awaitAllOrThrow()

    // 1 HTTP request will be sent. If batchSize is set to 5, the requests will be split into 2 HTTP requests.
    client.batch { eth.getBlocks(1uL..10uL, txHash) }.awaitAllOrThrow()

    client.batch3( // 1 HTTP request will be sent.
        { eth.chainId() },
        { eth.getBlock(txHash) },
        { eth.getTransactionByBlock(BlockReference.latest, 0) }
    ) { chainId: ApiResult<HexULong>,
        block: ApiResult<Block<Block.TransactionHash>?>,
        tx: ApiResult<Transaction?> ->
        println("chainId ${chainId.awaitOrThrow()}")
        println("block ${block.awaitOrThrow()}")
        println("tx ${tx.awaitOrThrow()}")
    }
}
```

### Contract

You can generate Kotlin contract wrappers from Solidity ABI and easily interact with the network.
When the plugin is configured, contract wrappers are automatically generated from ABI and bin (optional) files in the
`src/main/solidity` directory during the build (gradle task `generateKotlinContractWrapper`).
RPC calls interacting with the contract are also affected by `interval` and `batch`.

Sample code for interacting with the network using the auto-generated [ERC20.kt](src/test/kotlin/erc/ERC20.kt)

```kotlin
suspend fun main() {
    val client = EthereumClient("https://... or wss://...")
    val usdt = ERC20("0xdAC17F958D2ee523a2206206994597C13D831ec7")
    val tokenHolders = listOf(...)

    println(client.contract[usdt].name().call { }.awaitOrThrow())

    client.batch {
        tokenHolders.map { contract[usdt].balanceOf(it).call { } }
    }.awaitAllOrThrow()

    client.batch2(
        { client.contract[usdt].name().call { } },
        { client.contract[usdt].decimals().call { } },
    ) { name, decimals ->
    ...
    }

    // Fetch all events between blocks 10 and 20
    client.contract[usdt].getLogs {
        fromBlock = 10uL
        toBlock = 20uL
    }.awaitOrThrow().forEach { (e: ERC20.Event, l: Log) ->
        when (e) {
            is ERC20.Approval -> TODO()
            is ERC20.Transfer -> TODO()
        }
    }

    client.contract[usdt].getLogs(
        ERC20.Transfer.filter {
            filterByFrom(...) // Filter topic[1]
        }.onEach { transfer: ERC20.Transfer -> println(transfer) }
    ) {
        // Configure get log request
    }.awaitOrThrow().forEach { (e: ERC20.Event, l: Log) ->
        when (e) {
            is ERC20.Approval -> TODO()
            is ERC20.Transfer -> TODO()
        }
    }
}
```

## Setup

Since it is only distributed to the GitHub Package Registry, additional repository settings are required.
You can check the version here: [gradle.properties](gradle.properties).

### Rpc Client

#### build.gradle.kts

```kotlin
repositories {
    maven("https://maven.pkg.github.com/jyc228/keth-client") {
        credentials {
            username = "github user name"
            password =
                "github personal access token. only require 'read:packages' scope. you can generate via https://github.com/settings/tokens"
        }
    }
}

dependencies {
    implementation("com.github.jyc228:keth-client:$version")
}
```

### Solidity Plugin

#### settings.gradle.kts

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.github.com/jyc228/keth-client") {
            credentials {
                username = "github user name"
                password =
                    "github personal access token. only require 'read:packages' scope. you can generate via https://github.com/settings/tokens"
            }
        }
    }
}
```

#### build.gradle.kts

```kotlin
plugins {
    id("com.github.jyc228.keth") version "$version"
}
```