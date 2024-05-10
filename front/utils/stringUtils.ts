export {truncateString};

function truncateString(str: string, maxLength: number): string {
    if (str.length <= maxLength) {
        return str;
    } else {
        return str.slice(0, maxLength) + "...";
    }
}

