import 'package:flutter/foundation.dart';
import '../../core/constants/api_constants.dart';
import '../../core/utils/http_util.dart';
import '../models/recycle_item.dart';

class RecycleProvider with ChangeNotifier {
  final HttpUtil _http = HttpUtil();

  List<RecycleItem> _items = [];
  bool _isLoading = false;
  String? _error;

  List<RecycleItem> get items => _items;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> loadItems() async {
    _isLoading = true;
    notifyListeners();

    try {
      final response = await _http.get(ApiConstants.recycleList);
      if (response.data['code'] == 200) {
        final List<dynamic> records = response.data['data'];
        _items = records.map((json) => RecycleItem.fromJson(json)).toList();
      } else {
        _error = response.data['message'];
      }
    } catch (e) {
      _error = e.toString();
    }

    _isLoading = false;
    notifyListeners();
  }

  Future<bool> restore(Long recycleId) async {
    try {
      final response = await _http.post(ApiConstants.recycleRestore, data: {'recycleId': recycleId});
      if (response.data['code'] == 200) {
        await loadItems();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> permanentlyDelete(Long recycleId) async {
    try {
      final response = await _http.delete(ApiConstants.recycleDelete, params: {'recycleId': recycleId});
      if (response.data['code'] == 200) {
        await loadItems();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> emptyRecycle() async {
    try {
      final response = await _http.delete(ApiConstants.recycleEmpty);
      if (response.data['code'] == 200) {
        _items = [];
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }
}
