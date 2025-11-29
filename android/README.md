# Chronie Wallet App

![Chronie Logo](app/src/main/res/mipmap-xxhdpi/ic_launcher.png)

## 项目概述

Chronie是一个轻量级的移动钱包应用，提供安全、便捷的数字资产管理服务。本项目是Chronie钱包的Android客户端，采用现代化架构设计，确保应用性能和用户体验。

## 主要功能特性

### 核心钱包功能
- **钱包信息管理**：查看钱包余额、钱包ID等基本信息
- **交易历史记录**：浏览、筛选和搜索交易记录
- **转账功能**：支持向其他用户安全转账
- **CDK兑换**：通过输入CDK码兑换虚拟货币
- **交易状态验证**：实时查询交易处理状态

### 用户体验优化
- **响应式界面**：适配各种屏幕尺寸的设备
- **流畅的交互体验**：优化动画和过渡效果
- **离线数据访问**：本地缓存重要数据
- **深色模式支持**：保护用户在低光环境下的视力

### 安全特性
- **生物识别认证**：支持指纹和面部识别
- **应用锁定**：可设置超时自动锁定
- **防止截屏**：敏感信息页面禁止截屏和录屏
- **安全数据存储**：采用加密存储敏感信息

### 多语言支持
- 简体中文
- 繁体中文
- 英文
- 日语
- 韩语

## 技术栈

### 开发语言与框架
- **语言**：Java, Kotlin
- **框架**：Android SDK, AndroidX
- **UI组件**：Material Design 3
- **依赖注入**：Dagger Hilt

### 数据层
- **本地存储**：Room数据库
- **远程API**：Retrofit + OkHttp
- **响应式编程**：RxJava
- **数据绑定**：Android DataBinding

### 架构模式
- **MVVM** (Model-View-ViewModel)
- **Repository Pattern**
- **Clean Architecture** 思想

## 系统要求

- Android 8.0 (API 26) 及以上版本
- 至少 2GB RAM
- 至少 100MB 可用存储空间
- 需要网络连接

## 安装指南

### 从源码构建

1. 克隆项目仓库
   ```bash
   git clone https://github.com/chronieapp/Chrysorrhoe-client-lite.git
   cd Chrysorrhoe-client-lite/android
   ```

2. 配置开发环境
   - 安装 Android Studio Hedgehog 或更高版本
   - 安装 JDK 17
   - 配置 Android SDK (API 34)

3. 构建项目
   ```bash
   ./gradlew assembleDebug
   ```

4. 安装到设备
   ```bash
   ./gradlew installDebug
   ```

### 直接安装APK

1. 从发布页面下载最新的APK文件
2. 允许安装来自未知来源的应用（设置 → 安全 → 未知来源应用）
3. 点击APK文件进行安装

## 使用说明

### 首次使用
1. 打开应用并完成注册/登录
2. 创建新钱包或恢复现有钱包
3. 设置安全选项（如生物识别、应用锁定等）

### 查看钱包信息
- 在首页或钱包页面可以查看当前钱包余额
- 点击钱包详情可查看钱包ID等更多信息

### 进行转账
1. 点击底部导航栏中的转账按钮
2. 输入收款方ID
3. 输入转账金额
4. 添加可选备注
5. 确认转账信息并输入交易密码
6. 完成转账

### 兑换CDK
1. 进入CDK兑换页面
2. 输入有效的CDK码
3. 点击兑换按钮
4. 等待系统处理并确认兑换结果

### 查看交易历史
1. 在底部导航栏选择交易历史
2. 使用顶部筛选器按类型或状态筛选交易
3. 点击单条交易查看详细信息

## 应用设置

### 安全设置
- 启用/禁用生物识别认证
- 设置应用锁定超时时间
- 管理交易密码

### 通知设置
- 启用/禁用各类通知
- 自定义通知声音和振动

### 语言设置
- 切换应用界面语言
- 支持系统默认语言或手动选择

## 性能优化

应用内置了性能监控和优化机制，包括：
- 内存使用监控
- 网络请求优化
- 数据库操作优化
- UI渲染性能监控

## 故障排除

### 常见问题

#### 无法登录
- 检查网络连接
- 确认账号密码正确
- 尝试重置密码

#### 转账失败
- 确认余额充足
- 检查收款方ID是否正确
- 查看交易状态确认具体错误信息

#### CDK兑换失败
- 确认CDK码格式正确
- 检查CDK码是否已被使用或过期
- 联系客服获取帮助

## 隐私政策

我们重视用户隐私，承诺保护用户数据安全。详细信息请查看完整的[隐私政策文档](PRIVACY_POLICY.md)。

## 许可证

本项目采用MIT许可证。详见[LICENSE](LICENSE)文件。

## 贡献指南

我们欢迎社区贡献！如果您有兴趣参与项目开发，请遵循以下步骤：

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交您的更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 开启 Pull Request

## 联系我们

- 官方网站：[https://chronie.app](https://chronie.app)
- 邮箱：support@chronie.app
- 社交媒体：
  - Twitter: @chroniewallet
  - Telegram: @chronie_support

## 版本历史

### v1.0.0 (初始发布)
- 实现核心钱包功能
- 添加交易历史和转账功能
- 支持CDK兑换
- 多语言支持
- 基本安全特性

### v1.1.0 (计划中)
- 增加加密货币兑换功能
- 优化生物识别认证
- 添加更多安全设置选项
- 改进UI/UX设计

---

*© 2024 Chronie Wallet. All rights reserved.*
