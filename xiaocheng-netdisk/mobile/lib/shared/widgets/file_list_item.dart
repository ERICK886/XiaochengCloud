import 'package:flutter/material.dart';
import '../../data/models/file_item.dart';

class FileListItem extends StatelessWidget {
  final FileItem file;
  final bool isSelected;
  final bool isSelectionMode;
  final VoidCallback onTap;
  final VoidCallback onLongPress;
  final VoidCallback onFavorite;

  const FileListItem({
    super.key,
    required this.file,
    required this.isSelected,
    required this.isSelectionMode,
    required this.onTap,
    required this.onLongPress,
    required this.onFavorite,
  });

  IconData _getFileIcon() {
    if (file.isFolder) return Icons.folder;
    switch (file.fileType?.toLowerCase()) {
      case 'jpg':
      case 'jpeg':
      case 'png':
      case 'gif':
      case 'bmp':
      case 'webp':
        return Icons.image;
      case 'mp4':
      case 'avi':
      case 'mov':
      case 'wmv':
      case 'flv':
        return Icons.video_file;
      case 'mp3':
      case 'wav':
      case 'flac':
      case 'aac':
        return Icons.audio_file;
      case 'doc':
      case 'docx':
      case 'pdf':
      case 'txt':
      case 'rtf':
        return Icons.description;
      case 'xls':
      case 'xlsx':
        return Icons.table_chart;
      case 'ppt':
      case 'pptx':
        return Icons.slideshow;
      case 'zip':
      case 'rar':
      case '7z':
      case 'tar':
      case 'gz':
        return Icons.folder_zip;
      default:
        return Icons.insert_drive_file;
    }
  }

  Color _getIconColor() {
    if (file.isFolder) return const Color(0xFFFFA726);
    switch (file.fileType?.toLowerCase()) {
      case 'jpg':
      case 'jpeg':
      case 'png':
      case 'gif':
      case 'bmp':
      case 'webp':
        return const Color(0xFF66BB6A);
      case 'mp4':
      case 'avi':
      case 'mov':
      case 'wmv':
      case 'flv':
        return const Color(0xFFEF5350);
      case 'mp3':
      case 'wav':
      case 'flac':
      case 'aac':
        return const Color(0xFFAB47BC);
      case 'doc':
      case 'docx':
      case 'pdf':
      case 'txt':
      case 'rtf':
        return const Color(0xFF42A5F5);
      case 'xls':
      case 'xlsx':
        return const Color(0xFF26A69A);
      case 'ppt':
      case 'pptx':
        return const Color(0xFFEF5350);
      case 'zip':
      case 'rar':
      case '7z':
      case 'tar':
      case 'gz':
        return const Color(0xFF8D6E63);
      default:
        return const Color(0xFF78909C);
    }
  }

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: Stack(
        children: [
          Container(
            width: 48,
            height: 48,
            decoration: BoxDecoration(
              color: _getIconColor().withOpacity(0.1),
              borderRadius: BorderRadius.circular(8),
            ),
            child: Icon(
              _getFileIcon(),
              color: _getIconColor(),
              size: 28,
            ),
          ),
          if (isSelected)
            Positioned(
              right: 0,
              bottom: 0,
              child: Container(
                width: 18,
                height: 18,
                decoration: const BoxDecoration(
                  color: Color(0xFF1E88E5),
                  shape: BoxShape.circle,
                ),
                child: const Icon(
                  Icons.check,
                  color: Colors.white,
                  size: 12,
                ),
              ),
            ),
        ],
      ),
      title: Text(
        file.fileName,
        maxLines: 1,
        overflow: TextOverflow.ellipsis,
      ),
      subtitle: Text(
        '${file.formattedSize} · ${_formatDate(file.updateTime)}',
        style: TextStyle(color: Colors.grey[600], fontSize: 12),
      ),
      trailing: isSelectionMode
          ? null
          : IconButton(
              icon: Icon(
                file.isFavorite ? Icons.star : Icons.star_border,
                color: file.isFavorite ? Colors.amber : Colors.grey,
              ),
              onPressed: onFavorite,
            ),
      onTap: onTap,
      onLongPress: onLongPress,
    );
  }

  String _formatDate(DateTime date) {
    final now = DateTime.now();
    final diff = now.difference(date);

    if (diff.inDays == 0) {
      if (diff.inHours == 0) {
        return '${diff.inMinutes}分钟前';
      }
      return '${diff.inHours}小时前';
    } else if (diff.inDays < 7) {
      return '${diff.inDays}天前';
    } else {
      return '${date.month}/${date.day}/${date.year}';
    }
  }
}
