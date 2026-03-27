const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('electronAPI', {
  selectFiles: () => ipcRenderer.invoke('select-files'),
  selectFolder: () => ipcRenderer.invoke('select-folder'),
  saveFile: (options) => ipcRenderer.invoke('save-file', options),
  showItemInFolder: (filePath) => ipcRenderer.invoke('show-item-in-folder', filePath),
  
  onFilesSelected: (callback) => {
    ipcRenderer.on('files-selected', (event, filePaths) => callback(filePaths));
  },
  onCreateFolder: (callback) => {
    ipcRenderer.on('create-folder', () => callback());
  },
  onFocusSearch: (callback) => {
    ipcRenderer.on('focus-search', () => callback());
  },
  onUploadProgress: (callback) => {
    ipcRenderer.on('upload-progress', (event, progress) => callback(progress));
  },
});
