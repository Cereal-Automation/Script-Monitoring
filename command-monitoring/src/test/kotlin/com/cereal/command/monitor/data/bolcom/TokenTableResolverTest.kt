package com.cereal.command.monitor.data.bolcom

import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class TokenTableResolverTest {
    @Test
    fun `a literal int landed on via an index is not re-resolved as another table index`() {
        // Table layout:
        //  0: "data" marker
        //  1: edge -> root object at index 2
        //  2: root object; "percentage" is an edge -> index 3
        //  3: the literal value 13 (percentage) -- also happens to equal table index 13
        //  4: unrelated token-keyed object, just so the resolver's isAllTokenKeyedObject
        //     precondition is satisfied somewhere in the table
        //  5-12: filler
        //  13: an unrelated string that a buggy resolver would wrongly substitute for the
        //      literal 13 above by treating it as a second reference hop
        val raw =
            """
            [
              "data",
              2,
              {"__typename":"Discount","percentage":3},
              13,
              {"_0":1},
              null, null, null, null, null, null, null, null,
              "metaData"
            ]
            """.trimIndent()

        val resolved = requireNotNull(tryDecodeInternedTable(raw))

        val percentage = resolved.jsonObject.getValue("percentage").jsonPrimitive
        assertEquals(13, percentage.int)
    }
}
