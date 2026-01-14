# 欢迎来到AgentCore
AgentCore 是一个面向“可配置 Agent + 工具 + RAG 知识库 ”的后端服务，提供统一的对话会话、模型提供商接入、知识库检索增强、工具调用与执行追踪能力。
#### 体验地址：http://fanofacane.agentcore.top/

## 目录
- [项目简介](#项目简介)
- [核心能力](#核心能力)
- [技术栈](#技术栈)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [API 概览](#api-概览)
- [目录结构](#目录结构)

## 项目简介
AgentCore 是一个面向“可配置 Agent + 工具 + RAG 知识库 ”的后端服务，提供统一的对话会话、模型提供商接入、知识库检索增强、工具调用与执行追踪能力。

默认服务地址：
- Base URL：`http://localhost:8088/api`

## 核心能力
- Agent：创建/发布/版本管理、工作区安装与配置、Widget 形态对接
- Chat：流式与非流式对话、上下文与消息存储、工具调用次数限制与执行追踪
- RAG：数据集/知识库管理、文档解析（PDF/Office/Markdown 等）、向量化检索与重排
- Tool：内置工具与外部工具（MCP / SSE）接入、工具开关与配置管理
- Trace：会话与执行链路追踪、工具调用明细、统计聚合
- Auth：JWT 鉴权、外部 API Key、SSO/登录注册/验证码
- Billing/Payment：余额与计费
- Storage：S3/对象存储文件上传、知识库文件存储策略
- MQ：RabbitMQ 事件与异步处理（如 RAG 文档同步与处理）
- HA：高可用网关与上报（可选）


## 技术栈
| 分类 | 技术/组件 |
| --- | --- |
| Runtime | Java 21 |
| Framework | Spring Boot 3.5.x、Spring Web、Validation、WebSocket |
| ORM | MyBatis-Plus |
| DB | PostgreSQL（含向量表支持） |
| MQ | RabbitMQ（Spring AMQP） |
| LLM | LangChain4j（OpenAI / Gemini / Anthropic）、MCP |
| Storage | x-file-storage、S3/OSS 兼容对象存储 |
| Doc | Apache POI、PDFBox、Tika、Flexmark |
| Payment | Stripe、Alipay EasySDK |

## 快速开始
### 1）准备环境
- JDK：21
- PostgreSQL：建议 14+（并准备向量表/扩展，按项目配置落库）
- RabbitMQ：3.11+
- 可选：S3/OSS（用于文件/知识库存储）

### 2）启动服务（开发）
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Windows（CMD/PowerShell）：
```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

或使用 Maven：
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

启动后访问：
- `http://localhost:8088/api`

### 3）打包运行（可选）
```bash
./mvnw -DskipTests package
java -jar target/AgentCore-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### 4）环境变量示例（占位符）
Linux/macOS：
```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=agentcore
export DB_USER=postgres
export DB_PASSWORD=YOUR_PASSWORD

export RABBITMQ_HOST=localhost
export RABBITMQ_PORT=5672
export RABBITMQ_USERNAME=guest
export RABBITMQ_PASSWORD=YOUR_PASSWORD
```

Windows PowerShell：
```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="agentcore"
$env:DB_USER="postgres"
$env:DB_PASSWORD="YOUR_PASSWORD"

$env:RABBITMQ_HOST="localhost"
$env:RABBITMQ_PORT="5672"
$env:RABBITMQ_USERNAME="guest"
$env:RABBITMQ_PASSWORD="YOUR_PASSWORD"
```

## 配置说明
建议通过环境变量注入敏感配置，避免在仓库中提交明文密钥。

常用环境变量（节选）：
| 环境变量 | 说明 | 示例 |
| --- | --- | --- |
| SERVER_PORT | 服务端口 | 8088 |
| DB_HOST / DB_PORT / DB_NAME | PostgreSQL 地址 | localhost / 5432 / agentcore |
| DB_USER / DB_PASSWORD | PostgreSQL 账号 | postgres / ****** |
| RABBITMQ_HOST / RABBITMQ_PORT | RabbitMQ 地址 | localhost / 5672 |
| RABBITMQ_USERNAME / RABBITMQ_PASSWORD | RabbitMQ 账号 | guest / ****** |
| S3_ENDPOINT / S3_BUCKET_NAME | 对象存储 | https://s3.example.com / bucket |
| S3_SECRET_ID / S3_SECRET_KEY | 对象存储凭证 | ****** / ****** |
| HIGH_AVAILABILITY_GATEWAY_URL | HA 网关地址（可选） | http://localhost:8081 |
| HIGH_AVAILABILITY_API_KEY | HA 网关密钥（可选） | ****** |

## API 概览
该项目的接口统一在 `/api` 下（见 `server.servlet.context-path`）。按业务划分的 Controller 包括但不限于：
- Agent：配置、版本、工作区、Session、Widget
- RAG：知识库/数据集、文件、检索、发布与市场
- Tool：工具管理
- Trace：执行追踪
- Login/Auth：登录/注册/SSO/验证码
- Payment：订单/支付
- Open/Upload：开放接口与文件上传

请求示例（需鉴权时请先获取 JWT 并加到 Header）：
```bash
curl "http://localhost:8088/api/agents/workspaces/agents" \
  -H "Authorization: Bearer YOUR_JWT"
```

## 目录结构
```text
src/main/java/com/sky/AgentCore
  config/            配置、拦截器、工厂、异常处理、MQ
  controller/        REST API
  service/           应用服务与领域服务
  mapper/            MyBatis-Plus Mapper
  dto/               请求/响应/实体 DTO
  enums/             枚举定义
  constant/          常量与提示词模板
src/main/resources
  application-dev.yml
```

模块职责（按包维度）：
| 包/目录 | 职责 |
| --- | --- |
| controller/ | 对外 REST API（Agent/RAG/Tool/Trace/Login/Payment 等） |
| service/ | 应用服务与领域服务实现（对话、计费、检索、存储等） |
| dto/ | 请求/响应对象、实体对象、领域事件等 |
| mapper/ | MyBatis-Plus Mapper（数据访问层） |
| config/ | 统一配置、拦截器、工厂注册、异常处理、MQ 等 |
| constant/ | 常量、提示词模板、键名等 |
| enums/ | 业务枚举 |




