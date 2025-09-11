import ClientTable from "../components/table/ClientTable.tsx";
import {LoadClients} from "../hook/ClientHook.ts";


export default function SettingPage(){
    const {clients ,error} = LoadClients();
    if (error){
        console.log(error);
    }
    return (
        <div className="content-container">
            <ClientTable clients={clients} />
        </div>
    )
}