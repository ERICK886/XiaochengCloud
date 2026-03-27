class User {
  final Long id;
  final String username;
  final String? phone;
  final String? email;
  final String? nickname;
  final String? avatar;
  final Long totalCapacity;
  final Long usedCapacity;
  final int memberType;
  final DateTime? memberExpireTime;
  final int status;

  User({
    required this.id,
    required this.username,
    this.phone,
    this.email,
    this.nickname,
    this.avatar,
    required this.totalCapacity,
    required this.usedCapacity,
    required this.memberType,
    this.memberExpireTime,
    required this.status,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'],
      username: json['username'],
      phone: json['phone'],
      email: json['email'],
      nickname: json['nickname'],
      avatar: json['avatar'],
      totalCapacity: json['totalCapacity'],
      usedCapacity: json['usedCapacity'],
      memberType: json['memberType'],
      memberExpireTime: json['memberExpireTime'] != null
          ? DateTime.parse(json['memberExpireTime'])
          : null,
      status: json['status'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'username': username,
      'phone': phone,
      'email': email,
      'nickname': nickname,
      'avatar': avatar,
      'totalCapacity': totalCapacity,
      'usedCapacity': usedCapacity,
      'memberType': memberType,
      'memberExpireTime': memberExpireTime?.toIso8601String(),
      'status': status,
    };
  }

  double get usedPercentage => totalCapacity > 0 ? usedCapacity / totalCapacity : 0;
}
