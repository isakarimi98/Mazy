package ir.docscan.app.data.processor

import android.content.Context
import android.graphics.*
import android.net.Uri
import ir.docscan.app.data.model.DocFilterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max
import kotlin.math.min

/**
 * Offline, Privacy-First High-Performance Document Processing Engine.
 * Executes all pixel operations on Dispatchers.Default to ensure 60fps UI smoothness.
 */
object DocumentProcessor {

    /**
     * Applies the requested document filter to a source bitmap asynchronously.
     */
    suspend fun applyFilter(
        source: Bitmap,
        filterType: DocFilterType
    ): Bitmap = withContext(Dispatchers.Default) {
        when (filterType) {
            DocFilterType.ORIGINAL -> source.copy(source.config ?: Bitmap.Config.ARGB_8888, true)
            DocFilterType.PHOTOCOPY -> applyPhotocopyFilter(source)
            DocFilterType.BW_OFFICE -> applyBwOfficeFilter(source)
            DocFilterType.WHITEBOARD -> applyWhiteboardFilter(source)
            DocFilterType.MAGIC_COLOR -> applyMagicColorFilter(source)
        }
    }

    /**
     * High-contrast Photocopy algorithm:
     * Removes background shadows, creases, and lighting gradients.
     * Stretches paper tone to pure white and text/lines to crisp solid black.
     */
    private fun applyPhotocopyFilter(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        // Two-pass adaptive threshold approximation
        var sumLum = 0L
        for (i in pixels.indices) {
            val c = pixels[i]
            val r = (c shr 16) and 0xFF
            val g = (c shr 8) and 0xFF
            val b = c and 0xFF
            val lum = (299 * r + 587 * g + 114 * b) / 1000
            sumLum += lum
        }
        val avgLum = (sumLum / pixels.size).toInt().coerceIn(100, 180)
        val threshold = (avgLum * 0.88f).toInt().coerceIn(90, 160)

        for (i in pixels.indices) {
            val c = pixels[i]
            val r = (c shr 16) and 0xFF
            val g = (c shr 8) and 0xFF
            val b = c and 0xFF
            val lum = (299 * r + 587 * g + 114 * b) / 1000

            // High contrast curve
            val finalVal = if (lum > threshold) {
                // Background paper -> pure white
                val whiteBoost = ((lum - threshold) * 255) / (255 - threshold)
                if (whiteBoost > 180) 255 else (220 + (whiteBoost * 35 / 180)).coerceAtMost(255)
            } else {
                // Text and lines -> sharp dark
                val darkVal = (lum * 180 / threshold).coerceAtMost(100)
                (darkVal * 0.4f).toInt().coerceIn(0, 70)
            }

            pixels[i] = (0xFF shl 24) or (finalVal shl 16) or (finalVal shl 8) or finalVal
        }

        output.setPixels(pixels, 0, width, 0, 0, width, height)
        return output
    }

    /**
     * Clean B&W Office filter:
     * Standard grayscale conversion with dynamic range expansion and soft noise smoothing.
     */
    private fun applyBwOfficeFilter(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // ColorMatrix for standard office grayscale with enhanced contrast
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)

        // Contrast boost
        val contrast = 1.35f
        val brightness = -10f
        val scale = contrast
        val translate = (-0.5f * scale + 0.5f) * 255f + brightness

        val contrastMatrix = ColorMatrix(
            floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
        )
        contrastMatrix.preConcat(colorMatrix)
        paint.colorFilter = ColorMatrixColorFilter(contrastMatrix)

