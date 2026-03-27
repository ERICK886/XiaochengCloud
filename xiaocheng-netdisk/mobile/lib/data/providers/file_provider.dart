import 'package:flutter/foundation.dart';
import '../../core/constants/api_constants.dart';
import '../../core/utils/http_util.dart';
import '../models/file_item.dart';

class FileProvider with ChangeNotifier {
  final HttpUtil _http = HttpUtil();

  List<FileItem> _files = [];
  List<FileItem> _recentFiles = [];
  List<FileItem> _favoriteFiles = [];
  bool _isLoading = false;
  Long? _currentParentId;
  String? _error;

  List<FileItem> get files => _files;
  List<FileItem> get recentFiles => _recentFiles;
  List<FileItem> get favoriteFiles => _favoriteFiles;
  bool get isLoading => _isLoading;
  Long? get currentParentId => _currentParentId;
  String? get error => _error;

  Future<void> loadFiles({Long? parentId, String? keyword, String? fileType}) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final response = await _http.get(ApiConstants.fileList, params: {
        if (parentId != null) 'parentId': parentId,
        if (keyword != null) 'keyword': keyword,
        if (fileType != null) 'fileType': fileType,
        'page': 1,
        'pageSize': 100,
      });

      if (response.data['code'] == 200) {
        final List<dynamic> records = response.data['data']['records'];
        _files = records.map((json) => FileItem.fromJson(json)).toList();
        _currentParentId = parentId;
      } else {
        _error = response.data['message'];
      }
    } catch (e) {
      _error = e.toString();
    }

    _isLoading = false;
    notifyListeners();
  }

  Future<void> loadRecentFiles({int limit = 10}) async {
    try {
      final response = await _http.get(ApiConstants.fileRecent, params: {'limit': limit});
      if (response.data['code'] == 200) {
        final List<dynamic> records = response.data['data'];
        _recentFiles = records.map((json) => FileItem.fromJson(json)).toList();
        notifyListeners();
      }
    } catch (e) {
    }
  }

  Future<void> loadFavoriteFiles() async {
    try {
      final response = await _http.get(ApiConstants.fileFavorite);
      if (response.data['code'] == 200) {
        final List<dynamic> records = response.data['data'];
        _favoriteFiles = records.map((json) => FileItem.fromJson(json)).toList();
        notifyListeners();
      }
    } catch (e) {
    }
  }

  Future<bool> uploadFile(String filePath, {String? fileName, Long? parentId}) async {
    try {
      final response = await _http.uploadFile(
        ApiConstants.fileUpload,
        filePath,
        fileName: fileName ?? filePath.split('/').last,
        parentId: parentId ?? _currentParentId,
      );
      if (response.data['code'] == 200) {
        await loadFiles(parentId: _currentParentId);
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> createFolder(String folderName) async {
    try {
      final response = await _http.post(ApiConstants.createFolder, data: {
        'folderName': folderName,
        if (_currentParentId != null) 'parentId': _currentParentId,
      });
      if (response.data['code'] == 200) {
        await loadFiles(parentId: _currentParentId);
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> deleteFile(Long fileId) async {
    try {
      final response = await _http.delete(ApiConstants.fileDelete, params: {'fileId': fileId});
      if (response.data['code'] == 200) {
        await loadFiles(parentId: _currentParentId);
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> moveFile(Long fileId, Long targetParentId) async {
    try {
      final response = await _http.put(ApiConstants.fileMove, data: {
        'fileId': fileId,
        'targetParentId': targetParentId,
      });
      if (response.data['code'] == 200) {
        await loadFiles(parentId: _currentParentId);
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> renameFile(Long fileId, String newName) async {
    try {
      final response = await _http.put(ApiConstants.fileRename, data: {
        'fileId': fileId,
        'newName': newName,
      });
      if (response.data['code'] == 200) {
        await loadFiles(parentId: _currentParentId);
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> favoriteFile(Long fileId) async {
    try {
      final response = await _http.post(ApiConstants.fileFavorite, data: {'fileId': fileId});
      if (response.data['code'] == 200) {
        await loadFiles(parentId: _currentParentId);
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> unfavoriteFile(Long fileId) async {
    try {
      final response = await _http.post(ApiConstants.fileUnfavorite, data: {'fileId': fileId});
      if (response.data['code'] == 200) {
        await loadFiles(parentId: _currentParentId);
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  void navigateToFolder(Long? folderId) {
    loadFiles(parentId: folderId);
  }

  void navigateBack() {
    loadFiles(parentId: _currentParentId);
  }
}
