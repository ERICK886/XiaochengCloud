import 'package:flutter/material.dart';
import '../network/api_client.dart';
import '../models/user.dart';
import '../../core/constants/constants.dart';

class AuthProvider with ChangeNotifier {
  final ApiClient _apiClient = ApiClient();
  
  User? _user;
  bool _isLoading = false;
  String? _error;

  User? get user => _user;
  bool get isLoading => _isLoading;
  String? get error => _error;
  bool get isLoggedIn => _user != null;

  Future<bool> login(String username, String password) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final response = await _apiClient.post(
        ApiConstants.login,
        data: {'username': username, 'password': password},
      );
      
      if (response.data['code'] == 200) {
        final token = response.data['data'];
        await _apiClient.setToken(token);
        await fetchUserInfo();
        return true;
      } else {
        _error = response.data['message'];
        return false;
      }
    } catch (e) {
      _error = '登录失败: $e';
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<bool> register(String username, String password, String? phone) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final response = await _apiClient.post(
        ApiConstants.register,
        data: {
          'username': username,
          'password': password,
          'phone': phone,
        },
      );
      
      if (response.data['code'] == 200) {
        return true;
      } else {
        _error = response.data['message'];
        return false;
      }
    } catch (e) {
      _error = '注册失败: $e';
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> fetchUserInfo() async {
    try {
      await _apiClient.loadToken();
      if (!_apiClient.hasToken) return;

      final response = await _apiClient.get(ApiConstants.userInfo);
      if (response.data['code'] == 200) {
        _user = User.fromJson(response.data['data']);
        notifyListeners();
      }
    } catch (e) {
      _error = '获取用户信息失败';
    }
  }

  Future<void> logout() async {
    await _apiClient.clearToken();
    _user = null;
    notifyListeners();
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }
}
