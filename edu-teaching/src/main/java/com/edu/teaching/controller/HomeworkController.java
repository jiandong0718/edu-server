package com.edu.teaching.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.Result;
import com.edu.teaching.domain.entity.Homework;
import com.edu.teaching.domain.entity.HomeworkSubmit;
import com.edu.teaching.domain.vo.HomeworkStatsVO;
import com.edu.teaching.service.HomeworkService;
import com.edu.teaching.service.HomeworkSubmitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作业管理控制器
 */
@RestController
@RequestMapping("/teaching/homework")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;
    private final HomeworkSubmitService homeworkSubmitService;

    /**
     * 分页查询作业列表
     */
    @GetMapping("/page")
    public Result<IPage<Homework>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Homework query) {
        IPage<Homework> page = new Page<>(pageNum, pageSize);
        return Result.success(homeworkService.getHomeworkPage(page, query));
    }

    /**
     * 获取作业详情
     */
    @GetMapping("/{id}")
    public Result<Homework> getById(@PathVariable Long id) {
        return Result.success(homeworkService.getById(id));
    }

    /**
     * 创建作业
     */
    @PostMapping
    public Result<Void> create(@RequestBody Homework homework) {
        homeworkService.createHomework(homework);
        return Result.success();
    }

    /**
     * 更新作业
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Homework homework) {
        homework.setId(id);
        homeworkService.updateById(homework);
        return Result.success();
    }

    /**
     * 发布作业
     */
    @PutMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        homeworkService.publish(id);
        return Result.success();
    }

    /**
     * 关闭作业
     */
    @PutMapping("/{id}/close")
    public Result<Void> close(@PathVariable Long id) {
        homeworkService.close(id);
        return Result.success();
    }

    /**
     * 删除作业
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        homeworkService.deleteHomework(id);
        return Result.success();
    }

    /**
     * 获取作业提交列表
     */
    @GetMapping("/{id}/submits")
    public Result<List<HomeworkSubmit>> getSubmits(@PathVariable Long id) {
        return Result.success(homeworkSubmitService.getByHomeworkId(id));
    }

    /**
     * 获取作业统计信息
     */
    @GetMapping("/{id}/stats")
    public Result<HomeworkStatsVO> getStats(@PathVariable Long id) {
        return Result.success(homeworkService.getHomeworkStats(id));
    }

    /**
     * 分页查询作业提交
     */
    @GetMapping("/submit/page")
    public Result<IPage<HomeworkSubmit>> submitPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HomeworkSubmit query) {
        IPage<HomeworkSubmit> page = new Page<>(pageNum, pageSize);
        return Result.success(homeworkSubmitService.getSubmitPage(page, query));
    }

    /**
     * 提交作业
     */
    @PostMapping("/submit")
    public Result<Void> submit(@RequestBody HomeworkSubmit submit) {
        homeworkSubmitService.submit(submit);
        return Result.success();
    }

    /**
     * 批改作业
     */
    @PutMapping("/submit/{id}/review")
    public Result<Void> review(
            @PathVariable Long id,
            @RequestParam Integer score,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String comment) {
        homeworkSubmitService.review(id, score, grade, comment);
        return Result.success();
    }

    /**
     * 退回作业
     */
    @PutMapping("/submit/{id}/return")
    public Result<Void> returnSubmit(
            @PathVariable Long id,
            @RequestParam String reason) {
        homeworkSubmitService.returnSubmit(id, reason);
        return Result.success();
    }

    /**
     * 查询学员作业提交记录
     */
    @GetMapping("/submit/student/{studentId}")
    public Result<List<HomeworkSubmit>> getStudentSubmits(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long classId) {
        return Result.success(homeworkSubmitService.getStudentSubmits(studentId, classId));
    }
}
