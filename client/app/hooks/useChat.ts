import { useCallback, useEffect, useRef, useState } from "react";
import useWebSocket, { ReadyState } from "react-use-websocket";
import { ChatEvent, Message } from "../types/chat";
import { WS_URL } from "../config/websocket";

export function useChat(name: string, onError: (msg: string) => void) {
  const joinSent = useRef(false);
  const gotError = useRef(false);

  const { sendJsonMessage, lastJsonMessage, readyState } = useWebSocket<ChatEvent>(
    WS_URL,
    {
      share: false,
      shouldReconnect: () => !gotError.current,
    }
  );

  const [messages, setMessages] = useState<Message[]>([]);
  const [users, setUsers] = useState<string[]>([]);

  const appendMessage = useCallback(
    (author: string, content: string, ts: number) => {
      setMessages(m => [
        ...m,
        {
          id: ts,
          author,
          content,
          time: new Date(ts).toLocaleTimeString("pt-BR", {
            hour: "2-digit",
            minute: "2-digit",
          }),
        },
      ]);
    },
    []
  );

  useEffect(() => {
    if (readyState !== ReadyState.OPEN || joinSent.current) return;
    joinSent.current = true;
    sendJsonMessage({ type: "join", author: name });
  }, [readyState, name, sendJsonMessage]);

  useEffect(() => {
    if (!lastJsonMessage) return;

    switch (lastJsonMessage.type) {
      case "joined_ok":
        setUsers(lastJsonMessage.users ?? []);
        appendMessage("system", "Você entrou no chat.", Date.now());
        break;
      case "user_list":
        setUsers(lastJsonMessage.users ?? []);
        break;
      case "system": {
        const content = lastJsonMessage.content ?? "";
        if (!(content.includes(name) && content.includes("entrou"))) {
          appendMessage("system", content, Date.now());
        }
        break;
      }
      case "message":
        appendMessage(
          lastJsonMessage.author ?? "?",
          lastJsonMessage.content ?? "",
          lastJsonMessage.timestamp ?? Date.now()
        );
        break;
      case "history": {
        const items = lastJsonMessage.messages ?? [];
        setMessages(prev => [
          ...prev,
          ...items.map((item, idx) => ({
            id: item.timestamp * 10000 + idx,
            author: item.author,
            content: item.content,
            time: new Date(item.timestamp).toLocaleTimeString("pt-BR", {
              hour: "2-digit",
              minute: "2-digit",
            }),
          })),
        ]);
        break;
      }
      case "error":
        gotError.current = true;
        onError(lastJsonMessage.content ?? "Erro desconhecido.");
        break;
    }
  }, [lastJsonMessage, appendMessage, onError, name]);

  const sendMessage = useCallback(
    (content: string) => {
      if (!content.trim() || readyState !== ReadyState.OPEN) return;
      sendJsonMessage({
        type: "message",
        author: name,
        content: content.trim(),
        timestamp: Date.now(),
      });
    },
    [name, readyState, sendJsonMessage]
  );

  return {
    messages,
    users,
    sendMessage,
    isConnected: readyState === ReadyState.OPEN,
  };
}
