package com.micro.subject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.subject.dto.req.CreateSubjectReqDto;
import com.micro.subject.dto.res.SubjectResDto;
import com.micro.subject.exception.SubjectException;
import com.micro.subject.service.SubjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * NOTE: this uses @WebMvcTest, which does NOT load JwtAuthenticationFilter
 * or SecurityConfig from the auth service (those live in a different
 * module/package and weren't part of what was shared). It verifies
 * request-body VALIDATION (section 4/8's title/color/icon rules) and that
 * the controller/service properly surfaces a SubjectException as 401/404
 * with the required { success, message, error } shape.
 *
 * A true end-to-end "no JWT header -> 401" test belongs in an
 * integration test (e.g. @SpringBootTest with a real filter chain and
 * MockMvc against a live security context) once this module is wired
 * into the real Spring Security config - that config wasn't shared here.
 */
@WebMvcTest(SubjectController.class)
class SubjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SubjectService subjectService;

    // 8. invalid title
    @Test
    void create_rejectsBlankTitle() throws Exception {
        CreateSubjectReqDto req = new CreateSubjectReqDto();
        req.setTitle("   ");
        req.setColor("#4CAF50");
        req.setIcon("calculate");

        mockMvc.perform(post("/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void create_rejectsTitleTooLong() throws Exception {
        CreateSubjectReqDto req = new CreateSubjectReqDto();
        req.setTitle("a".repeat(200));
        req.setColor("#4CAF50");
        req.setIcon("calculate");

        mockMvc.perform(post("/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    // 9. invalid color
    @Test
    void create_rejectsInvalidColor() throws Exception {
        CreateSubjectReqDto req = new CreateSubjectReqDto();
        req.setTitle("Mathematics");
        req.setColor("not-a-color");
        req.setIcon("calculate");

        mockMvc.perform(post("/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    // 10. invalid icon
    @Test
    void create_rejectsBlankIcon() throws Exception {
        CreateSubjectReqDto req = new CreateSubjectReqDto();
        req.setTitle("Mathematics");
        req.setColor("#4CAF50");
        req.setIcon("");

        mockMvc.perform(post("/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void create_acceptsValidPayload() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        SubjectResDto resDto = SubjectResDto.builder()
                .id(id).userId(userId).title("Mathematics").color("#4CAF50").icon("calculate")
                .build();
        when(subjectService.create(any())).thenReturn(resDto);

        CreateSubjectReqDto req = new CreateSubjectReqDto();
        req.setTitle("Mathematics");
        req.setColor("#4CAF50");
        req.setIcon("calculate");

        mockMvc.perform(post("/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Mathematics"));
    }

    // Accessing another user's subject / not-found surfaces as a clean 404 envelope
    @Test
    void getById_returns404WithConsistentEnvelopeWhenNotOwned() throws Exception {
        UUID id = UUID.randomUUID();
        when(subjectService.getById(id)).thenThrow(SubjectException.notFound());

        mockMvc.perform(get("/subjects/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("SUBJECT_NOT_FOUND"));
    }

    // Unauthorized surfaces as a clean 401 envelope
    @Test
    void getById_returns401WithConsistentEnvelopeWhenUnauthenticated() throws Exception {
        UUID id = UUID.randomUUID();
        when(subjectService.getById(id)).thenThrow(SubjectException.unauthorized());

        mockMvc.perform(get("/subjects/{id}", id))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void delete_returnsSuccessEnvelope() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/subjects/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
