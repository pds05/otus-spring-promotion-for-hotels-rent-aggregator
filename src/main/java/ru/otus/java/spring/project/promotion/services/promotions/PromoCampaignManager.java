package ru.otus.java.spring.project.promotion.services.promotions;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.java.spring.project.promotion.domains.promotions.*;
import ru.otus.java.spring.project.promotion.dtos.request.PromoCampaignRqDto;
import ru.otus.java.spring.project.promotion.dtos.response.PromoCampaignDto;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignResult;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignStatus;
import ru.otus.java.spring.project.promotion.exceptions.ApplicationException;
import ru.otus.java.spring.project.promotion.exceptions.ResourceNotFoundException;
import ru.otus.java.spring.project.promotion.repositories.promotions.PromoCampaignRepository;

import java.util.List;

@RequiredArgsConstructor
@Service("promoCampaignService")
public class PromoCampaignManager implements PromoCampaignService {

    private final ModelMapper modelMapper;

    private final PromoCampaignRepository promoCampaignRepository;

    @Transactional(readOnly = true)
    @Override
    public List<PromoCampaignDto> getAll() {
        List<PromoCampaign> promoCampaigns = promoCampaignRepository.findAll();
        return promoCampaigns.stream().map(promoCampaign -> modelMapper.map(promoCampaign, PromoCampaignDto.class))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public PromoCampaignDto get(long promoCampaignId) {
        PromoCampaign promoCampaign = promoCampaignRepository.findById(promoCampaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Promo campaign id " + promoCampaignId + " not found"));
        return modelMapper.map(promoCampaign, PromoCampaignDto.class);
    }

    @Transactional
    @Override
    public PromoCampaignDto save(PromoCampaignRqDto request) {
        PromoCampaign promoCampaign;

        if (request.getId() != null) {
            promoCampaign = promoCampaignRepository.findById(request.getId()).orElseThrow(() -> new ResourceNotFoundException("Promo campaign id " + request.getId() + " not found"));
            if (promoCampaign.getStatus() == PromoCampaignStatus.READY) {
                throw new ApplicationException("Need to stop the campaign before changing");
            }
            if (promoCampaign.getStatus() == PromoCampaignStatus.IN_PROGRESS) {
                throw new ApplicationException("Promo campaign is in progress and cannot be changed");
            }
            if (promoCampaign.getStatus() == PromoCampaignStatus.COMPLETED) {
                throw new ApplicationException("Promo campaign is completed and cannot be changed");
            }
        } else {
            promoCampaign = new PromoCampaign();
            promoCampaign.setStatus(PromoCampaignStatus.CREATED);
        }

        modelMapper.map(request, promoCampaign);

        PromoCampaign saved = promoCampaignRepository.save(promoCampaign);
        return modelMapper.map(saved, PromoCampaignDto.class);
    }

    @Transactional
    @Override
    public PromoCampaignDto start(long promoCampaignId) {
        PromoCampaign promoCampaign = promoCampaignRepository.findById(promoCampaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Promo campaign id " + promoCampaignId + " not found"));
        PromoCampaignStatus status = promoCampaign.getStatus();

        if (status == PromoCampaignStatus.CREATED || status == PromoCampaignStatus.IDLE) {
            promoCampaign.setStatus(PromoCampaignStatus.READY);
        } else {
            throw new ApplicationException("Wrong campaign status " + status.name());
        }

        PromoCampaign saved = promoCampaignRepository.save(promoCampaign);
        return modelMapper.map(saved, PromoCampaignDto.class);
    }

    @Transactional
    @Override
    public PromoCampaignDto stop(long promoCampaignId) {
        PromoCampaign promoCampaign = promoCampaignRepository.findById(promoCampaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Promo campaign id " + promoCampaignId + " not found"));
        PromoCampaignStatus status = promoCampaign.getStatus();

        switch (status) {
            case READY: {
                promoCampaign.setStatus(PromoCampaignStatus.IDLE);
                promoCampaign.setResult(PromoCampaignResult.INTERRUPTED);
            }
            break;
            case IN_PROGRESS: {
                promoCampaign.setStatus(PromoCampaignStatus.COMPLETED);
                promoCampaign.setResult(PromoCampaignResult.OK);
            }
            break;
            default: {
                throw new ApplicationException("Wrong campaign status " + status.name());
            }
        }

        PromoCampaign saved = promoCampaignRepository.save(promoCampaign);
        return modelMapper.map(saved, PromoCampaignDto.class);
    }

    @Transactional
    @Override
    public PromoCampaignDto abort(long promoCampaignId, String reason) {
        PromoCampaign promoCampaign = promoCampaignRepository.findById(promoCampaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Promo campaign id " + promoCampaignId + " not found"));
        promoCampaign.setStatus(PromoCampaignStatus.COMPLETED);
        promoCampaign.setResult(PromoCampaignResult.NOK_FAILED);
        promoCampaign.setDetails(reason);

        PromoCampaign saved = promoCampaignRepository.save(promoCampaign);
        return modelMapper.map(saved, PromoCampaignDto.class);
    }

    @Transactional
    @Override
    public PromoCampaignDto changeStatus(long promoCampaignId, PromoCampaignStatus status, PromoCampaignResult result) {
        PromoCampaign promoCampaign = promoCampaignRepository.findById(promoCampaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Promo campaign id " + promoCampaignId + " not found"));
        promoCampaign.setStatus(status);
        promoCampaign.setResult(result);

        PromoCampaign saved = promoCampaignRepository.save(promoCampaign);
        return modelMapper.map(saved, PromoCampaignDto.class);
    }

    @Transactional(readOnly = true)
    @Override
    public PromoCampaignStatus getStatus(long promoCampaignId) {
        PromoCampaign promoCampaign = promoCampaignRepository.findById(promoCampaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Promo campaign id " + promoCampaignId + " not found"));
        return promoCampaign.getStatus();
    }

    @Transactional(readOnly = true)
    @Override
    public PromoCampaignResult getResult(long promoCampaignId) {
        PromoCampaign promoCampaign = promoCampaignRepository.findById(promoCampaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Promo campaign id " + promoCampaignId + " not found"));
        return promoCampaign.getResult();
    }

    @Transactional
    @Override
    public void delete(long promoCampaignId) {
        promoCampaignRepository.deleteById(promoCampaignId);
    }
}
