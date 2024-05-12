import Link from "next/link";
import React from "react";

// const osalejaData = {
//   "osaleja": "mc_CHAozz",
//   "ELO": 1200,
//   "results": [
//     {
//       "edetabel_nimi": "algarvuringid",
//       "koht": 1,
//       "skoor": "2.5",
//       "skoor2": null,
//       "skoor1ühik": "ms",
//       "skoor2ühik": null
//     },
//     {
//       "edetabel_nimi": "temperatuurid",
//       "koht": 2,
//       "skoor": "4769",
//       "skoor2": null,
//       "skoor1ühik": "ms",
//       "skoor2ühik": null
//     },
//     {
//       "edetabel_nimi": "korduvad_read",
//       "koht": 3,
//       "skoor": "121",
//       "skoor2": null,
//       "skoor1ühik": "ms",
//       "skoor2ühik": null
//     },
//     {
//       "edetabel_nimi": "ruut_ühtedest",
//       "koht": 19,
//       "skoor": "19.9",
//       "skoor2": null,
//       "skoor1ühik": "ms",
//       "skoor2ühik": null
//     },
//     {
//       "edetabel_nimi": "summad_ja_sõnepõime",
//       "koht": 1,
//       "skoor": "2.5",
//       "skoor2": null,
//       "skoor1ühik": "ms",
//       "skoor2ühik": null
//     },
//     {
//       "edetabel_nimi": "jaotus_rühmadeks",
//       "koht": 4,
//       "skoor": "28",
//       "skoor2": null,
//       "skoor1ühik": "ms",
//       "skoor2ühik": null
//     },
//     {
//       "edetabel_nimi": "kuningad",
//       "koht": 1,
//       "skoor": "(10, 9)",
//       "skoor2": "539.8",
//       "skoor1ühik": null,
//       "skoor2ühik": "ms"
//     },
    
//   ]
// }

async function getOsalejaData(osaleja: string) {
  const res: Response = await fetch("http://localhost:8080/osalejateEdetabel/" + osaleja);

  if (!res.ok) {
    throw new Error("Failed to fetch data");
  }

  const user: User = [...await res.json()][0];
  console.log(JSON.stringify(user.ELO));

  return user;
}

export default async function taskPage(props: any) {
  const pseudo = decodeURI(props.params.osalejaPseudo);

  const osalejaData = await getOsalejaData(props.params.osalejaPseudo);

  return <main className="flex min-h-screen h-screen w-full flex-col items-center justify-between">
    <div className="h-full w-full bg-white flex flex-col items-center">
      <h1 className="text-black text-center text-4xl font-bold mt-16">
        {pseudo}
      </h1>
      <p className="text-black text-xl">
        {"Reiting: " + parseInt(osalejaData.ELO)}
      </p>
      <table className="table-auto text-black w-3/4 mt-4">
        <thead>
          <tr className="text-lg lg:text-2xl">
            <th className="border">Ülesane nimi</th>
            <th className="border">Koht</th>
            <th className="border">Skoor 1</th>
            <th className="border">Skoor 2</th>
          </tr>
        </thead>
        <tbody>
          {osalejaData.results.map((res, i) => {
            return <tr key={i} className={i % 2 == 0 ? "bg-slate-200" : "bg-slate-300"}>
              <td className="border py-2 text-center underline"><Link href={"/task/" + res.edetabel_nimi}>{res.edetabel_nimi}</Link></td>
              <td className="border text-center">{res.koht}</td>
              <td className="border text-center">{res.skoor ? res.skoor + (res.skoor1ühik ? " " + res.skoor1ühik : "") : "-"}</td>
              <td className="border text-center">{res.skoor2 ? res.skoor2 + (res.skoor2ühik ? " " + res.skoor2ühik : "") : "-"}</td>
            </tr>
          })}
        </tbody>
      </table>
    </div>
  </main>
}