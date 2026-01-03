import pandas as pd
def build_features(req):
    return pd.DataFrame([req.model_dump()])


# # def get_time_day(time: int) -> str:
# #     if 0 <= time < 600:
# #         return "Early morning"
# #     elif 600 <= time < 1200:
# #         return "Morning"
# #     elif 1200 <= time < 1800:
# #         return "Afternoon"
# #     else:
# #         return "Night"
    
# # def get_duration(length: int) -> str:
# #     if 0 <= length < 90:
# #         return "Short"
# #     elif 90 <= length < 180:
# #         return "Medium"
# #     elif 180 <= length < 960:
# #         return "Long"

# # def build_features(req):
# #     data = {
# #         "Airline": req.Airline,
# #         "AirportFrom": req.AirportFrom,
# #         "AirportTo": req.AirportTo,
# #         "DayOfWeek": req.DayOfWeek,
# #         "Time": req.Time,
# #         "Length": req.Length,
# #         "TimeDay": get_time_day(req.Time),
# #         "Duration": get_duration(req.Length),
# #     }
# #     return pd.DataFrame([data])


# import pandas as pd

# def build_features(req):
#     """
#     Convierte request → dataframe EXACTO que espera el pipeline
#     """

#     # TimeDay: mañana / tarde / noche (según lógica del notebook)
#     hour = req.Time // 100

#     if hour < 12:
#         time_day = "morning"
#     elif hour < 18:
#         time_day = "afternoon"
#     else:
#         time_day = "night"

#     # Duration: en este dataset es equivalente a Length
#     duration = req.Length

#     # return pd.DataFrame([{
#     #     "Airline": req.Airline,
#     #     "AirportFrom": req.AirportFrom,
#     #     "AirportTo": req.AirportTo,
#     #     "TimeDay": time_day,
#     #     "Duration": duration,
#     #     "Time": req.Time,
#     #     "Length": req.Length,
#     # }])
#     return pd.DataFrame([{
#         "Airline": req.Airline,
#         "AirportFrom": req.AirportFrom,
#         "AirportTo": req.AirportTo,
#         "DayOfWeek": req.DayOfWeek,
#         "Time": req.Time,
#         "Length": req.Length,
#         # SOLO si tu modelo fue entrenado con estas:
#         "TimeDay": getattr(req, "TimeDay", None),
#         "Duration": getattr(req, "Duration", None),
#     }])
