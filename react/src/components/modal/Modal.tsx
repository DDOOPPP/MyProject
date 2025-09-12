import {type ReactNode, useRef} from "react";
import {useEffect} from "react";
import { FaBackspace } from "react-icons/fa";
interface ModalProps {
    open: boolean;
    onClose: () => void;
    children: ReactNode;
    title?: string;
}


export default function Modal({ open, onClose, children, title }: ModalProps) {
    const contentRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const onEsc = (e: KeyboardEvent) => e.key === "Escape" && onClose();
        if (open) document.addEventListener("keydown", onEsc);
        return () => document.removeEventListener("keydown", onEsc);
    }, [open, onClose]);

    if (!open) return null;

    return (
        <div className="modal-overlay" role="dialog" aria-modal="true" aria-label={title ?? "dialog"} onClick={onClose}>
            <div
                ref={contentRef}
                className="modal-content"
                onClick={(e) => e.stopPropagation()}
                tabIndex={-1}
            >
                <div className="modal-header">
                    <div aria-hidden="true"/>
                    {title && <h3 className="modal-title">{title}</h3>}
                    <button className="modal-close" aria-label="닫기" onClick={onClose}>
                        <FaBackspace/>
                    </button>
                </div>
                <div className="modal-title">
                    {children}
                </div>
            </div>
        </div>
    );
}