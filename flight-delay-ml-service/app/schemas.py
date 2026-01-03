from pydantic import BaseModel
from datetime import datetime

class FlightDelayRequest(BaseModel):
    Airline: str
    AirportFrom: str
    AirportTo: str
    #DayOfWeek: int
    Time: int
    Length: int
    TimeDay: str
    Duration: str

class PredictionResponse(BaseModel):
    prediction: str
    probability: float
    threshold: float
