// src/hooks/useStomp.ts
import { useEffect, useMemo, useRef, useState } from 'react';
import {type IMessage, type StompSubscription} from '@stomp/stompjs';
import { CreateStomp } from '../api/websocket/Stomp.ts';
import { useCallback } from 'react';
type UseStompOptions = {
    // 필요 시 사용자명/토큰 등 헤더 붙이기
    connectHeaders?: Record<string, string>;
};

export function useStomp({ connectHeaders }: UseStompOptions = {}) {
    const [connected, setConnected] = useState(false);
    const [errors, setErrors] = useState<unknown[]>([]);
    const subsRef = useRef<StompSubscription[]>([]);
    const client = useMemo(
        () =>
            CreateStomp({
                connectHeaders,
                onConnect: () => setConnected(true),
                onDisconnect: () => setConnected(false), // ← close/err에 의해 호출됨
                onError: (e) => setErrors((p) => [...p, e]),
            }),
        [connectHeaders]
    );

    useEffect(() => {
        if (!client.active && !client.connected) {
            client.activate();        // 이미 활성화/연결이면 재호출 안 함
        }
        return () => {
            for (const s of subsRef.current.splice(0)) {
                try { s.unsubscribe(); } catch {}
            }
            if (client.active || client.connected) {
                void client.deactivate();
            }
        };
    }, [client]);

    // 구독 도우미
    const subscribe = useCallback((destination: string, cb: (msg: IMessage) => void) => {
        if (!client.connected) return;
        const sub = client.subscribe(destination, cb);
        subsRef.current.push(sub);
        return sub;
    }, [client]); //

    // 발행 도우미
    const sendJson = (destination: string, body: unknown) => {
        if (!client.connected) return;
        client.publish({ destination, body: JSON.stringify(body) });
    };
    const sendText = (destination: string, text: string) => {
        if (!client.connected) return;
        client.publish({ destination, body: text });
    };

    return { client, connected, subscribe, sendJson, sendText, errors };
}
