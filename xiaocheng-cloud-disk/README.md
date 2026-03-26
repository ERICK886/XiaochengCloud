# 小程网盘

轻量化、高兼容、安全便捷的个人/小型团队云存储工具。

## 技术栈

| 端 | 技术 | 状态 |
|----|------|------|
| 移动端 | Flutter | 规划中 |
| PC端 | Electron | 规划中 |
| Web端 | Vue3 + Vite | 规划中 |
| 后端 | Spring Boot | 规划中 |

## 项目结构

```
xiaocheng-cloud-disk/
├── packages/
│   ├── mobile/    # 移动端应用
│   ├── desktop/    # PC端应用
│   ├── web/        # Web端应用
│   ├── server/     # 后端服务
│   └── shared/     # 共享类型定义
├── package.json
└── pnpm-workspace.yaml
```

## 开发

```bash
# 安装依赖
pnpm install

# 启动开发服务
pnpm dev:web      # Web端
pnpm dev:server   # 后端

# 构建
pnpm build
```

## 文档

详细规格说明请查看 [.monkeycode/docs/SPEC.md](../.monkeycode/docs/SPEC.md)
