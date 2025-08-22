package com.gigfinder.controller;

import com.gigfinder.model.Document;
import com.gigfinder.model.WorkerProfile;
import com.gigfinder.model.enums.DocumentType;
import com.gigfinder.repository.DocumentRepository;
import com.gigfinder.repository.WorkerProfileRepository;
import com.gigfinder.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private WorkerProfileRepository workerProfileRepository;

    @PostMapping("/document")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("workerId") Long workerId,
            @RequestParam("documentType") String documentType) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "File is empty"));
            }

            WorkerProfile worker = workerProfileRepository.findById(workerId)
                    .orElseThrow(() -> new RuntimeException("Worker not found"));

            String fileUrl = fileUploadService.saveFile(file);

            Document document = Document.builder()
                    .worker(worker)
                    .type(DocumentType.valueOf(documentType.toUpperCase()))
                    .fileUrl(fileUrl)
                    .build();

            Document savedDocument = documentRepository.save(document);

            return ResponseEntity.ok(Map.of(
                    "id", savedDocument.getId(),
                    "fileUrl", savedDocument.getFileUrl(),
                    "type", savedDocument.getType(),
                    "message", "Document uploaded successfully"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "File upload failed", "message", e.getMessage()));
        }
    }
}
