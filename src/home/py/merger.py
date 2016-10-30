import pandas as pd

a = pd.read_csv("week2_v1.csv")
b = pd.read_csv("b.csv")
merged = a.merge(b,on='Date & Time')
merged.to_csv("week2_v2.csv",index = False) 
