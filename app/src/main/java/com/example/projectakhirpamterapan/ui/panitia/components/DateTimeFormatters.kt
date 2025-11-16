package com.example.projectakhirpamterapan.ui.panitia.components

fun formatIndonesianDate(raw: String?): String {
    if (raw.isNullOrBlank()) return "-"

    val datePart = raw.split("T", " ")[0]
    val pieces = datePart.split("-")
    if (pieces.size < 3) return raw

    val year = pieces[0]
    val monthNum = pieces[1].toIntOrNull() ?: return raw
    val day = pieces[2].take(2)

    val monthName = when (monthNum) {
        1 -> "Januari"
        2 -> "Februari"
        3 -> "Maret"
        4 -> "April"
        5 -> "Mei"
        6 -> "Juni"
        7 -> "Juli"
        8 -> "Agustus"
        9 -> "September"
        10 -> "Oktober"
        11 -> "November"
        12 -> "Desember"
        else -> return raw
    }

    return "$day $monthName $year"
}

fun formatIndonesianTime(raw: String?): String {
    if (raw.isNullOrBlank()) return "-"

    val timePart = raw.trim().takeWhile { it != ' ' }
    val pieces = timePart.split(":")
    if (pieces.size < 2) return raw

    val hour = pieces[0].padStart(2, '0')
    val minute = pieces[1].padStart(2, '0')

    return "$hour.$minute WIB"
}
