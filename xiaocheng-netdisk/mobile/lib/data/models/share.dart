class Share {
  final Long id;
  final String shareCode;
  final Long userId;
  final String fileIds;
  final int shareType;
  final String? password;
  final DateTime? expireTime;
  final int viewCount;
  final int downloadCount;
  final int status;
  final DateTime createTime;

  Share({
    required this.id,
    required this.shareCode,
    required this.userId,
    required this.fileIds,
    required this.shareType,
    this.password,
    this.expireTime,
    required this.viewCount,
    required this.downloadCount,
    required this.status,
    required this.createTime,
  });

  factory Share.fromJson(Map<String, dynamic> json) {
    return Share(
      id: json['id'],
      shareCode: json['shareCode'],
      userId: json['userId'],
      fileIds: json['fileIds'],
      shareType: json['shareType'],
      password: json['password'],
      expireTime: json['expireTime'] != null ? DateTime.parse(json['expireTime']) : null,
      viewCount: json['viewCount'],
      downloadCount: json['downloadCount'],
      status: json['status'],
      createTime: DateTime.parse(json['createTime']),
    );
  }

  String get shareUrl => 'xiaocheng://share/$shareCode';

  String get shareTypeDesc {
    switch (shareType) {
      case 1:
        return '公开链接';
      case 2:
        return '密码链接';
      case 3:
        return '指定好友';
      default:
        return '未知';
    }
  }

  bool get isExpired => expireTime != null && expireTime!.isBefore(DateTime.now());
}
