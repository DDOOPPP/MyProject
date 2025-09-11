import api from "./Axios.ts";
import type {Client} from "../types/Client.tsx";

export const fetchClients = () => api.get<Client[]>("/client");
export const fetchClient = (id:string) => api.get<Client | null>(`/client/${id}`)
export const fetchCreate = (client:Client) => api.post<Client>("/client",client);
export const DeleteClient = (id:string) => api.delete(`/client/${id}`);
