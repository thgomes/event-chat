"use client";

import useWebSocket, { ReadyState } from "react-use-websocket";
import { useEffect, useRef, useState } from "react";
import "./globals.css";

type ChatEvent = {
  type: "message" | "system";
  author: string;
  content: string;
  timestamp: number;
};

type Message = {
  id: number;
  author: string;
  content: string;
  time: string;
};

const WS_URL = "ws://localhost:8080/chat";

export default function Home() {
  const userIdRef = useRef(
    "anonymous-" + Math.random().toString(16).slice(2, 6)
  );

  const { sendJsonMessage, lastJsonMessage, readyState } =
    useWebSocket<ChatEvent>(WS_URL, {
      share: true,
      shouldReconnect: () => true,
    });

  const [messages, setMessages] = useState<Message[]>([
    systemMessage("Terminal inicializado."),
  ]);

  const [input, setInput] = useState("");
  const bottomRef = useRef<HTMLDivElement>(null);

  /* =====================
     RECEBER MENSAGENS
  ====================== */
  useEffect(() => {
    if (!lastJsonMessage) return;

    if (lastJsonMessage.type === "message") {
      appendMessage(
        lastJsonMessage.author,
        lastJsonMessage.content,
        lastJsonMessage.timestamp
      );
    }

    if (lastJsonMessage.type === "system") {
      appendMessage("system", lastJsonMessage.content, Date.now());
    }
  }, [lastJsonMessage]);

  /* =====================
     ENVIAR MENSAGEM
  ====================== */
  function sendMessage() {
    if (!input.trim()) return;
    if (readyState !== ReadyState.OPEN) return;

    sendJsonMessage({
      type: "message",
      author: userIdRef.current,
      content: input,
      timestamp: Date.now(),
    });

    setInput("");
  }

  /* =====================
     HELPERS
  ====================== */
  function appendMessage(author: string, content: string, ts: number) {
    setMessages(m => [
      ...m,
      {
        id: ts,
        author,
        content,
        time: new Date(ts).toLocaleTimeString().slice(0, 5),
      },
    ]);
  }

  function systemMessage(content: string): Message {
    return {
      id: Date.now(),
      author: "system",
      content,
      time: new Date().toLocaleTimeString().slice(0, 5),
    };
  }

  /* =====================
     AUTO SCROLL
  ====================== */
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  /* =====================
     UI
  ====================== */
  return (
    <div className="terminal-frame">
      <div className="terminal">
        <div className="terminal-header">
          EVENTCHAT v1.0 — Distributed Chat Terminal
        </div>

        <div className="terminal-subheader">
          Connected as: {userIdRef.current} —{" "}
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
            autoFocus
          />
        </div>
      </div>
    </div>
  );
}
