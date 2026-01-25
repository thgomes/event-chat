"use client";

import { useState } from "react";
import { View } from "./types/chat";
import { NameForm } from "./components/NameForm";
import { ErrorView } from "./components/ErrorView";
import { ChatView } from "./components/ChatView";

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
