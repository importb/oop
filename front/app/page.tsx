import Image from "next/image";
import Header from "@/components/header";

import { truncateString } from "@/utils/stringUtils";
import "@/utils/types";
import Link from "next/link";
import { useEffect } from "react";

async function getUsers(): Promise<UserList> {
  const res: Response = await fetch("http://localhost:8080/users");

  if (!res.ok) {
    throw new Error("Failed to fetch data");
  }

  const users: UserList = await res.json();

  return users;
}

async function getTasks(): Promise<TaskList> {
  const res: Response = await fetch("http://localhost:8080/tasks");

  if (!res.ok) {
    throw new Error("Failed to fetch data");
  }

  const tasks: TaskList = await res.json();

  return tasks;
}

async function getTasksDummy(): Promise<TaskList> {
  return new Promise((resolve) => {
    let data: TaskList = [
      {
        edetabelNimi: "algarvuringid",
        userCount: 55,
      },
      {
        edetabelNimi: "temperatuurid",
        userCount: 15,
      },
      {
        edetabelNimi: "korduvad_read",
        userCount: 77,
      },
      {
        edetabelNimi: "ruut_ühtedest",
        userCount: 39,
      },
      {
        edetabelNimi: "summad_ja_sõnepõime",
        userCount: 78,
      },
      {
        edetabelNimi: "jaotus_rühmadeks",
        userCount: 69,
      },
      {
        edetabelNimi: "kuningad",
        userCount: 69,
      },
    ];

    setTimeout(() => {
      resolve(data);
    }, 500);
  });
}



const ELOedetabel = [
  {
    "osaleja": "mc_CHAozz",
    "ELO": 1200,
    "results": [
      {
        "edetabel_nimi": "algarvuringid",
        "koht": 1,
        "skoor": "2.5",
        "skoor2": null,
        "skoor1ühik": "ms",
        "skoor2ühik": null
      },
      {
        "edetabel_nimi": "temperatuurid",
        "koht": 2,
        "skoor": "4769",
        "skoor2": null,
        "skoor1ühik": "ms",
        "skoor2ühik": null
      },
      {
        "edetabel_nimi": "korduvad_read",
        "koht": 3,
        "skoor": "121",
        "skoor2": null,
        "skoor1ühik": "ms",
        "skoor2ühik": null
      },
      {
        "edetabel_nimi": "ruut_ühtedest",
        "koht": 19,
        "skoor": "19.9",
        "skoor2": null,
        "skoor1ühik": "ms",
        "skoor2ühik": null
      },
      {
        "edetabel_nimi": "summad_ja_sõnepõime",
        "koht": 1,
        "skoor": "2.5",
        "skoor2": null,
        "skoor1ühik": "ms",
        "skoor2ühik": null
      },
      {
        "edetabel_nimi": "jaotus_rühmadeks",
        "koht": 4,
        "skoor": "28",
        "skoor2": null,
        "skoor1ühik": "ms",
        "skoor2ühik": null
      },
      {
        "edetabel_nimi": "kuningad",
        "koht": 1,
        "skoor": "(10, 9)",
        "skoor2": "539.8",
        "skoor1ühik": null,
        "skoor2ühik": "ms"
      },

    ]
  },
  {
    "osaleja": "mc_CHAozz",
    "ELO": 1200,
    "results": [
      {
        "edetabel_nimi": "algarvuringid",
        "koht": 1,
        "skoor": "2.5",
        "skoor2": null,
        "skoor1ühik": "ms",
        "skoor2ühik": null
      },
      {
        "edetabel_nimi": "temperatuurid",
        "koht": 2,
        "skoor": "4769",
        "skoor2": null,
        "skoor1ühik": "ms",
        "skoor2ühik": null
      },
      {
        "edetabel_nimi": "korduvad_read",
        "koht": 3,
        "skoor": "121",
        "skoor2": null,
        "skoor1ühik": "ms",
        "skoor2ühik": null
      },
      {
        "edetabel_nimi": "ruut_ühtedest",
        "koht": 19,
        "skoor": "19.9",
        "skoor2": null,
        "skoor1ühik": "ms",
        "skoor2ühik": null
      },
      {
        "edetabel_nimi": "summad_ja_sõnepõime",
        "koht": 1,
        "skoor": "2.5",
        "skoor2": null,
        "skoor1ühik": "ms",
        "skoor2ühik": null
      },
      {
        "edetabel_nimi": "jaotus_rühmadeks",
        "koht": 4,
        "skoor": "28",
        "skoor2": null,
        "skoor1ühik": "ms",
        "skoor2ühik": null
      },
      {
        "edetabel_nimi": "kuningad",
        "koht": 1,
        "skoor": "(10, 9)",
        "skoor2": "539.8",
        "skoor1ühik": null,
        "skoor2ühik": "ms"
      },

    ]
  },
  {
    "osaleja": "mc_CHAozz",
    "ELO": 1200,
    "results": [
      {
        "edetabel_nimi": "algarvuringid",
        "koht": 1,
        "skoor": "2.5",
        "skoor2": null,
        "skoor1ühik": "ms",
        "skoor2ühik": null
      },
      {
        "edetabel_nimi": "temperatuurid",
        "koht": 2,
        "skoor": "4769",
        "skoor2": null,
        "skoor1ühik": "ms",
        "skoor2ühik": null
      },
      {
        "edetabel_nimi": "korduvad_read",
        "koht": 3,
        "skoor": "121",
        "skoor2": null,
        "skoor1ühik": "ms",
        "skoor2ühik": null
      },
      {
        "edetabel_nimi": "ruut_ühtedest",
        "koht": 19,
        "skoor": "19.9",
        "skoor2": null,
        "skoor1ühik": "ms",
        "skoor2ühik": null
      },
      {
        "edetabel_nimi": "summad_ja_sõnepõime",
        "koht": 1,
        "skoor": "2.5",
        "skoor2": null,
        "skoor1ühik": "ms",
        "skoor2ühik": null
      },
      {
        "edetabel_nimi": "jaotus_rühmadeks",
        "koht": 4,
        "skoor": "28",
        "skoor2": null,
        "skoor1ühik": "ms",
        "skoor2ühik": null
      },
      {
        "edetabel_nimi": "kuningad",
        "koht": 1,
        "skoor": "(10, 9)",
        "skoor2": "539.8",
        "skoor1ühik": null,
        "skoor2ühik": "ms"
      },

    ]
  }
]

