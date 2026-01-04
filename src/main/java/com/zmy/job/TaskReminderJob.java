package com.zmy.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.zmy.mapper.TaskMapper;
import com.zmy.mapper.UserMapper;
import com.zmy.pojo.entity.Task;
import com.zmy.pojo.entity.User;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TaskReminderJob {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JavaMailSender mailSender;

    @XxlJob("taskReminderJobHandler")
    public void taskReminderHandler() throws Exception {
        XxlJobHelper.log("开始执行任务提醒扫描");
        System.out.println("开始执行任务提醒扫描");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusHours(24);
        QueryWrapper<Task> qw = new QueryWrapper<>();
        qw.isNotNull("remind_time")
                .eq("remind_sent", 0)
                .ge("remind_time", now)
                .le("remind_time", end)
                .ne("status", "已完成");

        List<Task> tasks = taskMapper.selectList(qw);
        for (Task t : tasks) {
            try {
                User user = userMapper.selectById(t.getUserId());
                if (user == null || user.getEmail() == null) {
                    XxlJobHelper.log("用户或邮箱不存在，taskId=" + t.getTaskId());
                    continue;
                }
                // 构建邮件
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom("3277512331@qq.com");
                helper.setTo(user.getEmail());
                helper.setSubject("任务提醒：您有任务即将到期");
                String content = String.format("您好，您有任务《%s》，提醒时间：%s，请及时处理。",
                        t.getTaskName(),
                        t.getRemindTime() != null ? t.getRemindTime().toString() : "");
                helper.setText(content, false);
                mailSender.send(message);

                // 标记为已发送，避免重复发送
                t.setRemindSent(1);
                taskMapper.updateById(t);
                XxlJobHelper.log("已发送提醒邮件，taskId=" + t.getTaskId());
            } catch (Exception ex) {
                XxlJobHelper.log("发送提醒失败，taskId=" + t.getTaskId() + " error:" + ex.getMessage());
            }
        }
        XxlJobHelper.log("任务提醒扫描完成");
    }
}


