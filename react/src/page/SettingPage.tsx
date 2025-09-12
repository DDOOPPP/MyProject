import ClientTable from "../components/table/ClientTable.tsx";
import {LoadClients} from "../hook/ClientHook.ts";
import {useState} from "react";
import ClientAddModal from "../components/modal/ClientAddModal.tsx";


export default function SettingPage(){
    const {clients ,loading ,error,refetch} = LoadClients();
    const [open,setOpen] = useState(false);
    if (error){
        console.log(error);
    }
    console.log(import.meta.env.VITE_API_URL+"/client");
    console.log(clients)
    return (
            <div className="content-container">
                <div>
                    <ClientTable clients={clients} loading={loading} onAddClick={() => setOpen(true)} />
                    <ClientAddModal open={open} onClose={() => setOpen(false)} onCreated={async () => {
                        await refetch();
                        setOpen(false);
                    }} />
                </div>
            </div>
    )
}