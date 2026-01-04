from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from app.schemas import FlightDelayRequest, PredictionResponse
from app.model import load_bundle
from app.utils import build_features
import pandas as pd

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

@app.on_event("startup")
def startup():
    load_bundle()

@app.get("/")
def root():
    return {"message": "Flight Delay ML Service"}

@app.get("/health")
def health():
    return {"status": "ok"}

@app.post("/predict", response_model=PredictionResponse)
def predict(req: FlightDelayRequest):
    try:
        bundle = load_bundle()
        pipeline = bundle["model"]
        threshold = bundle["threshold"]
        print("TIPO DE MODELO:", type(pipeline))
        print("PIPELINE STEPS:", getattr(pipeline, "steps", None))

        # X = pd.DataFrame([req.model_dump()])

        # proba = float(pipeline.predict_proba(X)[0, 1])
        X = pd.DataFrame([req.model_dump()])
        #proba = pipeline.predict_proba(X)
        proba = pipeline.predict_proba(X).ravel()[1]


        prediction = int(proba >= threshold)

        return PredictionResponse(
            prediction="Retrasado" if prediction == 1 else "Puntual",
            probability=round(proba, 2),
            threshold=threshold,
        )
    
    except Exception as e:
        print("❌ ERROR REAL:", repr(e))
        raise HTTPException(status_code=500, detail=str(e))
