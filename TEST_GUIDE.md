# MCP微信通知服务测试指南

## 概述

本项目提供了完整的测试套件来验证MCP微信公众号消息通知服务的功能。测试包括基础API测试和完整的集成测试。

## 测试前准备

### 1. 检查配置文件

确保 `src/main/resources/application.yml` 中的微信API配置正确：

```yaml
weixin:
  api:
    original_id: 你的公众号原始ID
    app-id: 你的AppID  
    app-secret: 你的AppSecret
    template_id: 你的模板消息ID
    touser: 接收消息的用户OpenID
```

### 2. 验证微信公众号配置

- 确保微信公众号已认证或为测试号
- 模板消息已创建并审核通过
- 接收用户已关注公众号

## 测试类说明

### ApiTest.java - 基础API测试

这个测试类专注于测试底层的微信API调用：

- **test_get_access_token()**: 测试动态获取AccessToken
- **test_template_message()**: 测试发送模板消息
- **test_send_message_with_invalid_token()**: 测试无效Token错误处理
- **test_print_config()**: 验证配置完整性

### IntegrationTest.java - 集成测试

这个测试类测试完整的业务流程：

- **test_complete_weixin_notice_flow()**: 完整的MCP通知流程测试
- **test_multiple_notifications()**: 批量消息发送测试
- **test_error_handling()**: 异常情况处理测试
- **test_performance()**: 性能和压力测试
- **test_configuration_validation()**: 配置验证测试
- **test_long_message()**: 长文本消息测试

## 运行测试

### 方法1: 使用测试脚本

运行 `test-runner.bat` 脚本，根据提示选择测试类型：

```bash
test-runner.bat
```

### 方法2: 使用Maven命令

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=ApiTest
mvn test -Dtest=IntegrationTest

# 运行特定测试方法
mvn test -Dtest=ApiTest#test_get_access_token
mvn test -Dtest=IntegrationTest#test_complete_weixin_notice_flow
```

### 方法3: 在IDE中运行

在IDE中右键点击测试类或方法，选择"Run Test"。

## 测试结果解读

### 成功的测试输出示例

```
=== 开始测试完整的MCP微信通知流程 ===
测试请求数据: {"platform":"MCP测试平台","subject":"集成测试通知","description":"这是一条来自MCP微信服务集成测试的消息...","jumpUrl":"https://github.com/your-repo/mcp-server-weixin"}
调用微信通知服务...
✅ 微信通知发送成功！响应: {"success":true}
=== MCP微信通知集成测试完成 ===
```

### 可能的错误及解决方案

#### 1. AccessToken获取失败

**错误信息**: `获取accessToken失败，错误码：40013`

**原因**: AppID或AppSecret配置错误

**解决方案**: 
- 检查微信公众号后台的AppID和AppSecret
- 确保配置文件中的值正确无误

#### 2. 模板消息发送失败

**错误信息**: `发送微信模板消息失败！状态码：400`

**可能原因**:
- 模板ID不存在或未审核通过
- 用户未关注公众号
- 模板参数不匹配

**解决方案**:
- 检查模板消息配置
- 确认用户已关注公众号
- 验证模板参数名称

#### 3. 网络连接问题

**错误信息**: `java.net.ConnectException`

**解决方案**:
- 检查网络连接
- 确认防火墙设置
- 验证代理配置（如有）

## 测试最佳实践

### 1. 逐步测试

建议按以下顺序执行测试：

1. 首先运行配置验证: `test_print_config`
2. 然后测试Token获取: `test_get_access_token`
3. 再测试单条消息: `test_template_message`
4. 最后运行完整测试: `test_complete_weixin_notice_flow`

### 2. 频率控制

微信API有频率限制，建议：
- 单次测试间隔至少1秒
- 避免短时间内大量请求
- 使用测试号进行开发测试

### 3. 监控日志

注意观察日志输出：
- AccessToken获取和缓存状态
- HTTP请求和响应详情
- 错误信息和堆栈跟踪

## 故障排除

### 常见问题诊断

1. **运行 `test_print_config` 检查配置**
2. **运行 `test_get_access_token` 验证API连接**
3. **检查微信公众号后台设置**
4. **确认模板消息格式和参数**

### 调试技巧

- 启用详细日志: 在application.yml中设置logging.level.cn.bugstack=DEBUG
- 使用微信开发者工具进行接口调试
- 查看微信公众号后台的错误日志

## 生产环境注意事项

- 不要在生产环境运行压力测试
- 确保敏感配置信息的安全性
- 建立监控和告警机制
- 定期验证Token获取和消息发送功能

## 联系支持

如果遇到问题：
1. 查看微信官方文档
2. 检查项目的GitHub Issues
3. 联系项目维护者 