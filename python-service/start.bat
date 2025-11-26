@echo off
REM OCR和标签生成服务启动脚本 (Windows)

echo 启动OCR和标签生成服务...

REM 检查Python是否安装
python --version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到Python，请先安装Python3
    pause
    exit /b 1
)

REM 检查虚拟环境
if not exist "venv" (
    echo 创建虚拟环境...
    python -m venv venv
)

REM 激活虚拟环境
call venv\Scripts\activate.bat

REM 安装依赖
echo 安装依赖...
pip install -r requirements.txt

REM 检查Tesseract是否安装
where tesseract >nul 2>&1
if errorlevel 1 (
    echo 警告: 未找到Tesseract OCR，请先安装Tesseract OCR
    echo 请从 https://github.com/UB-Mannheim/tesseract/wiki 下载安装
)

REM 启动服务
echo 启动服务...
set PORT=5000
set HOST=0.0.0.0
set DEBUG=False

python app.py

pause

