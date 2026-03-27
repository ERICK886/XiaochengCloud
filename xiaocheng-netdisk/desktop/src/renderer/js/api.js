const API_BASE_URL = 'http://localhost:8080/api';

class ApiClient {
  constructor() {
    this.token = null;
  }

  async request(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers,
    };

    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`;
    }

    try {
      const response = await fetch(url, {
        ...options,
        headers,
      });

      const data = await response.json();
      return data;
    } catch (error) {
      console.error('API request failed:', error);
      throw error;
    }
  }

  async get(endpoint, params = {}) {
    const queryString = new URLSearchParams(params).toString();
    const url = queryString ? `${endpoint}?${queryString}` : endpoint;
    return this.request(url, { method: 'GET' });
  }

  async post(endpoint, data = {}) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async put(endpoint, data = {}) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async delete(endpoint, params = {}) {
    const queryString = new URLSearchParams(params).toString();
    const url = queryString ? `${endpoint}?${queryString}` : endpoint;
    return this.request(url, { method: 'DELETE' });
  }

  setToken(token) {
    this.token = token;
    if (window.electronAPI) {
      window.electronAPI.setStoreValue('token', token);
    }
  }

  async loadToken() {
    if (window.electronAPI) {
      this.token = await window.electronAPI.getStoreValue('token');
    }
    return this.token;
  }

  clearToken() {
    this.token = null;
    if (window.electronAPI) {
      window.electronAPI.setStoreValue('token', null);
    }
  }
}

const api = new ApiClient();

const userAPI = {
  login: (username, password) => api.post('/user/login', { username, password }),
  register: (username, password, phone) => api.post('/user/register', { username, password, phone }),
  logout: () => api.post('/user/logout'),
  getInfo: () => api.get('/user/info'),
};

const fileAPI = {
  list: (params) => api.get('/file/list', params),
  recent: (limit = 10) => api.get('/file/recent', { limit }),
  favorite: () => api.get('/file/favorite'),
  upload: '/api/file/upload',
  createFolder: (folderName, parentId) => api.post('/file/folder', { folderName, parentId }),
  delete: (fileId) => api.delete('/file/delete', { fileId }),
  move: (fileId, targetParentId) => api.put('/file/move', { fileId, targetParentId }),
  rename: (fileId, newName) => api.put('/file/rename', { fileId, newName }),
  favorite: (fileId) => api.post('/file/favorite', { fileId }),
  unfavorite: (fileId) => api.post('/file/unfavorite', { fileId }),
};

const shareAPI = {
  create: (fileIds, shareType, password, expireDays) => 
    api.post('/share/create', { fileIds, shareType, password, expireDays }),
  list: () => api.get('/share/list'),
  cancel: (shareId) => api.delete('/share/cancel', { shareId }),
};

const recycleAPI = {
  list: () => api.get('/recycle/list'),
  restore: (recycleId) => api.post('/recycle/restore', { recycleId }),
  delete: (recycleId) => api.delete('/recycle/delete', { recycleId }),
  empty: () => api.delete('/recycle/empty'),
};

const backupAPI = {
  list: () => api.get('/backup/task/list'),
  create: (taskType, sourcePath, targetPath, backupMode, qualityMode) =>
    api.post('/backup/task', { taskType, sourcePath, targetPath, backupMode, qualityMode }),
  updateStatus: (taskId, status) => api.put('/backup/task/status', { taskId, status }),
};
