package cn.bugstack.mcp.server.weixin.test;

import cn.bugstack.mcp.server.weixin.domain.model.WeiXinNoticeFunctionRequest;
import cn.bugstack.mcp.server.weixin.domain.model.WeiXinNoticeFunctionResponse;
import cn.bugstack.mcp.server.weixin.domain.service.WeiXinNoticeService;
import cn.bugstack.mcp.server.weixin.types.properties.WeiXinApiProperties;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * MCP微信通知服务集成测试
 * 
 * @author 测试
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class IntegrationTest {

    @Resource
    private WeiXinNoticeService weiXinNoticeService;
    
    @Resource
    private WeiXinApiProperties weiXinApiProperties;

    /**
     * 测试完整的MCP微信通知功能
     */
    @Test
    public void test_complete_weixin_notice_flow() throws IOException {
        log.info("=== 开始测试完整的MCP微信通知流程 ===");
        
        // 1. 准备测试数据
        WeiXinNoticeFunctionRequest request = new WeiXinNoticeFunctionRequest();
        request.setPlatform("MCP测试平台");
        request.setSubject("集成测试通知");
        request.setDescription("这是一条来自MCP微信服务集成测试的消息，验证从领域服务到微信API的完整调用链路。测试时间：" + System.currentTimeMillis());
        request.setJumpUrl("https://github.com/your-repo/mcp-server-weixin");
        
        log.info("测试请求数据: {}", JSON.toJSONString(request));
        
        // 2. 调用领域服务
        log.info("调用微信通知服务...");
        WeiXinNoticeFunctionResponse response = weiXinNoticeService.weixinNotice(request);
        
        // 3. 验证响应
        assertNotNull("响应不能为空", response);
        assertTrue("通知发送应该成功", response.isSuccess());
        
        log.info("✅ 微信通知发送成功！响应: {}", JSON.toJSONString(response));
        log.info("=== MCP微信通知集成测试完成 ===");
    }

    /**
     * 测试多条消息发送
     */
    @Test
    public void test_multiple_notifications() throws IOException {
        log.info("=== 测试发送多条通知消息 ===");
        
        // 发送3条不同的测试消息
        for (int i = 1; i <= 3; i++) {
            WeiXinNoticeFunctionRequest request = new WeiXinNoticeFunctionRequest();
            request.setPlatform("测试平台 " + i);
            request.setSubject("批量测试消息 #" + i);
            request.setDescription("这是第 " + i + " 条测试消息，用于验证系统的稳定性和可靠性。发送时间：" + System.currentTimeMillis());
            request.setJumpUrl("https://github.com/test" + i);
            
            log.info("发送第 {} 条消息: {}", i, request.getSubject());
            
            WeiXinNoticeFunctionResponse response = weiXinNoticeService.weixinNotice(request);
            
            assertNotNull("第 " + i + " 条消息响应不能为空", response);
            assertTrue("第 " + i + " 条消息应该发送成功", response.isSuccess());
            
            // 稍微延迟避免频率限制
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        log.info("✅ 批量消息发送测试完成");
    }

    /**
     * 测试异常情况处理
     */
    @Test
    public void test_error_handling() {
        log.info("=== 测试异常情况处理 ===");
        
        // 测试空请求
        try {
            weiXinNoticeService.weixinNotice(null);
            fail("空请求应该抛出异常");
        } catch (Exception e) {
            log.info("✅ 空请求正确抛出异常: {}", e.getMessage());
        }
        
        // 测试不完整的请求数据
        WeiXinNoticeFunctionRequest incompleteRequest = new WeiXinNoticeFunctionRequest();
        incompleteRequest.setPlatform("测试");
        // 缺少其他必要字段
        
        try {
            WeiXinNoticeFunctionResponse response = weiXinNoticeService.weixinNotice(incompleteRequest);
            log.info("不完整请求的响应: {}", JSON.toJSONString(response));
            // 这里可能不会失败，取决于微信API的验证策略
        } catch (Exception e) {
            log.info("不完整请求处理异常: {}", e.getMessage());
        }
        
        log.info("✅ 异常处理测试完成");
    }

    /**
     * 测试系统性能 - 压力测试
     */
    @Test
    public void test_performance() throws IOException {
        log.info("=== 开始性能测试 ===");
        
        int testCount = 5; // 发送5条消息测试性能
        long startTime = System.currentTimeMillis();
        
        for (int i = 1; i <= testCount; i++) {
            WeiXinNoticeFunctionRequest request = new WeiXinNoticeFunctionRequest();
            request.setPlatform("性能测试");
            request.setSubject("性能测试消息 #" + i);
            request.setDescription("性能测试消息，序号：" + i + "，时间戳：" + System.currentTimeMillis());
            request.setJumpUrl("https://github.com/performance-test");
            
            long messageStart = System.currentTimeMillis();
            WeiXinNoticeFunctionResponse response = weiXinNoticeService.weixinNotice(request);
            long messageEnd = System.currentTimeMillis();
            
            assertTrue("消息 " + i + " 应该发送成功", response.isSuccess());
            log.info("消息 {} 发送耗时: {} ms", i, messageEnd - messageStart);
            
            // 避免频率限制
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double avgTime = (double) totalTime / testCount;
        
        log.info("✅ 性能测试完成");
        log.info("总耗时: {} ms", totalTime);
        log.info("平均耗时: {:.2f} ms/条", avgTime);
        log.info("发送速率: {:.2f} 条/秒", 1000.0 / avgTime);
        
        // 验证性能要求（根据实际需求调整）
        assertTrue("平均发送时间应该小于10秒", avgTime < 10000);
    }

    /**
     * 测试配置验证
     */
    @Test
    public void test_configuration_validation() {
        log.info("=== 测试配置验证 ===");
        
        // 验证所有必要配置都已设置
        assertNotNull("APP ID不能为空", weiXinApiProperties.getAppid());
        assertNotNull("APP Secret不能为空", weiXinApiProperties.getAppsecret());
        assertNotNull("Template ID不能为空", weiXinApiProperties.getTemplate_id());
        assertNotNull("To User不能为空", weiXinApiProperties.getTouser());
        
        // 验证配置格式
        assertTrue("APP ID格式错误", weiXinApiProperties.getAppid().startsWith("wx"));
        assertTrue("APP Secret长度错误", weiXinApiProperties.getAppsecret().length() == 32);
        
        log.info("✅ 配置验证通过");
        log.info("当前配置 - APP ID: {}", weiXinApiProperties.getAppid());
        log.info("当前配置 - Template ID: {}", weiXinApiProperties.getTemplate_id());
        log.info("当前配置 - To User: {}", weiXinApiProperties.getTouser());
    }

    /**
     * 测试长文本消息
     */
    @Test
    public void test_long_message() throws IOException {
        log.info("=== 测试长文本消息 ===");
        
        // 构造长文本
        StringBuilder longDescription = new StringBuilder();
        longDescription.append("这是一条长文本测试消息。");
        for (int i = 1; i <= 10; i++) {
            longDescription.append("段落").append(i).append("：这是测试内容的第").append(i).append("段，用于验证系统对长文本的处理能力。");
        }
        
        WeiXinNoticeFunctionRequest request = new WeiXinNoticeFunctionRequest();
        request.setPlatform("长文本测试");
        request.setSubject("长文本消息测试");
        request.setDescription(longDescription.toString());
        request.setJumpUrl("https://github.com/long-text-test");
        
        log.info("长文本长度: {} 字符", request.getDescription().length());
        
        WeiXinNoticeFunctionResponse response = weiXinNoticeService.weixinNotice(request);
        
        assertNotNull("长文本消息响应不能为空", response);
        assertTrue("长文本消息应该发送成功", response.isSuccess());
        
        log.info("✅ 长文本消息测试完成");
    }
} 