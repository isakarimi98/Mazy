package ir.docscan.app.data.util

import java.util.Calendar

/**
 * Utility for converting Gregorian date to Persian (Solar Hijri / Shamsi) date
 * and formatting numbers with Persian digits.
 */
object ShamsiDateHelper {

    fun getCurrentShamsiDate(): String {
        val calendar = Calendar.getInstance()
        val gy = calendar.get(Calendar.YEAR)
        val gm = calendar.get(Calendar.MONTH) + 1
        val gd = calendar.get(Calendar.DAY_OF_MONTH)

        val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
        val monthStr = if (jm < 10) "0$jm" else "$jm"
        val dayStr = if (jd < 10) "0$jd" else "$jd"

        return toPersianDigits("$jy/$monthStr/$dayStr")
    }

    fun toPersianDigits(text: String): String {
        val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val sb = StringBuilder()
        for (ch in text) {
            if (ch in '0'..'9') {
                sb.append(persianDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun formatFileSize(bytes: Long): String {
        val formatted = when {
            bytes >= 1024 * 1024 -> String.format(java.util.Locale.US, "%.1f مگابایت", bytes / (1024f * 1024f))
            bytes >= 1024 -> String.format(java.util.Locale.US, "%d کیلوبایت", bytes / 1024)
            else -> "$bytes بایت"
        }
        return toPersianDigits(formatted)
    }

    private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val gy2 = if (gm > 2) gy + 1 else gy
        var gDayNo = 365 * gy + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400 - 80 + gd
        for (i in 0 until gm - 1) {
            gDayNo += gDaysInMonth[i]
        }
        if (gm > 2 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0))) {
            gDayNo++
        }

        var jDayNo = gDayNo - 79
        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        for (i in 0..11) {
            if (jDayNo < jDaysInMonth[i]) {
                jm = i + 1
                break
            }
            jDayNo -= jDaysInMonth[i]
        }
        val jd = jDayNo + 1
        return Triple(jy, jm, jd)
    }
}
