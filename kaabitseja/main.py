# TODO: panna kõik pip asjad requirements.txt faili
import os
import sys
import requests
import datetime
import json
import mysql.connector
from dotenv import load_dotenv

url = "http://www.phxc.ee"

print("Alustan kaapimist")

response = requests.get(url)
unfiltered = response.content.decode()
timestamp = datetime.datetime.now().isoformat()

print("Aeg: " + timestamp)

osalejad = []
skoorid = []
edetabel = 0
for line in unfiltered.splitlines():
    line = line.replace("\t", "")
    osaleja = None
    skoor = None

    if line.startswith("<span class=\"pseudo\">"):
        osaleja = line[21:-7]
        osaleja_nr = osaleja.split(".")[0]

        if osaleja_nr != "Juh":
            if int(osaleja_nr) == 1:
                edetabel += 1

            osalejad.append([edetabel, osaleja])

    if line.startswith("<span class=\"skoor\">"):
        skoorid.append(line[20:-7].replace("<span class=\"komakoht\">", "").replace("</span>", ""))

print("Kaabitsemine valmis")
print("Kirjutan faili")

file = open(f"data/data_{timestamp}.json", "w")
# file = open(f"/app/data/data_{timestamp}.json", "w")
file.write("[\n")

for i in range(len(osalejad)):
    objekt = {
        "edetabel": osalejad[i][0],
        "timestamp": timestamp,
        "osaleja": osalejad[i][1],
        "skoor": float(skoorid[i].split(" ")[0])
    }
    file.write(json.dumps(objekt, ensure_ascii=True))
    if (i != len(osalejad) - 1):
        file.write(",")
    file.write("\n")

file.write("]\n")
file.close()
print("Faili kirjutatud")
print("Ühendan andmebaasiga")

load_dotenv()
connection = mysql.connector.connect(
    user=os.getenv("MYSQL_USERNAME"),
    password=os.getenv("MYSQL_PASSWORD"),
    host=os.getenv("MYSQL_HOST"),
    port=os.getenv("MYSQL_PORT"),
    database=os.getenv("MYSQL_NAME")
)

if not connection.is_connected():
    print("Ühendumine ebaõnnestus!")
    # vb peaks siia ka mingi notificationi saatma
    sys.exit(1)

print("Ühendatud andmebaasiga")

cursor = connection.cursor()

insert_query = """
INSERT INTO results (contestID, timestamp, results)
VALUES (%s, %s, %s)
"""

contest_id = 123  
timestamp = "2024-03-14 15:30:02" 
results = '{"participant1": 10, "participant2": 20}' 

cursor.execute(insert_query, (contest_id, timestamp, results))
connection.commit()
cursor.close()

connection.close()
print("Ühendus suletud")
