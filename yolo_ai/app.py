from flask import Flask, request, jsonify
from flask_cors import CORS
import torch
import os
import logging
import cv2
import numpy as np
from PIL import Image
import mysql.connector
from openai import OpenAI
import config

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

#OpenAI 클라이언트 (환경변수 OPENAI_API_KEY 필요)
client = OpenAI(api_key=config.OPENAI_API_KEY)

#MySQL 연결
def get_db():
    
    return mysql.connector.connect(
        host="localhost",
        user="root",
        password="1234",
        database="testdb",
        charset="utf8mb4"
    )


# ✅ 영어 → 한글 라벨 매핑
label_map = {
    "abalone": "전복", "bean_sprout": "콩나물", "paprika": "파프리카", "broccoli": "브로콜리",
    "broth_anchovy": "멸치 육수", "burdock": "우엉", "carrot": "당근", "chives": "부추",
    "cooking_anchovy": "요리용 멸치", "corn": "옥수수", "dried_laver": "건조 김", "dried_pollock_shreds": "북어채",
    "dried_squid": "건오징어", "fish": "생선", "frozen_baby_octopus": "냉동 주꾸미", "frozen_large_octopus": "냉동 문어",
    "frozen_octopus": "냉동 낙지", "frozen_shrimp": "냉동 새우", "frozen_squid": "냉동 오징어", "ginger": "생강",
    "ginger_powder": "생강가루", "iceberg_lettuce": "아이스버그 상추", "lettuce": "상추", "lotus_root": "연근",
    "minced_ginger": "다진 생강", "mung_bean_sprout": "녹두나물", "packaged_abalone": "포장된 전복",
    "packaged_bean_sprout": "포장된 콩나물", "packaged_beef": "포장된 소고기", "packaged_broccoli": "포장된 브로콜리",
    "packaged_broth_anchovy": "포장된 멸치 육수", "packaged_burdock": "포장된 우엉", "packaged_cooking_anchovy": "포장된 요리용 멸치",
    "packaged_corn": "포장된 옥수수", "packaged_dried_pollock": "포장된 북어", "packaged_dried_pollock_shreds": "포장된 북어채",
    "packaged_flying_fish_roe": "포장된 날치알", "packaged_iceberg_lettuce": "포장된 아이스버그 상추",
    "packaged_lettuce": "포장된 상추", "packaged_lotus_root": "포장된 연근", "packaged_mung_bean_sprout": "포장된 녹두나물",
    "packaged_oyster": "포장된 굴", "packaged_perilla_leaf": "포장된 깻잎", "packaged_pork": "포장된 돼지고기",
    "packaged_raw_chicken": "포장된 생닭", "packaged_seaweed": "포장된 해조류", "packaged_spinach": "포장된 시금치",
    "packaged_squash": "포장된 애호박", "packaged_tofu": "포장된 두부", "perilla_leaf": "깻잎", "radish": "무",
    "refrigerated_chicken": "냉장 닭고기", "spinach": "시금치", "squash": "애호박", "sweet_potato": "고구마",
    "tofu": "두부", "Banana": "바나나", "chicken": "닭고기", "Butter": "버터", "Chopped Garlic": "다진 마늘",
    "Carrot": "당근", "Carrot Sliced": "당근 슬라이스", "Cheese": "치즈", "Cocoa Powder": "코코아 가루",
    "egg": "달걀", "eggplant": "가지", "Eggplant Sliced": "가지 슬라이스", "Flour": "밀가루", "pimento": "피멘토",
    "Garlic": "마늘", "Ground Meat": "간 고기", "potato": "감자", "Lasagna": "라자냐", "Leg Chicken": "닭다리",
    "Legume": "콩류", "Milk": "우유", "Mushroom": "버섯", "Nut": "견과류", "Nuts": "견과류",
    "onion": "양파", "Parsley": "파슬리", "Pasta": "파스타", "Potato No Skin": "껍질 없는 감자", "Rice": "쌀",
    "Spaghetti": "스파게티", "Thigh Chicken": "닭 허벅지살", "Tomato": "토마토", "Walnut": "호두", "Yogurt": "요거트",
    "Non-Ground Meat": "비다진 고기", "Green Pepper": "초록 고추", "Wing Chicken": "닭 날개", "lime": "라임",
    "Mushroom Sliced": "버섯 슬라이스", "Sausage": "소시지", "Shrimp Group": "새우류", "Shrimp": "새우",
    "Basil": "바질", "Coffee": "커피", "Sugar": "설탕", "Zucchini": "주키니 호박", "Beet": "비트",
    "Cabbage": "양배추", "Cucumber": "오이"
}

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

#========= 레시피 관련 함수 =========
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
            label_en = detection.get("name")
            label_ko = label_map.get(label_en, label_en)  # ✅ 한글로 변환
            if label_ko:
                object_counts[label_ko] = object_counts.get(label_ko, 0) + 1

        # 바운딩 박스 그린 이미지 저장 (필요시)
        processed_image_path = draw_bounding_boxes(file_path, detections)

        logging.info(f"Detections: {object_counts}")

        # 키:값 형태의 객체 개수 반환
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

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
# python app.py 후 ngrok http 5000