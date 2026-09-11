import { DocumentItem, FilterOption } from '../types';

export const FILTER_OPTIONS: FilterOption[] = [
  {
    id: 'original',
    titleFa: 'اصلی',
    descFa: 'تصویر بدون فیلتر با رنگ واقعی عکس‌برداری',
    iconName: 'Image'
  },
  {
    id: 'photocopy',
    titleFa: 'فتوکپی پرکنتراست',
    descFa: 'حداکثر وضوح متن و حذف خاکستری برای کپی و چاپ کاغذی',
    iconName: 'Copy'
  },
  {
    id: 'bw_office',
    titleFa: 'سیاه و سفید اداری',
    descFa: 'تبدیل کامل به دورنگ سیاه و سفید بدون نویز کاغذ',
    iconName: 'Contrast'
  },
  {
    id: 'whiteboard',
    titleFa: 'وایتبورد / حذف سایه',
    descFa: 'حذف سایه دست و گوشی و یکدست‌سازی نور زمینه',
    iconName: 'Sun'
  },
  {
    id: 'magic_color',
    titleFa: 'رنگی جادویی',
    descFa: 'تقویت رنگ جوهر، مهرها و امضا با تمیزکاری کاغذ',
    iconName: 'Sparkles'
  }
];

export const INITIAL_DOCUMENTS: DocumentItem[] = [
  {
    id: 'doc-1',
    title: 'قرارداد اجاره‌نامه مسکونی',
    dateShamsi: '۱۴۰۳/۰۶/۱۵',
    pageCount: 3,
    fileSize: '۱.۸ مگابایت',
    activeFilter: 'photocopy',
    isFavorite: true,
    sampleImage: 'contract'
  },
  {
    id: 'doc-2',
    title: 'کارت ملی هوشمند و شناسنامه',
    dateShamsi: '۱۴۰۳/۰۶/۱۱',
    pageCount: 2,
    fileSize: '۸۵۰ کیلوبایت',
    activeFilter: 'magic_color',
    isFavorite: false,
    sampleImage: 'id_card'
  },
  {
    id: 'doc-3',
    title: 'صورتحساب و فاکتور تجهیزات اداری',
    dateShamsi: '۱۴۰۳/۰۵/۲۸',
    pageCount: 1,
    fileSize: '۶۲۰ کیلوبایت',
    activeFilter: 'bw_office',
    isFavorite: false,
    sampleImage: 'invoice'
  },
  {
    id: 'doc-4',
    title: 'تخته وایت‌بورد جلسه استراتژی محصول',
    dateShamsi: '۱۴۰۳/۰۵/۱۴',
    pageCount: 4,
    fileSize: '۳.۲ مگابایت',
    activeFilter: 'whiteboard',
    isFavorite: true,
    sampleImage: 'whiteboard'
  }
];
