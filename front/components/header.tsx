"use client"
import React, { useEffect, useState } from "react"
import Image from "next/image";
import Link from "next/link";
import "@/utils/types";

async function getSearchables(): Promise<Searchables> {
  // halb, et peab requeste duplikeerima / lehelt
  const usersResponse: Response = await fetch("http://localhost:8080/users");

  if (!usersResponse.ok) {
    throw new Error("Failed to fetch data");
  }
  const users: UserList = await usersResponse.json();

  const tasksResponse: Response = await fetch("http://localhost:8080/tasks");
  if (!tasksResponse.ok) {
    throw new Error("Failed to fetch data");
  }
  const tasks: TaskList = await tasksResponse.json();


  const searchables: Searchables = [
    ...users.map((user) => ({
      type: 'user' as 'user',
      value: user.pseudoname
    })),
    ...tasks.map((task) => ({
      type: 'task' as 'task',
      value: task.edetabelNimi
    }))
  ];


  return searchables;
}

export default function Header(props: object) {


  const [searchItem, setSearchItem] = useState("");
  const [searchables, setSearchables] = useState([]);
  const [searchResults, setSearchResults] = useState([]);
  
  useEffect(() => {

  }, [searchItem]);

  useEffect(() => {
    const a = async () => {
      // const fetchedSearchables: Searchables = await getSearchables();
      // setSearchables(fetchedSearchables);
    }
    a();
  }, [])

  return <div className="flex flex-row justify-between w-full h-24 bg-gradient-to-r from-red-700 to-violet-500">

    <Link href="/" className="pl-8 my-auto font-semibold text-3xl">Programmeerimine II Hall of Fame</Link>

    <div className="flex flex-row h-8 w-80 my-auto bg-white border-slate-300 border-2 rounded-2xl">
      <svg xmlns="http://www.w3.org/2000/svg" fill="rgb(203, 213, 225)" height="24" viewBox="0 -960 960 960" width="24" className="my-auto mx-1">
        <path d="M796-121 533-384q-30 26-69.959 40.5T378-329q-108.162 0-183.081-75Q120-479 120-585t75-181q75-75 181.5-75t181 75Q632-691 632-584.85 632-542 618-502q-14 40-42 75l264 262-44 44ZM377-389q81.25 0 138.125-57.5T572-585q0-81-56.875-138.5T377-781q-82.083 0-139.542 57.5Q180-666 180-585t57.458 138.5Q294.917-389 377-389Z" />
      </svg>

      <input
        className="w-full mr-3 text-black focus:outline-none"
        placeholder="Otsi ülesannet või osalejat"
        value={searchItem}
        onChange={(e) => {
          setSearchItem(e.target.value);
        }}
      />
    </div>
    <button
      onClick={() => {
        alert("🤠")
      }}
    >
      <Image
        src={"/bioskullr.gif"}
        alt={"Vaheta teemat"}
        width={100}
        height={30}
      />
    </button>


  </div>

}