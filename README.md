# Planet Life / 口袋星球

口袋星球是一款面向 Android 的现实行为驱动星球养成应用。它把用户的步行、专注、久坐、夜间活跃、规律作息等行为转化为星球生态变化，让用户通过一颗会成长、会失衡、会出现事件和生物的星球感知自己的日常生活。

当前仓库先存放开发文档，用来指导后续 Gemini 或 Android Studio 中的原生 Android 开发。

## 文档入口

- [完整开发文档](docs/口袋星球_完整开发文档.md)
- [界面参考与 UI 实现规范](docs/界面参考_UI实现规范.md)
- [Gemini 阶段开发指挥文档](docs/gemini/Gemini_阶段开发指挥文档.md)
- [开发流程记录模板](docs/process/开发流程记录模板.md)

## MVP 技术方向

- Android 原生开发
- Kotlin
- Jetpack Compose
- Navigation Compose
- Room 本地数据库
- DataStore 或 SharedPreferences 保存设置
- MVVM 架构
- MVP 阶段不做服务器、登录、云同步、3D 星球、内购和 AI 日报

## MVP 核心闭环

1. 用户创建星球。
2. 用户记录行为或完成专注。
3. 行为转换为生态值。
4. 首页展示星球状态变化。
5. 系统生成星球日志。
6. 满足条件后解锁图鉴。
7. 每日任务推动用户继续行动。

## 界面参考

界面参考图已整理到 `docs/assets/ui-reference/`，后续开发应优先保持这些页面的视觉语言：浅蓝天空背景、像素风、奶油色卡片、五栏底部导航、星球生态主视觉。
