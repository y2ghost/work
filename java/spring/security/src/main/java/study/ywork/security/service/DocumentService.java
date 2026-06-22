package study.ywork.security.service;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import study.ywork.security.domain.Document;
import study.ywork.security.repository.DocumentRepository;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @PostAuthorize("hasPermission(returnObject, 'ROLE_admin')")
    // @RolesAllowed("ROLE_admin")
    // @Secured("ROLE_admin")
    public Document getDocument(String code) {
        return documentRepository.findDocument(code);
    }

    @PreAuthorize("hasPermission(#code, 'document', 'ROLE_admin')")
    public Document getDocument2(String code) {
        return documentRepository.findDocument(code);
    }
}

