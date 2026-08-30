package com.micro.auth.service;

import com.generic.service.dto.GenericPaginationRes;
import com.generic.service.exception.GenericException;
import com.generic.service.mapper.GenericMapper;
import com.generic.service.repository.GenericRepository;
import com.generic.service.service.impl.GenericService;
import com.generic.service.util.RequestContext;
import com.micro.auth.constants.Constants;
import com.micro.auth.dto.req.SubjectEntityReqDto;
import com.micro.auth.entity.SubjectEntity;
import com.micro.auth.repository.SubjectRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SubjectEntityService extends GenericService<SubjectEntityReqDto, SubjectEntity, SubjectEntity> {
    private final SubjectRepository subjectRepository;

    public SubjectEntityService(GenericRepository<SubjectEntity> repository, SubjectRepository subjectRepository) {
        super(repository, SubjectEntity.class, SubjectEntity.class);
        this.subjectRepository = subjectRepository;
    }

    @Override
    public SubjectEntity getById(UUID id) {
        return subjectRepository.findActiveByIdAndUserId(id, RequestContext.getUserFromRequestContextHolder().getUserId())
                .orElseThrow(() -> new GenericException(HttpStatus.FOUND.value(), "Subject not found"));
    }

    @Override
    public GenericPaginationRes<SubjectEntity> getAllPage(Pageable pageable) {
        final var tEntityPage = subjectRepository.findByUserIdAndDeletedFalse(RequestContext.getUserFromRequestContextHolder().getUserId(), pageable);
        return GenericPaginationRes.<SubjectEntity>builder().totalPages(tEntityPage.getTotalPages()).totalElements(tEntityPage.getNumberOfElements()).pageSize((long) tEntityPage.getSize()).pageNumber(tEntityPage.getNumber()).lastPage(tEntityPage.isLast()).content(tEntityPage.getContent().stream().map((tEntity) -> GenericMapper.map(tEntity, SubjectEntity.class)).toList()).build();
    }

    @Override
    public SubjectEntity update(SubjectEntityReqDto updateReq, UUID id) {
        updateReq.setUserId(RequestContext.getUserFromRequestContextHolder().getUserId());
        SubjectEntity subjectEntity = this.getById(id);
        if (!subjectEntity.getUserId().equals(updateReq.getUserId())) {
            throw new GenericException(HttpStatus.EXPECTATION_FAILED.value(), Constants.ANOTHER_ACCESS_ERR_MESSAGE);
        }
        return super.update(updateReq, id);
    }

    public SubjectEntity deleteById(UUID id) {
        SubjectEntity subjectEntity = this.getById(id);
        return super.deleteHard(subjectEntity.getId());
    }
}
