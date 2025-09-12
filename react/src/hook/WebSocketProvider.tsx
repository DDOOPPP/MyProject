import { createContext, useContext } from 'react';
import { useStomp } from './WebSocketHook.ts';

type Props = {
    children: React.ReactNode;
    connectHeaders?: Record<string, string>;
};

const StompCtx = createContext<ReturnType<typeof useStomp> | null>(null);

export function StompProvider({ children, connectHeaders }: Props) {
    const stomp = useStomp({ connectHeaders });
    return <StompCtx.Provider value={stomp}>{children}</StompCtx.Provider>;
}

export function useStompCtx() {
    const ctx = useContext(StompCtx);
    if (!ctx) throw new Error('useStompCtx must be used within <StompProvider>');
    return ctx;
}