package ir.docscan.app.data.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import androidx.print.PrintHelper
import java.io.File

/**
 * Helper for Sharing and Printing documents via standard Android System Intents.
 * Integrates FileProvider to avoid FileUriExposedException.
 */
object ShareHelper {

    fun sharePdf(context: Context, pdfFile: File, title: String) {
        val authority = "${context.packageName}.fileprovider"
        val uri = FileProvider.getUriForFile(context, authority, pdfFile)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TITLE, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "اشتراک‌گذاری سند PDF")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun shareImage(context: Context, imageFile: File, title: String) {
        val authority = "${context.packageName}.fileprovider"
        val uri = FileProvider.getUriForFile(context, authority, imageFile)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "اشتراک‌گذاری تصویر سند")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun printBitmap(context: Context, bitmap: Bitmap, jobName: String = "DocScan Document") {
        val printHelper = PrintHelper(context).apply {
            scaleMode = PrintHelper.SCALE_FIT
        }
        printHelper.printBitmap(jobName, bitmap)
    }
}
