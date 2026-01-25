# EventChat

Chat em tempo real com WebSocket, histórico de mensagens e lista de usuários conectados (até 10).

## O que faz
- Usuário entra com um nome único
- Mensagens são enviadas para todos
- Histórico é carregado ao entrar
- Lista de usuários conectados aparece na UI

## Tecnologias
Backend: Java 21, Quarkus, Hibernate ORM, H2, Jakarta WebSocket, Jackson  
Frontend: Next.js, React, TypeScript, Tailwind CSS

## Como executar
Pré-requisitos: Java 21+, Node.js 18+, npm

Backend:
```bash
cd server
./mvnw quarkus:dev
```
API: http://localhost:8080

Frontend:
```bash
cd client
npm install
npm run dev
```
App: http://localhost:3000

## Estrutura (resumo)
Camadas principais:
- `domain`: modelo de negócio (`ChatMessage`)
- `application`: caso de uso (`ChatUseCase`) e contratos (ports)
- `infrastructure`: persistência, websocket e executor
- `adapters`: endpoints (WebSocket)

O foco aqui é manter a regra de negócio no `application` e detalhes técnicos no `infrastructure`.
