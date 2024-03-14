import requests
import datetime
import json

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

#file = open(f"data/data_{timestamp}.json", "w")
file = open(f"/app/data/data_{timestamp}.json", "w")
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