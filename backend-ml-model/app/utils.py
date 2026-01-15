import json
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent
MODEL_DIR = BASE_DIR.parent / "model"

with open(MODEL_DIR / "airport_mappings.json", "r") as f:
    mappings = json.load(f)

FROM_MAP = mappings["from_map"]
TO_MAP = mappings["to_map"]
GLOBAL_MEAN = mappings["global_mean"]


def build_features(data):
    """
    Transforma FlightInput (API) a features listos para ONNX
    """

    return {
        "Airline": data.Airline,
        "AirportFrom": float(FROM_MAP.get(data.AirportFrom, GLOBAL_MEAN)),
        "AirportTo": float(TO_MAP.get(data.AirportTo, GLOBAL_MEAN)),
        "DayOfWeek": int(data.DayOfWeek),
        "Length": float(data.Length),
        "Hour": float(data.Time),
    }
