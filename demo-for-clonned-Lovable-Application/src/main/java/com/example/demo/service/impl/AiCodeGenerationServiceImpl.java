package com.example.demo.service.impl;

import com.example.demo.llm.PromptUtils;
import com.example.demo.llm.advisors.FileTreeContextAdvisor;
import com.example.demo.security.JwtService;
import com.example.demo.service.AiCodeGenerationService;
import com.example.demo.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
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

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>",Pattern.DOTALL);

    @Override
    public Flux<String> streamResponse(String message, Long projectId) {
        Long userId = jwtService.getCurrentUser();

        createChatSessionIfNotExists(projectId,userId);

        Map<String,Object> advisorParams = Map.of("userId",userId,"projectId",projectId);

        StringBuilder fullResponse = new StringBuilder();

        return chatClient.prompt()
                .system(PromptUtils.SYSTEM_Prompt)
                .user(message)
                .advisors(advisorSpec->{
                    advisorSpec.params(advisorParams);
                    advisorSpec.advisors(fileTreeContextAdvisor);
                })
                .stream()
                .chatResponse()
                .doOnNext(response->{
                    String sb= response.getResult().getOutput().getText();
                    fullResponse.append(sb);
                })
                .doOnComplete(
                        ()->{
                            log.info("full Response is:{}",fullResponse);
                            Schedulers.boundedElastic().schedule(()->{
                                parseAndSaveFile(fullResponse.toString(),projectId);
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

    private void parseAndSaveFile(String fullResponse, Long projectId) {

        Matcher matcher = FILE_TAG_PATTERN.matcher(fullResponse);

        while (matcher.find())
        {
            String filePath = matcher.group(1);
            String fileContent = matcher.group(2).trim();
            projectFileService.saveFile(projectId, filePath,fileContent);

        }
    }

    private void createChatSessionIfNotExists(Long projectId, Long userId)
    {

    }
}
