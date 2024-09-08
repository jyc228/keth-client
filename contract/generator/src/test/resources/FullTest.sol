// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract FullTest {
    constructor(string memory name){

    }

    function getOwners() public returns (address[] memory) {
        address[] memory arr;
        return arr;
    }

    function getUserReqs(address user) external view returns (FUserRegisterdReq[] memory reqs) {
        return reqs;
    }

    function getLevelPhazeProgress() external view returns (FPhaseProgress memory) {
        return FPhaseProgress(1, 1, 1, 1, "");
    }


    function getLevelPhazeProgressArray() external view returns (FPhaseProgress[] memory result) {
        return result;
    }

    function test() external view returns (Recipe[] memory result) {
        return result;
    }

    struct FUserRegisterdReq {
        bytes32 recipeId;
        uint64 count;
        uint64 phaze;
        uint64 indexPhazeReq;
        uint64 __;
    }
}

    struct FPhaseProgress {
        uint64 reqLength;
        uint64 reqIndex;
        uint64 snapSeed;
        uint64 __;
        bytes32 generatorSeed;
    }

    enum RandomSeedLevel {
        None,
        Eco,
        VRF
    }

    enum TokenType {
        None,
        ERC1155,
        ERC721,
        ERC1155Any
    }

    enum TokenBurnType {
        None,
        Burn,
        Dead,
        Transfer
    }

    struct FactoryAssetUnit {
        TokenType tokenType;
        TokenBurnType burnType;
        uint48 __;
        uint32 erc1155Id;
        address token;
        uint256 amount;
    }

    struct Recipe {
        RandomSeedLevel randomLevel;
        FactoryAssetUnit[] inputs;
        uint16[] probabilityBranches;
        FactoryAssetUnit[] probabilityOuts;
        FactoryAssetUnit defaultOut;
    }
