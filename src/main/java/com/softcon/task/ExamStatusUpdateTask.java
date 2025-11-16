package com.softcon.task;

import com.softcon.entity.Exam;
import com.softcon.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Component
public class ExamStatusUpdateTask {

    private static final Logger logger = Logger.getLogger(ExamStatusUpdateTask.class.getName());

    @Autowired
    private ExamService examService;

    @Scheduled(cron = "0 */1 * * * ?")
    public void updateExpiredExams() {
        logger.info("开始执行考试状态更新任务...");
        try {
            SimpleDateFormat fmtIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            SimpleDateFormat fmtDb = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();

            List<Exam> exams = examService.getAllExams();
            int updated = 0;
            for (Exam exam : exams) {
                if (exam.getEndTime() != null && exam.getStartTime() != null) {
                    Date start;
                    Date end;
                    try { start = fmtIso.parse(exam.getStartTime()); } catch (Exception pe) { start = fmtDb.parse(exam.getStartTime()); }
                    try { end = fmtIso.parse(exam.getEndTime()); } catch (Exception pe) { end = fmtDb.parse(exam.getEndTime()); }
                    if (now.before(start)) {
                        if (!"未开始".equals(exam.getStatus())) { exam.setStatus("未开始"); examService.updateExam(exam); updated++; }
                    } else if (!now.after(end)) {
                        if (!"进行中".equals(exam.getStatus())) { exam.setStatus("进行中"); examService.updateExam(exam); updated++; }
                    } else {
                        if (!"已结束".equals(exam.getStatus())) { exam.setStatus("已结束"); examService.updateExam(exam); updated++; }
                    }
                }
            }
            logger.info("考试状态更新任务执行完成，共更新了 " + updated + " 个考试");
        } catch (Exception e) {
            logger.severe("执行考试状态更新任务时发生错误: " + e.getMessage());
        }
    }
}