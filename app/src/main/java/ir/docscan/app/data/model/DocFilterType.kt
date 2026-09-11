package ir.docscan.app.data.model

/**
 * Filter types for processing scanned documents into clean photocopies and prints.
 */
enum class DocFilterType(
    val id: String,
    val titleFa: String,
    val descriptionFa: String,
    val iconName: String
) {
    ORIGINAL(
        id = "original",
        titleFa = "اصلی",
        descriptionFa = "تصویر بدون هیچ‌گونه فیلتر یا تغییر رنگ",
        iconName = "image"
    ),
    PHOTOCOPY(
        id = "photocopy",
        titleFa = "فتوکپی پرکنتراست",
        descriptionFa = "بهینه‌سازی حداکثری متن و پس‌زمینه سفید برای کپی و چاپ کاغذی",
        iconName = "content_copy"
    ),
    BW_OFFICE(
        id = "bw_office",
        titleFa = "سیاه و سفید اداری",
        descriptionFa = "تبدیل متن به سیاه مطلق و کاغذ به سفید خالص با حذف نویز",
        iconName = "contrast"
    ),
    WHITEBOARD(
        id = "whiteboard",
        titleFa = "وایتبورد / حذف سایه",
        descriptionFa = "روشن‌سازی یکنواخت نور محیطی و حذف سایه دست و گوشی",
        iconName = "wb_sunny"
    ),
    MAGIC_COLOR(
        id = "magic_color",
        titleFa = "رنگی جادویی",
        descriptionFa = "تقویت و شفاف‌سازی رنگ‌های مهر، امضا و سربرگ همزمان با پاکسازی زمینه",
        iconName = "auto_fix_high"
    )
}
