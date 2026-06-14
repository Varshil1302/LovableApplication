package com.example.demo.llm.tools;

import com.example.demo.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class CodeGenerationTools
{

    private final ProjectFileService projectFileService;
    private final Long projectId;

    @Tool(name = "read_files",
            description = "Read the content of files. Only input the file names present inside the FILE_TREE. DO NOT input any path which is not present under the FILE_TREE.")
    public List<String> readFiles(
            @ToolParam(description = "List of relative paths (e.g., ['src/App.tsx'])")
            List<String> paths){
        List<String> results = new ArrayList<>();
        for(String path:paths)
        {
            String cleanPath = path.startsWith("/")?path.substring(1):path;
            log.info("Requested File:{}",cleanPath);
            String content = projectFileService.getFileContent(projectId,cleanPath).content();
            results.add(String.format(
                    "\"--- START OF FILE: %s ---\\n%s\\n--- END OF FILE ---\"",
                    cleanPath,content
            ));
        }
        return results;
    }

}
