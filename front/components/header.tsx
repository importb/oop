"use client"
import React, { useEffect, useState } from "react"
import Image from "next/image";
import Link from "next/link";
import "@/utils/types";

import Fuse from "fuse.js";

export default function Header(props: object) {


  const [searchItem, setSearchItem] = useState<string>("");
  const [searchables, setSearchables] = useState<Searchable[]>([]);
  const [searchResults, setSearchResults] = useState<any>([]);
  const [selectedOptionIndex, setSelectedOptionIndex] = useState<number>(-1);
  const [showSearchBar, setShowSearchBar] = useState<boolean>(false);

  // plaan on panna kõik searchabled session storagesse, et teha efektiivsemaks päringuid
  useEffect(() => {
    const a = async () => {
      setTimeout(() => {
        setSearchables([
          {
            type: "user",
            value: "mc_Chaozz"
          },
          {
            type: "user",
            value: "XMithriLREPtIlE89X"
          },
          {
            type: "user",
            value: "XXIron_magEXX"
          },
          {
            type: "user",
            value: "zS4Tyr"
          },
          {
            type: "user",
            value: "MAGic_PrIncE"
          },
          {
            type: "user",
            value: "XliLGhoulX"
          },
          {
            type: "task",
            value: "algarvuringid"
          },
          {
            type: "task",
            value: "temperatuurid"
          },
          {
            type: "task",
            value: "korduvad_read"
          },
          {
            type: "task",
            value: "ruut_ühtedest"
          },
          {
            type: "task",
            value: "kuningad"
          },

        ])
      }, 1000);


    }
    a();
  }, [])

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchItem(e.target.value);

    if (e.target.value === "") {
      setSearchResults([]);
      setSelectedOptionIndex(-1);
      return;
    }
    setShowSearchBar(true);

    const fuse = new Fuse(searchables, {
      keys: ["value"]
    });

    const results = fuse.search(e.target.value);
    setSearchResults(results.slice(0, 3).map((res) => res.item));
    console.log(results.slice(0, 3).map((res) => res.item));
  }

  const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === "ArrowDown") {
      event.preventDefault();
      setSelectedOptionIndex((prevIndex) => (prevIndex < searchResults.length - 1 ? prevIndex + 1 : prevIndex));
    } else if (event.key === "ArrowUp") {
      event.preventDefault();
      setSelectedOptionIndex((prevIndex) => (prevIndex > 0 ? prevIndex - 1 : prevIndex));
    }
  };

  const handleKeyUp = (event: React.KeyboardEvent<HTMLInputElement>) => {
    event.preventDefault();
    if (searchItem == "")
      return;


    if (event.key === "Enter") {
      setShowSearchBar(false);
      // nooltega on valitud mingi komponent
      if (selectedOptionIndex >= 0 && selectedOptionIndex < searchResults.length) {
        const selectedOption: Searchable = searchResults[selectedOptionIndex];

        window.location.href = (selectedOption.type == "user" ? "/osaleja/" + selectedOption.value : "/task/" + selectedOption.value)
      } else {
        // lähme esimesse
        if (searchResults.length > 0) {
          window.location.href = (searchResults[0].type == "user" ? "/osaleja/" + searchResults[0].value : "/task/" + searchResults[0].value)
        }
      }

    };
  }


  return <div className="flex flex-row justify-between w-full h-24 bg-gradient-to-r from-red-700 to-violet-500 sticky">

    <Link href="/" className="pl-8 my-auto font-semibold text-3xl">Programmeerimine II Hall of Fame</Link>

    <div className={"flex-row h-10 w-80 my-auto bg-white border-2 border-slate-300 rounded-2xl  hidden md:flex " + (showSearchBar && searchResults.length > 0 ? " rounded-b-none " : "  ")}>
      <svg xmlns="http://www.w3.org/2000/svg" fill="#454545" height="24" viewBox="0 -960 960 960" width="24" className="my-auto mx-[9px]">
        <path d="M796-121 533-384q-30 26-69.959 40.5T378-329q-108.162 0-183.081-75Q120-479 120-585t75-181q75-75 181.5-75t181 75Q632-691 632-584.85 632-542 618-502q-14 40-42 75l264 262-44 44ZM377-389q81.25 0 138.125-57.5T572-585q0-81-56.875-138.5T377-781q-82.083 0-139.542 57.5Q180-666 180-585t57.458 138.5Q294.917-389 377-389Z" />
      </svg>


      <input
        className="w-full mr-3 text-black focus:outline-none"
        onKeyDown={handleKeyDown}
        onKeyUp={handleKeyUp}
        onFocus={() => {setShowSearchBar(true)}}
        onBlur={() => {setShowSearchBar(false)}}
        placeholder="Otsi ülesannet või osalejat"
        value={searchItem}
        onChange={(e) => handleSearch(e)}
      />

      {showSearchBar && searchResults.length > 0 && (
        <div className="absolute top-[68px] ml-[-2px] rounded-md">
          {searchResults.map((sr: Searchable, i: number) =>
            <Link href={(sr.type == "user" ? "/osaleja/" + sr.value : "/task/" + sr.value)} key={i}>
              <div className={"flex flex-row w-80 p-2 border-slate-300 border-2 border-t-0 " + (selectedOptionIndex == i ? " bg-slate-300 " : " bg-white ") + (i == searchResults.length - 1 ? " rounded-b-2xl " : " ") }>
                {
                  sr.type == "user" ?
                    <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#454545" className="mr-2">
                      <path d="M480-480q-66 0-113-47t-47-113q0-66 47-113t113-47q66 0 113 47t47 113q0 66-47 113t-113 47ZM160-160v-112q0-34 17.5-62.5T224-378q62-31 126-46.5T480-440q66 0 130 15.5T736-378q29 15 46.5 43.5T800-272v112H160Zm80-80h480v-32q0-11-5.5-20T700-306q-54-27-109-40.5T480-360q-56 0-111 13.5T260-306q-9 5-14.5 14t-5.5 20v32Zm240-320q33 0 56.5-23.5T560-640q0-33-23.5-56.5T480-720q-33 0-56.5 23.5T400-640q0 33 23.5 56.5T480-560Zm0-80Zm0 400Z" />
                    </svg>
                    :
                    <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#454545" className="mr-2">
                      <path d="m438-240 226-226-58-58-169 169-84-84-57 57 142 142ZM240-80q-33 0-56.5-23.5T160-160v-640q0-33 23.5-56.5T240-880h320l240 240v480q0 33-23.5 56.5T720-80H240Zm280-520v-200H240v640h480v-440H520ZM240-800v200-200 640-640Z" />
                    </svg>
                }
                <p className="text-black">{sr.value}</p>
              </div>
            </Link>
          )}
        </div>
      )}
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