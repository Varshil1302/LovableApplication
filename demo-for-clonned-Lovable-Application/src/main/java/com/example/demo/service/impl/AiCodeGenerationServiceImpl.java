package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.enums.ChatEventType;
import com.example.demo.enums.MessageRole;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.llm.LLMResponseParser;
import com.example.demo.llm.PromptUtils;
import com.example.demo.llm.advisors.FileTreeContextAdvisor;
import com.example.demo.llm.tools.CodeGenerationTools;
import com.example.demo.repository.*;
import com.example.demo.security.JwtService;
import com.example.demo.service.AiCodeGenerationService;
import com.example.demo.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiCodeGenerationServiceImpl implements AiCodeGenerationService
{

    private final ChatClient chatClient;
    private final JwtService jwtService;
    private final ProjectFileService projectFileService;
    private final FileTreeContextAdvisor fileTreeContextAdvisor;
    private final LLMResponseParser llmResponseParser;
    private final ChatSessionRepository chatSessionRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatEventRepository chatEventRepository;

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>",Pattern.DOTALL);

    @Override
    public Flux<String> streamResponse(String message, Long projectId) {
        Long userId = jwtService.getCurrentUser();

        ChatSession chatSession = createChatSessionIfNotExists(projectId,userId);
        AtomicReference<Long> startTime = new AtomicReference<>(System.currentTimeMillis());
        AtomicReference<Long> endTime = new AtomicReference<>(0L);

        Map<String,Object> advisorParams = Map.of("userId",userId,"projectId",projectId);

        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(projectFileService,projectId);

        StringBuilder fullResponse = new StringBuilder();

        return chatClient.prompt()
                .system(PromptUtils.SYSTEM_Prompt)
                .user(message)
                .tools(codeGenerationTools)
                .advisors(advisorSpec->{
                    advisorSpec.params(advisorParams);
                    advisorSpec.advisors(fileTreeContextAdvisor);
                })
                .stream()
                .chatResponse()
                .doOnNext(response->{
                    String sb= response.getResult().getOutput().getText();
                    if(sb!=null && !sb.isEmpty() && endTime.equals(0L))
                    {
                        endTime.set(System.currentTimeMillis());
                    }
                    fullResponse.append(sb);
                })
                .doOnComplete(
                        ()->{
                            Schedulers.boundedElastic().schedule(()->{
                                long duration = endTime.get()-startTime.get();
                                finalizeChats(message,chatSession,fullResponse.toString(),duration);
                            });
                        })
                .doOnError(error->log.error("Error during streaming for projectId: {}",projectId))
                .handle((resp, sink) -> {
                    var result = resp != null ? resp.getResult() : null;
                    var output = result != null ? result.getOutput() : null;
                    var text   = output != null ? output.getText() : null;

                    if (text != null && !text.isEmpty()) {
                        sink.next(text);
                    }
                    // else: ignore non-text events
                });
    }

    private void finalizeChats(String userMessage ,  ChatSession chatSession, String fullText,Long duration)
    {
           //Save the User Message.
           chatMessageRepository.save(
                   ChatMessage.builder()
                           .role(MessageRole.USER)
                           .chatSession(chatSession)
                           .content(userMessage)
                           .build()
           );

          //Save the Assistant Message.
        ChatMessage assistantchatMessage = ChatMessage.builder()
                                       .role(MessageRole.ASSISTANT)
                                        .content("Assistant Messages here..")
                                       .chatSession(chatSession)
                                       .build();

        assistantchatMessage = chatMessageRepository.save(assistantchatMessage);

        List<ChatEvent> chatEventList = llmResponseParser.parseChatEvents(fullText,assistantchatMessage);

        chatEventList.addFirst(ChatEvent.builder()
                                    .chatEventType(ChatEventType.THOUGHT)
                                    .chatMessage(assistantchatMessage)
                                    .sequenceOrder(0)
                                    .content("Thought For "+duration+"s")
                                    .build());

        chatEventList.stream()
                .filter(event->event.getChatEventType()== ChatEventType.FILE_EDIT)
                .forEach(event->projectFileService.saveFile(chatSession.getProject().getId(),event.getFilePath(),event.getContent()));
        chatEventRepository.saveAll(chatEventList);
    }

    private void parseAndSaveFile(String fullResponse, Long projectId) {

        Matcher matcher = FILE_TAG_PATTERN.matcher(fullResponse);

        while (matcher.find())
        {
            String filePath = matcher.group(1);
            String fileContent = matcher.group(2).trim();
            projectFileService.saveFile(projectId, filePath,fileContent);

        }
    }

    private ChatSession createChatSessionIfNotExists(Long projectId, Long userId)
    {
        ChatSessionId chatSessionId = ChatSessionId.builder()
                                       .userId(userId).projectId(projectId)
                                       .build();

        ChatSession chatSession = chatSessionRepository.findById(chatSessionId).orElse(null);
        Project project = projectRepository.findById(projectId).orElseThrow(()->new ResourceNotFoundException("Project Not Found With Id::{}"+projectId));
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User Not Found With Id::{}"+userId));


        if(chatSession==null){
            ChatSession newchatSession = ChatSession.builder()
                                                    .chatSessionId(chatSessionId)
                                                    .project(project)
                                                     .user(user)
                                                     .createdAt(Instant.now())
                                                     .build();

            chatSession = chatSessionRepository.save(newchatSession);
        }
        return chatSession;
    }
}
