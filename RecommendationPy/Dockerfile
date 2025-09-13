FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY RecommendationTrain.py .

# Chỉ chạy script, script tự loop
CMD ["python", "RecommendationTrain.py"]
