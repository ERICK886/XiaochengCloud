class ApiConstants {
  static const String baseUrl = 'http://localhost:8080';
  static const String apiPrefix = '/api';

  static const String login = '$apiPrefix/user/login';
  static const String register = '$apiPrefix/user/register';
  static const String logout = '$apiPrefix/user/logout';
  static const String userInfo = '$apiPrefix/user/info';

  static const String fileList = '$apiPrefix/file/list';
  static const String fileUpload = '$apiPrefix/file/upload';
  static const String fileDelete = '$apiPrefix/file/delete';
  static const String fileMove = '$apiPrefix/file/move';
  static const String fileRename = '$apiPrefix/file/rename';
  static const String fileFavorite = '$apiPrefix/file/favorite';
  static const String fileUnfavorite = '$apiPrefix/file/unfavorite';
  static const String fileRecent = '$apiPrefix/file/recent';
  static const String fileDownload = '$apiPrefix/file/download';
  static const String createFolder = '$apiPrefix/file/folder';

  static const String shareCreate = '$apiPrefix/share/create';
  static const String shareList = '$apiPrefix/share/list';
  static const String shareDetail = '$apiPrefix/share/detail';
  static const String shareCancel = '$apiPrefix/share/cancel';

  static const String backupTaskList = '$apiPrefix/backup/task/list';
  static const String backupTaskCreate = '$apiPrefix/backup/task';
  static const String backupTaskUpdate = '$apiPrefix/backup/task';

  static const String recycleList = '$apiPrefix/recycle/list';
  static const String recycleRestore = '$apiPrefix/recycle/restore';
  static const String recycleDelete = '$apiPrefix/recycle/delete';
  static const String recycleEmpty = '$apiPrefix/recycle/empty';
}

class StorageConstants {
  static const String tokenKey = 'auth_token';
  static const String userIdKey = 'user_id';
  static const String usernameKey = 'username';
}
