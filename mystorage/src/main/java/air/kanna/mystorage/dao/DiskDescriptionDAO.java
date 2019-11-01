package air.kanna.mystorage.dao;

import java.util.List;

import air.kanna.mystorage.model.dto.DiskDescriptionDTO;

public interface DiskDescriptionDAO extends BaseModelDAO<DiskDescriptionDTO>{
    
    List<DiskDescriptionDTO> listByCondition(String basePath, String desc, OrderBy order, Pager pager);
}
