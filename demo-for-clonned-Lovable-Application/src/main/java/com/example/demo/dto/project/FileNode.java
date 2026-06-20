package com.example.demo.dto.project;

import org.jetbrains.annotations.NotNull;

public record FileNode(
        String path
) {
    @Override
    public String toString() {
        return path;
    }
}
