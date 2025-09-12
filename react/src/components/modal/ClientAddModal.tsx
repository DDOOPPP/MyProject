import {useEffect, useState} from "react";
import type {Client} from "../../types/Client";
import Modal from "./Modal";
import {useCreate} from "../../hook/ClientHook.ts";
import "./Modal.css"
import "./Form.css"
interface Props{
    open: boolean;
    onClose: () => void;
    onCreated?: () => void;
}

export default function ClientAddModal({ open, onClose, onCreated }: Props) {
    const [form, setForm] = useState<Client>({
        name: "",
        host: "",
        path: "",
        serverType: "Server",
        port: 25565,
        min_memory: 1,
        max_memory: 2,
    });
    const { action, loading } = useCreate();

    // 모달이 열릴 때마다 폼 초기화(선택)
    useEffect(() => {
        if (open) {
            setForm({
                name: "",
                host: "",
                path: "",
                serverType: "Server",
                port: 25565,
                min_memory: 1,
                max_memory: 2,
            });
        }
    }, [open]);

    const onChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setForm(prev => ({
            ...prev,
            [name]: name === "port" || name.endsWith("_memory") ? Number(value) : value,
        }));
    };

    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!form.name.trim()) return alert("이름을 입력하세요.");
        if (!form.host.trim()) return alert("HOST를 입력하세요.");
        if (!form.path.trim()) return alert("파일 경로를 입력하세요.");
        if (form.min_memory > form.max_memory) return alert("Min 메모리는 Max 이하로 설정하세요.");

        try{
            await action(form);
            onCreated?.();
            onClose();
        }catch (err) {
            console.error(err);
            alert("등록 중 오류가 발생했습니다.");
        }
    };

    return (
        <Modal open={open} onClose={onClose} title="서버 등록">
            <form className="modal-form" onSubmit={onSubmit}>
                <label>
                    이름
                    <input name="name" value={form.name} onChange={onChange} required />
                </label>
                <label>
                    HOST
                    <input name="host" value={form.host} onChange={onChange} placeholder="127.0.0.1" required />
                </label>
                <label>
                    포트
                    <input name="port" type="number" min={1} max={65535} value={form.port} onChange={onChange} required />
                </label>
                <label>
                    파일 경로
                    {/* \는 문자열에서 이스케이프 필요 → \\ */}
                    <input name="path" value={form.path} onChange={onChange} placeholder="C:\\Servers\\Paper" required />
                </label>
                <label>
                    타입
                    <select name="serverType" value={form.serverType} onChange={onChange}>
                        <option value="Server">Server</option>
                        <option value="Velocity">Velocity</option>
                    </select>
                </label>
                <div className="grid-2">
                    <label>
                        메모리(GB)
                        <input name="min_memory" type="number" min={1} value={form.min_memory} onChange={onChange}/>
                         ~
                        <input name="max_memory" type="number" min={1} value={form.max_memory} onChange={onChange}/>
                    </label>
                </div>

                <div className="modal-actions">
                    <button type="submit">등록</button>
                    <button type="button" onClick={onClose}>취소</button>
                </div>
            </form>
        </Modal>
    );
}
