class FileItem {
  final Long id;
  final Long userId;
  final Long? parentId;
  final String fileName;
  final String? filePath;
  final Long fileSize;
  final String? fileType;
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
    this.fileType,
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
      fileSize: json['fileSize'],
      fileType: json['fileType'],
      mimeType: json['mimeType'],
      isFolder: json['isFolder'] == 1,
      fileHash: json['fileHash'],
      storagePath: json['storagePath'],
      thumbnail: json['thumbnail'],
      isFavorite: json['isFavorite'] == 1,
      isDeleted: json['isDeleted'] == 1,
      deleteTime: json['deleteTime'] != null ? DateTime.parse(json['deleteTime']) : null,
      createTime: DateTime.parse(json['createTime']),
      updateTime: DateTime.parse(json['updateTime']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'parentId': parentId,
      'fileName': fileName,
      'filePath': filePath,
      'fileSize': fileSize,
      'fileType': fileType,
      'mimeType': mimeType,
      'isFolder': isFolder ? 1 : 0,
      'fileHash': fileHash,
      'storagePath': storagePath,
      'thumbnail': thumbnail,
      'isFavorite': isFavorite ? 1 : 0,
      'isDeleted': isDeleted ? 1 : 0,
      'deleteTime': deleteTime?.toIso8601String(),
      'createTime': createTime.toIso8601String(),
      'updateTime': updateTime.toIso8601String(),
    };
  }

  String get formattedSize {
    if (isFolder) return '--';
    if (fileSize < 1024) return '$fileSize B';
    if (fileSize < 1024 * 1024) return '${(fileSize / 1024).toStringAsFixed(1)} KB';
    if (fileSize < 1024 * 1024 * 1024) return '${(fileSize / (1024 * 1024)).toStringAsFixed(1)} MB';
    return '${(fileSize / (1024 * 1024 * 1024)).toStringAsFixed(2)} GB';
  }

  String get iconName {
    if (isFolder) return 'folder';
    switch (fileType?.toLowerCase()) {
      case 'jpg':
      case 'jpeg':
      case 'png':
      case 'gif':
      case 'bmp':
      case 'webp':
        return 'image';
      case 'mp4':
      case 'avi':
      case 'mov':
      case 'wmv':
      case 'flv':
        return 'video';
      case 'mp3':
      case 'wav':
      case 'flac':
      case 'aac':
        return 'audio';
      case 'doc':
      case 'docx':
      case 'pdf':
      case 'txt':
      case 'rtf':
        return 'document';
      case 'xls':
      case 'xlsx':
        return 'excel';
      case 'ppt':
      case 'pptx':
        return 'ppt';
      case 'zip':
      case 'rar':
      case '7z':
      case 'tar':
      case 'gz':
        return 'archive';
      default:
        return 'file';
    }
  }
}
