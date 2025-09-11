import ClientTable from "../components/table/ClientTable.tsx";
import {LoadClients} from "../hook/ClientHook.ts";


export default function SettingPage(){
    const {clients ,loading ,error} = LoadClients();
    if (error){
        console.log(error);
    }
    console.log(import.meta.env.VITE_API_URL+"/client");
    console.log(clients)
    return (
        <div className="content-container">
            <ClientTable clients={clients} loading={loading}/>
        </div>
    )
}