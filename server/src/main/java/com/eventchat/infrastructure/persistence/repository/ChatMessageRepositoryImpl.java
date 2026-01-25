package com.eventchat.infrastructure.persistence.repository;

import com.eventchat.application.port.output.ChatMessageRepository;
import com.eventchat.domain.model.ChatMessage;
import com.eventchat.infrastructure.persistence.entity.ChatMessageEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

    @Inject
    EntityManager entityManager;

    @Override
    @Transactional
    public void save(ChatMessage message) {
        ChatMessageEntity entity = toEntity(message);
        entityManager.persist(entity);
        message.setId(entity.getId());
    }

    @Override
    @Transactional
    public List<ChatMessage> findAllOrderByTime() {
        TypedQuery<ChatMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM ChatMessageEntity m ORDER BY m.msgTimestamp ASC",
            ChatMessageEntity.class
        );
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    private ChatMessageEntity toEntity(ChatMessage domain) {
        ChatMessageEntity entity = new ChatMessageEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        entity.setAuthor(domain.getAuthor());
        entity.setContent(domain.getContent());
        entity.setMsgTimestamp(domain.getMsgTimestamp());
        return entity;
    }

    private ChatMessage toDomain(ChatMessageEntity entity) {
        ChatMessage domain = new ChatMessage();
        domain.setId(entity.getId());
        domain.setAuthor(entity.getAuthor());
        domain.setContent(entity.getContent());
        domain.setMsgTimestamp(entity.getMsgTimestamp());
        return domain;
    }
}
