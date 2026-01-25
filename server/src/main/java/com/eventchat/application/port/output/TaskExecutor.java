package com.eventchat.application.port.output;

public interface TaskExecutor {
    void execute(Runnable task);
}
