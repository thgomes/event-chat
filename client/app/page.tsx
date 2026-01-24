"use client";

import useWebSocket, { ReadyState } from "react-use-websocket";
import { useCallback, useEffect, useRef, useState } from "react";
import "./globals.css";

const WS_URL = "ws://localhost:8080/chat";

type HistoryItem = {
  author: string;
  content: string;
  timestamp: number;
};

type ChatEvent = {
  type: "message" | "system" | "joined_ok" | "user_list" | "error" | "history";
  author?: string;
  content?: string;
  timestamp?: number;
  users?: string[];
  messages?: HistoryItem[];
};

type Message = {
  id: number;
  author: string;
  content: string;
  time: string;
};

type View = "name" | "chat" | "error";

function NameForm({ onSubmit }: { onSubmit: (name: string) => void }) {
  const [value, setValue] = useState("");

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    const n = value.trim();
    if (n) onSubmit(n);
  }

  return (
    <div className="terminal-frame">
      <div className="terminal name-form">
        <div className="terminal-header">EVENTCHAT v1.0 — Entrar</div>
        <form className="name-form-inner" onSubmit={handleSubmit}>
          <label htmlFor="name">Digite seu nome:</label>
          <input
            id="name"
            type="text"
            value={value}
            onChange={e => setValue(e.target.value)}
            placeholder="ex: Alice"
            maxLength={32}
            autoFocus
          />
          <button type="submit">Entrar no chat</button>
        </form>
      </div>
    </div>
  );
}

function ErrorView({
  message,
  onBack,
}: {
  message: string;
  onBack: () => void;
}) {
  return (
    <div className="terminal-frame">
      <div className="terminal name-form">
        <div className="terminal-header">EVENTCHAT — Erro</div>
        <div className="error-view">
          <p>{message}</p>
          <button type="button" onClick={onBack}>
            Voltar
          </button>
        </div>
      </div>
    </div>
  );
}

function ChatView({
  name,
  onError,
}: {
  name: string;
  onError: (msg: string) => void;
}) {
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
  const [input, setInput] = useState("");
  const bottomRef = useRef<HTMLDivElement>(null);

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

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  function sendMessage() {
    if (!input.trim() || readyState !== ReadyState.OPEN) return;
    sendJsonMessage({
      type: "message",
      author: name,
      content: input,
      timestamp: Date.now(),
    });
    setInput("");
  }

  return (
    <div className="terminal-frame chat-layout">
      <div className="terminal chat-main">
        <div className="terminal-header">
          EVENTCHAT v1.0 — Distributed Chat Terminal
        </div>
        <div className="terminal-subheader">
          Conectado como: <strong>{name}</strong> —{" "}
          {readyState === ReadyState.OPEN ? "ONLINE" : "OFFLINE"}
        </div>
        <div className="terminal-body">
          {messages.map(m => (
            <div key={m.id} className="terminal-line">
              [{m.time}] {m.author}: {m.content}
            </div>
          ))}
          <div ref={bottomRef} />
        </div>
        <div className="terminal-input">
          <span>&gt;</span>
          <input
            value={input}
            onChange={e => setInput(e.target.value)}
            onKeyDown={e => e.key === "Enter" && sendMessage()}
            disabled={readyState !== ReadyState.OPEN}
            placeholder="Digite sua mensagem..."
            autoFocus
          />
        </div>
      </div>
      <aside className="users-sidebar">
        <div className="users-header">
          Conectados ({users.length})
        </div>
        <ul className="users-list">
          {users.map(u => (
            <li key={u} className={u === name ? "users-list-you" : ""}>
              {u === name ? `${u} (você)` : u}
            </li>
          ))}
        </ul>
      </aside>
    </div>
  );
}

export default function Home() {
  const [view, setView] = useState<View>("name");
  const [userName, setUserName] = useState<string | null>(null);
  const [errorMessage, setErrorMessage] = useState("");

  function handleNameSubmit(name: string) {
    setUserName(name);
    setView("chat");
  }

  function handleChatError(msg: string) {
    setErrorMessage(msg);
    setView("error");
  }

  function handleErrorBack() {
    setView("name");
    setUserName(null);
    setErrorMessage("");
  }

  if (view === "name") {
    return <NameForm onSubmit={handleNameSubmit} />;
  }
  if (view === "error") {
    return (
      <ErrorView message={errorMessage} onBack={handleErrorBack} />
    );
  }
  return (
    userName && (
      <ChatView name={userName} onError={handleChatError} />
    )
  );
}
