package com.eventchat.infrastructure.executor;

import com.eventchat.application.port.output.TaskExecutor;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class AsyncExecutor implements TaskExecutor {

    private final ExecutorService worker = Executors.newFixedThreadPool(4);

    @Override
    public void execute(Runnable task) {
        worker.submit(task);
    }
}
