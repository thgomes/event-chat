import { useState } from "react";

interface NameFormProps {
  onSubmit: (name: string) => void;
}

export function NameForm({ onSubmit }: NameFormProps) {
  const [value, setValue] = useState("");

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    const name = value.trim();
    if (name) onSubmit(name);
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
