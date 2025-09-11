from flask import Flask, request, jsonify
from flask_cors import CORS
import torch
import os
import logging
import cv2
import numpy as np
from PIL import Image

# Flask 설정
app = Flask(__name__)
CORS(app)  # CORS 허용 (모바일 및 웹에서 API 호출 가능)

# 로깅 설정
logging.basicConfig(level=logging.INFO)

# YOLOv5 모델 로드
MODEL_PATH = "./yolov5-master/runs/train/exp6/weights/best.pt"
model = torch.hub.load('./yolov5-master', 'custom', path=MODEL_PATH, source='local')
model.eval()  # 추론 모드 설정

# 이미지 저장 폴더 생성
UPLOAD_FOLDER = "uploads"
PROCESSED_FOLDER = "processed"
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
os.makedirs(PROCESSED_FOLDER, exist_ok=True)

def draw_bounding_boxes(image_path, detections):
    """ 감지된 객체에 대한 바운딩 박스를 그린 후 이미지 저장 """
    # 이미지 로드 (OpenCV 사용)
    image = cv2.imread(image_path)
    if image is None:
        return None  # 이미지 로드 실패 시 None 반환
    
    # 바운딩 박스 색상 및 폰트 설정
    color = (0, 255, 0)  # 초록색
    font = cv2.FONT_HERSHEY_SIMPLEX

    for det in detections:
        x_min, y_min, x_max, y_max = int(det["xmin"]), int(det["ymin"]), int(det["xmax"]), int(det["ymax"])
        label = f"{det['name']} ({det['confidence']*100:.2f}%)"

        # 바운딩 박스 그리기
        cv2.rectangle(image, (x_min, y_min), (x_max, y_max), color, 2)

        # 라벨 텍스트 추가
        cv2.putText(image, label, (x_min, y_min - 10), font, 0.5, color, 2)

    # 결과 이미지 저장
    processed_path = os.path.join(PROCESSED_FOLDER, os.path.basename(image_path))
    cv2.imwrite(processed_path, image)
    
    return processed_path

@app.before_request
def log_request_info():
    print(f"📡 요청: {request.method} {request.url}")
    print(f"📋 헤더: {request.headers}")
    print(f"📂 데이터: {request.data}")

@app.route('/')
def home():
    return "<h1>YOLOv5 Flask App</h1><p>API is running. Use the correct endpoint for predictions.</p>"

@app.route('/detect', methods=['POST'])
def detect():
    if 'image' not in request.files:
        return jsonify({"error": "No image provided"}), 400
    
    file = request.files['image']
    try:
        # 원본 이미지 저장
        file_path = os.path.join(UPLOAD_FOLDER, file.filename)
        file.save(file_path)

        # YOLOv5 모델 예측 수행
        image = Image.open(file.stream)
        results = model(image)
        detections = results.pandas().xyxy[0].to_dict(orient="records")
        
        # 객체 종류별로 개수를 카운트
        object_counts = {}
        for detection in detections:
            label = detection.get("name")
            if label:
                object_counts[label] = object_counts.get(label, 0) + 1

        # 바운딩 박스 그린 이미지 저장 (필요시)
        processed_image_path = draw_bounding_boxes(file_path, detections)

        logging.info(f"Detections: {object_counts}")

        # 키:값 형태의 객체 개수 반환
        return jsonify(object_counts), 200
    except Exception as e:
        logging.error(f"Error: {str(e)}")
        return jsonify({"error": str(e)}), 500

=======
from PIL import Image
import mysql.connector
from openai import OpenAI
import config

# Flask 설정
app = Flask(__name__)
CORS(app)  # CORS 허용

# 로깅 설정
logging.basicConfig(level=logging.INFO)

# YOLOv5 모델 로드
MODEL_PATH = "./yolov5-master/runs/train/exp6/weights/best.pt"
model = torch.hub.load('./yolov5-master', 'custom', path=MODEL_PATH, source='local')
model.eval()

# 이미지 저장 폴더
UPLOAD_FOLDER = "uploads"
PROCESSED_FOLDER = "processed"
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
os.makedirs(PROCESSED_FOLDER, exist_ok=True)

