import Image from "next/image";
import Header from "@/components/header";

import "@/utils/types";
import Link from "next/link";

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
    ];

    setTimeout(() => {
      resolve(data);
    }, 500);
  });
}


export default async function Home() {

  // const tasks = await getTasks();
  const tasks = await getTasksDummy();

  return (
    <main className="flex h-screen w-full flex-col items-center justify-between">
      <div className="h-full w-full bg-white flex flex-row justify-between pt-4">
        <div className="w-3/5 h-full">
          <h2 className="text-black text-2xl font-medium">Üldedetabel</h2>

        </div>
        <div className="w-2/5 h-full flex flex-col">
          <h2 className="text-black text-2xl font-medium">Ülesanded</h2>
          <div className="flex flex-col">
            {
              tasks.map((task, i) => {
                return <Link key={i} href={"/task/" + task.edetabelNimi} className="text-black">
                  {`${i+1}. ${task.edetabelNimi} (${task.userCount} osalejat)`}
                </Link>
              })
            }
          </div>
        </div>
      </div>
    </main>
  );
}