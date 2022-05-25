export function update(from,to) {
    for (const entry of Object.entries(from))
    {
        const [key,value] = entry;
        to[key] = value;
    }
    return to;
}
