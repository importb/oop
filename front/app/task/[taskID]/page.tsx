"use client"
import React, { useState } from "react";
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

      ]
    }
  ]

  const [currentTimestamp, setCurrentTimestamp] = useState<string | undefined>(fetchedData.at(-1)?.timestamp);
  // console.log(getHumanReadableDate(fetchedData.at(-1)?.timestamp));
  const [curLeaderboard, setCurLeaderboard] = useState(fetchedData.at(-1)?.results);


  return <main className="flex h-screen w-full flex-col items-center justify-between">
    <div className="h-full w-full bg-white pt-16">
      <div className="flex flex-row ">
        <button className="flex flex-row bg-green-600 items-center pr-4 rounded-lg ml-32 absolute">
          <svg xmlns="http://www.w3.org/2000/svg" height="48px" viewBox="0 -960 960 960" width="48px" fill="#fff">
            <path d="M320-200v-560l440 280-440 280Zm80-280Zm0 134 210-134-210-134v268Z" />
          </svg>
          <p className="text-white text-xl">Animeeri</p>
        </button>

        <div className="mx-auto flex flex-col">
          <h1 className="text-black text-center text-4xl font-bold">
            {taskName}
          </h1>
          <h2 className="text-black text-xl text-center">
            {getHumanReadableDate(currentTimestamp)}
          </h2>
        </div>
      </div>
      <div className="flex flex-col items-center">
        {
          curLeaderboard?.length >= 1 ?
            <div>
              <p className="text-black">
                {"1. " + curLeaderboard[0].pseudo}
              </p>
            </div> : <></>
        }
        {
          curLeaderboard?.length >= 2 ?
            <div>
              <p className="text-black">
                {"2. " + curLeaderboard[1].pseudo}
              </p>
            </div> : <></>
        }
        {
          curLeaderboard?.length >= 3 ?
            <div>
              <p className="text-black">
                {"3. " + curLeaderboard[2].pseudo}
              </p>
            </div> : <></>
        }
        {
          curLeaderboard?.slice(3).map((osaleja, i) => {
            return <div>
              <p className="text-black">
                {(i + 4) + ". " + osaleja.pseudo}
              </p>
            </div>
          })
        }
      </div>
    </div>
  </main>
}