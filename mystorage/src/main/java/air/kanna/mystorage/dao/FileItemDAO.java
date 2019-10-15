package air.kanna.mystorage.dao;

import java.util.List;

import air.kanna.mystorage.dao.condition.FileItemCondition;
import air.kanna.mystorage.model.dto.FileItemDTO;

public interface FileItemDAO extends BaseModelDAO<FileItemDTO>{
    
    List<FileItemDTO> listByCondition(FileItemCondition condition, Pager pager);
}
