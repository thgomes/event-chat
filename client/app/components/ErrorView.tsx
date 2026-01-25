interface ErrorViewProps {
  message: string;
  onBack: () => void;
}

export function ErrorView({ message, onBack }: ErrorViewProps) {
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
