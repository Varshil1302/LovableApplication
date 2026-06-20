package com.example.demo.llm;

import com.example.demo.entity.ChatEvent;
import com.example.demo.entity.ChatMessage;
import com.example.demo.enums.ChatEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class LLMResponseParser
{

    private static final Pattern GENERIC_TAG_PATTERN = Pattern.compile(
            "(<(message|file|tool)([^>]*)>)([\\s\\S]*?)(</\\2>)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile(
            "(path|args)=\"([^\"]+)\""
    );

    public List<ChatEvent> parseChatEvents(String fullResponse , ChatMessage chatMessage)
    {
        List<ChatEvent> events = new ArrayList<>();
        int orderCounter = 1;
        Matcher matcher = GENERIC_TAG_PATTERN.matcher(fullResponse);

        while (matcher.find())
        {
            String tagName = matcher.group(2).toLowerCase();
            String attributes = matcher.group(3);
            String content = matcher.group(4).trim();

            Map<String,String> attrMap = extractMap(attributes);

            ChatEvent.ChatEventBuilder chatEventBuilder = ChatEvent.builder()
                                                      .chatMessage(chatMessage)
                    .content(content).sequenceOrder(orderCounter++);
            switch (tagName)
            {
                case "message" -> chatEventBuilder.chatEventType(ChatEventType.MESSAGE);
                case "file" -> {
                    chatEventBuilder.chatEventType(ChatEventType.FILE_EDIT);
                    chatEventBuilder.filePath(attrMap.get("path"));
                }
                case "tool" ->{
                    chatEventBuilder.chatEventType(ChatEventType.TOOL_LOG);
                    chatEventBuilder.metadata(attrMap.get("args"));
                }
                default -> {continue;}
            }
            events.add(chatEventBuilder.build());
        }
        return events;
    }

    private Map<String, String> extractMap(String attributes)
    {
        Map<String,String> attributeMap = new HashMap<>();
        if(attributes==null) return attributeMap;
        Matcher mather = ATTRIBUTE_PATTERN.matcher(attributes);
        while (mather.find())
        {
            attributeMap.put(mather.group(1),mather.group(2));
        }
        return attributeMap;
    }

}
