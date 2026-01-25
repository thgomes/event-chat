import { useEffect, useRef, useState } from "react";
import { useChat } from "../hooks/useChat";
import { Message } from "../types/chat";

interface ChatViewProps {
  name: string;
  onError: (msg: string) => void;
}

export function ChatView({ name, onError }: ChatViewProps) {
  const { messages, users, sendMessage, isConnected } = useChat(name, onError);
  const [input, setInput] = useState("");
  const bottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  function handleSend() {
    if (!input.trim()) return;
    sendMessage(input);
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
          {isConnected ? "ONLINE" : "OFFLINE"}
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
            onKeyDown={e => e.key === "Enter" && handleSend()}
            disabled={!isConnected}
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
