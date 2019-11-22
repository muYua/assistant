## 一、环境依赖
Eclipse\
Tomcat 9.0\
MySQL 5.7\
Redis

## 二、部署步骤

## 三、目录结构描述
例子:

├──&ensp;README.md &emsp;              // help\
├── app &emsp;                         // 应用\
├── config &emsp;                      // 配置\
│   ├── default.json\
│   ├── dev.json &emsp;                // 开发环境\
│   ├── experiment.json &emsp;         // 实验\
│   ├── index.js &emsp;                // 配置控制\
│   ├── local.json &emsp;              // 本地\
│   ├── production.json &emsp;         // 生产环境\
│   └── test.json &emsp;               // 测试环境\
├── data\
├── doc &emsp;                         // 文档\
├── environment\
├── gulpfile.js\
├── locales\
├── logger-service.js &emsp;           // 启动日志配置\
├── node_modules\
├── package.json\
├── app-service.js &emsp;              // 启动应用配置\
├── static &emsp;                      // web静态资源加载\
│   └── initjson\
│       └── config.js &emsp;           // 提供给前端的配置\
├── test\
├── test-service.js\
└── tools

## 四、版本内容更新
1. 新功能 &emsp; XXXX
2. 新功能 &emsp; XXXX

---
### Markdown语法笔记
#### 空格
- 半角的空白：[&ensp;]或[&#8194;]
- 全角的空白：[&emsp;]或[&#8195;]
- 不换行的空白：[&nbsp;]或[&#160;]
