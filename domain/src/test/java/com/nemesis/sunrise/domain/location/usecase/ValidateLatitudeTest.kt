package com.nemesis.sunrise.domain.location.usecase

import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ValidateLatitudeTest {
    private lateinit var validateLatitude: ValidateLatitude

    @BeforeTest
    fun init() {
        validateLatitude = ValidateLatitude()
    }

    @Test
    fun `validate valid latitude`() {
        val latitude = 59.894
        val isLatitudeValid = validateLatitude(latitude)
        assertTrue(isLatitudeValid)
    }

    @Test
    fun `validate invalid latitude`() {
        val latitude = -100.1
        val isLatitudeValid = validateLatitude(latitude)
        assertFalse(isLatitudeValid)
    }
}
