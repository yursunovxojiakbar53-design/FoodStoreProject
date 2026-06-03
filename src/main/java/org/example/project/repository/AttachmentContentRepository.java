package org.example.project.repository;

import org.example.project.entity.AttachmentContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttachmentContentRepository extends JpaRepository<AttachmentContent, Long> {
    Optional<AttachmentContent> findByAttachmentId(Integer attachment_id);
}
