import 'package:flutter/foundation.dart';
import '../../core/constants/api_constants.dart';
import '../../core/utils/http_util.dart';
import '../models/backup_task.dart';

class BackupProvider with ChangeNotifier {
  final HttpUtil _http = HttpUtil();

  List<BackupTask> _tasks = [];
  bool _isLoading = false;
  String? _error;

  List<BackupTask> get tasks => _tasks;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> loadTasks() async {
    _isLoading = true;
    notifyListeners();

    try {
      final response = await _http.get(ApiConstants.backupTaskList);
      if (response.data['code'] == 200) {
        final List<dynamic> records = response.data['data'];
        _tasks = records.map((json) => BackupTask.fromJson(json)).toList();
      } else {
        _error = response.data['message'];
      }
    } catch (e) {
      _error = e.toString();
    }

    _isLoading = false;
    notifyListeners();
  }

  Future<BackupTask?> createTask({
    required int taskType,
    String? sourcePath,
    String? targetPath,
    int backupMode = 1,
    int qualityMode = 1,
  }) async {
    try {
      final response = await _http.post(ApiConstants.backupTaskCreate, data: {
        'taskType': taskType,
        if (sourcePath != null) 'sourcePath': sourcePath,
        if (targetPath != null) 'targetPath': targetPath,
        'backupMode': backupMode,
        'qualityMode': qualityMode,
      });
      if (response.data['code'] == 200) {
        final task = BackupTask.fromJson(response.data['data']);
        await loadTasks();
        return task;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  Future<bool> updateTask(Long taskId, {int? backupMode, int? qualityMode}) async {
    try {
      final response = await _http.put(ApiConstants.backupTaskUpdate, params: {'taskId': taskId}, data: {
        if (backupMode != null) 'backupMode': backupMode,
        if (qualityMode != null) 'qualityMode': qualityMode,
      });
      if (response.data['code'] == 200) {
        await loadTasks();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> updateTaskStatus(Long taskId, int status) async {
    try {
      final response = await _http.put('${ApiConstants.backupTaskUpdate}/status', params: {
        'taskId': taskId,
        'status': status,
      });
      if (response.data['code'] == 200) {
        await loadTasks();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> deleteTask(Long taskId) async {
    try {
      final response = await _http.delete(ApiConstants.backupTaskUpdate, params: {'taskId': taskId});
      if (response.data['code'] == 200) {
        await loadTasks();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }
}
