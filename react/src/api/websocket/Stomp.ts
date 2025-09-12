
import { Client, type StompHeaders } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export type StompEventHandlers = {
    onConnect?: () => void;
    onDisconnect?: () => void;
    onError?: (e: unknown) => void;
    connectHeaders?: StompHeaders | Record<string, string>;
};

export function CreateStomp({
                                onConnect,
                                onDisconnect,
                                onError,
                                connectHeaders,
                            }: StompEventHandlers = {}) {
    const client = new Client({
        webSocketFactory: () =>
            new SockJS('/ws', undefined, { transports: ['websocket'] }),
        reconnectDelay: 3000,
        heartbeatIncoming: 10000,
        heartbeatOutgoing: 10000,
        debug: (s) => console.log('[stomp]', s),

        onConnect: () => onConnect?.(),

        // ⚠️ 갑작스런 종료/에러는 여기로 옴 → 우리 쪽에 '끊김' 신호 보내기
        onWebSocketClose: () => onDisconnect?.(),
        onWebSocketError: (e) => {
            onError?.(e);
            onDisconnect?.();
        },
        onStompError: (frame) => {
            onError?.(frame);
            onDisconnect?.();
        },

        connectHeaders,
    });
    return client;
}
