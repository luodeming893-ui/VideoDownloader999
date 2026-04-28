#!/usr/bin/env python3
"""
视频解析后端服务
使用 yt-dlp 提供视频解析 API
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import yt_dlp
import json

app = Flask(__name__)
CORS(app)  # 允许跨域请求

@app.route('/')
def index():
    return """
    <html>
    <head><title>视频解析API</title></head>
    <body>
        <h1>视频解析API服务</h1>
        <p>POST /parse - 解析视频</p>
        <p>GET /info - 服务信息</p>
    </body>
    </html>
    """

@app.route('/info', methods=['GET'])
def info():
    return jsonify({
        'service': 'Video Parser API',
        'version': '1.0',
        'status': 'running'
    })

@app.route('/parse', methods=['POST'])
def parse_video():
    """
    解析视频接口
    请求体: {"url": "视频链接"}
    返回: {"title": "标题", "video_url": "视频地址", "audio_url": "音频地址(可选)"}
    """
    try:
        data = request.get_json()
        url = data.get('url')
        
        if not url:
            return jsonify({'error': '缺少视频链接'}), 400
        
        # 使用 yt-dlp 解析
        ydl_opts = {
            'format': 'best',
            'quiet': True,
            'no_warnings': True,
        }
        
        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            info = ydl.extract_info(url, download=False)
            
            # 提取需要的信息
            result = {
                'title': info.get('title', 'unknown'),
                'video_url': info.get('url'),
                'duration': info.get('duration'),
                'thumbnail': info.get('thumbnail'),
                'platform': info.get('extractor', 'unknown')
            }
            
            # 如果是多格式，尝试获取所有格式
            if 'formats' in info:
                formats = []
                for f in info['formats']:
                    if f.get('url'):
                        formats.append({
                            'format_id': f.get('format_id'),
                            'ext': f.get('ext'),
                            'resolution': f.get('resolution'),
                            'url': f.get('url')
                        })
                result['formats'] = formats
            
            return jsonify(result)
    
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/download', methods=['POST'])
def download_video():
    """
    下载视频到服务器
    请求体: {"url": "视频链接"}
    返回: 下载链接
    """
    try:
        data = request.get_json()
        url = data.get('url')
        
        if not url:
            return jsonify({'error': '缺少视频链接'}), 400
        
        ydl_opts = {
            'format': 'best',
            'outtmpl': 'downloads/%(title)s.%(ext)s',
        }
        
        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            info = ydl.extract_info(url, download=True)
            filename = ydl.prepare_filename(info)
            
            return jsonify({
                'message': '下载成功',
                'filename': filename,
                'download_url': f'/files/{filename}'
            })
    
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    import os
    # 创建下载目录
    os.makedirs('downloads', exist_ok=True)
    
    # 启动服务
    print("="*50)
    print("视频解析API服务")
    print("="*50)
    print("服务地址: http://0.0.0.0:5000")
    print("解析接口: POST /parse")
    print("="*50)
    
    app.run(host='0.0.0.0', port=5000, debug=True)
