"use client"
import React, { useEffect, useState } from "react";
import { getHumanReadableDate } from "@/utils/dateUtils";
import Link from "next/link";

export default function TaskPage(props: any) {
  const taskName = decodeURI(props.params.taskID);

  const [currentTimestamp, setCurrentTimestamp] = useState<string>("0");
  // console.log(getHumanReadableDate(fetchedData.at(-1)?.timestamp));
  const [curLeaderboard, setCurLeaderboard] = useState<SingleEdetabel>({
    timestamp: Date.now(),
    results: []
  });


  const [allLeaderboards, setAllLeaderboards] = useState<SingleEdetabel[]>([]);
  // peale esimest painti, fetchime kõik edetabelid ajas
  useEffect(() => {
    async function f() {
      const response = await fetch(process.env.NEXT_PUBLIC_BACKEND_URL +  "/edetabel/" + encodeURI(taskName) + "?type=all");

      if (!response.ok) {
        console.error("Failed to fetch");
        return;
      }

      const jsonData = await response.json();
      console.log(jsonData);

      setAllLeaderboards(jsonData);


      // hiljem maha:
      setCurLeaderboard(jsonData.at(-1)?.results);
      setCurrentTimestamp(jsonData.at(-1)?.timestamp)
    }

    f();
  }, [])

  const [timestampIndex, setTimestampIndex] = useState(0);
  const [isCounting, setIsCounting] = useState(false);
  function animeeri() {
    if (isCounting) {
      console.log("Peatasin lugemise")
    } else {
      console.log("Alustan lugemist")
      setTimestampIndex(0);
    }

    setIsCounting(!isCounting);
  }

  let interval: NodeJS.Timeout;
  useEffect(() => {

    if (isCounting) {
      interval = setInterval(() => {
        setTimestampIndex(prev => prev + 1);
      }, 50);

    } else {
      clearInterval(interval);
    }

    // Cleanup function to clear the interval when component unmounts
    return () => clearInterval(interval);
  }, [isCounting]);

  useEffect(() => {
    const leaderboard = allLeaderboards[timestampIndex];
    if (leaderboard) {
      setCurLeaderboard(leaderboard);
      setCurrentTimestamp(leaderboard.timestamp.toString());
    }
  }, [timestampIndex, allLeaderboards]);

  useEffect(() => {
    console.log(timestampIndex, allLeaderboards.length - 1);
    if (timestampIndex >= allLeaderboards.length - 1) {
      console.log("Stopping");
      setIsCounting(false);
    }
  }, [timestampIndex]);

  return <main className="flex h-full w-full flex-col items-center justify-between">
    <div className=" h-full w-full bg-white pb-16 pt-16">
      <div className="flex flex-row lg:justify-normal justify-center">
        {
          isCounting ?
            <button
              className="flex flex-row bg-red-600 items-center pr-4 rounded-lg absolute lg:ml-32 lg:m-0 mt-20 hover:bg-red-700"
              onClick={animeeri}
            >
              <svg xmlns="http://www.w3.org/2000/svg" height="48px" viewBox="0 -960 960 960" width="48px" fill="#fff">
                <path d="M320-640v320-320Zm-80 400v-480h480v480H240Zm80-80h320v-320H320v320Z" />
              </svg>
              <p className="text-white text-xl">Peata</p>
            </button>
            :
            <button
              className="flex flex-row bg-green-600 items-center pr-4 rounded-lg absolute lg:ml-32 lg:m-0 mt-20 hover:bg-green-700"
              onClick={animeeri}
            >
              <svg xmlns="http://www.w3.org/2000/svg" height="48px" viewBox="0 -960 960 960" width="48px" fill="#fff">
                <path d="M320-200v-560l440 280-440 280Zm80-280Zm0 134 210-134-210-134v268Z" />
              </svg>
              <p className="text-white text-xl">Animeeri</p>
            </button>
        }

        <div className="mx-auto flex flex-col">
          <h1 className="text-black text-center text-4xl font-bold">
            {taskName}
          </h1>
          <h2 className="text-black text-xl text-center">
            {getHumanReadableDate(currentTimestamp)}
          </h2>
        </div>
      </div>
      <div className="flex flex-col items-center mt-20 lg:mt-4">
        {
          curLeaderboard?.results.length >= 1 ?
            <div className=" bg-slate-300 py-4 rounded-md w-96 shadow-sm">
              <p className="text-black text-center text-lg hover:underline">
                <Link href={"/osaleja/" + curLeaderboard.results[0].pseudo}>
                  {"1. " + curLeaderboard.results[0].pseudo + " " + curLeaderboard.results[0].skoor + (curLeaderboard.results[0].skoor2 ? ", " + curLeaderboard.results[0].skoor2 : "")}
                </Link>
              </p>
            </div> : <></>
        }
        {
          curLeaderboard?.results.length >= 2 ?
            <div className=" bg-slate-300 py-4 rounded-md w-96 mt-2 shadow-sm">
              <p className="text-black text-center text-lg hover:underline">
                <Link href={"/osaleja/" + curLeaderboard.results[1].pseudo}>
                  {"2. " + curLeaderboard.results[1].pseudo + " " + curLeaderboard.results[1].skoor + (curLeaderboard.results[1].skoor2 ? ", " + curLeaderboard.results[1].skoor2 : "")}
                </Link>
              </p>
            </div> : <></>
        }
        {
          curLeaderboard?.results.length >= 3 ?
            <div className=" bg-slate-300 py-4 rounded-md w-96 mt-2 mb-1 shadow-sm">
              <p className="text-black text-center text-lg hover:underline">
                <Link href={"/osaleja/" + curLeaderboard.results[2].pseudo}>
                  {"3. " + curLeaderboard.results[2].pseudo + " " + curLeaderboard.results[2].skoor + (curLeaderboard.results[2].skoor2 ? ", " + curLeaderboard.results[2].skoor2 : "")}
                </Link>
              </p>
            </div> : <></>
        }
        {
          curLeaderboard?.results.slice(3).map((osaleja, i) => {
            return <div className=" bg-slate-300 py-2 rounded-md w-64 mt-1 shadow-sm" key={i + 4}>
              <p className="text-black text-center text-md hover:underline">
                <Link href={"/osaleja/" + osaleja.pseudo}>
                  {(i + 4) + ". " + osaleja.pseudo + " " + osaleja.skoor + (osaleja.skoor2 ? ", " + osaleja.skoor2 : "")}
                </Link>
              </p>
            </div>
          })
        }
      </div>
    </div>
  </main>
}