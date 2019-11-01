package air.kanna.mystorage.service.impl;

import java.util.List;

import air.kanna.mystorage.dao.BaseModelDAO;
import air.kanna.mystorage.dao.FileItemDAO;
import air.kanna.mystorage.dao.OrderBy;
import air.kanna.mystorage.dao.Pager;
import air.kanna.mystorage.dao.condition.FileItemCondition;
import air.kanna.mystorage.model.FileItem;
import air.kanna.mystorage.model.dto.FileItemDTO;
import air.kanna.mystorage.service.FileItemService;

public class FileItemServiceImpl 
        extends BaseCRUDServiceImpl<FileItem, FileItemDTO> 
        implements FileItemService {

    private FileItemDAO fileItemDao;
    @Override
    protected FileItem exchangeToPojo(FileItemDTO dto) {
        return new FileItem(dto);
    }

    @Override
    protected FileItemDTO exchangeToDto(FileItem pojo) {
        return pojo;
    }
    
    @Override
    public int getByConditionCount(FileItemCondition condition) {
        if(condition == null) {
            throw new NullPointerException("FileItemCondition is null");
        }
        return fileItemDao.listByConditionCount(condition);
    }
    
    @Override
    public List<FileItem> getByCondition(FileItemCondition condition, OrderBy order, Pager pager){
        if(condition == null) {
            throw new NullPointerException("FileItemCondition is null");
        }
        return exchangeToPojoList(fileItemDao.listByCondition(condition, order, pager));
    }
    
    @Override
    public int deleteByCondition(FileItemCondition condition) {
        return fileItemDao.deleteByCondition(condition);
    }
    
    @Override
    public void setModelDao(BaseModelDAO<FileItemDTO> modelDao) {
        if(modelDao instanceof FileItemDAO) {
            this.modelDao = modelDao;
            fileItemDao = (FileItemDAO)modelDao;
        }else {
            throw new java.lang.IllegalArgumentException("ModelDao must instanceof FileItemDAO");
        }
    }
}