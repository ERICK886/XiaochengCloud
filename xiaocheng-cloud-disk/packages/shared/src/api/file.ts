import { apiClient } from './client';
import type {
  FileItem,
  FileListRequest,
  FileListResponse,
  FileOperationRequest,
  FileUploadProgress,
  ShareItem,
  ShareCreateRequest,
  ShareListRequest,
  SearchRequest,
} from '../entities/file';
import type { ApiResponse, PaginatedResponse } from '../index';

export const fileApi = {
  async list(params: FileListRequest): Promise<ApiResponse<FileListResponse>> {
    return apiClient.get('/files', params);
  },

  async getFile(id: string): Promise<ApiResponse<FileItem>> {
    return apiClient.get(`/files/${id}`);
  },

  async createFolder(name: string, parentId?: string): Promise<ApiResponse<FileItem>> {
    return apiClient.post('/files/folder', { name, parentId });
  },

  async rename(id: string, filename: string): Promise<ApiResponse<FileItem>> {
    return apiClient.put(`/files/${id}/rename`, { filename });
  },

  async move(request: FileOperationRequest): Promise<ApiResponse<void>> {
    return apiClient.post('/files/move', request);
  },

  async copy(request: FileOperationRequest): Promise<ApiResponse<void>> {
    return apiClient.post('/files/copy', request);
  },

  async delete(fileIds: string[]): Promise<ApiResponse<void>> {
    return apiClient.delete('/files', { ids: fileIds });
  },

  async permanentlyDelete(fileIds: string[]): Promise<ApiResponse<void>> {
    return apiClient.delete('/files/permanent', { ids: fileIds });
  },

  async restore(fileIds: string[]): Promise<ApiResponse<void>> {
    return apiClient.post('/files/restore', { ids: fileIds });
  },

  async star(fileIds: string[]): Promise<ApiResponse<void>> {
    return apiClient.post('/files/star', { ids: fileIds });
  },

  async unstar(fileIds: string[]): Promise<ApiResponse<void>> {
    return apiClient.post('/files/unstar', { ids: fileIds });
  },

  async search(params: SearchRequest): Promise<ApiResponse<PaginatedResponse<FileItem>>> {
    return apiClient.get('/files/search', params);
  },

  async getRecentFiles(limit: number = 10): Promise<ApiResponse<FileItem[]>> {
    return apiClient.get('/files/recent', { limit });
  },

  async getStarredFiles(): Promise<ApiResponse<FileItem[]>> {
    return apiClient.get('/files/starred');
  },

  async getDownloadUrl(id: string): Promise<ApiResponse<{ url: string }>> {
    return apiClient.get(`/files/${id}/download`);
  },

  async getThumbnail(id: string): Promise<ApiResponse<{ thumbnail: string }>> {
    return apiClient.get(`/files/${id}/thumbnail`);
  },
};

export const shareApi = {
  async createShare(request: ShareCreateRequest): Promise<ApiResponse<ShareItem>> {
    return apiClient.post('/shares', request);
  },

  async getShare(id: string): Promise<ApiResponse<ShareItem>> {
    return apiClient.get(`/shares/${id}`);
  },

  async getShareByUrl(url: string, password?: string): Promise<ApiResponse<ShareItem & { file: FileItem }>> {
    return apiClient.post('/shares/url', { url, password });
  },

  async listShares(params: ShareListRequest): Promise<ApiResponse<PaginatedResponse<ShareItem>>> {
    return apiClient.get('/shares', params);
  },

  async updateShare(id: string, data: Partial<ShareCreateRequest>): Promise<ApiResponse<ShareItem>> {
    return apiClient.put(`/shares/${id}`, data);
  },

  async deleteShare(id: string): Promise<ApiResponse<void>> {
    return apiClient.delete(`/shares/${id}`);
  },

  async saveToMyDisk(shareId: string, targetFolderId?: string): Promise<ApiResponse<void>> {
    return apiClient.post(`/shares/${shareId}/save`, { targetFolderId });
  },

  async getShareRecords(id: string): Promise<ApiResponse<{ views: any[]; downloads: any[] }>> {
    return apiClient.get(`/shares/${id}/records`);
  },
};
