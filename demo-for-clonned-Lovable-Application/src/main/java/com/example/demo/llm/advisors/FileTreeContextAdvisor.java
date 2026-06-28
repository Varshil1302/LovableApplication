package com.example.demo.llm.advisors;

import com.example.demo.dto.project.FileNode;
import com.example.demo.entity.ProjectFile;
import com.example.demo.repository.ProjectFileRepository;
import com.example.demo.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import java.util.*;

@Component
@RequiredArgsConstructor
public class FileTreeContextAdvisor implements StreamAdvisor
{

    private final ProjectFileService projectFileService;

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain)
    {
        Map<String,Object> context = chatClientRequest.context();
        Long projectId= Long.parseLong(context.getOrDefault("projectId",0).toString());
        Long userId = Long.parseLong(context.getOrDefault("userId",0).toString());
        ChatClientRequest augmentedChatClientRequest = augmentRequestWithFileTree(chatClientRequest,projectId,userId);
        return streamAdvisorChain.nextStream(augmentedChatClientRequest);
    }

    private ChatClientRequest augmentRequestWithFileTree(ChatClientRequest chatClientRequest, Long projectId, Long userId)
    {
        List<Message> incomingMessage = chatClientRequest.prompt().getInstructions();
        Message systemPropmt = incomingMessage.stream()
                .filter(m->m.getMessageType()== MessageType.SYSTEM)
                .findFirst()
                .get();

        List<Message> userMessage = incomingMessage.stream().filter(m->m.getMessageType()!=MessageType.SYSTEM).toList();

        List<FileNode> fileTrees = projectFileService.getFileTree(projectId,userId).files();
        String fileTreeContext = "\n\n ---- FILE_TREE ----\n"+fileTrees.toString();


        List<Message> allMessages = new ArrayList<>();

        if(systemPropmt!=null)
        {
            allMessages.add(systemPropmt);
        }
        allMessages.add(new SystemMessage(fileTreeContext));
        allMessages.addAll(userMessage);
        return chatClientRequest.mutate().prompt(new Prompt(allMessages, chatClientRequest.prompt().getOptions())).build();
    }

    @Override
    public String getName() {
        return "FileTreeContextAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
