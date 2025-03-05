import requests
import json

# Flask API 서버 주소
# url = "http://127.0.0.1:5000/detect"
url = "https://3cfd-39-115-67-181.ngrok-free.app/detect"

# 테스트할 이미지 경로
image_path = "C:/Users/Administrator/Downloads/16tzAKiZNcGGQ.jpg"

# 이미지 업로드 및 요청 전송 > 로컬 이미지 서버로 전송
with open(image_path, "rb") as img_file:
    files = {"image": img_file}
    response = requests.post(url, files=files)

# 응답 데이터 변환
try:
    data = response.json()  # JSON 변환
    print("🔍 서버 응답 원본 데이터:\n", data)  # JSON 구조 확인

    # ** 데이터가 리스트인 경우 첫 번째 요소를 가져오기 **
    if isinstance(data, list):
        data = data[0]  # 리스트의 첫 번째 요소를 딕셔너리로 가져오기

    # 감지된 객체 출력
    print("\n🔍 감지된 객체 목록:\n")
    for detection in data.get("detections", []):
        class_name = detection["name"]
        confidence = detection["confidence"] * 100  # 확률을 %로 변환
        print(f"📌 클래스: {class_name} | 🔢 확률: {confidence:.2f}%")

except json.JSONDecodeError:
    print("❌ 서버 응답이 JSON 형식이 아닙니다.")
except Exception as e:
    print(f"❌ 오류 발생: {e}")
