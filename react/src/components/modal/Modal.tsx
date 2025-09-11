import type {ReactNode} from "react";
import {useEffect} from "react";

interface ModalProps {
    open: boolean;
    onClose: () => void;
    children: ReactNode
}

export default function Modal({open, onClose, children}:ModalProps){
    useEffect(() => {
        const onEsc = (e: KeyboardEvent) => e.key === "Escape" && onClose();
        if (open) document.addEventListener("keydown", onEsc);
        return () => document.removeEventListener("keydown", onEsc);
    }, [open, onClose]);

    if (!open){
        return null;
    }

    return (
        <div onClick={onClose} className="modal-overlay">
            <div className="modal-content">
                {children}
            </div>
        </div>
    )
}