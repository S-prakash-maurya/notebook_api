package com.micro.auth.service;

import com.generic.service.dto.GenericPaginationRes;
import com.generic.service.exception.GenericException;
import com.generic.service.mapper.GenericMapper;
import com.generic.service.repository.GenericRepository;
import com.generic.service.service.impl.GenericService;
import com.generic.service.util.RequestContext;
import com.micro.auth.dto.req.CreateNoteRequest;
import com.micro.auth.dto.res.NoteItemResponse;
import com.micro.auth.dto.res.NoteResponse;
import com.micro.auth.entity.Note;
import com.micro.auth.entity.NotePoint;
import com.micro.auth.entity.UserEntity;
import com.micro.auth.repository.NoteRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class NoteService extends GenericService<CreateNoteRequest, NoteResponse, Note> {

    private final NoteRepository noteRepository;
    private final UserService userService;

    public NoteService(
            GenericRepository<Note> repository,
            NoteRepository noteRepository,
            UserService userService
    ) {
        super(repository, NoteResponse.class, Note.class);
        this.noteRepository = noteRepository;
        this.userService = userService;
    }

    public NoteResponse create(CreateNoteRequest createNoteRequest) {

        if (createNoteRequest == null || createNoteRequest.getType() == null) {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "invalid request");
        }

        final var user = userService.getById(RequestContext.getUserFromRequestContextHolder().getUserId());

        final var note = GenericMapper.map(createNoteRequest, Note.class);

        note.setUser(GenericMapper.map(user, UserEntity.class));
        note.setId(UUID.randomUUID());
        note.setTitle(createNoteRequest.getTitle());
        note.setColorValue(createNoteRequest.getColorValue());
        note.setPinned(createNoteRequest.getIsPinned() != null ? createNoteRequest.getIsPinned() : false);
        note.setFavorite(createNoteRequest.getIsFavorite() != null ? createNoteRequest.getIsFavorite() : false);

        // IMPORTANT
        if (note.getNotePoints() != null) {
            note.getNotePoints().forEach(point -> point.setNote(note));
        }

        final var savedNote = noteRepository.saveAndFlush(note);

        return toResponse(savedNote);
    }

    public NoteResponse update(UUID noteId, CreateNoteRequest request) {

        if (noteId == null || request == null) {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "invalid request");
        }

        final var currentUserId = RequestContext
                        .getUserFromRequestContextHolder()
                        .getUserId();

        final Note note = noteRepository.findByIdAndUserIdAndDeletedFalse(noteId, currentUserId).orElseThrow(()-> new GenericException(HttpStatus.NOT_FOUND.value(), "Note not found"));
        if (request.getType() != null) {
            note.setType(request.getType());
        }

        if (request.getTitle() != null) {
            note.setTitle(request.getTitle());
        }

        if (request.getContent() != null) {
            note.setContent(request.getContent());
        }

        if (request.getColorValue() != null) {
            note.setColorValue(request.getColorValue());
        }

        if (request.getIsPinned() != null) {
            note.setPinned(request.getIsPinned());
        }

        if (request.getIsFavorite() != null) {
            note.setFavorite(request.getIsFavorite());
        }

        if (request.getPoints() != null) {
            final List<NotePoint> points =
                    request.getPoints()
                            .stream()
                            .map(pointRequest -> {
                                NotePoint point;
                                if (pointRequest.getId() != null) {
                                    point = note.getNotePoints()
                                            .stream()
                                            .filter(existingPoint -> existingPoint.getNote().getId().equals(pointRequest.getId()))
                                            .findFirst()
                                            .orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND.value(), "Note point not found"));
                                    if (pointRequest.getText() != null) {
                                        point.setText(pointRequest.getText()
                                        );
                                    }
                                    if (pointRequest.getCompleted() != null) {
                                        point.setCompleted(pointRequest.getCompleted());
                                    }
                                } else {
                                    point = GenericMapper.map(pointRequest, NotePoint.class);
                                }
                                point.setNote(note);
                                return point;
                            })
                            .toList();
            note.setNotePoints(points);
        }

        final Note updatedNote = noteRepository.saveAndFlush(note);

        return toResponse(updatedNote);
    }

    public GenericPaginationRes<NoteResponse> getAllPage(Pageable pageable) {

        final var userId =
                RequestContext
                        .getUserFromRequestContextHolder()
                        .getUserId();

        final var notePage =
                noteRepository.findByUserIdAndDeletedFalse(
                        userId,
                        pageable
                );

        final List<NoteResponse> content =
                notePage.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return GenericPaginationRes.<NoteResponse>builder()
                .totalPages(notePage.getTotalPages())
                .totalElements((int) notePage.getTotalElements())
                .pageSize((long) notePage.getSize())
                .pageNumber(notePage.getNumber())
                .lastPage(notePage.isLast())
                .content(content)
                .build();
    }

    /**
     * Built by hand (instead of GenericMapper) so that the JSON contract
     * (items / isCompleted / isPinned / isFavorite) stays exact regardless
     * of how the generic mapper matches field names.
     */
    private NoteResponse toResponse(Note note) {
        final List<NoteItemResponse> items =
                note.getNotePoints() == null
                        ? Collections.emptyList()
                        : note.getNotePoints()
                                .stream()
                                .map(point -> NoteItemResponse.builder()
                                        .id(point.getId())
                                        .text(point.getText())
                                        .completed(point.getCompleted())
                                        .build())
                                .toList();

        return NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .type(note.getType())
                .colorValue(note.getColorValue())
                .items(items)
                .pinned(note.getPinned())
                .favorite(note.getFavorite())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }
}
