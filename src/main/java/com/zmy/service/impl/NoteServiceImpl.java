package com.zmy.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmy.common.Result;
import com.zmy.exception.NoteException.NoteNoExistedException;
import com.zmy.mapper.NoteMapper;
import com.zmy.mapper.ReviewScheduleMapper;
import com.zmy.pojo.entity.Review_schedule;
import com.zmy.pojo.entity.Note;
import com.zmy.pojo.form.Update.UpdateNoteForm;
import com.zmy.pojo.form.add.AddNoteForm;
import com.zmy.pojo.query.NoteQuery;
import com.zmy.pojo.vo.NoteVo;
import com.zmy.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Slf4j
@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements NoteService {

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private ReviewScheduleMapper reviewScheduleMapper;

    @Value("${spring.ai.deepseek.api-key}")
    private String deepseekApiKey;

    @Value("${spring.ai.deepseek.chat.options.model:deepseek-chat}")
    private String deepseekModel;

    @Value("${proxy.enabled:false}")
    private boolean proxyEnabled;

    @Value("${proxy.host:}")
    private String proxyHost;

    @Value("${proxy.port:0}")
    private int proxyPort;

    private RestTemplate restTemplate;

    @jakarta.annotation.PostConstruct
    public void init() {
        // 创建RestTemplate并配置代理
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        // 如果启用了代理，配置代理
        if (proxyEnabled && proxyHost != null && !proxyHost.isEmpty() && proxyPort > 0) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            factory.setProxy(proxy);
            log.info("已配置HTTP代理: {}:{}", proxyHost, proxyPort);
        } else {
            log.info("未启用代理配置");
        }
        
        factory.setConnectTimeout(30000); // 连接超时30秒
        factory.setReadTimeout(60000);    // 读取超时60秒
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public Result<?> getInfo(Integer id) {
        NoteVo noteVo = noteMapper.selectVoById(id);
        if (noteVo == null || noteVo.getDeleteFlag() == 1) {
            throw new NoteNoExistedException();
        }
        return Result.success(noteVo);
    }

    @Override
    public Result<?> listPage(NoteQuery query) {
        log.info("分页参数: current={}, size={}", query.getCurrentPage(), query.getPageSize());
        Page<NoteVo> page = new Page<>(query.getCurrentPage(), query.getPageSize());
        noteMapper.listPage(page, query);
        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("currentPage", page.getCurrent());
        data.put("pageNumber", page.getPages());
        data.put("records", page.getRecords());
        return Result.success(data);
    }

    @Override
    @Transactional
    public Result<?> addOne(AddNoteForm addForm) {
        Note newNote = new Note();
        newNote.setUserId(addForm.getUserId());
        newNote.setSubjectId(addForm.getSubjectId());
        newNote.setTagId(addForm.getTagId());
        newNote.setTitle(addForm.getTitle());
        newNote.setContent(addForm.getContent());
        newNote.setDuration(0f);
        newNote.setCreateTime(LocalDateTime.now());
        newNote.setUpdateTime(LocalDateTime.now());
        newNote.setDeleteFlag(0);
        noteMapper.insert(newNote);

        // 创建笔记时，向review_schedule表中插入一条数据
        // 状态标记为0，times标记为1，时间为当前时间+1天（第一次复习时间）
        Review_schedule reviewSchedule = new Review_schedule();
        reviewSchedule.setUserId(addForm.getUserId());
        reviewSchedule.setNoteId(newNote.getNoteId());
        reviewSchedule.setReviewTime(LocalDateTime.now().plusDays(1)); // 第一次复习：1天后
        reviewSchedule.setStatus(0);
        reviewSchedule.setTimes(1);
        reviewScheduleMapper.insert(reviewSchedule);

        return Result.success("笔记添加成功");
    }

    @Override
    public Result<?> updateInfo(UpdateNoteForm updateForm) {
        Note note = noteMapper.selectById(updateForm.getNoteId());
        if (note == null || note.getDeleteFlag() == 1) {
            throw new NoteNoExistedException();
        }
        noteMapper.updateInfo(updateForm);
        return Result.success("笔记更新成功");
    }

    @Override
    public Result<?> addStudyDuration(Integer noteId, Float durationToAdd) {
        Note note = noteMapper.selectById(noteId);
        if (note == null || note.getDeleteFlag() == 1) {
            throw new NoteNoExistedException();
        }
        if (durationToAdd == null) {
            return Result.fail(400, "学习时长不能为空", null);
        }
        Float current = note.getDuration();
        if (current == null) {
            current = 0f;
        }
        note.setDuration(current + durationToAdd);
        note.setUpdateTime(LocalDateTime.now());
        noteMapper.updateById(note);
        return Result.success("学习时长更新成功");
    }

    @Override
    public Result<?> getTotalStudyDuration(Integer userId) {
        if (userId == null) {
            return Result.fail(400, "userId 不能为空", null);
        }
        Float total = noteMapper.selectTotalDurationByUserId(userId);
        if (total == null) total = 0f;
        return Result.success(total);
    }

    @Override
    public Result<?> delete(Integer id) {
        Note note = noteMapper.selectById(id);
        if (note == null || note.getDeleteFlag() == 1) {
            throw new NoteNoExistedException();
        }
        // 逻辑删除
        note.setDeleteFlag(1);
        note.setUpdateTime(LocalDateTime.now());
        noteMapper.updateById(note);
        return Result.success("笔记删除成功");
    }

    @Override
    public Result<?> listReviewNotesInWeek(Integer userId) {
        // 计算时间范围：当前时间到未来一周
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusDays(7);
        
        log.info("查询近一周需要复习的笔记，userId={}, startTime={}, endTime={}", userId, now, endTime);
        
        List<NoteVo> notes = noteMapper.listReviewNotesInWeek(userId, now, endTime);
        return Result.success(notes);
    }

    @Override
    public Result<?> summarizeNote(Integer noteId) {
        try {
            // 获取笔记信息
            Note note = noteMapper.selectById(noteId);
            if (note == null || note.getDeleteFlag() == 1) {
                throw new NoteNoExistedException();
            }

            // 获取笔记内容
            String content = note.getContent();
            if (content == null || content.trim().isEmpty()) {
                return Result.fail(400, "笔记内容为空，无法生成摘要", null);
            }

            // 构建优化的提示词，让AI生成更有效的摘要
            String promptText = String.format(
                "你是一位专业的笔记总结助手。请仔细阅读以下笔记内容，并生成一份简洁、准确、有价值的摘要。\n\n" +
                "【任务要求】\n" +
                "1. 提取笔记的核心信息和关键要点，避免遗漏重要内容\n" +
                "2. 保持逻辑清晰，按照重要程度组织信息\n" +
                "3. 使用简洁、准确的中文表达，避免冗余和重复\n" +
                "4. 如果笔记包含概念、定义、公式、方法等，确保在摘要中体现\n" +
                "5. 如果笔记包含步骤或流程，简要概括主要步骤\n" +
                "6. 摘要字数必须严格控制在100-150字之间（包含标点符号）\n\n" +
                "【输出要求】\n" +
                "- 直接输出摘要内容，不要添加摘要、总结等标题\n" +
                "- 不要使用本文、该笔记等指代词，直接描述内容\n" +
                "- 确保摘要完整、通顺，可以独立理解\n" +
                "- 如果笔记内容较短，确保摘要涵盖主要信息；如果内容较长，提炼最核心的部分\n\n" +
                "【笔记内容】\n%s",
                content
            );

            // 调用DeepSeek API生成摘要
            String summary = callDeepSeekAPI(promptText);

            // 处理返回的摘要，确保字数在合理范围内
            summary = summary.trim();
            if (summary.length() > 200) {
                // 如果超过200字，截取前200字
                summary = summary.substring(0, 200);
                // 确保在句号或标点处截断
                int lastPunctuation = Math.max(
                    Math.max(summary.lastIndexOf('。'), summary.lastIndexOf('！')),
                    summary.lastIndexOf('？')
                );
                if (lastPunctuation > 100) {
                    summary = summary.substring(0, lastPunctuation + 1);
                }
            }

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("summary", summary);
            result.put("charCount", summary.length());
            result.put("noteId", noteId);

            return Result.success(result);

        } catch (NoteNoExistedException e) {
            throw e;
        } catch (Exception e) {
            log.error("笔记摘要生成失败, noteId={}", noteId, e);
            return Result.fail(500, "笔记摘要生成失败: " + e.getMessage(), null);
        }
    }

    /**
     * 调用DeepSeek API生成摘要
     */
    private String callDeepSeekAPI(String promptText) {
        try {
            // DeepSeek API端点（类似OpenAI格式）
            String url = "https://api.deepseek.com/v1/chat/completions";
            
            log.info("调用DeepSeek API, Model: {}", deepseekModel);

            // 构建请求体（类似OpenAI格式）
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", deepseekModel);
            
            // 构建消息列表
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", promptText);
            requestBody.put("messages", List.of(message));
            
            // 设置生成参数
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 500);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(deepseekApiKey); // 使用Bearer Token认证

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = (ResponseEntity<Map<String, Object>>) 
                    (ResponseEntity<?>) restTemplate.postForEntity(url, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                
                // 解析响应（DeepSeek使用类似OpenAI的响应格式）
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> messageMap = (Map<String, Object>) choice.get("message");
                    if (messageMap != null) {
                        String content = (String) messageMap.get("content");
                        if (content != null) {
                            return content.trim();
                        }
                    }
                }
                
                log.error("DeepSeek API响应格式异常: {}", responseBody);
                throw new RuntimeException("DeepSeek API响应格式异常");
            } else {
                log.error("DeepSeek API调用失败，状态码: {}", response.getStatusCode());
                throw new RuntimeException("DeepSeek API调用失败");
            }

        } catch (org.springframework.web.client.ResourceAccessException e) {
            // 网络连接异常
            log.error("调用DeepSeek API网络连接失败，请检查网络连接或API端点", e);
            throw new RuntimeException("网络连接失败，请检查网络连接: " + e.getMessage(), e);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // HTTP客户端错误（如401, 403等）
            log.error("调用DeepSeek API失败，HTTP错误: {}, 响应: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("API调用失败: " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("调用DeepSeek API失败", e);
            throw new RuntimeException("调用DeepSeek API失败: " + e.getMessage(), e);
        }
    }
}

