class App {
  constructor() {
    this.currentPage = 'files';
    this.currentFolderId = null;
    this.files = [];
    this.selectedFiles = new Set();
    this.user = null;
    this.isLoggedIn = false;
  }

  async init() {
    await this.checkAuth();
    this.bindEvents();
  }

  async checkAuth() {
    await api.loadToken();
    if (api.token) {
      try {
        const response = await userAPI.getInfo();
        if (response.code === 200) {
          this.user = response.data;
          this.isLoggedIn = true;
          this.showMainUI();
          this.loadFiles();
        } else {
          this.showLoginUI();
        }
      } catch (e) {
        this.showLoginUI();
      }
    } else {
      this.showLoginUI();
    }
  }

  showLoginUI() {
    document.getElementById('loginModal').classList.remove('hidden');
    document.querySelector('.sidebar').classList.add('hidden');
    document.querySelector('.main-content').classList.add('hidden');
  }

  showMainUI() {
    document.getElementById('loginModal').classList.add('hidden');
    document.querySelector('.sidebar').classList.remove('hidden');
    document.querySelector('.main-content').classList.remove('hidden');
    this.updateStorageInfo();
  }

  updateStorageInfo() {
    if (this.user) {
      const usedGB = (this.user.usedCapacity / (1024 * 1024 * 1024)).toFixed(2);
      const totalGB = (this.user.totalCapacity / (1024 * 1024 * 1024)).toFixed(2);
      const percentage = (this.user.usedCapacity / this.user.totalCapacity) * 100;
      
      document.getElementById('storageText').textContent = `${usedGB} GB / ${totalGB} GB`;
      document.getElementById('storageBarFill').style.width = `${percentage}%`;
    }
  }

  bindEvents() {
    document.getElementById('loginForm').addEventListener('submit', (e) => this.handleLogin(e));
    document.getElementById('btnLogout').addEventListener('click', () => this.handleLogout());
    document.getElementById('btnUpload').addEventListener('click', () => this.handleUpload());
    document.getElementById('btnNewFolder').addEventListener('click', () => this.handleNewFolder());
    document.getElementById('btnBack').addEventListener('click', () => this.navigateBack());
    document.getElementById('selectAll').addEventListener('change', (e) => this.handleSelectAll(e));
    document.getElementById('searchBtn').addEventListener('click', () => this.handleSearch());
    document.getElementById('searchInput').addEventListener('keypress', (e) => {
      if (e.key === 'Enter') this.handleSearch();
    });
    document.getElementById('btnDelete').addEventListener('click', () => this.handleDelete());
    document.getElementById('btnShare').addEventListener('click', () => this.handleShare());

    document.querySelectorAll('.nav-item').forEach(item => {
      item.addEventListener('click', (e) => this.switchPage(e.currentTarget.dataset.page));
    });

    document.getElementById('closeUploadPanel').addEventListener('click', () => {
      document.getElementById('uploadPanel').classList.remove('show');
    });
  }

