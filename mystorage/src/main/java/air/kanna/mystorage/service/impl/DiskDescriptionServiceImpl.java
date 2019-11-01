package air.kanna.mystorage.service.impl;

import air.kanna.mystorage.model.DiskDescription;
import air.kanna.mystorage.model.dto.DiskDescriptionDTO;
import air.kanna.mystorage.service.DiskDescriptionService;

public class DiskDescriptionServiceImpl
        extends BaseCRUDServiceImpl<DiskDescription, DiskDescriptionDTO> 
        implements DiskDescriptionService {

    @Override
    protected DiskDescription exchangeToPojo(DiskDescriptionDTO dto) {
        return new DiskDescription(dto);
    }

    @Override
    protected DiskDescriptionDTO exchangeToDto(DiskDescription pojo) {
        return pojo;
    }
}
