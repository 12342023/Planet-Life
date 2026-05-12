# 第0-9阶段 Android MVP 开发记录

## 基本信息

- 阶段编号：0-9
- 阶段名称：Android Kotlin + Jetpack Compose MVP
- 开发日期：2026-05-12
- 开发工具：Gemini + Codex 审查
- 执行人：Gemini / Codex
- 审查人：Codex

## 阶段目标

完成口袋星球 Android MVP：项目骨架、创建星球、首页生态、行为记录、日志、专注、任务、图鉴、我的/设置、通知提醒、演示打包。

## 本次实现摘要

已建立 Android 原生项目，技术栈为 Kotlin、Jetpack Compose、Navigation Compose、Room、DataStore、MVVM。底部导航固定为“星球、日志、专注、任务、我的”，图鉴从“我的”进入。

## 新增文件

```text
app/
build.gradle.kts
settings.gradle.kts
gradle.properties
gradle/
gradlew
gradlew.bat
.gitignore
```

主要源码目录：

```text
app/src/main/java/com/example/planetlife/
app/src/main/java/com/example/planetlife/data/
app/src/main/java/com/example/planetlife/domain/
app/src/main/java/com/example/planetlife/navigation/
app/src/main/java/com/example/planetlife/notification/
app/src/main/java/com/example/planetlife/ui/
app/src/main/res/
```

## 修改文件

```text
docs/process/第0-9阶段_Android_MVP开发记录.md
```

## 删除文件

无。

## 已完成功能

- 阶段 0：Android Kotlin + Compose 项目骨架，Gradle wrapper，五栏底部导航。
- 阶段 1：首次创建星球流程，首页基础展示，Room 持久化。
- 阶段 2：行为记录与生态规则，支持步行、久坐、夜间活跃、通勤外出。
- 阶段 3：星球事件日志，按时间线展示世界观事件。
- 阶段 4：专注倒计时、专注记录、水晶奖励、专注日志。
- 阶段 5：每日生态任务、任务进度、奖励领取和防重复领取。
- 阶段 6：图鉴系统，基于生态和行为解锁生物。
- 阶段 7：我的页、资料编辑、目标设置、主题切换、隐私说明、清空本地数据。
- 阶段 8：Android 13+ 通知权限申请、本地久坐提醒、通知点击回 App。
- 阶段 9：首页动态星球、演示随机生态按钮、小屏检查、debug APK 构建。

## 未完成功能

- 真实后台长期久坐监测仍为 MVP 级，当前主要通过手动记录久坐触发提醒。
- 专注计时为前台页面内倒计时，暂未做后台恢复和系统级计时保障。
- 暂未加入自动化单元测试和 UI 测试。
- 图标和星球视觉为 Compose/Vector MVP 方案，后续可替换为完整像素资产。

## 验证命令

```text
./gradlew assembleDebug
/Users/jianghao/Library/Android/sdk/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk
/Users/jianghao/Library/Android/sdk/platform-tools/adb shell am start -W -n com.example.planetlife/.MainActivity
/Users/jianghao/Library/Android/sdk/platform-tools/adb shell pidof com.example.planetlife
/Users/jianghao/Library/Android/sdk/platform-tools/adb logcat -d -v time -s AndroidRuntime ActivityTaskManager
```

运行结果：

```text
assembleDebug 成功。
APK 安装成功。
App 启动成功。
启动后进程存活。
logcat 未出现 AndroidRuntime FATAL EXCEPTION。
```

## 手动测试流程

1. 启动 App。
2. 创建星球并进入首页。
3. 在首页记录行为，观察生态值和今日事件变化。
4. 进入日志页，确认事件以时间线展示。
5. 进入专注页，运行专注流程并生成水晶奖励。
6. 进入任务页，确认任务进度和奖励领取。
7. 进入我的页，查看图鉴、修改资料、调整设置。
8. 打开通知开关，验证 Android 13+ 权限申请。
9. 点击首页“随机生态演示”，确认星球生态随机变化并写入日志。

实际结果：

```text
核心流程可连续演示，App 未出现启动级闪退。
```

## 发现的问题

| 编号 | 问题 | 严重程度 | 影响 | 建议修复 |
| --- | --- | --- | --- | --- |
| 1 | 底部导航 AppRoute 静态初始化曾导致 null NPE | 高 | App 启动闪退 | 已改为 getter 按需生成 bottomTabs |
| 2 | 主题设置曾只保存不生效 | 中 | 设置体验不完整 | 已接入 MainActivity 和 PlanetLifeTheme |
| 3 | 清空数据导航曾使用 popUpTo(0) | 中 | 可能出现导航异常 | 已改为基于 startDestinationId 清栈 |
| 4 | 通知提醒不做后台长期监测 | 低 | MVP 限制 | 后续引入 WorkManager 或系统调度 |

## Codex 审查结论

```text
通过
```

原因：

```text
阶段 0-9 功能范围已完成，debug 构建通过，模拟器启动通过，已修复发现的启动闪退和关键设置缺口。
```

## APK 路径

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Git 提交

```text
84fd82e Add Planet Life Android app MVP
```

## 下一步任务

1. 准备演示脚本和答辩截图。
2. 视需要补充 README，说明构建方式和主要功能。
3. 后续版本可优先补 WorkManager 通知、自动化测试和更精细的像素素材。

## 本次开发流程总结

本次流程是否顺利：

```text
顺利
```

总结：

```text
阶段推进中多次先检查代码再继续实现，发现启动闪退后通过 logcat 定位并修复。后续给 Gemini 下命令时仍应保持每阶段小范围输入、完成后立即构建和真机/模拟器启动验证。
```
