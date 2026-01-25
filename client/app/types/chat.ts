export type HistoryItem = {
  author: string;
  content: string;
  timestamp: number;
};

export type ChatEvent = {
  type: "message" | "system" | "joined_ok" | "user_list" | "error" | "history";
  author?: string;
  content?: string;
  timestamp?: number;
  users?: string[];
  messages?: HistoryItem[];
};

export type Message = {
  id: number;
  author: string;
  content: string;
  time: string;
};

export type View = "name" | "chat" | "error";
