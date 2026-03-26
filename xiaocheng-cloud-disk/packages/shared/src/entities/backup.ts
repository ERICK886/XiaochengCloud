export interface BackupTask {
  id: string;
  userId: string;
  name: string;
  backupType: BackupType;
  backupPath: string;
  localPath?: string;
  isEnabled: boolean;
  lastBackupAt?: string;
  status: BackupStatus;
  totalFiles: number;
  completedFiles: number;
  createdAt: string;
  updatedAt: string;
}

export type BackupType = 'photo' | 'video' | 'folder' | 'custom';
export type BackupStatus = 'idle' | 'running' | 'paused' | 'error' | 'completed';

export interface BackupCreateRequest {
  name: string;
  backupType: BackupType;
  backupPath: string;
  localPath?: string;
}

export interface BackupUpdateRequest {
  name?: string;
  backupPath?: string;
  localPath?: string;
  isEnabled?: boolean;
}

export interface BackupProgress {
  taskId: string;
  status: BackupStatus;
  totalFiles: number;
  completedFiles: number;
  currentFile?: string;
  error?: string;
}
