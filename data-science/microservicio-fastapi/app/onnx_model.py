import json
from pathlib import Path
import numpy as np
import onnxruntime as ort

# Paths
BASE_DIR = Path(__file__).resolve().parent
MODEL_DIR = BASE_DIR.parent / "model"

# Load threshold
with open(MODEL_DIR / "fastapi-model/flight_delay_threshold_calibrado.json", "r") as f:
    THRESHOLD = json.load(f)["best_threshold"]

# Load ONNX model
session = ort.InferenceSession(
    str(MODEL_DIR / "fastapi-model/flight_delay_model_calibrado.onnx"),
    providers=["CPUExecutionProvider"]
)

# Get input names dynamically (extra safe)
INPUT_NAMES = [i.name for i in session.get_inputs()]


def predict_onnx(features: dict):
    """
    features: dict producido por build_features
    """

    inputs = {
        "Airline": np.array([[features["Airline"]]], dtype=object),
        "AirportFrom": np.array([[features["AirportFrom"]]], dtype=np.float32),
        "AirportTo": np.array([[features["AirportTo"]]], dtype=np.float32),
        "DayOfWeek": np.array([[features["DayOfWeek"]]], dtype=np.int64),
        "Length": np.array([[features["Length"]]], dtype=np.float32),
        "Hour": np.array([[features["Hour"]]], dtype=np.float32),
    }

    # output = session.run(None, inputs)[0]
    # prob = float(output.squeeze())
    # prediction = int(prob >= THRESHOLD)

    outputs = session.run(None, inputs)
    # labels = outputs[0]
    probs = outputs[1]

    prob = float(probs[0][1])
    prediction = int(prob >= THRESHOLD)


    return prediction, float(prob)
