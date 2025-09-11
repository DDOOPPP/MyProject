import { Client, type Frame, type IMessage, type StompSubscription } from "@stomp/stompjs";
import { useEffect, useMemo, useRef, useState } from "react";
import SockJS from "sockjs-client";

type Json = Record<string, unknown>;

export type StompStatus = "DISCONNECTED" | "CONNECTED" | "CONNECTING";

export interface UseStompOptions<TOut extends Json = Json> {
    endpoint: string;
    connectHeaders?: Record<string, string>;
    reconnectDelay?: number;
    parse?: (raw: string) => TOut | null;
    debug?: boolean;
    maxPending?: number; // 추가: 전송 큐 제한 (기본 100)
}

export interface SubscriptionSpec<T = unknown> {
    destination: string; // "/topic/...","/queue/...","/user/queue/..."
    handler: (body: T | string | null, message: IMessage) => void;
}

export function useStomp<TOut extends Json = Json>({
                                                       endpoint,
                                                       connectHeaders,
                                                       reconnectDelay = 5000,
                                                       parse = (raw) => { try { return JSON.parse(raw) as TOut; } catch { return null; } },
                                                       debug = false,
                                                       maxPending = 100,
                                                   }: UseStompOptions<TOut>) {
    const [status, setStatus] = useState<StompStatus>("DISCONNECTED");
    const clientRef = useRef<Client | null>(null);

    const subsRef = useRef<Map<string, StompSubscription>>(new Map());
    const specRef = useRef<Map<string, SubscriptionSpec<TOut>>>(new Map());
    const pendingSends = useRef<Array<{ dest: string; body: unknown; headers?: Record<string, string> }>>([]);

    const client = useMemo(() => {
        const c = new Client({
            webSocketFactory: () => new SockJS(endpoint),
            reconnectDelay,
            heartbeatIncoming: 10000,
            heartbeatOutgoing: 10000,
            connectHeaders,
            debug: debug ? (msg) => console.log("STOMP:", msg) : () => {},
        });
        clientRef.current = c;
        return c;

    }, [endpoint, reconnectDelay, JSON.stringify(connectHeaders), debug]);

    useEffect(() => {
        setStatus("CONNECTING");

        client.onConnect = (frame: Frame) => {
            setStatus("CONNECTED");

            console.log("headers:", frame.headers);


            const version = frame.headers["version"];
            const sessionId = frame.headers["session"];
            const heartBeat = frame.headers["heart-beat"];

            console.log("STOMP version:", version);
            console.log("Session ID:", sessionId);
            console.log("Heart-beat:", heartBeat);

            if (specRef.current.size) {
                specRef.current.forEach((spec, dest) => {
                    const sub = client.subscribe(dest, (msg: IMessage) => {
                        if (!spec.handler) return;
                        if (msg.body) {
                            const parsed = parse(msg.body);
                            spec.handler(parsed ?? msg.body, msg);
                        } else {
                            spec.handler(null, msg);
                        }
                    });
                    subsRef.current.set(dest, sub);
                });
            }


            if (pendingSends.current.length) {
                pendingSends.current.forEach(({ dest, body, headers }) =>
                    client.publish({ destination: dest, body: JSON.stringify(body), headers }),
                );
                pendingSends.current = [];
            }
        };

        client.onStompError = (frame) => {
            console.error("STOMP ERROR:", frame.headers["message"], frame.body);
        };
        client.onWebSocketClose = () => setStatus("DISCONNECTED");
        client.onDisconnect = () => setStatus("DISCONNECTED");

        client.activate();

        return () => {
            subsRef.current.forEach((s) => s.unsubscribe());
            subsRef.current.clear();
            specRef.current.clear();
            client.deactivate();
        };
    }, [client, parse]);


    const subscribe = (spec: SubscriptionSpec<TOut>) => {
        const { destination } = spec;

        const prev = subsRef.current.get(destination);
        prev?.unsubscribe();

        specRef.current.set(destination, spec);

        const c = clientRef.current;
        if (!c) return () => {};

        const sub = c.subscribe(destination, (msg: IMessage) => {
            if (!spec.handler) return;
            if (msg.body) {
                const parsed = parse(msg.body);
                spec.handler(parsed ?? msg.body, msg);
            } else {
                spec.handler(null, msg);
            }
        });

        subsRef.current.set(destination, sub);

        // 언구독 함수 반환
        return () => unsubscribe(destination);
    };

    const unsubscribe = (destination: string) => {
        const sub = subsRef.current.get(destination);
        sub?.unsubscribe();
        subsRef.current.delete(destination);
        specRef.current.delete(destination);
    };

    const send = (destination: string, body: unknown, headers?: Record<string, string>) => {
        const c = clientRef.current;
        if (c?.connected) {
            c.publish({ destination, body: JSON.stringify(body), headers });
        } else {
            if (pendingSends.current.length >= maxPending) {
                pendingSends.current.shift(); // 가장 오래된 항목 드롭
            }
            pendingSends.current.push({ dest: destination, body, headers });
        }
    };

    // 선택: 수동 종료(테스트/페이지 전환 시 편함)
    const disconnect = async () => {
        subsRef.current.forEach((s) => s.unsubscribe());
        subsRef.current.clear();
        specRef.current.clear();
        await clientRef.current?.deactivate();
        setStatus("DISCONNECTED");
    };

    return {
        status,
        connected: status === "CONNECTED",
        subscribe,
        unsubscribe,
        send,
        disconnect,
    };
}
