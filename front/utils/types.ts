interface Result {
    edetabel_nimi: string;
    skoor1: number;
    skoor1Unit: string;
    skoor2: number | null;
    skoor2Unit: string | null;
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
}

type TaskList = Task[];

interface Searchable {
   type: 'user' | 'task';
   value: string;
}

type Searchables = Searchable[];
