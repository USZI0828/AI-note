package com.zmy.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmy.common.Result;
import com.zmy.exception.TaskException.TaskExistedException;
import com.zmy.exception.TaskException.TaskNoExistedException;
import com.zmy.mapper.TaskMapper;
import com.zmy.mapper.TagMapper;
import com.zmy.mapper.NoteMapper;
import com.zmy.pojo.entity.Task;
import com.zmy.pojo.form.Update.UpdateTaskForm;
import com.zmy.pojo.form.add.AddTaskForm;
import com.zmy.pojo.query.TaskDeadlineQuery;
import com.zmy.pojo.query.TaskQuery;
import com.zmy.pojo.vo.NoteVo;
import com.zmy.pojo.vo.TaskTodoVo;
import com.zmy.pojo.vo.TaskVo;
import com.zmy.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private NoteMapper noteMapper;

    @Override
    public Result<?> getInfo(Long id) {
        TaskVo taskVo = taskMapper.getInfoById(id);
        if (taskVo == null) {
            throw new TaskNoExistedException();
        }
        // 为单个任务匹配推荐笔记（复用分页匹配逻辑，但仅对该任务执行一次）
        try {
            List<String> keywords = new java.util.ArrayList<>();
            if (taskVo.getTagNames() != null && !taskVo.getTagNames().trim().isEmpty()) {
                for (String tname : taskVo.getTagNames().split(",")) {
                        if (!tname.trim().isEmpty()) {
                            keywords.add(tname.trim());
                        }
                    }
            } else if (taskVo.getTagId() != null && !taskVo.getTagId().trim().isEmpty()) {
                String[] ids = taskVo.getTagId().split(",");
                List<Integer> idInts = new java.util.ArrayList<>();
                    for (String idStr : ids) {
                        try {
                            idInts.add(Integer.parseInt(idStr.trim()));
                        } catch (Exception ignored) {}
                    }
                    if (!idInts.isEmpty()) {
                    List<com.zmy.pojo.entity.Tag> tags = tagMapper.selectBatchIds(idInts);
                        for (com.zmy.pojo.entity.Tag t : tags) {
                            if (t != null && t.getTagName() != null) {
                                keywords.add(t.getTagName());
                            }
                        }
                    }
                }
            if (taskVo.getTaskName() != null && !taskVo.getTaskName().trim().isEmpty()) {
                keywords.add(taskVo.getTaskName().trim());
            }
            if (keywords.isEmpty() || taskVo.getSubjectId() == null) {
                taskVo.setRecommendedNotes(new java.util.ArrayList<NoteVo>());
                log.debug("No keywords or subject for taskId={}, keywords={}, subjectId={}", taskVo.getTaskId(), keywords, taskVo.getSubjectId());
            } else {
                // 不使用数据库的全文检索，改为内存打分：
                // 1) 获取该科目下所有笔记
                List<NoteVo> candidates = noteMapper.selectBySubject(taskVo.getSubjectId().intValue());
                log.debug("Found {} candidate notes for subjectId={}", candidates == null ? 0 : candidates.size(), taskVo.getSubjectId());
                // 2) 更精细的内存打分策略：
                //    - 使用分词（非字母/数字分隔）进行精确匹配（word-boundary），避免子串误匹配
                //    - 标题匹配权重较高（titleWeight），内容较低（contentWeight）
                //    - 计算命中不同关键词的覆盖比（distinctCoverageWeight）
                //    - 使用简单停用词过滤避免噪声
                final double titleWeight = 0.6;
                final double contentWeight = 0.3;
                final double distinctCoverageWeight = 0.1;
                final Set<String> stopwords = new HashSet<>(java.util.Arrays.asList(
                        "的","了","在","是","和","也","有","就","都","而","与","或","及","把","被","为","对","着","其","者","等","以"
                ));
                // 规范化关键词列表，移除停用词，转小写
                List<String> normKeywords = new ArrayList<>();
                for (String k : keywords) {
                    if (k == null) continue;
                    String nk = k.trim().toLowerCase();
                    if (nk.isEmpty()) continue;
                    if (stopwords.contains(nk)) continue;
                    normKeywords.add(nk);
                }
                List<java.util.AbstractMap.SimpleEntry<com.zmy.pojo.vo.NoteVo, Double>> scoreEntries = new ArrayList<>();
                for (com.zmy.pojo.vo.NoteVo note : (candidates == null ? new ArrayList<NoteVo>() : candidates)) {
                    String title = note.getTitle() == null ? "" : note.getTitle();
                    String content = note.getContent() == null ? "" : note.getContent();
                    String titleLower = title.toLowerCase();
                    String contentLower = content.toLowerCase();
                    // 将文本分词为单词列表，供归一化使用
                    int titleWords = Math.max(1, titleLower.split("\\W+").length);
                    int contentWords = Math.max(1, contentLower.split("\\W+").length);
                    int titleMatchCount = 0;
                    int contentMatchCount = 0;
                    java.util.Set<String> matchedKeywords = new java.util.HashSet<>();
                    for (String kw : normKeywords) {
                        if (kw.isEmpty()) continue;
                        // 使用 word-boundary 匹配：简单实现为按非字母数字切分后比较等于
                        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\b" + java.util.regex.Pattern.quote(kw) + "\\b", java.util.regex.Pattern.CASE_INSENSITIVE);
                        java.util.regex.Matcher mt = p.matcher(titleLower);
                        int tm = 0;
                        while (mt.find()) {
                            tm++;
                        }
                        titleMatchCount += tm;
                        java.util.regex.Matcher mc = p.matcher(contentLower);
                        int cm = 0;
                        while (mc.find()) {
                            cm++;
                        }
                        contentMatchCount += cm;
                        if (tm + cm > 0) matchedKeywords.add(kw);
                    }
                    double titleScore = (double) titleMatchCount / (double) titleWords;
                    double contentScore = (double) contentMatchCount / (double) contentWords;
                    double distinctCoverage = normKeywords.isEmpty() ? 0.0 : ((double) matchedKeywords.size() / (double) normKeywords.size());
                    double finalScore = titleWeight * titleScore + contentWeight * contentScore + distinctCoverageWeight * distinctCoverage;
                    scoreEntries.add(new java.util.AbstractMap.SimpleEntry<>(note, finalScore));
                }
                // 3) 按 score 排序并取前三
                List<com.zmy.pojo.vo.NoteVo> topList = new java.util.ArrayList<>();
                scoreEntries.stream()
                        .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                        .limit(3)
                        .forEachOrdered(entry -> topList.add(entry.getKey()));
                log.debug("Top recommended notes count for taskId={} is {}", topList.size(), taskVo.getTaskId());
                // 只返回笔记 id 列表给前端
                List<Integer> noteIds = new ArrayList<>();
                for (com.zmy.pojo.vo.NoteVo n : topList) {
                    if (n != null && n.getNoteId() != null) noteIds.add(n.getNoteId());
                }
                taskVo.setRecommendedNoteIds(noteIds);
                taskVo.setRecommendedNotes(new ArrayList<NoteVo>());
            }
        } catch (Exception e) {
            log.warn("为单个任务匹配推荐笔记时出错，taskId={}, error={}", taskVo.getTaskId(), e.getMessage());
            taskVo.setRecommendedNotes(new java.util.ArrayList<com.zmy.pojo.vo.NoteVo>());
        }
        return Result.success(taskVo);
    }

    @Override
    public Result<?> getInfoWithNotes(Long id) {
        // 复用 getInfo 的逻辑来返回单个任务及其推荐笔记
        return this.getInfo(id);
    }

    @Override
    public Result<?> listPage(TaskQuery query) {
        log.info("分页参数: current={}, size={}", query.getCurrentPage(), query.getPageSize());
        Page<TaskVo> page = new Page<>(query.getCurrentPage(), query.getPageSize());
        taskMapper.listPage(page, query);
        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("currentPage", page.getCurrent());
        data.put("pageNumber", page.getPages());
        data.put("records", page.getRecords());
        return Result.success(data);
    }

    @Override
    public Result<?> addOne(AddTaskForm addForm) {
        Task task = taskMapper.selectByName(addForm.getTaskName());
        if (task != null) {
            throw new TaskExistedException();
        }
        // 创建新任务，默认状态为 草稿
        Task newTask = new Task(null,
                addForm.getUserId(),
                addForm.getSubjectId(),
                addForm.getTagId(),
                addForm.getTaskName(),
                addForm.getDeadline(),
                addForm.getDescription(),
                addForm.getPriority(),
                "草稿",
                addForm.getRemindTime(),
                LocalDateTime.now(),
                null,
                0);
        taskMapper.insert(newTask);
        Map<String, Object> data = new HashMap<>();
        data.put("taskId", newTask.getTaskId());
        return Result.success(data);
    }

    @Override
    public Result<?> updateInfo(UpdateTaskForm updateForm) {
        Task task = taskMapper.selectById(updateForm.getTaskId());
        if (task == null) {
            throw new TaskNoExistedException();
        }
        taskMapper.updateInfo(updateForm);
        return Result.success("任务更新成功");
    }

    @Override
    public Result<?> delete(Long id) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new TaskNoExistedException();
        }
        taskMapper.deleteById(id);
        return Result.success("任务删除成功");
    }

    @Override
    public Result<?> listPageOfTodo(TaskDeadlineQuery query) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now;
        LocalDateTime endTime = now.plusDays(1);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        log.info("分页参数: current={}, size={}", query.getCurrentPage(), query.getPageSize());
        Page<TaskTodoVo> page = new Page<>(query.getCurrentPage(), query.getPageSize());
        taskMapper.listPageOfTodo(page, query);
        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("currentPage", page.getCurrent());
        data.put("pageNumber", page.getPages());
        data.put("records", page.getRecords());
        return Result.success(data);
    }

    @Override
    public Result<?> listPageOfDeadline(TaskDeadlineQuery query) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusDays(1);
        LocalDateTime endTime = now.plusDays(3);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        log.info("分页参数: current={}, size={}", query.getCurrentPage(), query.getPageSize());
        Page<TaskTodoVo> page = new Page<>(query.getCurrentPage(), query.getPageSize());
        taskMapper.listPageOfDeadline(page, query);
        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("currentPage", page.getCurrent());
        data.put("pageNumber", page.getPages());
        data.put("records", page.getRecords());
        return Result.success(data);
    }
}
