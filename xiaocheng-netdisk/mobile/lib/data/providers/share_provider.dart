import 'package:flutter/foundation.dart';
import '../../core/constants/api_constants.dart';
import '../../core/utils/http_util.dart';
import '../models/share.dart';

class ShareProvider with ChangeNotifier {
  final HttpUtil _http = HttpUtil();

  List<Share> _shares = [];
  bool _isLoading = false;
  String? _error;

  List<Share> get shares => _shares;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> loadShares() async {
    _isLoading = true;
    notifyListeners();

    try {
      final response = await _http.get(ApiConstants.shareList);
      if (response.data['code'] == 200) {
        final List<dynamic> records = response.data['data'];
        _shares = records.map((json) => Share.fromJson(json)).toList();
      } else {
        _error = response.data['message'];
      }
    } catch (e) {
      _error = e.toString();
    }

    _isLoading = false;
    notifyListeners();
  }

  Future<Share?> createShare(List<Long> fileIds, {int shareType = 1, String? password, int? expireDays}) async {
    try {
      final response = await _http.post(ApiConstants.shareCreate, data: {
        'fileIds': fileIds,
        'shareType': shareType,
        if (password != null) 'password': password,
        if (expireDays != null) 'expireDays': expireDays,
      });
      if (response.data['code'] == 200) {
        final share = Share.fromJson(response.data['data']);
        await loadShares();
        return share;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  Future<bool> cancelShare(Long shareId) async {
    try {
      final response = await _http.delete(ApiConstants.shareCancel, params: {'shareId': shareId});
      if (response.data['code'] == 200) {
        await loadShares();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<Share?> getShareDetail(String shareCode) async {
    try {
      final response = await _http.get(ApiConstants.shareDetail, params: {'shareCode': shareCode});
      if (response.data['code'] == 200) {
        return Share.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
