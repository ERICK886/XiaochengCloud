export * from './client';
export * from './auth';
export * from './file';

export interface UploadOptions {
  file: File;
  parentId?: string;
  onProgress?: (progress: FileUploadProgress) => void;
  onComplete?: (file: FileItem) => void;
  onError?: (error: Error) => void;
}

export interface FileUploadProgress {
  fileId: string;
  filename: string;
  totalSize: number;
  uploadedSize: number;
  status: 'pending' | 'uploading' | 'paused' | 'completed' | 'failed';
  error?: string;
}

export interface FileItem {
  id: string;
  userId: string;
  parentId: string | null;
  filename: string;
  filePath: string;
  fileSize: number;
  fileType: string;
  mimeType?: string;
  thumbnail?: string;
  isFolder: boolean;
  isDeleted: boolean;
  deletedAt?: string;
  isStarred: boolean;
  createdAt: string;
  updatedAt: string;
}

const CHUNK_SIZE = 2 * 1024 * 1024;

export class FileUploader {
  private file: File;
  private parentId?: string;
  private onProgress?: (progress: FileUploadProgress) => void;
  private onComplete?: (file: FileItem) => void;
  private onError?: (error: Error) => void;
  private abortController?: AbortController;
  private isPaused = false;

  constructor(options: UploadOptions) {
    this.file = options.file;
    this.parentId = options.parentId;
    this.onProgress = options.onProgress;
    this.onComplete = options.onComplete;
    this.onError = options.onError;
  }

  async upload(): Promise<void> {
    const totalChunks = Math.ceil(this.file.size / CHUNK_SIZE);
    const fileId = this.generateFileId();

    this.reportProgress({
      fileId,
      filename: this.file.name,
      totalSize: this.file.size,
      uploadedSize: 0,
      status: 'uploading'
    });

    try {
      for (let i = 0; i < totalChunks; i++) {
        if (this.isPaused) {
          await this.waitForResume();
        }

        const start = i * CHUNK_SIZE;
        const end = Math.min(start + CHUNK_SIZE, this.file.size);
        const chunk = this.file.slice(start, end);

        const formData = new FormData();
        formData.append('fileId', fileId);
        formData.append('chunkIndex', String(i));
        formData.append('totalChunks', String(totalChunks));
        formData.append('chunkSize', String(chunk.size));
        formData.append('filename', this.file.name);
        formData.append('totalSize', String(this.file.size));
        if (this.parentId) {
          formData.append('parentId', this.parentId);
        }
        formData.append('chunk', chunk);

        await this.uploadChunk(formData, i, totalChunks);
      }

      await this.mergeChunks(fileId, totalChunks);

      this.reportProgress({
        fileId,
        filename: this.file.name,
        totalSize: this.file.size,
        uploadedSize: this.file.size,
        status: 'completed'
      });
    } catch (error) {
      const err = error instanceof Error ? error : new Error('Upload failed');
      this.reportProgress({
        fileId,
        filename: this.file.name,
        totalSize: this.file.size,
        uploadedSize: 0,
        status: 'failed',
        error: err.message
      });
      this.onError?.(err);
    }
  }

  pause(): void {
    this.isPaused = true;
  }

  resume(): void {
    this.isPaused = false;
  }

  cancel(): void {
    this.abortController?.abort();
    this.isPaused = true;
  }

  private generateFileId(): string {
    return `${Date.now()}-${Math.random().toString(36).substring(2, 11)}`;
  }

  private async uploadChunk(formData: FormData, chunkIndex: number, totalChunks: number): Promise<void> {
    this.abortController = new AbortController();

    const response = await fetch('/api/files/upload-chunk', {
      method: 'POST',
      body: formData,
      signal: this.abortController.signal
    });

    if (!response.ok) {
      throw new Error(`Chunk ${chunkIndex} upload failed: ${response.statusText}`);
    }

    const uploadedSize = Math.min((chunkIndex + 1) * CHUNK_SIZE, this.file.size);
    this.reportProgress({
      fileId: formData.get('fileId') as string,
      filename: this.file.name,
      totalSize: this.file.size,
      uploadedSize,
      status: 'uploading'
    });
  }

  private async mergeChunks(fileId: string, totalChunks: number): Promise<void> {
    const response = await fetch('/api/files/merge-chunks', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        fileId,
        filename: this.file.name,
        totalSize: this.file.size,
        parentId: this.parentId
      })
    });

    if (!response.ok) {
      throw new Error(`Merge chunks failed: ${response.statusText}`);
    }

    const result = await response.json();
    this.onComplete?.(result.data);
  }

  private waitForResume(): Promise<void> {
    return new Promise(resolve => {
      const check = () => {
        if (!this.isPaused) {
          resolve();
        } else {
          setTimeout(check, 100);
        }
      };
      check();
    });
  }

  private reportProgress(progress: FileUploadProgress): void {
    this.onProgress?.(progress);
  }
}
