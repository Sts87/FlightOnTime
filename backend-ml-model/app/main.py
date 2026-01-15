from fastapi import FastAPI, HTTPException, UploadFile, File
from fastapi.middleware.cors import CORSMiddleware
import pandas as pd
from app.schemas import FlightInput, PredictionResponse
from app.onnx_model import predict_onnx
from app.utils import build_features

app = FastAPI(
    title="FlightOnTime ML Service",
    version="0.1.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], # O el dominio: ["http://localhost:5500"]
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
def root():
    return {"message": "Flight Delay ML Service"}

@app.get("/health")
def health():
    return {"status": "ok"}

@app.post("/predict", response_model=PredictionResponse)
def predict(data: FlightInput):
    try:
        features = build_features(data)
        prediction, prob = predict_onnx(features)
        
        return PredictionResponse(
            prediction=prediction,
            probability=prob
        )
    
    except Exception as e:
        print("❌ ERROR REAL:", repr(e))
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/predict/csv")
async def predict_csv(file: UploadFile = File(...)):
    try:
        df = pd.read_csv(file.file)

        required_columns = [
            "Airline",
            "AirportFrom",
            "AirportTo",
            "DayOfWeek",
            "Length",
            "Time",
        ]

        if not all(col in df.columns for col in required_columns):
            raise ValueError("Faltan columnas requeridas")
        
        predictions = []

        for _, row in df.iterrows():
            req = FlightInput(**row.to_dict())
            features = build_features(req)
            pred, prob = predict_onnx(features)

            predictions.append({
                "prediction": pred,
                "probability": prob
            })

        return {
            "total": len(predictions),
            "predictions": predictions
        }
    
    except Exception as e:
        print("❌ ERROR CSV:", repr(e))
        raise HTTPException(status_code=500, detail=str(e))
