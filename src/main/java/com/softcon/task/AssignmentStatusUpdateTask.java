package com.softcon.task;

import com.softcon.entity.Assignment;
import com.softcon.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * 作业状态定时更新任务
 * 在每天凌晨0点检查并更新已到期作业的状态
 */
@Component
public class AssignmentStatusUpdateTask {
    
    private static final Logger logger = Logger.getLogger(AssignmentStatusUpdateTask.class.getName());
    
    @Autowired
    private AssignmentService assignmentService;
    
    /**
     * 每天凌晨0点执行一次
     * 格式：秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateExpiredAssignments() {
        logger.info("开始执行作业状态更新任务...");
        
        try {
            // 获取当前日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());
            
            // 获取所有作业
            List<Assignment> allAssignments = assignmentService.getAllAssignments();
            
            int updatedCount = 0;
            for (Assignment assignment : allAssignments) {
                // 检查作业是否未完成且已过截止日期
                if (!assignment.getIsCompleted() && assignment.getDeadline() != null) {
                    // 比较截止日期和当前日期
                    Date deadlineDate = sdf.parse(assignment.getDeadline());
                    Date todayDate = sdf.parse(today);
                    
                    if (deadlineDate.before(todayDate) || deadlineDate.equals(todayDate)) {
                        // 更新作业状态为已完成
                        assignment.setIsCompleted(true);
                        assignmentService.updateAssignment(assignment);
                        updatedCount++;
                        logger.info("更新作业状态: 作业ID=" + assignment.getId() + ", 标题=" + assignment.getTitle());
                    }
                }
            }
            
            logger.info("作业状态更新任务执行完成，共更新了 " + updatedCount + " 个作业");
            
        } catch (ParseException e) {
            logger.severe("日期解析错误: " + e.getMessage());
        } catch (Exception e) {
            logger.severe("执行作业状态更新任务时发生错误: " + e.getMessage());
        }
    }
}