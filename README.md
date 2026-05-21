# contest-hub — 高校竞赛管理系统

基于 **Spring Boot 3.2.0** + **MySQL** + **MyBatis-Plus 3.5.5** 的高校竞赛管理平台，支持竞赛发布、学生报名组队、教师评审、申诉处理和数据统计等功能。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.2.0 |
| 安全认证 | Spring Security + JWT |
| ORM | MyBatis-Plus 3.5.5 |
| 数据库 | MySQL 8.0 |
| 前端 | 原生 HTML/CSS/JS（SPA 模式） |
| 图表 | Chart.js |
| 构建工具 | Maven |
| JDK | 17 |

## 功能模块

### 学生端（student.html）
- 竞赛浏览与搜索、热门竞赛推荐
- 报名参赛、创建/加入团队
- 消息通知、个人信息管理

### 教师端（teacher.html）
- 创建与管理竞赛
- 评审学生作品、分配指导教师
- 团队审核、消息通知

### 管理员端（admin.html）
- 用户管理（学生/教师审批）
- 竞赛管理、竞赛类别管理
- 申诉处理、公告管理
- 数据统计与可视化
- 权限设置

## 快速启动

### 环境要求
- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 数据库配置
```sql
CREATE DATABASE jingsai DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 修改配置
编辑 `backend/src/main/resources/application.yml`，修改 MySQL 用户名和密码：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jingsai?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&allowPublicKeyRetrieval=true
    username: root
    password: 你的密码
```

### 启动
```bash
cd backend
mvn clean package -DskipTests
java -jar target/jingsai-1.0.0.jar
```

启动后访问 http://localhost:8080

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 教师 | T1001 / T1002 / T1003 | 123456 |
| 学生 | 1001 ~ 1005 | 123456 |

## 项目结构

```
├── backend/                          # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/example/jingsai/
│       │   ├── config/               # 安全配置、数据初始化
│       │   ├── controller/           # REST API 控制器
│       │   ├── entity/               # 实体类
│       │   ├── mapper/               # MyBatis 映射器
│       │   ├── service/              # 服务层
│       │   └── util/                 # JWT、用户上下文工具
│       └── resources/
│           ├── application.yml       # 应用配置
│           ├── schema.sql            # 建表 SQL
│           └── static/               # 前端静态资源
├── index.html                        # 登录/注册首页
├── student.html                      # 学生端
├── teacher.html                      # 教师端
├── admin.html                        # 管理员端
└── js/api.js                         # 共享 API 封装
```

## 数据库表

| 表名 | 说明 |
|------|------|
| user | 用户表（管理员/教师/学生） |
| competition | 竞赛表 |
| category | 竞赛类别表 |
| team | 团队表 |
| team_member | 团队成员表 |
| registration | 报名表 |
| review | 评审表 |
| appeal | 申诉表 |
| announcement | 公告表 |
| comment | 评论表 |
| message | 消息表 |

## License

MIT
