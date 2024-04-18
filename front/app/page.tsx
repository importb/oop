import Image from "next/image";
import Header from "@/components/header";

import "@/utils/types";

async function getUsers() {
  const res:Response = await fetch("localhost:8080/users");

  if (!res.ok) {
    throw new Error("Failed to fetch data");
  }

  const users: UserList = await res.json();

  return users;
}

export default function Home() {

  return (
    <main className="flex h-screen w-full flex-col items-center justify-between">
      <div className="h-full w-full bg-white flex flex-row justify-between pt-4">
        <div className="w-3/5 h-full">
          <h2 className="text-black text-2xl font-medium">Üldedetabel</h2>


        </div>
        <div className="w-2/5 h-full">
          <h2 className="text-black text-2xl font-medium">Ülesanded</h2>
        </div>
      </div>
    </main>
  );
}