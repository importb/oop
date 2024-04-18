interface Result {
    edetabelNimi: string;
    skoor1: number;
    skoor1Unit: string;
    skoor2: number | null;
    skoor2Unit: string | null;
}

interface User {
    pseudoname: string;
    ELO: number;
    results: Result[];
}

type UserList = User[];
