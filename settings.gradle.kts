plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "keth-client"

include("codegen")
include("contract")
include("contract:abi")
include("contract:generator")
include("contract:solidity-plugin")
