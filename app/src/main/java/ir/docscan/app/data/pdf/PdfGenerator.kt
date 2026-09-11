package ir.docscan.app.data.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Native Android PDF Generator using standard android.graphics.pdf.PdfDocument.
 * 100% Offline, lightweight, with zero external dependencies.
 */
object PdfGenerator {

    // Dimensions in Points (72 dpi)
    const val PAGE_A4_WIDTH = 595
    const val PAGE_A4_HEIGHT = 842

    const val PAGE_LETTER_WIDTH = 612
    const val PAGE_LETTER_HEIGHT = 792

    /**
     * Generates a standard PDF document containing the provided document bitmaps.
     */
    suspend fun generatePdf(
        context: Context,
        title: String,
        bitmaps: List<Bitmap>,
        paperSize: String = "A4",
        quality: String = "high"
    ): File = withContext(Dispatchers.IO) {
        val (pageWidth, pageHeight) = if (paperSize.equals("Letter", ignoreCase = true)) {
            PAGE_LETTER_WIDTH to PAGE_LETTER_HEIGHT
        } else {
            PAGE_A4_WIDTH to PAGE_A4_HEIGHT
        }

        val pdfDocument = PdfDocument()

        val margin = 28 // ~ 10mm margins for crisp printing
        val printableWidth = pageWidth - (margin * 2)
        val printableHeight = pageHeight - (margin * 2)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        for ((index, originalBitmap) in bitmaps.withIndex()) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            // Fill page background with crisp white
            canvas.drawRGB(255, 255, 255)

            // Scale and center bitmap within printable bounds
            val imgW = originalBitmap.width.toFloat()
            val imgH = originalBitmap.height.toFloat()
            val scale = minOf(printableWidth / imgW, printableHeight / imgH)

            val destW = (imgW * scale).toInt()
            val destH = (imgH * scale).toInt()

            val left = margin + (printableWidth - destW) / 2
            val top = margin + (printableHeight - destH) / 2

            val srcRect = Rect(0, 0, originalBitmap.width, originalBitmap.height)
            val destRect = Rect(left, top, left + destW, top + destH)

            canvas.drawBitmap(originalBitmap, srcRect, destRect, paint)

            pdfDocument.finishPage(page)
        }

        // Output file in app cache or Documents dir
        val outputDir = File(context.cacheDir, "pdf").apply { mkdirs() }
        val safeFileName = title.replace("[^a-zA-Z0-9آ-ی]".toRegex(), "_")
        val outputFile = File(outputDir, "${safeFileName}_${System.currentTimeMillis()}.pdf")

        FileOutputStream(outputFile).use { fos ->
            pdfDocument.writeTo(fos)
        }
        pdfDocument.close()

        outputFile
    }
}
