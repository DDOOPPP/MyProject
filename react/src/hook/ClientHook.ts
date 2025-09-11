import {useCallback, useEffect, useState} from "react";
import type {Client} from "../types/Client.tsx";
import {DeleteClient, fetchClient, fetchClients, fetchCreate} from "../api/ClientAPI.ts";

export function LoadClients(){
    const [clients , setClients] = useState<Client[]>([]);
    const [loading, setLoading] = useState(true);
    const [error,setError] = useState<unknown|null>(null);

    const refetch = useCallback(async () => {
        try{
            setLoading(true);
            const {data} = await fetchClients();
            setClients(data);
            setError(null)
        }catch (e){
            setError(e);
        }finally {
            setLoading(false);
        }
    },[])

    useEffect(() => {
        refetch();
    }, [refetch]);
    
    return {clients,loading,error,refetch}
}

export function LoadClient({id = ""}){
    const [client,setClient] = useState<Client | null>(null);
    const [loading, setLoading] = useState(true);
    const [error,setError] = useState<unknown|null>(null);

    const refetch = useCallback(async () => {
        try{
            setLoading(true);
            const {data} = await fetchClient(id);
            setClient(data);
            setError(null)
        }catch (e){
            setError(e);
            setClient(null);
        }finally {
            setLoading(false);
        }
    },[])

    useEffect(() => {
        refetch();
    }, [refetch]);

    return {client,loading,error,refetch}
}

export function useDelete(){
    const [loading , setLoading] = useState(true);
    const [error, setError] = useState<unknown|null>(null);

    const clientDelete = useCallback(async (id: string) => {
        try{
            setLoading(true);
            await DeleteClient(id);
        }catch (e) {
            setError(e);
            throw e;
        } finally {
            setLoading(false);
        }
    },[]);

    return { clientDelete , loading, error};
}

export function useCreate(){
    const [loading , setLoading] = useState(true);
    const [error, setError] = useState<unknown|null>(null);

    const action = useCallback(async (client: Client) => {
        try{
            setLoading(true);
            await fetchCreate(client);
        }catch (e) {
            setError(e);
            throw e;
        } finally {
            setLoading(false);
        }
    },[]);

    return { action , loading, error};
}