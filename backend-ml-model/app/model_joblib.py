import joblib

MODEL_PATH = "model/flight_delay_bundle.joblib"

_bundle = None

def load_bundle():
    global _bundle

    if _bundle is None:
        _bundle = joblib.load(MODEL_PATH)

        if not isinstance(_bundle, dict):
            raise ValueError("El modelo cargado no es un bundle válido")

        if "model" not in _bundle:
            raise ValueError("El bundle no contiene la clave 'model'")

    return _bundle
