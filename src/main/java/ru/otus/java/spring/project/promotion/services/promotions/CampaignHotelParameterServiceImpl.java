package ru.otus.java.spring.project.promotion.services.promotions;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.java.spring.project.promotion.domains.promotions.CampaignHotelParameter;
import ru.otus.java.spring.project.promotion.dtos.request.CampaignHotelParameterRqDto;
import ru.otus.java.spring.project.promotion.dtos.response.CampaignHotelParameterDto;
import ru.otus.java.spring.project.promotion.exceptions.ResourceNotFoundException;
import ru.otus.java.spring.project.promotion.repositories.promotions.CampaignHotelParameterRepository;

import java.util.List;

@RequiredArgsConstructor
@Service("campaignHotelParameterService")
public class CampaignHotelParameterServiceImpl implements CampaignHotelParameterService {

    private final CampaignHotelParameterRepository campaignHotelParameterRepository;

    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    @Override
    public List<CampaignHotelParameterDto> getAllByCampaignId(Long campaignId) {
        List<CampaignHotelParameter> campaignHotelParameters = campaignHotelParameterRepository.findByCampaignId(campaignId);
        return modelMapper.map(campaignHotelParameters, new TypeToken<List<CampaignHotelParameterDto>>() {}.getType());
    }

    @Transactional(readOnly = true)
    @Override
    public CampaignHotelParameterDto getById(Long id) {
        CampaignHotelParameter campaignHotelParameter = campaignHotelParameterRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Campaign hotel parameter with id " + id + " not found"));
        return modelMapper.map(campaignHotelParameter, CampaignHotelParameterDto.class);
    }

    @Transactional
    @Override
    public CampaignHotelParameterDto save(CampaignHotelParameterRqDto request) {
        CampaignHotelParameter campaignHotelParameter;
        if (request.getId() != null) {
            campaignHotelParameter = campaignHotelParameterRepository.findById(request.getId()).orElseThrow(() -> new ResourceNotFoundException("Campaign hotel parameter with id " + request.getId() + " not found"));
        } else {
            campaignHotelParameter = new CampaignHotelParameter();
        }
        modelMapper.map(request, campaignHotelParameter);

        CampaignHotelParameter savedCampaignHotelParameter = campaignHotelParameterRepository.save(campaignHotelParameter);

        return modelMapper.map(savedCampaignHotelParameter, CampaignHotelParameterDto.class);
    }

    @Transactional
    @Override
    public void deleteByCampaignId(Long campaignId) {
        campaignHotelParameterRepository.deleteByCampaignId(campaignId);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        campaignHotelParameterRepository.deleteById(id);
    }
}