  async handleLogin(e) {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
      const response = await userAPI.login(username, password);
      if (response.code === 200) {
        api.setToken(response.data.token);
        this.user = {
          id: response.data.userId,
          username: response.data.username,
          totalCapacity: response.data.totalCapacity,
          usedCapacity: response.data.usedCapacity,
        };
        this.isLoggedIn = true;
        this.showMainUI();
        this.loadFiles();
      } else {
        this.showToast(response.message || '登录失败', 'error');
      }
    } catch (e) {
      this.showToast('登录失败', 'error');
    }
  }

  async handleLogout() {
    try {
      await userAPI.logout();
    } catch (e) {}
    api.clearToken();
    this.user = null;
    this.isLoggedIn = false;
    this.showLoginUI();
  }

  async loadFiles() {
    try {
      const params = {};
      if (this.currentFolderId) {
        params.parentId = this.currentFolderId;
      }
      
      const response = await fileAPI.list(params);
      if (response.code === 200) {
        this.files = response.data.records || [];
        this.renderFileList();
      }
    } catch (e) {
      this.showToast('加载文件失败', 'error');
    }
  }

  renderFileList() {
    const tbody = document.getElementById('fileListBody');
    
    if (this.files.length === 0) {
      tbody.innerHTML = `
        <div class="empty-state">
          <div class="empty-state-icon">📂</div>
          <div class="empty-state-text">文件夹为空</div>
        </div>
      `;
      return;
    }

    tbody.innerHTML = this.files.map(file => `
      <div class="file-item ${this.selectedFiles.has(file.id) ? 'selected' : ''}" data-id="${file.id}">
        <div class="file-name">
          <span class="file-icon">${this.getFileIcon(file)}</span>
          <span class="file-name-text">${file.fileName}</span>
        </div>
        <div class="file-size">${file.isFolder ? '--' : this.formatSize(file.fileSize)}</div>
        <div class="file-time">${this.formatDate(file.updateTime)}</div>
        <div class="file-actions">
          <button class="file-action-btn" title="收藏" data-action="favorite">⭐</button>
          <button class="file-action-btn" title="重命名" data-action="rename">✏️</button>
          <button class="file-action-btn" title="删除" data-action="delete">🗑️</button>
        </div>
      </div>
    `).join('');

    tbody.querySelectorAll('.file-item').forEach(item => {
      item.addEventListener('click', (e) => this.handleFileClick(e, item));
      item.addEventListener('dblclick', (e) => this.handleFileDoubleClick(e, item));
    });
  }

  getFileIcon(file) {
    if (file.isFolder) return '📁';
    const type = file.fileType?.toLowerCase();
    if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(type)) return '🖼️';
    if (['mp4', 'avi', 'mov', 'wmv', 'flv'].includes(type)) return '🎬';
    if (['mp3', 'wav', 'flac', 'aac'].includes(type)) return '🎵';
    if (['doc', 'docx', 'pdf', 'txt', 'rtf'].includes(type)) return '📄';
    if (['xls', 'xlsx'].includes(type)) return '📊';
    if (['ppt', 'pptx'].includes(type)) return '📽️';
    if (['zip', 'rar', '7z', 'tar', 'gz'].includes(type)) return '📦';
    return '📎';
  }

  formatSize(bytes) {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
    return (bytes / (1024 * 1024 * 1024)).toFixed(2) + ' GB';
  }

  formatDate(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleDateString('zh-CN');
  }

  handleFileClick(e, item) {
    const fileId = parseInt(item.dataset.id);
    
    if (e.target.closest('.file-actions')) {
      const action = e.target.closest('.file-action-btn').dataset.action;
      this.handleFileAction(action, fileId);
      return;
    }

    if (e.ctrlKey || e.metaKey) {
      if (this.selectedFiles.has(fileId)) {
        this.selectedFiles.delete(fileId);
      } else {
        this.selectedFiles.add(fileId);
      }
      item.classList.toggle('selected');
      this.updateToolbarState();
    } else {
      this.selectedFiles.clear();
      this.selectedFiles.add(fileId);
      document.querySelectorAll('.file-item').forEach(el => el.classList.remove('selected'));
      item.classList.add('selected');
      this.updateToolbarState();
    }
  }

  handleFileDoubleClick(e, item) {
    const fileId = parseInt(item.dataset.id);
    const file = this.files.find(f => f.id === fileId);
    if (file && file.isFolder) {
      this.currentFolderId = fileId;
      this.loadFiles();
      this.updateBreadcrumb();
    }
  }

  handleFileAction(action, fileId) {
    const file = this.files.find(f => f.id === fileId);
    
    switch (action) {
      case 'favorite':
        if (file.isFavorite) {
          fileAPI.unfavorite(fileId);
        } else {
          fileAPI.favorite(fileId);
        }
        this.loadFiles();
        break;
      case 'rename':
        this.promptRename(file);
        break;
      case 'delete':
        this.confirmDelete([fileId]);
        break;
    }
  }

  handleSelectAll(e) {
    if (e.target.checked) {
      this.files.forEach(f => this.selectedFiles.add(f.id));
    } else {
      this.selectedFiles.clear();
    }
    document.querySelectorAll('.file-item').forEach(item => {
      item.classList.toggle('selected', e.target.checked);
    });
    this.updateToolbarState();
  }

  updateToolbarState() {
    const hasSelection = this.selectedFiles.size > 0;
    document.getElementById('btnDownload').disabled = !hasSelection;
    document.getElementById('btnShare').disabled = !hasSelection;
    document.getElementById('btnDelete').disabled = !hasSelection;
    document.getElementById('btnMove').disabled = !hasSelection;
  }

  async handleUpload() {
    if (!window.electronAPI) return;
    
    const filePaths = await window.electronAPI.selectFile();
    if (!filePaths || filePaths.length === 0) return;

    document.getElementById('uploadPanel').classList.add('show');
    const uploadList = document.getElementById('uploadList');

    for (const filePath of filePaths) {
      const fileName = filePath.split(/[/\\]/).pop();
      const itemId = 'upload-' + Date.now() + Math.random();
      
      uploadList.innerHTML += `
        <div class="upload-item" id="${itemId}">
          <span class="upload-item-icon">📄</span>
          <div class="upload-item-info">
            <div class="upload-item-name">${fileName}</div>
            <div class="upload-item-progress">准备上传...</div>
          </div>
        </div>
      `;

      try {
        const formData = new FormData();
        formData.append('file', await fetch(filePath).then(r => r.blob()), fileName);
        if (this.currentFolderId) {
          formData.append('parentId', this.currentFolderId);
        }

        const response = await fetch('http://localhost:8080/api/file/upload', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${api.token}`,
          },
          body: formData,
        });

        const result = await response.json();
        const progressEl = document.querySelector(`#${itemId} .upload-item-progress`);
        if (result.code === 200) {
          progressEl.textContent = '上传成功';
          progressEl.classList.add('success');
        } else {
          progressEl.textContent = '上传失败';
          progressEl.classList.add('error');
        }
      } catch (e) {
        const progressEl = document.querySelector(`#${itemId} .upload-item-progress`);
        progressEl.textContent = '上传失败';
        progressEl.classList.add('error');
      }
    }

    this.loadFiles();
  }

  async handleNewFolder() {
    const folderName = prompt('请输入文件夹名称：');
    if (!folderName) return;

    try {
      const response = await fileAPI.createFolder(folderName, this.currentFolderId);
      if (response.code === 200) {
        this.showToast('文件夹创建成功', 'success');
        this.loadFiles();
      } else {
        this.showToast(response.message || '创建失败', 'error');
      }
    } catch (e) {
      this.showToast('创建失败', 'error');
    }
  }

  async handleDelete() {
    if (this.selectedFiles.size === 0) return;
    this.confirmDelete(Array.from(this.selectedFiles));
  }

  async confirmDelete(fileIds) {
    if (!confirm(`确定要删除选中的 ${fileIds.length} 个项目吗？`)) return;

    try {
      for (const fileId of fileIds) {
        await fileAPI.delete(fileId);
      }
      this.showToast('删除成功', 'success');
      this.selectedFiles.clear();
      this.loadFiles();
      this.updateToolbarState();
    } catch (e) {
      this.showToast('删除失败', 'error');
    }
  }

  async handleShare() {
    if (this.selectedFiles.size === 0) return;
    
    const shareType = prompt('分享类型：1-公开链接 2-密码链接', '1');
    const password = shareType === '2' ? prompt('请输入分享密码：') : null;
    
    try {
      const response = await shareAPI.create(
        Array.from(this.selectedFiles),
        parseInt(shareType),
        password,
        null
      );
      if (response.code === 200) {
        const shareUrl = `xiaocheng://share/${response.data.shareCode}`;
        if (navigator.clipboard) {
          navigator.clipboard.writeText(shareUrl);
        }
        this.showToast(`分享链接已复制: ${shareUrl}`, 'success');
      } else {
        this.showToast(response.message || '分享失败', 'error');
      }
    } catch (e) {
      this.showToast('分享失败', 'error');
    }
  }

  promptRename(file) {
    const newName = prompt('请输入新名称：', file.fileName);
    if (!newName || newName === file.fileName) return;

    fileAPI.rename(file.id, newName).then(response => {
      if (response.code === 200) {
        this.showToast('重命名成功', 'success');
        this.loadFiles();
      } else {
        this.showToast(response.message || '重命名失败', 'error');
      }
    });
  }

  handleSearch() {
    const keyword = document.getElementById('searchInput').value.trim();
    if (!keyword) {
      this.loadFiles();
      return;
    }

    fileAPI.list({ keyword }).then(response => {
      if (response.code === 200) {
        this.files = response.data.records || [];
        this.renderFileList();
      }
    });
  }

  navigateBack() {
    if (this.currentFolderId) {
      this.currentFolderId = null;
      this.loadFiles();
      this.updateBreadcrumb();
    }
  }

  updateBreadcrumb() {
    const breadcrumb = document.getElementById('breadcrumb');
    if (this.currentFolderId) {
      breadcrumb.innerHTML = `
        <span class="breadcrumb-item" onclick="app.navigateToRoot()">全部文件</span>
        <span class="breadcrumb-item">当前文件夹</span>
      `;
    } else {
      breadcrumb.innerHTML = '<span class="breadcrumb-item">全部文件</span>';
    }
  }

  navigateToRoot() {
    this.currentFolderId = null;
    this.loadFiles();
    this.updateBreadcrumb();
  }

  switchPage(page) {
    this.currentPage = page;
    document.querySelectorAll('.nav-item').forEach(item => {
      item.classList.toggle('active', item.dataset.page === page);
    });

    switch (page) {
      case 'favorite':
        this.loadFavorites();
        break;
      case 'recycle':
        this.loadRecycle();
        break;
      case 'share':
        this.loadShares();
        break;
      default:
        this.loadFiles();
    }
  }

  async loadFavorites() {
    try {
      const response = await fileAPI.favorite();
      if (response.code === 200) {
        this.files = response.data || [];
        this.renderFileList();
      }
    } catch (e) {
      this.showToast('加载失败', 'error');
    }
  }

  async loadRecycle() {
    try {
      const response = await recycleAPI.list();
      if (response.code === 200) {
        this.files = response.data.map(r => ({
          id: r.fileId,
          fileName: r.fileName,
          fileSize: r.fileSize,
          updateTime: r.deleteTime,
          isFolder: false,
        }));
        this.renderFileList();
      }
    } catch (e) {
      this.showToast('加载失败', 'error');
    }
  }

  async loadShares() {
    try {
      const response = await shareAPI.list();
      if (response.code === 200) {
        this.showToast('分享记录加载成功', 'success');
      }
    } catch (e) {
      this.showToast('加载失败', 'error');
    }
  }

  showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
  }
}

const app = new App();
document.addEventListener('DOMContentLoaded', () => app.init());