# OpenAI 클라이언트 (환경변수 OPENAI_API_KEY 필요)
client = OpenAI(api_key=config.OPENAI_API_KEY)

# MySQL 연결
def get_db():
    return mysql.connector.connect(
        host="localhost",
        user="root",
        password="0340",
        database="data",
        charset="utf8mb4"
    )

# ========= YOLO 관련 함수 =========
def draw_bounding_boxes(image_path, detections):
    image = cv2.imread(image_path)
    if image is None:
        return None
    
    color = (0, 255, 0)
    font = cv2.FONT_HERSHEY_SIMPLEX

    for det in detections:
        x_min, y_min, x_max, y_max = int(det["xmin"]), int(det["ymin"]), int(det["xmax"]), int(det["ymax"])
        label = f"{det['name']} ({det['confidence']*100:.2f}%)"
        cv2.rectangle(image, (x_min, y_min), (x_max, y_max), color, 2)
        cv2.putText(image, label, (x_min, y_min - 10), font, 0.5, color, 2)

    processed_path = os.path.join(PROCESSED_FOLDER, os.path.basename(image_path))
    cv2.imwrite(processed_path, image)
    return processed_path

# ========= 레시피 관련 함수 =========
def load_steps(recipe_id: str):
    conn = get_db()
    cur = conn.cursor(dictionary=True)
    cur.execute(
        "SELECT step_number, description FROM recipe_order "
        "WHERE recipe_id = %s ORDER BY step_number ASC",
        (recipe_id,)
    )
    rows = cur.fetchall()
    cur.close()
    conn.close()
    return rows

def chat_with_recipe(recipe_id: str, user_message: str) -> str:
    steps = load_steps(recipe_id)
    if not steps:
        return "해당 레시피를 찾지 못했습니다."

    context = "[Steps]\n"
    for s in steps:
        desc = s["description"].lstrip("0123456789.) ").strip()
        context += f"{s['step_number']}) {desc}\n"

    system_rule = """너는 '레시피 전용 안내봇'이다.
사용자의 질문에 오직 아래 제공된 레시피 본문(steps)에 근거해서만 한국어로 간단명료하게 답해라.
본문에 없는 내용은 '레시피에 없음'이라고 말해라.
질문이 모호하면 레시피 범위 안에서 필요한 최소한의 추가 질문만 해라.
"""

    resp = client.responses.create(
        model="gpt-5-nano",
        input=[
            {"role": "system", "content": system_rule},
            {"role": "assistant", "content": context},
            {"role": "user", "content": user_message}
        ]
    )
    return resp.output_text

# ========= 라우트 =========
@app.route('/')
def home():
    return "<h1>Flask App</h1><p>YOLO + Recipe Chat API is running.</p>"

@app.route('/detect', methods=['POST'])
def detect():
    if 'image' not in request.files:
        return jsonify({"error": "No image provided"}), 400
    
    file = request.files['image']
    try:
        file_path = os.path.join(UPLOAD_FOLDER, file.filename)
        file.save(file_path)

        image = Image.open(file.stream)
        results = model(image)
        detections = results.pandas().xyxy[0].to_dict(orient="records")
        
        object_counts = {}
        for detection in detections:
            label = detection.get("name")
            if label:
                object_counts[label] = object_counts.get(label, 0) + 1

        processed_image_path = draw_bounding_boxes(file_path, detections)
        logging.info(f"Detections: {object_counts}")

        return jsonify(object_counts), 200
    except Exception as e:
        logging.error(f"Error: {str(e)}")
        return jsonify({"error": str(e)}), 500

@app.route("/recipes/<recipe_id>/chat", methods=["POST"])
def recipe_chat(recipe_id):
    data = request.get_json()
    message = data.get("message", "")
    if not message:
        return jsonify({"error": "메시지를 입력하세요"}), 400
    answer = chat_with_recipe(recipe_id, message)
    return jsonify({"answer": answer}), 200

# ========= 실행 =========
>>>>>>> branch 'main' of https://github.com/JHTek/capstone-2025-MyFridge.git
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
