package com.nemesis.sunrise.domain.location.usecase

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ReduceCoordinateExcessAccuracyTest {

    @Test
    fun `scale coordinate to maximum decimal precision`() {
        val coordinate = 1.2345678
        val result = ReduceCoordinateExcessAccuracy().invoke(coordinate)
        val expectedValueRange = 1.22..1.24
        assertTrue(result in expectedValueRange)
    }

    @Test
    fun `do not change coordinate if less or equal than maximum decimal precision`() {
        val coordinate = 1.23
        val result = ReduceCoordinateExcessAccuracy().invoke(coordinate)
        assertEquals(coordinate, result)
    }
}
