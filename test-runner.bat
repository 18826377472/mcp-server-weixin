@echo off
echo ================================
echo MCP微信通知服务测试脚本
echo ================================

echo.
echo 选择测试类型:
echo 1. 基础API测试 (ApiTest)
echo 2. 完整集成测试 (IntegrationTest)
echo 3. 运行所有测试
echo 4. 单独测试获取AccessToken
echo 5. 单独测试发送消息
echo.

set /p choice=请输入选择 (1-5): 

if "%choice%"=="1" (
    echo 运行基础API测试...
    mvn test -Dtest=ApiTest
) else if "%choice%"=="2" (
    echo 运行完整集成测试...
    mvn test -Dtest=IntegrationTest
) else if "%choice%"=="3" (
    echo 运行所有测试...
    mvn test
) else if "%choice%"=="4" (
    echo 测试获取AccessToken...
    mvn test -Dtest=ApiTest#test_get_access_token
) else if "%choice%"=="5" (
    echo 测试发送消息...
    mvn test -Dtest=ApiTest#test_template_message
) else (
    echo 无效选择，退出。
    goto end
)

echo.
echo ================================
echo 测试完成！
echo ================================

:end
pause 