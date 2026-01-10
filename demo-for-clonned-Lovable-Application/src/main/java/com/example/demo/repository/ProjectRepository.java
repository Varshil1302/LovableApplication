package com.example.demo.repository;


import com.example.demo.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Long>
{
    @Query("""
            select p from Project p 
            where p.owner.userId= :userId
            AND p.deletedAt is NULL 
            ORDER BY p.updatedAt DESC
            """)
    List<Project> findAllByUserId(@Param("userId") Long userId);
}
