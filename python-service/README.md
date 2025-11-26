# OCR和智能标签生成服务

基于Python的独立服务，提供OCR文字识别、智能标签生成和文本摘要功能。

## 功能特性

- **OCR文字识别**: 使用Tesseract OCR引擎，支持中英文识别
- **智能标签生成**: 基于jieba分词和TextRank算法，自动提取2-3个相关标签（仅根据识别内容推荐，不进行学科匹配）
- **文本摘要生成**: 使用TextRank算法生成文本摘要

## 技术栈

- Flask: Web框架
- jieba: 中文分词（TF-IDF关键词提取）
- textrank4zh: TextRank算法实现（关键词提取和摘要生成）
- pytesseract: Tesseract OCR的Python封装
- Pillow: 图像处理

## 标签生成算法

标签生成采用混合算法策略：
1. **jieba TF-IDF**: 提取文本中TF-IDF值最高的关键词
2. **TextRank**: 使用TextRank算法提取重要关键词
3. **权重融合**: 合并两种方法的结果，TF-IDF结果权重更高
4. **智能筛选**: 过滤长度在2-6字之间的关键词，按权重排序
5. **标签推荐**: 返回权重最高的2-3个关键词作为标签建议

## 安装依赖

```bash
pip install -r requirements.txt
```

## 安装Tesseract OCR

### Windows
1. 下载安装包: https://github.com/UB-Mannheim/tesseract/wiki
2. 安装后添加到系统PATH，或设置环境变量:
   ```bash
   set TESSDATA_PREFIX=C:\Program Files\Tesseract-OCR\tessdata
   ```

### Linux
```bash
sudo apt-get update
sudo apt-get install tesseract-ocr
sudo apt-get install tesseract-ocr-chi-sim  # 中文语言包
```

### macOS
```bash
brew install tesseract
brew install tesseract-lang  # 语言包
```

## 运行服务

```bash
python app.py
```

或使用环境变量配置:

```bash
export PORT=5000
export HOST=0.0.0.0
export DEBUG=True
python app.py
```

服务默认运行在 `http://localhost:5000`

## API接口

### 1. 健康检查
```
GET /health
```

### 2. OCR文字提取
```
POST /ocr/extract
Content-Type: multipart/form-data

file: [图片文件]
```

或

```
POST /ocr/extract
Content-Type: application/json

{
  "image": "base64编码的图片数据"
}
```

### 3. 文本标签提取
```
POST /text/extract_tags
Content-Type: application/json

{
  "text": "要分析的文本内容"
}
```

### 4. 文本摘要生成
```
POST /text/generate_summary
Content-Type: application/json

{
  "text": "要生成摘要的文本内容",
  "sentence_count": 3  // 可选，默认3句
}
```

### 5. OCR提取并生成标签（一站式）
```
POST /ocr/extract_and_tag
Content-Type: multipart/form-data

file: [图片文件]
```

## Docker部署

创建 `Dockerfile`:

```dockerfile
FROM python:3.11-slim

WORKDIR /app

# 安装系统依赖
RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-chi-sim \
    && rm -rf /var/lib/apt/lists/*

# 复制依赖文件
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 复制应用代码
COPY . .

EXPOSE 5000

CMD ["python", "app.py"]
```

构建和运行:

```bash
docker build -t ocr-tag-service .
docker run -p 5000:5000 ocr-tag-service
```

## 与Java服务集成

Java服务可以通过HTTP请求调用Python服务:

```java
// 示例代码
RestTemplate restTemplate = new RestTemplate();
String url = "http://localhost:5000/ocr/extract_and_tag";
// 发送请求...
```

