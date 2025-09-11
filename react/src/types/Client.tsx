export interface Client {
    name : string;
    host: string;
    path: string;
    serverType: string;
    port: number;
    min_memory: number;
    max_memory: number;
}