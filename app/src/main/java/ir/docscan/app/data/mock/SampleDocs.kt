package ir.docscan.app.data.mock

import ir.docscan.app.data.model.DocFilterType
import ir.docscan.app.data.model.ScannedDoc

object SampleDocs {
    val initialDocuments: List<ScannedDoc> = listOf(
        ScannedDoc(
            id = "doc-1",
            title = "قرارداد اجاره‌نامه مسکونی",
            dateShamsi = "۱۴۰۳/۰۶/۱۵",
            pageCount = 3,
            fileSizeFormatted = "۱.۸ مگابایت",
            thumbnailUri = null,
            activeFilter = DocFilterType.PHOTOCOPY,
            isFavorite = true
        ),
        ScannedDoc(
            id = "doc-2",
            title = "کارت ملی هوشمند و شناسنامه",
            dateShamsi = "۱۴۰۳/۰۶/۱۱",
            pageCount = 2,
            fileSizeFormatted = "۸۵۰ کیلوبایت",
            thumbnailUri = null,
            activeFilter = DocFilterType.MAGIC_COLOR,
            isFavorite = false
        ),
        ScannedDoc(
            id = "doc-3",
            title = "صورتحساب و فاکتور تجهیزات اداری",
            dateShamsi = "۱۴۰۳/۰۵/۲۸",
            pageCount = 1,
            fileSizeFormatted = "۶۲۰ کیلوبایت",
            thumbnailUri = null,
            activeFilter = DocFilterType.BW_OFFICE,
            isFavorite = false
        ),
        ScannedDoc(
            id = "doc-4",
            title = "تخته وایت‌بورد جلسه استراتژی محصول",
            dateShamsi = "۱۴۰۳/۰۵/۱۴",
            pageCount = 4,
            fileSizeFormatted = "۳.۲ مگابایت",
            thumbnailUri = null,
            activeFilter = DocFilterType.WHITEBOARD,
            isFavorite = true
        )
    )
}
