package air.kanna.mystorage.dao;

import java.util.List;

import air.kanna.mystorage.dao.condition.FileItemCondition;
import air.kanna.mystorage.model.dto.FileItemDTO;

public interface FileItemDAO extends BaseModelDAO<FileItemDTO>{
    int listByConditionCount(FileItemCondition condition);
    List<FileItemDTO> listByCondition(FileItemCondition condition, OrderBy order, Pager pager);
    
    int deleteByCondition(FileItemCondition condition);
}
