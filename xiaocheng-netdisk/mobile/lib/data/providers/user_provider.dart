import 'package:flutter/foundation.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../../core/constants/api_constants.dart';
import '../../core/utils/http_util.dart';
import '../models/user.dart';

class UserProvider with ChangeNotifier {
  final FlutterSecureStorage _storage = const FlutterSecureStorage();
  final HttpUtil _http = HttpUtil();

  User? _currentUser;
  bool _isLoggedIn = false;
  bool _isLoading = false;

  User? get currentUser => _currentUser;
  bool get isLoggedIn => _isLoggedIn;
  bool get isLoading => _isLoading;

  Future<void> init() async {
    final token = await _storage.read(key: StorageConstants.tokenKey);
    if (token != null) {
      await getUserInfo();
    }
  }

  Future<bool> login(String username, String password) async {
    _isLoading = true;
    notifyListeners();

    try {
      final response = await _http.post(ApiConstants.login, data: {
        'username': username,
        'password': password,
      });

      if (response.data['code'] == 200) {
        final data = response.data['data'];
        await _storage.write(key: StorageConstants.tokenKey, value: data['token']);
        await _storage.write(key: StorageConstants.userIdKey, value: data['userId'].toString());
        _isLoggedIn = true;
        _isLoading = false;
        notifyListeners();
        return true;
      }
      _isLoading = false;
      notifyListeners();
      return false;
    } catch (e) {
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  Future<bool> register(String username, String password, {String? phone}) async {
    _isLoading = true;
    notifyListeners();

    try {
      final response = await _http.post(ApiConstants.register, data: {
        'username': username,
        'password': password,
        if (phone != null) 'phone': phone,
      });

      if (response.data['code'] == 200) {
        final data = response.data['data'];
        await _storage.write(key: StorageConstants.tokenKey, value: data['token']);
        await _storage.write(key: StorageConstants.userIdKey, value: data['userId'].toString());
        _isLoggedIn = true;
        _isLoading = false;
        notifyListeners();
        return true;
      }
      _isLoading = false;
      notifyListeners();
      return false;
    } catch (e) {
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  Future<void> logout() async {
    try {
      await _http.post(ApiConstants.logout);
    } catch (e) {
    }
    await _storage.delete(key: StorageConstants.tokenKey);
    await _storage.delete(key: StorageConstants.userIdKey);
    _currentUser = null;
    _isLoggedIn = false;
    notifyListeners();
  }

  Future<void> getUserInfo() async {
    try {
      final response = await _http.get(ApiConstants.userInfo);
      if (response.data['code'] == 200) {
        _currentUser = User.fromJson(response.data['data']);
        _isLoggedIn = true;
        notifyListeners();
      }
    } catch (e) {
      await logout();
    }
  }
}
