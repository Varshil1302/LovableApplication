package com.example.demo.mapper;

import com.example.demo.dto.project.FileNode;
import com.example.demo.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper
{
    List<FileNode> getFromProjectFile(List<ProjectFile> projectFiles);
}
