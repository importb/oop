interface Result {
    edetabel_nimi: string;
    skoor: number;
    skoor1ühik: string;
    skoor2: number | null;
    skoor2ühik: string | null;
    koht: number | undefined;
}

interface User {
    osaleja: string;
    ELO: number;
    results: Result[];
}

type UserList = User[];

interface Task {
    edetabel_nimi: string;
    userCount: number;
    finished: boolean | undefined;
}

type TaskList = Task[];

interface Searchable {
   type: 'user' | 'task';
   value: string;
}

interface SingleEdetabel {
    timestamp: number;
    results: {
        skoor: string,
        skoor2: number,
        pseudo: string;
    }[];
}

