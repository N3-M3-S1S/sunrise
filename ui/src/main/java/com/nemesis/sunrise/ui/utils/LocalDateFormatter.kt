package com.nemesis.sunrise.ui.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDateFormatter @Inject constructor() {
    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun formatToString(localDate: LocalDate): String = localDate.toJavaLocalDate().format(formatter)
}
