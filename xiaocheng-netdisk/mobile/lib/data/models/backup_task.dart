class BackupTask {
  final Long id;
  final Long userId;
  final int taskType;
  final String? sourcePath;
  final String? targetPath;
  final int backupMode;
  final int qualityMode;
  final int status;
  final DateTime? lastBackupTime;
  final DateTime createTime;

  BackupTask({
    required this.id,
    required this.userId,
    required this.taskType,
    this.sourcePath,
    this.targetPath,
    required this.backupMode,
    required this.qualityMode,
    required this.status,
    this.lastBackupTime,
    required this.createTime,
  });

  factory BackupTask.fromJson(Map<String, dynamic> json) {
    return BackupTask(
      id: json['id'],
      userId: json['userId'],
      taskType: json['taskType'],
      sourcePath: json['sourcePath'],
      targetPath: json['targetPath'],
      backupMode: json['backupMode'],
      qualityMode: json['qualityMode'],
      status: json['status'],
      lastBackupTime: json['lastBackupTime'] != null
          ? DateTime.parse(json['lastBackupTime'])
          : null,
      createTime: DateTime.parse(json['createTime']),
    );
  }

  String get taskTypeDesc {
    switch (taskType) {
      case 1:
        return '相册备份';
      case 2:
        return '视频备份';
      case 3:
        return '文件夹同步';
      default:
        return '未知';
    }
  }

  String get backupModeDesc => backupMode == 1 ? '仅WiFi' : '所有网络';
  String get qualityModeDesc => qualityMode == 1 ? '原图' : '压缩';

  String get statusDesc {
    switch (status) {
      case 0:
        return '未启动';
      case 1:
        return '运行中';
      case 2:
        return '已暂停';
      case 3:
        return '已停止';
      default:
        return '未知';
    }
  }
}
