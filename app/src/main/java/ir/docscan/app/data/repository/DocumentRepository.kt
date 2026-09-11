package ir.docscan.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import ir.docscan.app.data.mock.SampleDocs
import ir.docscan.app.data.model.DocFilterType
import ir.docscan.app.data.model.ScannedDoc
import ir.docscan.app.data.processor.DocumentProcessor
import ir.docscan.app.data.util.ShamsiDateHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

/**
 * 100% Offline Local Document Repository.
 * Persists document metadata as JSON and stores full-resolution bitmaps and thumbnails
 * in the private application files directory.
 */
class DocumentRepository(private val context: Context) {

    private val docsDir = File(context.filesDir, "docs").apply { mkdirs() }
    private val metaFile = File(context.filesDir, "documents_meta.json")

    private val _documents = MutableStateFlow<List<ScannedDoc>>(emptyList())
    val documents: StateFlow<List<ScannedDoc>> = _documents.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            loadFromDisk()
        }
    }

    private suspend fun loadFromDisk() = withContext(Dispatchers.IO) {
        if (!metaFile.exists()) {
            // Seed with sample documents on first run
            _documents.value = SampleDocs.initialDocuments
            saveToDisk(_documents.value)
            return@withContext
        }

        try {
            val jsonText = metaFile.readText()
            val jsonArray = JSONArray(jsonText)
            val list = mutableListOf<ScannedDoc>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val filterId = obj.optString("activeFilter", "photocopy")
                val filter = DocFilterType.values().find { it.id == filterId } ?: DocFilterType.PHOTOCOPY

                list.add(
                    ScannedDoc(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        dateShamsi = obj.optString("dateShamsi", ShamsiDateHelper.getCurrentShamsiDate()),
                        pageCount = obj.optInt("pageCount", 1),
                        fileSizeFormatted = obj.optString("fileSizeFormatted", "۱.۲ مگابایت"),
                        thumbnailUri = obj.optString("thumbnailUri").takeIf { it.isNotBlank() },
                        imagePath = obj.optString("imagePath").takeIf { it.isNotBlank() },
                        activeFilter = filter,
                        isFavorite = obj.optBoolean("isFavorite", false),
                        createdAtMillis = obj.optLong("createdAtMillis", System.currentTimeMillis())
                    )
                )
            }
            _documents.value = list
        } catch (e: Exception) {
            e.printStackTrace()
            _documents.value = SampleDocs.initialDocuments
        }
    }

    private suspend fun saveToDisk(list: List<ScannedDoc>) = withContext(Dispatchers.IO) {
        try {
            val jsonArray = JSONArray()
            for (doc in list) {
                val obj = JSONObject().apply {
                    put("id", doc.id)
                    put("title", doc.title)
                    put("dateShamsi", doc.dateShamsi)
                    put("pageCount", doc.pageCount)
                    put("fileSizeFormatted", doc.fileSizeFormatted)
                    put("thumbnailUri", doc.thumbnailUri ?: "")
                    put("imagePath", doc.imagePath ?: "")
                    put("activeFilter", doc.activeFilter.id)
                    put("isFavorite", doc.isFavorite)
                    put("createdAtMillis", doc.createdAtMillis)
                }
                jsonArray.put(obj)
            }
            metaFile.writeText(jsonArray.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getDocument(id: String): ScannedDoc? {
        return _documents.value.find { it.id == id }
    }

    /**
     * Saves a new scanned document and writes full-resolution image and thumbnail to storage.
     */
    suspend fun addDocument(
        title: String,
        bitmap: Bitmap,
        filter: DocFilterType = DocFilterType.PHOTOCOPY
    ): ScannedDoc = withContext(Dispatchers.IO) {
        val docId = "doc_" + UUID.randomUUID().toString().take(8)
        val imageFile = File(docsDir, "${docId}_full.jpg")
        val thumbFile = File(docsDir, "${docId}_thumb.jpg")

        // Save full image
        DocumentProcessor.saveBitmapToFile(bitmap, imageFile, quality = 90)

        // Generate and save thumbnail
        val thumbnail = DocumentProcessor.createThumbnail(bitmap, 256)
        DocumentProcessor.saveBitmapToFile(thumbnail, thumbFile, quality = 80)

        val fileSizeFormatted = ShamsiDateHelper.formatFileSize(imageFile.length())
        val dateShamsi = ShamsiDateHelper.getCurrentShamsiDate()

        val newDoc = ScannedDoc(
            id = docId,
            title = title.ifBlank { "سند اسکن شده جدید" },
            dateShamsi = dateShamsi,
            pageCount = 1,
            fileSizeFormatted = fileSizeFormatted,
            thumbnailUri = thumbFile.absolutePath,
            imagePath = imageFile.absolutePath,
            activeFilter = filter,
            isFavorite = false
        )

        val updatedList = listOf(newDoc) + _documents.value
        _documents.value = updatedList
        saveToDisk(updatedList)

        newDoc
    }

    /**
     * Updates an existing document's properties and optionally updates image.
     */
    suspend fun updateDocument(
        doc: ScannedDoc,
        newBitmap: Bitmap? = null
    ): Boolean = withContext(Dispatchers.IO) {
        if (newBitmap != null && doc.imagePath != null) {
            val imageFile = File(doc.imagePath)
            DocumentProcessor.saveBitmapToFile(newBitmap, imageFile, quality = 90)
            if (doc.thumbnailUri != null) {
                val thumb = DocumentProcessor.createThumbnail(newBitmap, 256)
                DocumentProcessor.saveBitmapToFile(thumb, File(doc.thumbnailUri), quality = 80)
            }
        }

        val updatedList = _documents.value.map {
            if (it.id == doc.id) doc else it
        }
        _documents.value = updatedList
        saveToDisk(updatedList)
        true
    }

    /**
     * Renames a document.
     */
    suspend fun renameDocument(id: String, newTitle: String): Boolean = withContext(Dispatchers.IO) {
        val updatedList = _documents.value.map {
            if (it.id == id) it.copy(title = newTitle) else it
        }
        _documents.value = updatedList
        saveToDisk(updatedList)
        true
    }

    /**
     * Toggles favorite status.
     */
    suspend fun toggleFavorite(id: String): Boolean = withContext(Dispatchers.IO) {
        val updatedList = _documents.value.map {
            if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
        }
        _documents.value = updatedList
        saveToDisk(updatedList)
        true
    }

    /**
     * Deletes a document and cleans up associated image files from internal storage.
     */
    suspend fun deleteDocument(id: String): Boolean = withContext(Dispatchers.IO) {
        val docToDelete = _documents.value.find { it.id == id }
        if (docToDelete != null) {
            docToDelete.imagePath?.let { File(it).delete() }
            docToDelete.thumbnailUri?.let { File(it).delete() }
        }

        val updatedList = _documents.value.filter { it.id != id }
        _documents.value = updatedList
        saveToDisk(updatedList)
        true
    }

    /**
     * Loads the high-resolution bitmap for a document from storage.
     */
    suspend fun loadDocumentBitmap(doc: ScannedDoc): Bitmap? = withContext(Dispatchers.IO) {
        val path = doc.imagePath ?: return@withContext null
        val file = File(path)
        if (!file.exists()) return@withContext null
        try {
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        @Volatile
        private var instance: DocumentRepository? = null

        fun getInstance(context: Context): DocumentRepository {
            return instance ?: synchronized(this) {
                instance ?: DocumentRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
