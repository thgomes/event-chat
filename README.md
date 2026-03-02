# EventChat

Chat em tempo real com WebSocket, histórico de mensagens e lista de usuários conectados (até 10).

## O que faz
- Usuário entra com um nome único
- Mensagens são enviadas para todos
- Histórico é carregado ao entrar
- Lista de usuários conectados aparece na UI
- API REST para listar histórico de mensagens

## Tecnologias
Backend: Java 21, Quarkus, Hibernate ORM, H2, Jakarta WebSocket, Jackson, Nginx 
Frontend: Next.js, React, TypeScript, Tailwind CSS

## Como executar
Pré-requisitos: Java 21+, Node.js 18+, npm, mvn

Backend:
```bash
cd server
mvn clean package
docker compose up --build 
```
API: http://localhost:8080
Histórico mensagens: http://localhost:8080/messages

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
- `adapters`: endpoints (WebSocket e Rest)

O foco aqui é manter a regra de negócio no `application` e detalhes técnicos no `infrastructure`.

## Como testar configs do Nginx
### Verificação de proxy para a porta 80
Realizar uma requisição para a rota http://localhost/messages, para verificar que a comunicação está ocorrendo pela porta padrão (80).

### Verificação de headers de segurança e GZIP
Utilize um client para consultar a rota http://localhost/messages e confira os headers da resposta.

Obs: para garantir que o GZIP esteja presente é necessário adicionar no header da requisição `Accept-Encoding: gzip` e garantir que o conteúdo do retorno vai ter bytes suficientes para a compactação.

### Verificação de log estruturado
Ao realizar qualquer chamada HTTP para o backend da aplicação, verifique no terminal a presença de um log no seguinte formato.

```
{"timestamp":"2026-03-02T06:49:37+00:00","client_ip":"172.21.0.1","method":"GET","uri":"/messages","status":500,"upstream_response_time":"0.008","request_time":"0.007"}

```

### Verificação rate limit
Utilize alguma ferramenta para enviar uma quantidade de requisições superior a 10 simultâneas para obter o retorno de erro 429.

É possível realizar este teste com o seguinte comando:

```bash
for i in {1..20}; do                                     
  curl -I localhost/messages
  sleep 0.001
done
```

### Verificação limite de payload
Envie um arquivo de tamanho superior a 1MB com o nome "file" no endereço "http://localhost/uploads" para obter o retorno de erro 413.

### Verificação de autenticada
Acesse a rota do swagger "http://localhost/q/swagger-ui" pelo navegador, onde será exigido uma senha para acessar a pagina. A senha padrão é: "usuario: admin", "senha: 1234". Mas uma nova senha pode ser gerada e configurada no arquivo ".htpasswd".

### Verificação de erros customizados
É possível verificar o erro customizado referente ao status 404 acessando qualquer endereço inexistente, como por exemplo "http://localhost/teste".

### Verificação de cache
O tempo de cache configurado é de 10 segundos, então é possível testar o cache fazendo duas requisições em menos de 10 segundos e comparando o tempo da primeira e da segunda requisição realizada.