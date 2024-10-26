package com.github.jyc228.keth.client.contract

import com.github.jyc228.keth.client.eth.GetLogsRequest
import erc.ERC20
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull

class ContractTest : DescribeSpec({
    context("GetEventRequest") {
        val eventRequests = listOf(
            ERC20.Transfer.filter { },
            ERC20.Approval.filter { }
        )
        val request = GetLogsRequest().apply { eventRequests.forEach { it.buildTopic(topics) } }

        request.topics.list shouldHaveSize 1
        request.topics.list[0].shouldNotBeNull() shouldHaveSize 2
    }
})