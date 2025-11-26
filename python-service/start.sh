#!/bin/bash

# OCR和标签生成服务启动脚本

echo "启动OCR和标签生成服务..."

# 检查Python是否安装
if ! command -v python3 &> /dev/null; then
    echo "错误: 未找到Python3，请先安装Python3"
    exit 1
fi

# 检查虚拟环境
if [ ! -d "venv" ]; then
    echo "创建虚拟环境..."
    python3 -m venv venv
fi

# 激活虚拟环境
source venv/bin/activate

# 安装依赖
echo "安装依赖..."
pip install -r requirements.txt

# 检查Tesseract是否安装
if ! command -v tesseract &> /dev/null; then
    echo "警告: 未找到Tesseract OCR，请先安装Tesseract OCR和中文语言包"
    echo "Linux: sudo apt-get install tesseract-ocr tesseract-ocr-chi-sim"
    echo "macOS: brew install tesseract tesseract-lang"
    echo "Windows: 请从 https://github.com/UB-Mannheim/tesseract/wiki 下载安装"
fi

# 启动服务
echo "启动服务..."
export PORT=${PORT:-5000}
export HOST=${HOST:-0.0.0.0}
export DEBUG=${DEBUG:-False}

python app.py

