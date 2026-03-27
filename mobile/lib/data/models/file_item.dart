class FileItem {
  final int id;
  final int userId;
  final int? parentId;
  final String fileName;
  final String? filePath;
  final int fileSize;
  final String fileType;
  final String? mimeType;
  final bool isFolder;
  final String? fileHash;
  final String? storagePath;
  final String? thumbnail;
  final bool isFavorite;
  final bool isDeleted;
  final DateTime? deleteTime;
  final DateTime createTime;
  final DateTime updateTime;

  FileItem({
    required this.id,
    required this.userId,
    this.parentId,
    required this.fileName,
    this.filePath,
    required this.fileSize,
    required this.fileType,
    this.mimeType,
    required this.isFolder,
    this.fileHash,
    this.storagePath,
    this.thumbnail,
    required this.isFavorite,
    required this.isDeleted,
    this.deleteTime,
    required this.createTime,
    required this.updateTime,
  });

  factory FileItem.fromJson(Map<String, dynamic> json) {
    return FileItem(
      id: json['id'],
      userId: json['userId'],
      parentId: json['parentId'],
      fileName: json['fileName'],
      filePath: json['filePath'],
      fileSize: json['fileSize'] ?? 0,
      fileType: json['fileType'] ?? '',
      mimeType: json['mimeType'],
      isFolder: json['isFolder'] == 1,
      fileHash: json['fileHash'],
      storagePath: json['storagePath'],
      thumbnail: json['thumbnail'],
      isFavorite: json['isFavorite'] == 1,
      isDeleted: json['isDeleted'] == 1,
      deleteTime: json['deleteTime'] != null
          ? DateTime.parse(json['deleteTime'])
          : null,
      createTime: DateTime.parse(json['createTime']),
      updateTime: DateTime.parse(json['updateTime']),
    );
  }

  String get formattedSize {
    if (isFolder) return '--';
    if (fileSize < 1024) return '$fileSize B';
    if (fileSize < 1024 * 1024) return '${(fileSize / 1024).toStringAsFixed(1)} KB';
    if (fileSize < 1024 * 1024 * 1024) {
      return '${(fileSize / (1024 * 1024)).toStringAsFixed(1)} MB';
    }
    return '${(fileSize / (1024 * 1024 * 1024)).toStringAsFixed(1)} GB';
  }

  String get extension {
    if (isFolder) return '';
    final dotIndex = fileName.lastIndexOf('.');
    return dotIndex > 0 ? fileName.substring(dotIndex + 1).toLowerCase() : '';
  }
}
