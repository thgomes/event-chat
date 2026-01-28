package com.eventchat.application.usecase;

import com.eventchat.application.dto.MessageDto;
import com.eventchat.application.port.input.ChatInputPort;
import com.eventchat.application.port.output.ChatMessageRepository;
import com.eventchat.application.port.output.MessageBroadcaster;
import com.eventchat.application.port.output.SessionManager;
import com.eventchat.domain.model.ChatMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@ActivateRequestContext
public class ChatUseCase implements ChatInputPort {

    private static final int MAX_USERS = 10;

    @Inject
    ChatMessageRepository messageRepository;

    @Inject
    SessionManager sessionManager;

    @Inject
    MessageBroadcaster broadcaster;

    @Override
    @Transactional
    public void join(String sessionId, String author) throws JoinException {
        validateName(author);
        validateRoomCapacity();
        validateNameUniqueness(author);

        sessionManager.register(sessionId, author);

        broadcaster.broadcast(MessageDto.system(author + " entrou no chat."));
        broadcaster.broadcast(MessageDto.userList(sessionManager.getAllUsernames().toArray(String[]::new)));

        List<MessageDto> history = loadHistory();
        broadcaster.sendToSession(sessionId, MessageDto.history(history.toArray(new MessageDto[0])));
        sendJoinedOkToUser(sessionId, author);
    }

    @Override
    @Transactional
    public void send(String sessionId, String content, long timestamp) {
        String author = sessionManager.getUsername(sessionId);
        if (author == null) {
            broadcaster.sendToSession(sessionId, MessageDto.error("Envie 'join' com seu nome primeiro."));
            return;
        }

        ChatMessage message = new ChatMessage(author, content, timestamp);
        messageRepository.save(message);
        broadcaster.broadcast(MessageDto.message(author, content, timestamp));
    }

    @Override
    public void disconnect(String sessionId) {
        String author = sessionManager.unregister(sessionId);
        if (author != null) {
            broadcaster.broadcast(MessageDto.system(author + " saiu do chat."));
            broadcaster.broadcast(MessageDto.userList(sessionManager.getAllUsernames().toArray(String[]::new)));
        }
    }

    @Override
    @Transactional
    public List<MessageDto> listHistory() {
        return loadHistory();
    }

    private void validateName(String name) throws JoinException {
        if (name == null || name.trim().isEmpty()) {
            throw new JoinException("Nome não pode ser vazio.");
        }
    }

    private void validateRoomCapacity() throws JoinException {
        if (sessionManager.getUserCount() >= MAX_USERS) {
            throw new JoinException("Sala cheia. Máximo " + MAX_USERS + " usuários.");
        }
    }

    private void validateNameUniqueness(String name) throws JoinException {
        if (sessionManager.isNameTaken(name)) {
            throw new JoinException("Nome já está em uso.");
        }
    }

    private List<MessageDto> loadHistory() {
        return messageRepository.findAllOrderByTime().stream()
            .map(m -> MessageDto.message(m.getAuthor(), m.getContent(), m.getMsgTimestamp()))
            .collect(Collectors.toList());
    }

    private void sendJoinedOkToUser(String sessionId, String author) {
        String[] users = sessionManager.getAllUsernames().toArray(String[]::new);
        broadcaster.sendToSession(sessionId, MessageDto.joinedOk(author, users));
    }

}
