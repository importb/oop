"use client"
import React, { useEffect, useState } from "react";
import { getHumanReadableDate } from "@/utils/dateUtils";

export default function taskPage(props: any) {
  const taskName = decodeURI(props.params.taskID);

  const fetchedData = [
    {
      timestamp: "1713732602",
      // ...
    },
    {
      timestamp: "1713732902",
      results: [
        {
          pseudo: "Osaleja1",
          skoor1: "(10, 9)",
          skoor2: "539"
        },
        {
          pseudo: "Osaleja2",
          skoor1: "(10, 9)",
          skoor2: "567"
        },
        {
          pseudo: "Osaleja3",
          skoor1: "(7, 5)",
          skoor2: "107"
        },
        {
          pseudo: "Osaleja4",
          skoor1: "(7, 5)",
          skoor2: "434"
        },
        {
          pseudo: "Osaleja5",
          skoor1: "(6, 4)",
          skoor2: "271"
        },
        {
          pseudo: "Osaleja6",
          skoor1: "(6, 4)",
          skoor2: "294"
        },
        {
          pseudo: "Osaleja3",
          skoor1: "(7, 5)",
          skoor2: "107"
        },
        {
          pseudo: "Osaleja4",
          skoor1: "(7, 5)",
          skoor2: "434"
        },
        {
          pseudo: "Osaleja5",
          skoor1: "(6, 4)",
          skoor2: "271"
        },
        {
          pseudo: "Osaleja6",
          skoor1: "(6, 4)",
          skoor2: "294"
        },
      ]
    }
  ]

  const [currentTimestamp, setCurrentTimestamp] = useState<string | undefined>(fetchedData.at(-1)?.timestamp);
  // console.log(getHumanReadableDate(fetchedData.at(-1)?.timestamp));
  const [curLeaderboard, setCurLeaderboard] = useState(fetchedData.at(-1)?.results);


  const [allLeaderboards, setAllLeaderboards] = useState([]);
  // peale esimest painti, fetchime kõik edetabelid ajas
  useEffect(() => {
    async function f() {
      const response = await fetch("/2024-05-10T131414.200.json");

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
    setCurLeaderboard(allLeaderboards[timestampIndex]?.results);
    setCurrentTimestamp(allLeaderboards[timestampIndex]?.timestamp);
  }, [timestampIndex]);

  useEffect(() => {
    console.log(timestampIndex, allLeaderboards.length - 1);
    if (timestampIndex >= allLeaderboards.length - 1) {
      console.log("Stopping");
      setIsCounting(false);
    }
  }, [timestampIndex]);

  return <main className="flex min-h-screen h-screen w-full flex-col items-center justify-between">
    <div className="h-full w-full bg-white pb-16 pt-16">
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
          curLeaderboard?.length >= 1 ?
            <div className=" bg-slate-300 py-4 rounded-md w-96 shadow-sm">
              <p className="text-black text-center text-lg">
                {"1. " + curLeaderboard[0].pseudo + " " + curLeaderboard[0].skoor + (curLeaderboard[0].skoor2 ? ", " + curLeaderboard[0].skoor2 : "")}
              </p>
            </div> : <></>
        }
        {
          curLeaderboard?.length >= 2 ?
            <div className=" bg-slate-300 py-4 rounded-md w-96 mt-2 shadow-sm">
              <p className="text-black text-center text-lg">
                {"2. " + curLeaderboard[1].pseudo + " " + curLeaderboard[1].skoor + (curLeaderboard[1].skoor2 ? ", " + curLeaderboard[1].skoor2 : "")}
              </p>
            </div> : <></>
        }
        {
          curLeaderboard?.length >= 3 ?
            <div className=" bg-slate-300 py-4 rounded-md w-96 mt-2 mb-1 shadow-sm">
              <p className="text-black text-center text-lg">
                {"3. " + curLeaderboard[2].pseudo + " " + curLeaderboard[2].skoor + (curLeaderboard[2].skoor2 ? ", " + curLeaderboard[2].skoor2 : "")}
              </p>
            </div> : <></>
        }
        {
          curLeaderboard?.slice(3).map((osaleja, i) => {
            return <div className=" bg-slate-300 py-2 rounded-md w-64 mt-1 shadow-sm" key={i + 4}>
              <p className="text-black text-center text-md">
                {(i + 4) + ". " + osaleja.pseudo + " " + osaleja.skoor + (osaleja.skoor2 ? ", " + osaleja.skoor2 : "")}
              </p>
            </div>
          })
        }
      </div>
    </div>
  </main>
}