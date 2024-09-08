// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract EventTest {
    event MerkleDropERC20Disabled(bytes32 indexed root);
    event MerkleDropERC20Activated(bytes32 indexed root);

    event ERC20MerkleClaimAirdropped(bytes32 indexed root, address indexed claimer, ER20ClaimMLF leafData);
    event ERC20MerkleSignAirdropped(bytes32 indexed root, address indexed claimer, ER20SigMLF extraAirdropInfo);

    struct ER20ClaimMLF {
        address user;
        uint256 amount;
    }

    struct ER20SigMLF {
        uint256 id;
        uint256 amount;
    }
}
