import Image from "next/image";
import Header from "@/components/header";

import { truncateString } from "@/utils/stringUtils";
import "@/utils/types";
import Link from "next/link";
import { useEffect } from "react";

async function getUsers(): Promise<UserList> {
  const res: Response = await fetch("http://localhost:8080/osalejateEdetabel");

  if (!res.ok) {
    throw new Error("Failed to fetch data");
  }

  const users: UserList = await res.json();

  users.sort((u1, u2) => u2.ELO - u1.ELO);

  // console.log(users);

  return users;
}

async function getTasks(): Promise<TaskList> {
  const res: Response = await fetch("http://localhost:8080/ulesanded");

  if (!res.ok) {
    throw new Error("Failed to fetch data");
  }

  const tasks: TaskList = await res.json();

  return tasks;
}

export default async function Home() {

  const users: UserList = await getUsers();
  const tasks: TaskList = await getTasks();
  // const tasks = await getTasksDummy();


  return (
    <main className="flex h-screen w-full flex-col items-center justify-between">
      <div className="h-full w-full bg-white flex flex-row justify-between pt-4">
        <div className="w-4/5 h-full px-8">
          <h2 className="text-black text-2xl font-medium">Üldedetabel</h2>
          <table className="table-auto text-black w-full mt-4">
            <thead>
              <tr className="text-lg lg:text-xl">
                <th className="border">#</th>
                <th className="border">Pseudonüüm</th>
                <th className="border">ELO</th>
                {
                  tasks.map((task, i) => {
                    return <th className="border underline" key={i}>
                      <Link href={"/task/" + task.edetabelNimi}>
                        {"Ül " + (i + 1)}
                      </Link>
                    </th>
                  })
                }
              </tr>
            </thead>
            <tbody>
              {users.map((osaleja, i) => {
                return <tr key={i} className={i % 2 == 0 ? "bg-slate-200" : "bg-slate-300"}>
                  <td className="border text-center font-bold">{i + 1}</td>
                  <td className="border py-2 text-center underline">
                    <Link href={"/osaleja/" + osaleja.osaleja}>{osaleja.osaleja}
                    </Link>
                  </td>
                  <td className="border text-center">
                    {osaleja.ELO}
                  </td>
                  {
                    tasks.map((task, j) => {
                      if (osaleja.results.map(res => res.edetabel_nimi).includes(task.edetabelNimi)) {
                        return <td className="border text-center" key={j}>
                          {osaleja.results.filter(res => res.edetabel_nimi == task.edetabelNimi)[0].koht + "."}
                        </td>
                      } else {
                        return <td className="border text-center" key={j}>
                          -
                        </td>
                      }
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