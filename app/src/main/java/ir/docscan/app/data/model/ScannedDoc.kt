package ir.docscan.app.data.model

import java.util.UUID

/**
 * Model representing a scanned document item.
 */
data class ScannedDoc(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val dateShamsi: String,
    val pageCount: Int = 1,
    val fileSizeFormatted: String = "۱.۲ مگابایت",
    val thumbnailUri: String? = null,
    val imagePath: String? = null,
    val activeFilter: DocFilterType = DocFilterType.PHOTOCOPY,
    val isFavorite: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)
