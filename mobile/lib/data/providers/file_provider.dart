import 'package:flutter/material.dart';
import '../network/api_client.dart';
import '../models/file_item.dart';
import '../../core/constants/constants.dart';

class FileProvider with ChangeNotifier {
  final ApiClient _apiClient = ApiClient();
  
  List<FileItem> _files = [];
  List<FileItem> _recentFiles = [];
  bool _isLoading = false;
  String? _error;
  int? _currentParentId;

  List<FileItem> get files => _files;
  List<FileItem> get recentFiles => _recentFiles;
  bool get isLoading => _isLoading;
  String? get error => _error;
  int? get currentParentId => _currentParentId;

  Future<void> loadFiles({int? parentId}) async {
    _isLoading = true;
    _error = null;
    _currentParentId = parentId;
    notifyListeners();

    try {
      final response = await _apiClient.get(
        ApiConstants.fileList,
        params: parentId != null ? {'parentId': parentId} : null,
      );
      
      if (response.data['code'] == 200) {
        final List<dynamic> data = response.data['data'] ?? [];
        _files = data.map((json) => FileItem.fromJson(json)).toList();
      } else {
        _error = response.data['message'];
      }
    } catch (e) {
      _error = '加载文件列表失败: $e';
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> loadRecentFiles({int limit = 20}) async {
    try {
      final response = await _apiClient.get(
        ApiConstants.recentFiles,
        params: {'limit': limit},
      );
      
      if (response.data['code'] == 200) {
        final List<dynamic> data = response.data['data'] ?? [];
        _recentFiles = data.map((json) => FileItem.fromJson(json)).toList();
        notifyListeners();
      }
    } catch (e) {
      _error = '加载最近文件失败: $e';
    }
  }

  Future<bool> createFolder(String folderName, {int? parentId}) async {
    try {
      final response = await _apiClient.post(
        ApiConstants.createFolder,
        data: {
          'folderName': folderName,
          'parentId': parentId ?? _currentParentId,
        },
      );
      
      if (response.data['code'] == 200) {
        await loadFiles(parentId: _currentParentId);
        return true;
      } else {
        _error = response.data['message'];
        notifyListeners();
        return false;
      }
    } catch (e) {
      _error = '创建文件夹失败: $e';
      notifyListeners();
      return false;
    }
  }

  Future<bool> deleteFile(int fileId) async {
    try {
      final response = await _apiClient.delete(
        ApiConstants.deleteFile,
        params: {'fileId': fileId},
      );
      
      if (response.data['code'] == 200) {
        _files.removeWhere((f) => f.id == fileId);
        notifyListeners();
        return true;
      } else {
        _error = response.data['message'];
        notifyListeners();
        return false;
      }
    } catch (e) {
      _error = '删除文件失败: $e';
      notifyListeners();
      return false;
    }
  }

  Future<bool> renameFile(int fileId, String newName) async {
    try {
      final response = await _apiClient.put(
        ApiConstants.renameFile,
        data: {'fileId': fileId, 'newName': newName},
      );
      
      if (response.data['code'] == 200) {
        await loadFiles(parentId: _currentParentId);
        return true;
      } else {
        _error = response.data['message'];
        notifyListeners();
        return false;
      }
    } catch (e) {
      _error = '重命名失败: $e';
      notifyListeners();
      return false;
    }
  }

  Future<bool> moveFile(int fileId, int targetParentId) async {
    try {
      final response = await _apiClient.put(
        ApiConstants.moveFile,
        data: {'fileId': fileId, 'targetParentId': targetParentId},
      );
      
      if (response.data['code'] == 200) {
        await loadFiles(parentId: _currentParentId);
        return true;
      } else {
        _error = response.data['message'];
        notifyListeners();
        return false;
      }
    } catch (e) {
      _error = '移动文件失败: $e';
      notifyListeners();
      return false;
    }
  }

  Future<List<FileItem>> searchFiles(String keyword) async {
    try {
      final response = await _apiClient.get(
        ApiConstants.searchFile,
        params: {'keyword': keyword},
      );
      
      if (response.data['code'] == 200) {
        final List<dynamic> data = response.data['data'] ?? [];
        return data.map((json) => FileItem.fromJson(json)).toList();
      } else {
        _error = response.data['message'];
        notifyListeners();
        return [];
      }
    } catch (e) {
      _error = '搜索失败: $e';
      notifyListeners();
      return [];
    }
  }

  Future<bool> toggleFavorite(int fileId, bool isFavorite) async {
    final api = isFavorite ? ApiConstants.unfavorite : ApiConstants.favorite;
    try {
      final response = await _apiClient.post(api, data: {'fileId': fileId});
      if (response.data['code'] == 200) {
        return true;
      } else {
        _error = response.data['message'];
        notifyListeners();
        return false;
      }
    } catch (e) {
      _error = '操作失败: $e';
      notifyListeners();
      return false;
    }
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }
}
