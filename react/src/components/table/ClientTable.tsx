import type {Client} from "../../types/Client.tsx";
import { GoBrowser } from "react-icons/go";
import { FaEdit } from "react-icons/fa";
import { FaTrash } from "react-icons/fa";
import { FaPlus } from "react-icons/fa";
import "./Table.css"

interface ClientTableProps {
    clients: Client[];
    loading: boolean;
    onAddClick? : () => void;
}

export default function ClientTable({ clients, loading, onAddClick }: ClientTableProps) {
    return (
        <div className="article">
            <div className="table title">
                <h4 >등록된 서버 리스트</h4>
                <button onClick={onAddClick}><FaPlus /> 등록</button>
            </div>
            <div>
                <table className="table-overlay">
                <thead>
                    <tr>
                        <th>이름</th>
                        <th>HOST</th>
                        <th>파일 위치</th>
                        <th>메모리 (Min/Max)</th>
                        <th>세부</th>
                    </tr>
                    </thead>
                    <tbody>
                    {loading ? (
                        <tr>
                            <td colSpan={5}>데이터를 불러오는 중...</td>
                        </tr>
                    ) : clients.length > 0 ? (
                        clients.map(client => (
                            <tr key={client.name}>
                                <td>{client.name}</td>
                                <td>{client.host}:{client.port}</td>
                                <td>{client.path}</td>
                                <td>{client.min_memory}(GB)/{client.max_memory}(GB)</td>
                                <td>
                                    <div>
                                        <GoBrowser/>
                                        <FaEdit/>
                                        <FaTrash/>
                                    </div>
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan={5}>등록된 서버가 없습니다.</td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    )
}