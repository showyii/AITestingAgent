# AI 测试 Agent（需求理解-用例生成-缺陷分析-测试报告生成）

## 1. 项目简介
本项目是一个面向软件测试场景的 AI Agent。它聚焦测试人员日常高频但重复的工作链路：
- 自动理解需求
- 拆分测试点
- 生成结构化测试用例
- 分析缺陷并给出修复建议
- 输出可复用的测试报告

项目支持两种运行模式：
- `mock` 模式：不依赖真实大模型 API，便于本地快速联调
- `real api` 模式：接入 OpenAI 兼容接口，获得真实推理结果

适合个人独立开发、课程项目、测试平台原型验证，也可作为后续扩展接口自动化与 CI 集成的基础。

---

## 2. 技术栈
- 后端：`Java 17`、`Spring Boot 3`、`Spring Data JPA`
- 前端：`Vue 3`、`Vite`、`Axios`
- 数据库：`MySQL 8`
- AI 接入：`OpenAI 兼容协议`（可切换 `mock-mode`）

---

## 3. Agent 工作流程
1. **需求输入**：用户提交原始需求文本
2. **需求理解**：Agent 识别功能点、边界条件、潜在风险
3. **测试点拆分**：按功能/异常/边界等维度拆解测试点
4. **用例生成**：产出结构化测试用例（前置条件、步骤、预期）
5. **缺陷分析**：针对缺陷描述给出根因候选与修复建议
6. **报告生成**：汇总测试结果、风险与结论，形成测试报告

---

## 4. 项目目录结构
```text
AgentProject1/
├─ backend/                                # Spring Boot 后端
│  ├─ pom.xml
│  └─ src/main/
│     ├─ java/com/example/aitestagent/
│     │  ├─ agent/                         # Prompt 模板等
│     │  ├─ config/                        # 配置类（含 AI 配置）
│     │  ├─ controller/                    # 对外 API
│     │  ├─ dto/                           # 请求响应模型
│     │  ├─ entity/                        # JPA 实体
│     │  ├─ llm/                           # LLM 客户端封装
│     │  ├─ repository/                    # 数据访问层
│     │  ├─ service/                       # 核心业务编排
│     │  └─ AiTestAgentApplication.java
│     └─ resources/
│        ├─ application.yml
│        └─ sql/schema.sql                 # 初始化建表脚本
├─ frontend/                               # Vue 3 前端
│  ├─ package.json
│  └─ src/
│     ├─ api/                              # 前端 API 调用封装
│     ├─ router/
│     ├─ views/
│     ├─ App.vue
│     └─ main.js
└─ README.md
```

---

## 5. 数据库初始化方式
### 方式 A：手动执行建表脚本（推荐）
1. 本地准备 `MySQL 8`
2. 创建数据库：
```sql
CREATE DATABASE ai_test_agent DEFAULT CHARACTER SET utf8mb4;
```
3. 执行脚本：`backend/src/main/resources/sql/schema.sql`

### 方式 B：JPA 自动建表
当前 `application.yml` 中配置了：
- `spring.jpa.hibernate.ddl-auto: update`

启动后端时会按实体自动补充表结构（生产环境建议改为更严格策略并配合迁移工具）。

---

## 6. 如何配置 API Key
项目通过环境变量读取 API Key，避免明文写入仓库。

`backend/src/main/resources/application.yml` 关键配置：
- `app.ai.base-url`
- `app.ai.api-key: ${OPENAI_API_KEY:}`
- 数据库账号密码与 AI 配置统一从环境变量读取，示例见根目录 `.env.example`
- `app.ai.model`
- `app.ai.mock-mode`

### Windows PowerShell（当前会话）
```powershell
$env:OPENAI_API_KEY="你的Key"
$env:DB_PASSWORD="你的数据库密码"
```

### 切换为真实 API 模式
将 `app.ai.mock-mode` 设置为 `false`，并确保：
- 已配置 `OPENAI_API_KEY`
- `app.ai.base-url` 指向可用服务

---

## 7. 如何启动后端
```bash
cd backend
mvn spring-boot:run
```
默认地址：`http://localhost:8080`

健康检查：
- `GET /api/health`

---

## 8. 如何启动前端
```bash
cd frontend
npm install
npm run dev
```
默认地址：`http://localhost:5173`

当前前端 API 基础地址在 `frontend/src/api/agent.js` 中配置为：
- `http://localhost:8080`

---

## 9. mock 模式说明
`mock-mode=true` 时：
- 后端不依赖真实大模型 API
- 可快速验证前后端流程、接口协议、页面交互
- 适合离线开发、教学演示、联调早期阶段

`mock-mode=false` 时：
- 后端调用真实模型服务
- 结果质量取决于模型能力、Prompt 和输入质量

建议开发流程：
- 先 `mock` 联调流程
- 再切换真实 API 优化提示词与结构化输出

---

## 10. 示例输入
### 需求分析输入示例
```json
{
  "requirement": "用户可以通过手机号+验证码登录，验证码5分钟有效，连续输错5次需锁定10分钟。"
}
```

### 缺陷分析输入示例
```json
{
  "defectText": "登录接口在验证码过期后仍返回成功，导致未授权登录。"
}
```

---

## 11. 示例输出
### 测试用例示例（节选）
```json
{
  "cases": [
    {
      "title": "验证码在有效期内登录成功",
      "precondition": "用户已获取验证码且未过期",
      "steps": "输入手机号和验证码后提交",
      "expected": "登录成功并返回用户会话"
    },
    {
      "title": "验证码过期登录失败",
      "precondition": "验证码已超过5分钟",
      "steps": "输入手机号和过期验证码后提交",
      "expected": "返回验证码过期提示，不创建会话"
    }
  ]
}
```

### 缺陷分析示例（节选）
```json
{
  "riskLevel": "HIGH",
  "rootCause": "服务端未校验验证码过期时间或校验逻辑失效",
  "fixSuggestion": "在鉴权链路增加过期时间强校验，并补充回归用例"
}
```

---

## 12. 项目亮点
- 面向测试场景的端到端 Agent 链路，覆盖“需求 -> 用例 -> 缺陷 -> 报告”
- 支持 `mock` 与真实 API 双模式，兼顾开发效率与真实效果
- 后端按模块分层（controller/service/repository/llm），便于维护与扩展
- 用例与报告输出可结构化，利于后续接入自动化执行与数据平台
- 可本地独立运行，适合个人持续迭代

---

## 13. 后续扩展方向
- 接口自动化测试生成与执行（对接 Postman/Newman 或自研执行器）
- 与 CI/CD 集成，在提交后自动触发测试与报告生成
- 历史缺陷知识库与相似缺陷检索，提升根因分析准确率
- 多模型路由与成本控制策略（按任务类型选择模型）
- 测试覆盖率可视化、质量趋势追踪与项目级对比
- 引入权限与项目空间，实现多人协作测试管理

---


