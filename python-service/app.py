"""
OCR和智能标签生成服务
使用Flask提供RESTful API
"""
from flask import Flask, request, jsonify
from flask_cors import CORS
import jieba
import jieba.analyse
from textrank4zh import TextRank4Keyword, TextRank4Sentence
import base64
import io
from PIL import Image
import pytesseract
import os
os.environ["TESSDATA_PREFIX"] = "/usr/local/tesseract-4.1.0/tessdata/"
import logging
import re

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app)  # 允许跨域请求

# 配置jieba
jieba.initialize()
# 可以加载自定义词典
# jieba.load_userdict('userdict.txt')


@app.route('/health', methods=['GET'])
def health():
    """健康检查接口"""
    return jsonify({"status": "ok", "message": "OCR and Tag Service is running"})


@app.route('/ocr/extract', methods=['POST'])
def extract_text():
    """
    从图片中提取文字
    接收base64编码的图片或文件上传
    """
    try:
        # 支持两种方式：base64或文件上传
        if 'file' in request.files:
            file = request.files['file']
            image = Image.open(io.BytesIO(file.read()))
        elif 'image' in request.json:
            # base64编码的图片
            image_data = request.json['image']
            if image_data.startswith('data:image'):
                image_data = image_data.split(',')[1]
            image_bytes = base64.b64decode(image_data)
            image = Image.open(io.BytesIO(image_bytes))
        else:
            return jsonify({"code": 400, "msg": "请提供图片文件或base64编码的图片", "data": None}), 400

        # 使用Tesseract进行OCR识别
        # 支持中英文
        text = pytesseract.image_to_string(image, lang='chi_sim+eng')
        text = ' '.join(text.split())  # 清理多余空格

        if not text or len(text.strip()) == 0:
            return jsonify({"code": 400, "msg": "未能从图片中提取到文字", "data": None}), 400

        return jsonify({
            "code": 200,
            "msg": "成功",
            "data": {
                "text": text,
                "length": len(text)
            }
        })

    except Exception as e:
        logger.error(f"OCR提取失败: {str(e)}", exc_info=True)
        return jsonify({"code": 500, "msg": f"OCR提取失败: {str(e)}", "data": None}), 500


@app.route('/text/extract_tags', methods=['POST'])
def extract_tags():
    """
    从文本中提取标签（使用jieba分词和TextRank算法）
    """
    try:
        data = request.get_json()
        if not data or 'text' not in data:
            return jsonify({"code": 400, "msg": "请提供文本内容", "data": None}), 400

        text = data['text']
        if not text or len(text.strip()) == 0:
            return jsonify({"code": 400, "msg": "文本内容不能为空", "data": None}), 400

        # 提取标签
        tags = generate_tags(text)

        return jsonify({
            "code": 200,
            "msg": "成功",
            "data": {
                "tags": tags,
                "count": len(tags)
            }
        })

    except Exception as e:
        logger.error(f"标签提取失败: {str(e)}", exc_info=True)
        return jsonify({"code": 500, "msg": f"标签提取失败: {str(e)}", "data": None}), 500


@app.route('/text/generate_summary', methods=['POST'])
def generate_summary():
    """
    使用TextRank算法生成文本摘要
    """
    try:
        data = request.get_json()
        if not data or 'text' not in data:
            return jsonify({"code": 400, "msg": "请提供文本内容", "data": None}), 400

        text = data['text']
        sentence_count = data.get('sentence_count', 3)  # 默认3句摘要

        if not text or len(text.strip()) == 0:
            return jsonify({"code": 400, "msg": "文本内容不能为空", "data": None}), 400

        # 生成摘要
        summary = generate_textrank_summary(text, sentence_count)

        return jsonify({
            "code": 200,
            "msg": "成功",
            "data": {
                "summary": summary,
                "sentence_count": len(summary)
            }
        })

    except Exception as e:
        logger.error(f"摘要生成失败: {str(e)}", exc_info=True)
        return jsonify({"code": 500, "msg": f"摘要生成失败: {str(e)}", "data": None}), 500


@app.route('/ocr/extract_and_tag', methods=['POST'])
def extract_and_tag():
    """
    从图片中提取文字并生成标签建议（一站式服务）
    """
    try:
        # 提取文字
        if 'file' in request.files:
            file = request.files['file']
            image = Image.open(io.BytesIO(file.read()))
        elif 'image' in request.json:
            image_data = request.json['image']
            if image_data.startswith('data:image'):
                image_data = image_data.split(',')[1]
            image_bytes = base64.b64decode(image_data)
            image = Image.open(io.BytesIO(image_bytes))
        else:
            return jsonify({"code": 400, "msg": "请提供图片文件或base64编码的图片", "data": None}), 400

        # OCR识别
        text = pytesseract.image_to_string(image, lang='chi_sim+eng')
        text = ' '.join(text.split())

        if not text or len(text.strip()) == 0:
            return jsonify({"code": 400, "msg": "未能从图片中提取到文字", "data": None}), 400

        # 生成标签
        tags = generate_tags(text)

        # 生成摘要
        summary = generate_textrank_summary(text, 2)

        return jsonify({
            "code": 200,
            "msg": "成功",
            "data": {
                "text": text,
                "length": len(text),
                "suggestedTags": tags,
                "summary": summary
            }
        })

    except Exception as e:
        logger.error(f"OCR和标签生成失败: {str(e)}", exc_info=True)
        return jsonify({"code": 500, "msg": f"处理失败: {str(e)}", "data": None}), 500


