package com.nemesis.sunrise.domain.location.usecase

import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ValidateLongitudeTest {
    private lateinit var validatelongitude: ValidateLongitude

    @BeforeTest
    fun init() {
        validatelongitude = ValidateLongitude()
    }

    @Test
    fun `validate valid longitude`() {
        val longitude = 30.264
        val isLongitudeValid = validatelongitude(longitude)
        assertTrue(isLongitudeValid)
    }

    @Test
    fun `validate invalid longitude`() {
        val longitude = 9999.9999
        val isLongitudeValid = validatelongitude(longitude)
        assertFalse(isLongitudeValid)
    }
}
