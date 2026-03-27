class RecycleItem {
  final Long id;
  final Long userId;
  final Long fileId;
  final String fileName;
  final String? filePath;
  final Long fileSize;
  final String? fileType;
  final DateTime deleteTime;
  final DateTime expireTime;

  RecycleItem({
    required this.id,
    required this.userId,
    required this.fileId,
    required this.fileName,
    this.filePath,
    required this.fileSize,
    this.fileType,
    required this.deleteTime,
    required this.expireTime,
  });

  factory RecycleItem.fromJson(Map<String, dynamic> json) {
    return RecycleItem(
      id: json['id'],
      userId: json['userId'],
      fileId: json['fileId'],
      fileName: json['fileName'],
      filePath: json['filePath'],
      fileSize: json['fileSize'],
      fileType: json['fileType'],
      deleteTime: DateTime.parse(json['deleteTime']),
      expireTime: DateTime.parse(json['expireTime']),
    );
  }

  String get formattedSize {
    if (fileSize < 1024) return '$fileSize B';
    if (fileSize < 1024 * 1024) return '${(fileSize / 1024).toStringAsFixed(1)} KB';
    if (fileSize < 1024 * 1024 * 1024) return '${(fileSize / (1024 * 1024)).toStringAsFixed(1)} MB';
    return '${(fileSize / (1024 * 1024 * 1024)).toStringAsFixed(2)} GB';
  }

  int get remainingDays => expireTime.difference(DateTime.now()).inDays;
}