def generate_tags(text, max_tags=3):
    """
    使用jieba分词和TextRank算法生成标签
    只根据识别出的内容进行推荐，不进行学科匹配
    只生成中文标签，且标签在原文中复现次数较高
    """
    if not text or len(text.strip()) == 0:
        return []
    
    # 清理文本，移除多余空格但保留必要的结构
    text_clean = ' '.join(text.split())
    # 去除空格后的文本（用于匹配）
    text_no_space = text_clean.replace(' ', '').replace('  ', '')
    # 只保留中文的文本（用于匹配）
    text_chinese_only = re.sub(r'[^\u4e00-\u9fa5]', '', text_clean)
    
    # 方法1: 使用jieba的TF-IDF提取关键词（主要方法）
    keywords_tfidf = jieba.analyse.extract_tags(text_clean, topK=30, withWeight=False)
    logger.info(f"TF-IDF提取的关键词: {keywords_tfidf[:10]}")
    
    # 方法2: 使用TextRank提取关键词（辅助方法，可能失败）
    keywords_textrank = []
    try:
        # 先尝试使用去除空格后的文本，因为TextRank对空格敏感
        tr4w = TextRank4Keyword()
        # 使用纯中文文本进行分析，提高准确性
        text_for_textrank = text_chinese_only if len(text_chinese_only) > 10 else text_no_space
        tr4w.analyze(text_for_textrank, lower=True, window=3)
        keywords_items = tr4w.get_keywords(30, word_min_len=2)
        keywords_textrank = [item.word for item in keywords_items]
        logger.info(f"TextRank提取的关键词: {keywords_textrank[:10]}")
    except Exception as e:
        logger.warning(f"TextRank提取失败: {str(e)}，将仅使用TF-IDF结果")
        keywords_textrank = []
    
    # 合并两种方法的结果，保留权重信息
    keyword_weights = {}
    
    # TF-IDF关键词（权重较高，主要来源）
    for i, keyword in enumerate(keywords_tfidf):
        if keyword not in keyword_weights:
            keyword_weights[keyword] = 0
        keyword_weights[keyword] += (len(keywords_tfidf) - i) * 3  # TF-IDF权重更高（×3）
    
    # TextRank关键词（辅助，权重较低）
    if keywords_textrank:
        for i, keyword in enumerate(keywords_textrank):
            if keyword not in keyword_weights:
                keyword_weights[keyword] = 0
            keyword_weights[keyword] += (len(keywords_textrank) - i)  # TextRank权重较低（×1）
    
    # 过滤和评分关键词（主要从TF-IDF结果中筛选）
    filtered_keywords = []
    for kw, weight in keyword_weights.items():
        # 只保留中文关键词（必须包含中文字符）
        chinese_chars = re.findall(r'[\u4e00-\u9fa5]', kw)
        if len(chinese_chars) == 0:
            continue
        
        # 确保中文字符占比至少30%
        chinese_ratio = len(chinese_chars) / len(kw) if len(kw) > 0 else 0
        if chinese_ratio < 0.3:
            continue
        
        # 过滤掉长度不符合要求的（考虑空格）
        kw_clean = kw.replace(' ', '')
        if len(kw_clean) < 2 or len(kw_clean) > 8:
            continue
        
        # 计算关键词在原文中的出现次数
        # 尝试多种匹配方式
        count_in_text = 0
        
        # 方式1: 直接匹配（带空格）
        count_in_text = max(count_in_text, text_clean.count(kw))
        
        # 方式2: 去除空格后匹配
        kw_no_space = kw.replace(' ', '')
        count_in_text = max(count_in_text, text_no_space.count(kw_no_space))
        
        # 方式3: 只匹配中文字符部分（重要：针对OCR结果中空格分割的情况）
        kw_chinese_only = ''.join(chinese_chars)
        if len(kw_chinese_only) >= 2:
            count_chinese = text_chinese_only.count(kw_chinese_only)
            count_in_text = max(count_in_text, count_chinese)
        
        # 如果关键词在原文中出现次数为0，尝试更宽松的匹配
        if count_in_text == 0:
            # 尝试匹配去除所有非中文字符后的结果
            kw_chinese_clean = ''.join(chinese_chars)
            if len(kw_chinese_clean) >= 2:
                count_in_text = text_chinese_only.count(kw_chinese_clean)
        
        # 如果还是0，但对于TF-IDF结果，只要纯中文部分在原文中存在，就使用（降低要求）
        if count_in_text == 0:
            kw_chinese_clean = ''.join(chinese_chars)
            # 检查是否是TF-IDF的关键词（权重较高，说明是重要的）
            is_tfidf_keyword = kw in keywords_tfidf
            if is_tfidf_keyword and len(kw_chinese_clean) >= 2:
                # 检查纯中文版本是否在原文中出现（允许部分匹配）
                if kw_chinese_clean in text_chinese_only:
                    count_in_text = 1  # 给予最小权重，表示存在
                    logger.debug(f"TF-IDF关键词 '{kw}' 的纯中文版本 '{kw_chinese_clean}' 在原文中找到")
            else:
                continue  # 非TF-IDF关键词且匹配不到，跳过
        
        # 综合权重 = 算法权重 + 出现次数权重（出现次数权重更高）
        final_weight = weight + count_in_text * 10
        
        filtered_keywords.append((kw, final_weight, count_in_text))
        logger.debug(f"关键词候选: {kw}, 权重: {final_weight}, 出现次数: {count_in_text}")
    
    # 按综合权重降序排序
    filtered_keywords.sort(key=lambda x: x[1], reverse=True)
    
    # 提取前max_tags个标签，清理空格并提取纯中文版本
    final_tags = []
    for kw, _, _ in filtered_keywords[:max_tags]:
        # 提取纯中文部分（去除空格和标点）
        chinese_chars_in_kw = re.findall(r'[\u4e00-\u9fa5]', kw)
        if len(chinese_chars_in_kw) >= 2:
            kw_clean = ''.join(chinese_chars_in_kw)
            # 确保在原文中存在
            if kw_clean in text_chinese_only and kw_clean not in final_tags:
                final_tags.append(kw_clean)
        
        if len(final_tags) >= max_tags:
            break
    
    logger.info(f"最终生成的标签（正常流程）: {final_tags}")
    
    # 如果标签为空，尝试返回中文字符频率最高的词（降级策略）
    if len(final_tags) == 0:
        logger.warning("未能通过正常流程生成标签，使用降级策略")
        # 从TF-IDF结果中选择中文关键词（即使匹配次数为0也使用）
        for kw in keywords_tfidf:
            chinese_chars = re.findall(r'[\u4e00-\u9fa5]', kw)
            if len(chinese_chars) < 2:
                continue
            
            # 计算中文字符占比
            chinese_ratio = len(chinese_chars) / len(kw) if len(kw) > 0 else 0
            if chinese_ratio < 0.3:
                continue
            
            # 长度检查（去除空格）
            kw_clean = kw.replace(' ', '')
            if len(kw_clean) < 2 or len(kw_clean) > 8:
                continue
            
            # 检查中文字符部分是否在原文中出现
            kw_chinese_only = ''.join(chinese_chars)
            if kw_chinese_only in text_chinese_only:
                final_tags.append(kw_chinese_only)  # 使用纯中文版本
                logger.info(f"降级策略添加标签: {kw_chinese_only}")
                if len(final_tags) >= max_tags:
                    break
        
        # 如果还是为空，尝试使用TextRank结果
        if len(final_tags) == 0:
            for kw in keywords_textrank:
                chinese_chars = re.findall(r'[\u4e00-\u9fa5]', kw)
                if len(chinese_chars) >= 2:
                    kw_chinese_only = ''.join(chinese_chars)
                    if len(kw_chinese_only) >= 2 and len(kw_chinese_only) <= 8:
                        final_tags.append(kw_chinese_only)
                        logger.info(f"从TextRank降级添加标签: {kw_chinese_only}")
                        if len(final_tags) >= max_tags:
                            break
    
    # 清理标签中的空格（针对OCR识别结果）
    final_tags = [tag.replace(' ', '') for tag in final_tags if tag]
    
    return final_tags


def generate_textrank_summary(text, sentence_count=3):
    """
    使用TextRank算法生成文本摘要
    """
    tr4s = TextRank4Sentence()
    tr4s.analyze(text, lower=True, source='all_filters')
    
    # 获取摘要句子
    summary_sentences = []
    for item in tr4s.get_key_sentences(num=sentence_count):
        summary_sentences.append(item.sentence)
    
    return summary_sentences


if __name__ == '__main__':
    # 从环境变量读取配置
    port = int(os.environ.get('PORT', 5000))
    host = os.environ.get('HOST', '0.0.0.0')
    debug = os.environ.get('DEBUG', 'False').lower() == 'true'
    
    logger.info(f"启动OCR和标签生成服务，端口: {port}")
    app.run(host=host, port=port, debug=debug)

