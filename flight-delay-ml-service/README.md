# FlightOnTime – ML API

FastAPI service that predicts flight delays using a Logistic Regression model.

## Run locally

```bash
source .venv/Scripts/activate
uvicorn app.main:app --reload
```

## Test locally

```bash
curl -X POST http://127.0.0.1:8000/predict \
  -H "Content-Type: application/json" \
  -d '{
    "Airline": "AA",
    "AirportFrom": "SFO",
    "AirportTo": "LAX",
    "Time": 1430,
    "Length": 120,
    "TimeDay": "Afternoon",
    "Duration": "Medium"
  }'
```

## POST /predict
Payload:
{
  "Airline": "AA",
  "AirportFrom": "SFO",
  "AirportTo": "LAX",
  "Time": 1430,
  "Length": 120,
  "TimeDay": "Afternoon",
  "Duration": "Medium"
}

Response:
{
  "prediction": "Retrasado",
  "probability": 0.73,
  "threshold": 0.325
}

