package org.dromara.swimming.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.mail.utils.MailUtils;
import org.dromara.common.sse.dto.SseMessageDto;
import org.dromara.common.sse.utils.SseMessageUtils;
import org.dromara.swimming.domain.SpIotDeviceAlarm;
import org.dromara.swimming.service.IAlarmNotifyService;
import org.dromara.system.domain.SysUserRole;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.mapper.SysUserRoleMapper;
import org.dromara.system.mapper.SysUserMapper;
import org.dromara.system.mapper.SysRoleMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 告警通知服务实现
 *
 * @author swimming
 * @date 2026-02-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmNotifyServiceImpl implements IAlarmNotifyService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;

    @Value("${alarm.notify.role-key:swimming_admin}")
    private String alarmNotifyRoleKey;

    @Value("${alarm.notify.email-enabled:true}")
    private boolean emailEnabled;

    @Value("${alarm.notify.sse-enabled:true}")
    private boolean sseEnabled;

    @Override
    public void sendAlarmNotification(SpIotDeviceAlarm alarm) {
        try {
            // 查询具有指定角色的用户
            List<SysUserVo> adminUsers = getUsersByRoleKey(alarmNotifyRoleKey);

            if (CollUtil.isEmpty(adminUsers)) {
                log.warn("没有找到角色为 {} 的用户，跳过告警通知", alarmNotifyRoleKey);
                return;
            }

            log.info("找到 {} 个角色为 {} 的管理员用户，开始发送告警通知", adminUsers.size(), alarmNotifyRoleKey);

            // 提取用户ID列表和邮箱列表
            List<Long> userIds = adminUsers.stream()
                .map(SysUserVo::getUserId)
                .collect(Collectors.toList());

            List<String> emails = adminUsers.stream()
                .map(SysUserVo::getEmail)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());

            // 发送SSE推送
            if (sseEnabled) {
                sendSseNotification(userIds, alarm);
            }

            // 发送邮件通知
            if (emailEnabled && CollUtil.isNotEmpty(emails)) {
                sendEmailNotification(emails, alarm);
            }

        } catch (Exception e) {
            log.error("发送告警通知失败 - 告警ID: {}, 错误: {}", alarm.getId(), e.getMessage(), e);
        }
    }

    /**
     * 根据角色权限字符串查询用户列表
     */
    private List<SysUserVo> getUsersByRoleKey(String roleKey) {
        try {
            // 1. 先根据role_key查询角色ID
            Long roleId = roleMapper.selectObjs(
                new LambdaQueryWrapper<org.dromara.system.domain.SysRole>()
                    .eq(org.dromara.system.domain.SysRole::getRoleKey, roleKey)
                    .select(org.dromara.system.domain.SysRole::getRoleId)
            ).stream().findFirst().map(obj -> (Long) obj).orElse(null);

            if (roleId == null) {
                log.warn("未找到角色权限为 {} 的角色", roleKey);
                return List.of();
            }

            // 2. 根据角色ID查询用户ID列表
            List<Long> userIds = userRoleMapper.selectObjs(
                new LambdaQueryWrapper<SysUserRole>()
                    .eq(SysUserRole::getRoleId, roleId)
                    .select(SysUserRole::getUserId)
            ).stream().map(obj -> (Long) obj).collect(Collectors.toList());

            if (CollUtil.isEmpty(userIds)) {
                log.warn("角色 {} 下没有关联的用户", roleKey);
                return List.of();
            }

            // 3. 根据用户ID列表查询用户信息
            return userMapper.selectVoList(
                new LambdaQueryWrapper<org.dromara.system.domain.SysUser>()
                    .in(org.dromara.system.domain.SysUser::getUserId, userIds)
                    .eq(org.dromara.system.domain.SysUser::getStatus, "0")
                    .eq(org.dromara.system.domain.SysUser::getDelFlag, "0")
            );

        } catch (Exception e) {
            log.error("查询角色 {} 的用户失败: {}", roleKey, e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * 发送SSE推送通知
     */
    private void sendSseNotification(List<Long> userIds, SpIotDeviceAlarm alarm) {
        try {
            // 构建SSE消息
            String message = buildAlarmMessage(alarm);
            SseMessageDto sseMessage = new SseMessageDto();
            sseMessage.setUserIds(userIds);
            sseMessage.setMessage(message);

            // 发布SSE消息
            SseMessageUtils.publishMessage(sseMessage);

            log.info("SSE告警通知已发送 - 用户数: {}, 告警ID: {}", userIds.size(), alarm.getId());
        } catch (Exception e) {
            log.error("发送SSE通知失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 发送邮件通知
     */
    private void sendEmailNotification(List<String> emails, SpIotDeviceAlarm alarm) {
        try {
            String subject = buildEmailSubject(alarm);
            String content = buildEmailContent(alarm);

            // 发送HTML邮件
            MailUtils.sendHtml(emails, subject, content);

            log.info("告警邮件已发送 - 收件人数: {}, 告警ID: {}", emails.size(), alarm.getId());
        } catch (Exception e) {
            log.error("发送邮件通知失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 构建告警消息（用于SSE推送）
     */
    private String buildAlarmMessage(SpIotDeviceAlarm alarm) {
        return String.format(
            "【设备告警】%s - %s\n设备: %s\n告警内容: %s",
            alarm.getAlarmTitle(),
            alarm.getAlarmLevel(),
            alarm.getDeviceKey(),
            alarm.getAlarmMessage()
        );
    }

    /**
     * 构建邮件主题
     */
    private String buildEmailSubject(SpIotDeviceAlarm alarm) {
        return String.format("【设备告警】%s - %s", alarm.getAlarmTitle(), alarm.getDeviceKey());
    }

    /**
     * 构建邮件内容（HTML格式）
     */
    private String buildEmailContent(SpIotDeviceAlarm alarm) {
        String levelColor = getLevelColor(alarm.getAlarmLevel());
        String levelText = getLevelText(alarm.getAlarmLevel());

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: %s; color: white; padding: 20px; border-radius: 5px 5px 0 0; }
                    .content { background-color: #f9f9f9; padding: 20px; border: 1px solid #ddd; }
                    .info-item { margin: 10px 0; }
                    .label { font-weight: bold; color: #555; }
                    .footer { margin-top: 20px; font-size: 12px; color: #999; text-align: center; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2 style="margin: 0;">🚨 设备告警通知</h2>
                    </div>
                    <div class="content">
                        <div class="info-item">
                            <span class="label">告警级别：</span>
                            <span style="color: %s; font-weight: bold;">%s</span>
                        </div>
                        <div class="info-item">
                            <span class="label">告警标题：</span>
                            <span>%s</span>
                        </div>
                        <div class="info-item">
                            <span class="label">设备标识：</span>
                            <span>%s</span>
                        </div>
                        <div class="info-item">
                            <span class="label">告警类型：</span>
                            <span>%s</span>
                        </div>
                        <div class="info-item">
                            <span class="label">告警内容：</span>
                            <div style="background: white; padding: 10px; border-radius: 3px; margin-top: 5px;">%s</div>
                        </div>
                        <div class="info-item">
                            <span class="label">告警时间：</span>
                            <span>%s</span>
                        </div>
                    </div>
                    <div class="footer">
                        <p>此邮件由系统自动发送，请勿回复</p>
                        <p>游泳馆管理系统 © 2026</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            levelColor,
            levelColor,
            levelText,
            alarm.getAlarmTitle(),
            alarm.getDeviceKey(),
            alarm.getAlarmType(),
            alarm.getAlarmMessage(),
            alarm.getCreateTime() != null ? alarm.getCreateTime().toString() : "未知"
        );
    }

    /**
     * 获取告警级别对应的颜色
     */
    private String getLevelColor(String level) {
        return switch (level) {
            case "critical" -> "#f56c6c";
            case "warning" -> "#e6a23c";
            case "info" -> "#409eff";
            default -> "#909399";
        };
    }

    /**
     * 获取告警级别对应的文本
     */
    private String getLevelText(String level) {
        return switch (level) {
            case "critical" -> "严重";
            case "warning" -> "警告";
            case "info" -> "信息";
            default -> "未知";
        };
    }
}
