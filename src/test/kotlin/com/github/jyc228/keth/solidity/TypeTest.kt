package com.github.jyc228.keth.solidity

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe

class TypeTest : DescribeSpec({
    val parse: TestScope.() -> Type = { Type.of(testCase.name.testName) }

    it("uint256") { parse() shouldBe PrimitiveType("uint", 256) }
    it("int") { parse() shouldBe PrimitiveType("int", null) }
    it("(uint256,bool)") { parse() shouldBe TupleType("uint256", "bool") }
    it("(uint256,bool,string,uint[])") { parse() shouldBe TupleType("uint256", "bool", "string", "uint[]") }
    it("(uint256,(string[],bool),string)") { parse() shouldBe TupleType("uint256", "(string[],bool)", "string") }
    it("(uint256,bytes32,(string[],bool))") { parse() shouldBe TupleType("uint256", "bytes32", "(string[],bool)") }
    it("(uint256,(string[],bytes,bool),(string[],bool))") {
        parse() shouldBe TupleType("uint256", "(string[],bytes,bool)", "(string[],bool)")
    }
    it("uint256[]") { parse() shouldBe ArrayType("uint256", null, Type.of("uint256")) }
    it("uint256[][]") {
        parse() shouldBe ArrayType("uint256[]", null, ArrayType("uint256", null, "uint256"))
    }
    it("uint256[2]") { parse() shouldBe ArrayType("uint256", 2, "uint256") }
    it("uint256[0]") { parse() shouldBe ArrayType("uint256", 0, "uint256") }
    it("uint256[eq]") { shouldThrowAny { parse() } }
})