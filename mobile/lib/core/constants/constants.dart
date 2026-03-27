class ApiConstants {
  static const String baseUrl = 'http://localhost:8080/api';
  
  // User APIs
  static const String login = '/user/login';
  static const String register = '/user/register';
  static const String userInfo = '/user/info';
  
  // File APIs
  static const String fileList = '/file/list';
  static const String fileDetail = '/file/detail';
  static const String createFolder = '/file/createFolder';
  static const String deleteFile = '/file/delete';
  static const String moveFile = '/file/move';
  static const String renameFile = '/file/rename';
  static const String searchFile = '/file/search';
  static const String favorite = '/file/favorite';
  static const String unfavorite = '/file/unfavorite';
  static const String recentFiles = '/file/recent';
  
  // Share APIs
  static const String createShare = '/share/create';
  static const String shareList = '/share/list';
  static const String shareDetail = '/share/detail';
  static const String cancelShare = '/share/cancel';
  
  // Backup APIs
  static const String photoBackup = '/backup/photo';
  static const String videoBackup = '/backup/video';
  static const String backupHistory = '/backup/history';
}

class StorageConstants {
  static const String tokenKey = 'auth_token';
  static const String userIdKey = 'user_id';
  static const String userInfoKey = 'user_info';
}
