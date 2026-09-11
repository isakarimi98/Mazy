export type FilterType = 
  | 'original'
  | 'photocopy'
  | 'bw_office'
  | 'whiteboard'
  | 'magic_color';

export interface FilterOption {
  id: FilterType;
  titleFa: string;
  descFa: string;
  iconName: string;
}

export interface DocumentItem {
  id: string;
  title: string;
  dateShamsi: string;
  pageCount: number;
  fileSize: string;
  activeFilter: FilterType;
  isFavorite: boolean;
  sampleImage: string;
}

export type AppScreen = 'home' | 'crop' | 'preview';

export interface CropPoint {
  x: number; // percentage 0 to 100
  y: number; // percentage 0 to 100
}

export interface CropCorners {
  topLeft: CropPoint;
  topRight: CropPoint;
  bottomRight: CropPoint;
  bottomLeft: CropPoint;
}