export default async function Home() {

  // const tasks = await getTasks();
  const tasks = await getTasksDummy();

  return (
    <main className="flex h-screen w-full flex-col items-center justify-between">
      <div className="h-full w-full bg-white flex flex-row justify-between pt-4">
        <div className="w-4/5 h-full px-8">
          <h2 className="text-black text-2xl font-medium">Üldedetabel</h2>
          <table className="table-auto text-black w-full mt-4">
            <thead>
              <tr className="text-lg lg:text-xl">
                <th className="border">Pseudonüüm</th>
                <th className="border">ELO</th>
                {
                  tasks.map((task, i) => {
                    return <th className="border" key={i}>
                      <Link href={"/task/" + task.edetabelNimi}>
                        {"Ül " + (i + 1)}
                      </Link>
                    </th>
                  })
                }
              </tr>
            </thead>
            <tbody>
              {ELOedetabel.map((osaleja, i) => {
                return <tr key={i} className={i % 2 == 0 ? "bg-slate-200" : "bg-slate-300"}>
                  <td className="border py-2 text-center underline">
                    <Link href={"/osaleja/" + osaleja.osaleja}>{osaleja.osaleja}
                    </Link>
                  </td>
                  <td className="border text-center">
                    {osaleja.ELO}
                  </td>
                  {
                    osaleja.results.map((res, j) => {
                      return <td className="border text-center" key={i*100 + j}>
                        {res.koht + "."}
                      </td>
                    })
                  }
                </tr>
              })}
            </tbody>
          </table>
        </div>


        <div className="w-2/5 h-full flex flex-col items-center">
          <h2 className="text-black text-2xl font-medium">Ülesanded</h2>
          <div className="flex flex-col">
            {
              tasks.map((task, i) => {
                return <Link key={i} href={"/task/" + task.edetabelNimi} className="text-black">
                  {`${i + 1}. ${task.edetabelNimi} (${task.userCount} osalejat)`}
                </Link>
              })
            }
          </div>
        </div>
      </div>
    </main>
  );
}