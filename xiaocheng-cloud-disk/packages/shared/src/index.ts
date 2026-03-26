export * from './user';
export * from './file';
export * from './backup';

export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
}

export interface PaginatedResponse<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
}

export interface UploadChunkRequest {
  fileId: string;
  chunkIndex: number;
  totalChunks: number;
  chunkSize: number;
  filename: string;
  totalSize: number;
  parentId?: string;
}

export interface UploadChunkResponse {
  fileId: string;
  chunkIndex: number;
  uploaded: boolean;
}

export interface MergeChunksRequest {
  fileId: string;
  filename: string;
  totalSize: number;
  parentId?: string;
}

export interface StorageInfo {
  used: number;
  limit: number;
  usedPercentage: number;
  fileCount: number;
}

export interface FileVersion {
  id: string;
  fileId: string;
  versionId: string;
  versionPath: string;
  fileSize: number;
  createdAt: string;
}

export type SortField = 'name' | 'size' | 'createdAt' | 'updatedAt';
export type SortOrder = 'asc' | 'desc';

export interface SearchRequest {
  keyword: string;
  fileType?: string;
  parentId?: string | null;
  page?: number;
  pageSize?: number;
}