        canvas.drawBitmap(source, 0f, 0f, paint)
        return output
    }

    /**
     * Whiteboard & Shadow Removal filter:
     * Brightens uneven background illumination and removes hand/phone shadows.
     */
    private fun applyWhiteboardFilter(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        val hsv = FloatArray(3)
        for (i in pixels.indices) {
            val c = pixels[i]
            Color.colorToHSV(c, hsv)

            // Desaturate background paper while preserving strong marker inks
            if (hsv[1] < 0.25f) {
                hsv[1] = hsv[1] * 0.3f // clear tinted lighting
            }
            // Lift midtones and shadows
            hsv[2] = (hsv[2] * 1.25f).coerceAtMost(1.0f)

            pixels[i] = Color.HSVToColor(hsv)
        }

        output.setPixels(pixels, 0, width, 0, 0, width, height)
        return output
    }

    /**
     * Magic Color filter:
     * Brightens paper background while boosting saturation and clarity of official stamps,
     * signatures, and ID photos.
     */
    private fun applyMagicColorFilter(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        val hsv = FloatArray(3)
        for (i in pixels.indices) {
            val c = pixels[i]
            Color.colorToHSV(c, hsv)

            // If it's a colored stamp or signature (saturation > 0.15), boost color intensity
            if (hsv[1] > 0.12f) {
                hsv[1] = (hsv[1] * 1.35f).coerceAtMost(1.0f)
            } else {
                // Near neutral paper: push towards clean white
                hsv[1] = (hsv[1] * 0.4f)
                hsv[2] = (hsv[2] * 1.18f).coerceAtMost(1.0f)
            }

            pixels[i] = Color.HSVToColor(hsv)
        }

        output.setPixels(pixels, 0, width, 0, 0, width, height)
        return output
    }

    /**
     * Rotates bitmap by given degrees (90, 180, 270) clockwise.
     */
    suspend fun rotateBitmap(
        source: Bitmap,
        degrees: Float
    ): Bitmap = withContext(Dispatchers.Default) {
        if (degrees % 360f == 0f) return@withContext source
        val matrix = Matrix().apply { postRotate(degrees) }
        Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    /**
     * Crops bitmap within normalised/pixel coordinates.
     */
    suspend fun cropBitmap(
        source: Bitmap,
        leftRatio: Float,
        topRatio: Float,
        rightRatio: Float,
        bottomRatio: Float
    ): Bitmap = withContext(Dispatchers.Default) {
        val l = (source.width * leftRatio.coerceIn(0f, 1f)).toInt()
        val t = (source.height * topRatio.coerceIn(0f, 1f)).toInt()
        val r = (source.width * rightRatio.coerceIn(0f, 1f)).toInt()
        val b = (source.height * bottomRatio.coerceIn(0f, 1f)).toInt()

        val cropW = max(10, r - l)
        val cropH = max(10, b - t)

        val safeX = l.coerceIn(0, max(0, source.width - cropW))
        val safeY = t.coerceIn(0, max(0, source.height - cropH))

        Bitmap.createBitmap(source, safeX, safeY, cropW, cropH)
    }

    /**
     * Loads a downsampled bitmap safely without OOM errors.
     */
    suspend fun loadBitmapFromUri(
        context: Context,
        uri: Uri,
        maxDimension: Int = 2048
    ): Bitmap? = withContext(Dispatchers.IO) {
        try {
            var input: InputStream? = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(input, null, options)
            input?.close()

            val origW = options.outWidth
            val origH = options.outHeight
            if (origW <= 0 || origH <= 0) return@withContext null

            var sampleSize = 1
            while ((origW / sampleSize) > maxDimension || (origH / sampleSize) > maxDimension) {
                sampleSize *= 2
            }

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            input = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(input, null, decodeOptions)
            input?.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Saves bitmap to a local private file.
     */
    suspend fun saveBitmapToFile(
        bitmap: Bitmap,
        file: File,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 90
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            file.parentFile?.mkdirs()
            FileOutputStream(file).use { out ->
                bitmap.compress(format, quality, out)
                out.flush()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Generates a square or proportional thumbnail for lists.
     */
    fun createThumbnail(source: Bitmap, targetSize: Int = 256): Bitmap {
        val ratio = source.width.toFloat() / source.height.toFloat()
        val w: Int
        val h: Int
        if (ratio > 1) {
            w = targetSize
            h = (targetSize / ratio).toInt().coerceAtLeast(1)
        } else {
            h = targetSize
            w = (targetSize * ratio).toInt().coerceAtLeast(1)
        }
        return Bitmap.createScaledBitmap(source, w, h, true)
    }
}
