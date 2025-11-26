package com.zmy.service.impl;

import com.zmy.exception.NoteException.NoteNoExistedException;
import com.zmy.mapper.NoteMapper;
import com.zmy.pojo.entity.Note;
import com.zmy.pojo.vo.NoteVo;
import com.zmy.service.NotePdfService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.servlet.ServletOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import com.zmy.pojo.query.NoteQuery;

@Slf4j
@Service
public class NotePdfServiceImpl implements NotePdfService {

    @Autowired
    private NoteMapper noteMapper;

    @Override
    public void exportNoteToPdf(Integer noteId, HttpServletResponse response) throws IOException {
        // 查询笔记信息
        Note note = noteMapper.selectById(noteId);
        if (note == null || note.getDeleteFlag() == 1) {
            throw new NoteNoExistedException();
        }

        // 查询笔记的完整信息（包含标签和科目）
        NoteQuery query = new com.zmy.pojo.query.NoteQuery();
        NoteVo noteVo = noteMapper.listPage(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<NoteVo>(1, 1),
                query
        ).getRecords().stream().findFirst().orElse(null);

        if (noteVo == null) {
            throw new NoteNoExistedException();
        }

        // 构建HTML内容并处理换行符
        String htmlContent = prepareHtmlContent(noteVo);
        // 确保 meta 标签自闭合
        htmlContent = htmlContent.replace("<meta charset=\"UTF-8\">", "<meta charset=\"UTF-8\"/>");

        log.info("准备导出PDF，noteId={}, htmlContent=\n{}", noteId, htmlContent);
        // 生成PDF
        byte[] pdfBytes = generatePdf(htmlContent);

        // 设置响应头（支持中文文件名）
        String fileName = String.format("笔记_%s_%s.pdf",
                noteVo.getTitle() != null ? noteVo.getTitle().replaceAll("[\\\\/:*?\"<>|]", "_") : "未命名",
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        );
        String encodedFileName = java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
        response.setContentLength(pdfBytes.length);

        // 写入响应流（try-with-resources 确保 flush & close）
        try (ServletOutputStream out = response.getOutputStream()) {
            out.write(pdfBytes);
            out.flush();
        }

        log.info("笔记导出PDF成功: noteId={}, fileName={}", noteId, fileName);
    }

    /**
     * 构建HTML内容，保留用户在 content 中的原始 HTML 结构
     */
    private String prepareHtmlContent(NoteVo noteVo) {
        String content = noteVo.getContent();
        if (!StringUtils.hasText(content)) {
            return buildFallbackHtml(noteVo);
        }

        String sanitizedContent = sanitizeContentForPdf(content);
        String contentWithImages = processImagesInContent(sanitizedContent, content);

        // 如果用户提供了完整的 HTML 结构，则保留其原始格式
            return contentWithImages;

    }

    /**
     * 规范化内容中的换行符，避免PDF中出现无法解析的 /n /r 字符
     */
    private String sanitizeContentForPdf(String content) {
        if (!StringUtils.hasText(content)) {
            return content;
        }
        String sanitized = content;
        // 先将转义或手动输入的 /n /r、\\n \\r 替换为真实换行
        sanitized = sanitized.replace("/r/n", "\n")
                .replace("/n", "\n")
                .replace("/r", "\n");
        // 统一处理实际存在的回车换行
        sanitized = sanitized.replace("\r\n", "\n").replace("\r", "\n");
        return sanitized;
    }

    private String buildFallbackHtml(NoteVo noteVo) {
        String title = escapeHtml(noteVo.getTitle() != null ? noteVo.getTitle() : "未命名笔记");
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset='UTF-8'>");
        html.append("<style>body { font-family: 'Microsoft YaHei', Arial, sans-serif; padding: 20px; line-height: 1.6; }</style>");
        html.append("</head><body>");
        html.append("<h1>").append(title).append("</h1>");
        html.append("<p>暂无内容</p>");
        html.append("</body></html>");
        return html.toString();
    }

    /**
     * 处理HTML内容中的图片
     * 支持base64图片和URL图片
     */
    private String processImagesInContent(String htmlContent, String originalContent) {
        if (!StringUtils.hasText(htmlContent)) {
            return htmlContent;
        }

        Document doc = Jsoup.parse(htmlContent);
        doc.outputSettings()
                .syntax(Document.OutputSettings.Syntax.html)
                .escapeMode(org.jsoup.nodes.Entities.EscapeMode.base);
        Elements imgElements = doc.select("img");

        for (Element img : imgElements) {
            String src = img.attr("src");
            if (StringUtils.hasText(src)) {
                try {
                    // 处理base64图片
                    if (src.startsWith("data:image")) {
                        // base64图片已经可以直接使用，无需处理
                        continue;
                    }
                    // 处理URL图片 - 转换为base64
                    else if (src.startsWith("http://") || src.startsWith("https://")) {
                        String base64Image = convertUrlToBase64(src);
                        if (base64Image != null) {
                            img.attr("src", base64Image);
                        }
                    }
                    // 处理相对路径或MinIO URL
                    else {
                        // 如果是MinIO的URL，尝试下载并转换为base64
                        if (src.contains("minio") || src.contains("139.9.179.161")) {
                            String base64Image = convertUrlToBase64(src);
                            if (base64Image != null) {
                                img.attr("src", base64Image);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("处理图片失败: {}", src, e);
                    // 如果图片处理失败，移除图片或显示占位符
                    img.attr("src", "");
                }
            }
        }

        String processed = doc.outerHtml();
        return ensureDoctypeCase(originalContent, processed);
    }

    /**
     * 确保处理后内容中的 DOCTYPE 保持原有大小写，避免被 Jsoup 改成小写 <!doctype>
     */
    private String ensureDoctypeCase(String originalContent, String processedContent) {
        if (!StringUtils.hasText(originalContent) || !StringUtils.hasText(processedContent)) {
            return processedContent;
        }
        if (originalContent.contains("<!DOCTYPE") || originalContent.contains("<!doctype")) {
            return processedContent.replaceFirst("(?i)<!doctype", "<!DOCTYPE");
        }
        return processedContent;
    }

    /**
     * 将URL图片转换为base64
     */
    private String convertUrlToBase64(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            try (InputStream inputStream = url.openStream()) {
                byte[] imageBytes = inputStream.readAllBytes();
                String mimeType = getImageMimeType(imageUrl);
                return "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(imageBytes);
            }
        } catch (Exception e) {
            log.error("转换图片URL为base64失败: {}", imageUrl, e);
            return null;
        }
    }

    /**
     * 根据URL获取图片MIME类型
     */
    private String getImageMimeType(String url) {
        url = url.toLowerCase();
        if (url.contains(".jpg") || url.contains(".jpeg")) {
            return "image/jpeg";
        } else if (url.contains(".png")) {
            return "image/png";
        } else if (url.contains(".gif")) {
            return "image/gif";
        } else if (url.contains(".webp")) {
            return "image/webp";
        } else if (url.contains(".bmp")) {
            return "image/bmp";
        }
        return "image/jpeg"; // 默认
    }

    /**
     * HTML转义
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * 生成PDF
     */
    private byte[] generatePdf(String htmlContent) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        } catch (Exception e) {
            log.error("生成PDF失败", e);
            throw new IOException("生成PDF失败: " + e.getMessage(), e);
        }
    }
}

