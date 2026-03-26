export interface FileItem {
  id: string;
  userId: string;
  parentId: string | null;
  filename: string;
  filePath: string;
  fileSize: number;
  fileType: FileType;
  mimeType?: string;
  thumbnail?: string;
  isFolder: boolean;
  isDeleted: boolean;
  deletedAt?: string;
  isStarred: boolean;
  createdAt: string;
  updatedAt: string;
}

export type FileType = 
  | 'document' 
  | 'image' 
  | 'video' 
  | 'audio' 
  | 'archive' 
  | 'application'
  | 'folder';

export interface FileUploadProgress {
  fileId: string;
  filename: string;
  totalSize: number;
  uploadedSize: number;
  status: 'pending' | 'uploading' | 'paused' | 'completed' | 'failed';
  error?: string;
}

export interface FileListRequest {
  parentId?: string | null;
  search?: string;
  fileType?: FileType;
  sortBy?: 'name' | 'size' | 'createdAt' | 'updatedAt';
  sortOrder?: 'asc' | 'desc';
  page?: number;
  pageSize?: number;
}

export interface FileListResponse {
  items: FileItem[];
  total: number;
  page: number;
  pageSize: number;
}

export interface FileOperationRequest {
  fileIds: string[];
  targetParentId?: string;
  filename?: string;
}

export type ShareType = 'public' | 'password' | 'private';
export type SharePermission = 'view' | 'download';

export interface ShareItem {
  id: string;
  fileId: string;
  fileName: string;
  shareType: ShareType;
  shareUrl: string;
  sharePassword?: string;
  permission: SharePermission;
  expiresAt?: string;
  createdBy: string;
  viewCount: number;
  downloadCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface ShareCreateRequest {
  fileIds: string[];
  shareType: ShareType;
  password?: string;
  permission?: SharePermission;
  expiresAt?: string;
}

export interface ShareListRequest {
  fileType?: FileType;
  sortBy?: 'createdAt' | 'expiresAt';
  sortOrder?: 'asc' | 'desc';
  page?: number;
  pageSize?: number;
}
