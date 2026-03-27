import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:file_picker/file_picker.dart';
import '../../../data/providers/file_provider.dart';
import '../../../data/providers/user_provider.dart';
import '../../../data/models/file_item.dart';
import '../../../shared/widgets/file_list_item.dart';
import '../../../shared/widgets/storage_usage_widget.dart';
import 'login_page.dart';

class FileListPage extends StatefulWidget {
  const FileListPage({super.key});

  @override
  State<FileListPage> createState() => _FileListPageState();
}

class _FileListPageState extends State<FileListPage> {
  final TextEditingController _searchController = TextEditingController();
  final Set<Long> _selectedFiles = {};
  bool _isSelectionMode = false;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<FileProvider>().loadFiles();
      context.read<UserProvider>().getUserInfo();
    });
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _uploadFile() async {
    final result = await FilePicker.platform.pickFiles();
    if (result != null && result.files.single.path != null) {
      final fileProvider = context.read<FileProvider>();
      await fileProvider.uploadFile(result.files.single.path!);
    }
  }

  Future<void> _createFolder() async {
    final controller = TextEditingController();
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('新建文件夹'),
        content: TextField(
          controller: controller,
          decoration: const InputDecoration(
            labelText: '文件夹名称',
          ),
          autofocus: true,
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('创建'),
          ),
        ],
      ),
    );

    if (confirmed == true && controller.text.isNotEmpty) {
      await context.read<FileProvider>().createFolder(controller.text);
    }
  }

  void _toggleSelection(Long fileId) {
    setState(() {
      if (_selectedFiles.contains(fileId)) {
        _selectedFiles.remove(fileId);
      } else {
        _selectedFiles.add(fileId);
      }
      _isSelectionMode = _selectedFiles.isNotEmpty;
    });
  }

  void _exitSelectionMode() {
    setState(() {
      _selectedFiles.clear();
      _isSelectionMode = false;
    });
  }

  Future<void> _deleteSelectedFiles() async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('确认删除'),
        content: Text('确定要删除选中的 ${_selectedFiles.length} 个文件吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('删除'),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      final fileProvider = context.read<FileProvider>();
      for (final fileId in _selectedFiles) {
        await fileProvider.deleteFile(fileId);
      }
      _exitSelectionMode();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: _isSelectionMode
            ? Text('已选择 ${_selectedFiles.length} 个项目')
            : const Text('小程网盘'),
        leading: _isSelectionMode
            ? IconButton(
                icon: const Icon(Icons.close),
                onPressed: _exitSelectionMode,
              )
            : null,
        actions: [
          if (_isSelectionMode) ...[
            IconButton(
              icon: const Icon(Icons.delete),
              onPressed: _deleteSelectedFiles,
            ),
          ] else ...[
            IconButton(
              icon: const Icon(Icons.search),
              onPressed: () {
              },
            ),
            PopupMenuButton<String>(
              onSelected: (value) {
                if (value == 'upload') {
                  _uploadFile();
                } else if (value == 'folder') {
                  _createFolder();
                } else if (value == 'logout') {
                  context.read<UserProvider>().logout();
                  Navigator.of(context).pushReplacement(
                    MaterialPageRoute(builder: (_) => const LoginPage()),
                  );
                }
              },
              itemBuilder: (context) => [
                const PopupMenuItem(
                  value: 'upload',
                  child: Row(
                    children: [
                      Icon(Icons.upload_file),
                      SizedBox(width: 8),
                      Text('上传文件'),
                    ],
                  ),
                ),
                const PopupMenuItem(
                  value: 'folder',
                  child: Row(
                    children: [
                      Icon(Icons.create_new_folder),
                      SizedBox(width: 8),
                      Text('新建文件夹'),
                    ],
                  ),
                ),
                const PopupMenuDivider(),
                const PopupMenuItem(
                  value: 'logout',
                  child: Row(
                    children: [
                      Icon(Icons.logout),
                      SizedBox(width: 8),
                      Text('退出登录'),
                    ],
                  ),
                ),
              ],
            ),
          ],
        ],
      ),
      body: Column(
        children: [
          Consumer<UserProvider>(
            builder: (context, userProvider, _) {
              if (userProvider.currentUser != null) {
                return StorageUsageWidget(
                  usedCapacity: userProvider.currentUser!.usedCapacity,
                  totalCapacity: userProvider.currentUser!.totalCapacity,
                );
              }
              return const SizedBox.shrink();
            },
          ),
          Expanded(
            child: Consumer<FileProvider>(
              builder: (context, fileProvider, _) {
                if (fileProvider.isLoading) {
                  return const Center(child: CircularProgressIndicator());
                }

                if (fileProvider.error != null) {
                  return Center(child: Text('错误: ${fileProvider.error}'));
                }

                if (fileProvider.files.isEmpty) {
                  return const Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.folder_open, size: 64, color: Colors.grey),
                        SizedBox(height: 16),
                        Text('文件夹为空', style: TextStyle(color: Colors.grey)),
                      ],
                    ),
                  );
                }

                return RefreshIndicator(
                  onRefresh: () => fileProvider.loadFiles(),
                  child: ListView.builder(
                    itemCount: fileProvider.files.length,
                    itemBuilder: (context, index) {
                      final file = fileProvider.files[index];
                      return FileListItem(
                        file: file,
                        isSelected: _selectedFiles.contains(file.id),
                        isSelectionMode: _isSelectionMode,
                        onTap: () {
                          if (_isSelectionMode) {
                            _toggleSelection(file.id);
                          } else if (file.isFolder) {
                            fileProvider.navigateToFolder(file.id);
                          }
                        },
                        onLongPress: () {
                          if (!_isSelectionMode) {
                            _toggleSelection(file.id);
                          }
                        },
                        onFavorite: () {
                          if (file.isFavorite) {
                            fileProvider.unfavoriteFile(file.id);
                          } else {
                            fileProvider.favoriteFile(file.id);
                          }
                        },
                      );
                    },
                  ),
                );
              },
            ),
          ),
        ],
      ),
      floatingActionButton: _isSelectionMode
          ? null
          : FloatingActionButton(
              onPressed: _uploadFile,
              child: const Icon(Icons.add),
            ),
    );
  }
}
