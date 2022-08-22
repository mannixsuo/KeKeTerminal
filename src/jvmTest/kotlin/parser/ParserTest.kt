package parser

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ParserTest : DescribeSpec({
    describe("Parser Test") {
        val parser = Parser()
        with(parser) {
            onIntArray(arrayOf(0x20))
            currentAction shouldBe ParserAction.PRINT
            currentState shouldBe ParserState.GROUND

            onIntArray(arrayOf(0x1a))
            currentAction shouldBe ParserAction.EXECUTE
            currentState shouldBe ParserState.GROUND

            onIntArray(arrayOf(0x1b))
            currentAction shouldBe ParserAction.CLEAR
            currentState shouldBe ParserState.ESCAPE



        }

    }
})