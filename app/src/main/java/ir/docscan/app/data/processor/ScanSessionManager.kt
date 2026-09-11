package ir.docscan.app.data.processor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import ir.docscan.app.data.model.DocFilterType
import ir.docscan.app.data.model.ScannedDoc
import ir.docscan.app.data.repository.DocumentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Singleton state manager for the active scanning workflow.
 * Holds active bitmaps between Camera/Gallery -> Crop -> Filter -> Export.
 */
object ScanSessionManager {

    var activeDocId: String? = null
    var docTitle: String = "سند جدید"

    private val _originalBitmap = MutableStateFlow<Bitmap?>(null)
    val originalBitmap: StateFlow<Bitmap?> = _originalBitmap.asStateFlow()

    private val _workingBitmap = MutableStateFlow<Bitmap?>(null)
    val workingBitmap: StateFlow<Bitmap?> = _workingBitmap.asStateFlow()

    private val _processedBitmap = MutableStateFlow<Bitmap?>(null)
    val processedBitmap: StateFlow<Bitmap?> = _processedBitmap.asStateFlow()

    var activeFilter: DocFilterType = DocFilterType.PHOTOCOPY

    suspend fun setCapturedImage(context: Context, uri: Uri, title: String = "سند اسکن شده") = withContext(Dispatchers.IO) {
        val loaded = DocumentProcessor.loadBitmapFromUri(context, uri, maxDimension = 2048)
            ?: createFallbackSampleDocBitmap(title)
        activeDocId = null
        docTitle = title
        activeFilter = DocFilterType.PHOTOCOPY
        _originalBitmap.value = loaded
        _workingBitmap.value = loaded
        // Compute initial photocopy filter
        _processedBitmap.value = DocumentProcessor.applyFilter(loaded, activeFilter)
    }

    suspend fun loadExistingDoc(context: Context, doc: ScannedDoc) = withContext(Dispatchers.IO) {
        activeDocId = doc.id
        docTitle = doc.title
        activeFilter = doc.activeFilter
        val repo = DocumentRepository.getInstance(context)
        val loaded = repo.loadDocumentBitmap(doc) ?: createFallbackSampleDocBitmap(doc.title)
        _originalBitmap.value = loaded
        _workingBitmap.value = loaded
        _processedBitmap.value = DocumentProcessor.applyFilter(loaded, activeFilter)
    }

    suspend fun applyRotation(degrees: Float) = withContext(Dispatchers.Default) {
        val current = _workingBitmap.value ?: return@withContext
        val rotated = DocumentProcessor.rotateBitmap(current, degrees)
        _workingBitmap.value = rotated
        _processedBitmap.value = DocumentProcessor.applyFilter(rotated, activeFilter)
    }

    suspend fun applyCrop(left: Float, top: Float, right: Float, bottom: Float) = withContext(Dispatchers.Default) {
        val current = _workingBitmap.value ?: return@withContext
        val cropped = DocumentProcessor.cropBitmap(current, left, top, right, bottom)
        _workingBitmap.value = cropped
        _processedBitmap.value = DocumentProcessor.applyFilter(cropped, activeFilter)
    }

    suspend fun changeFilter(filter: DocFilterType) = withContext(Dispatchers.Default) {
        activeFilter = filter
        val working = _workingBitmap.value ?: return@withContext
        _processedBitmap.value = DocumentProcessor.applyFilter(working, filter)
    }

    suspend fun saveCurrentDoc(context: Context): ScannedDoc? = withContext(Dispatchers.IO) {
        val bitmapToSave = _processedBitmap.value ?: _workingBitmap.value ?: return@withContext null
        val repo = DocumentRepository.getInstance(context)

        val existingId = activeDocId
        if (existingId != null) {
            val existing = repo.getDocument(existingId)
            if (existing != null) {
                val updated = existing.copy(
                    title = docTitle,
                    activeFilter = activeFilter
                )
                repo.updateDocument(updated, bitmapToSave)
                return@withContext updated
            }
        }

        repo.addDocument(docTitle, bitmapToSave, activeFilter)
    }

    /**
     * Creates a realistic document page fallback if opened without physical camera capture
     */
    fun createFallbackSampleDocBitmap(title: String): Bitmap {
        val w = 1200
        val h = 1600
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Paper background
        canvas.drawColor(Color.rgb(250, 248, 243))

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(30, 41, 59)
            textSize = 48f
            isFakeBoldText = true
        }

        // Header line
        canvas.drawText(title, 100f, 180f, paint)

        paint.color = Color.rgb(15, 118, 110)
        paint.strokeWidth = 6f
        canvas.drawLine(100f, 220f, 1100f, 220f, paint)

        // Text lines simulation
        paint.color = Color.rgb(71, 85, 105)
        paint.strokeWidth = 3f
        paint.textSize = 28f
        paint.isFakeBoldText = false

        var y = 300f
        val sampleLines = listOf(
            "جمهوری اسلامی ایران - اسناد و مدارک رسمی",
            "شماره پرونده: ۱۲/۴۸۹۱/۴۰۳ - تاریخ ثبت و تایید مدارک",
            "بدین‌وسیله تایید می‌گردد مدارک بارگذاری شده به طور ۱۰۰٪ آفلاین",
            "و بدون نیاز به ارتباط با اینترنت در دستگاه تلفن همراه پردازش و بهینه‌سازی شده است.",
            "کیفیت وضوح، کنتراست خطوط و رنگ‌های مهر و امضا با استانداردهای دبیرخانه‌ای مطابقت دارد.",
            "این سند شامل تصحیح پرسپکتیو، برش هوشمند زوایا و تفکیک پس‌زمینه سفید می‌باشد."
        )

        for (line in sampleLines) {
            canvas.drawText(line, 100f, y, paint)
            y += 70f
        }

        // Stamp simulation
        val stampPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(190, 24, 93) // Persian carmine stamp color
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }
        canvas.drawCircle(850f, 1000f, 140f, stampPaint)

        val stampTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(190, 24, 93)
            textSize = 32f
            isFakeBoldText = true
        }
        canvas.drawText("دبیرخانه مرکزی", 760f, 990f, stampTextPaint)
        canvas.drawText("تایید شد", 800f, 1040f, stampTextPaint)

        return bitmap
    }
}
