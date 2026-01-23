from pydantic import BaseModel

class FlightInput(BaseModel):
    Airline: str
    AirportFrom: str
    AirportTo: str
    DayOfWeek: int
    Length: float
    Time: int #HHMM format

class PredictionResponse(BaseModel):
    prediction: int
    probability: float
