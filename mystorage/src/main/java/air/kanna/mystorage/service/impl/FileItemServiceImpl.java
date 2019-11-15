package air.kanna.mystorage.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import air.kanna.mystorage.dao.BaseModelDAO;
import air.kanna.mystorage.dao.FileItemDAO;
import air.kanna.mystorage.dao.OrderBy;
import air.kanna.mystorage.dao.Pager;
import air.kanna.mystorage.dao.condition.FileItemCondition;
import air.kanna.mystorage.model.DiskDescription;
import air.kanna.mystorage.model.FileItem;
import air.kanna.mystorage.model.dto.FileItemDTO;
import air.kanna.mystorage.service.DiskDescriptionService;
import air.kanna.mystorage.service.FileItemService;

public class FileItemServiceImpl 
        extends BaseCRUDServiceImpl<FileItem, FileItemDTO> 
        implements FileItemService {
    private static final Logger logger = Logger.getLogger(FileItemServiceImpl.class);
    
    private FileItemDAO fileItemDao;
    private DiskDescriptionService diskService;
    private Map<Long, DiskDescription> diskIdMap = new HashMap<>();
    
    @Override
    protected FileItem exchangeToPojo(FileItemDTO dto) {
        FileItem item = new FileItem(dto);
        DiskDescription disk = diskIdMap.get(item.getDiskId());
        if(disk != null) {
            item.setDiskDescription(disk);
        }
        return item;
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

    public void setDiskService(DiskDescriptionService service) {
        if(service == null) {
            throw new NullPointerException("DiskDescriptionService is null");
        }
        this.diskService = service;
        diskIdMap.clear();
        
        try {
            List<DiskDescription> diskList = diskService.listAll(null, null);
            if(diskList == null || diskList.size() <= 0) {
                logger.warn("Cannot found DiskDescription");
                return;
            }
            for(DiskDescription disk : diskList) {
                diskIdMap.put(disk.getId(), disk);
            }
        }catch(Exception e) {
            logger.warn("Get DiskDescription error.", e);
        }
    }
    
}