// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract structParam {
    constructor(ResourceParams memory param1, address param2){
    }

    struct ResourceParams {
        uint128 prevBaseFee;
        uint64 prevBoughtGas;
        uint64 prevBlockNum;
    }
}
