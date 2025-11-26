FROM openjdk:17-slim

# 替换为国内镜像源（适用于 Debian 系统）
RUN sed -i 's/deb.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list

# 更新包列表并安装依赖
#RUN set -xe \
#    && apt-get update \
#    && apt-get install -y --no-install-recommends \
#        fontconfig \
#        libfreetype6 \
#        libxrender1 \
#        libxtst6 \
#        tzdata \
#    && apt-get clean \
#    && rm -rf /var/lib/apt/lists/*

# 设置时区为 Asia/Shanghai
ENV TZ=Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 拷贝 Java 应用程序
COPY target/AI_note-0.0.1-SNAPSHOT.jar AI_note--8088-0.0.1-SNAPSHOT.jar

# 暴露端口
EXPOSE 8088

# 启动 Java 应用程序
CMD ["java", "-jar", "AI_note--8088-0.0.1-SNAPSHOT.jar"]
