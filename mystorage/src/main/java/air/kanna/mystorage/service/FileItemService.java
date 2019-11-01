package air.kanna.mystorage.service;

import java.util.List;

import air.kanna.mystorage.dao.OrderBy;
import air.kanna.mystorage.dao.Pager;
import air.kanna.mystorage.dao.condition.FileItemCondition;
import air.kanna.mystorage.model.FileItem;

public interface FileItemService
        extends BaseCRUDService<FileItem>{

    int getByConditionCount(FileItemCondition condition);
    List<FileItem> getByCondition(FileItemCondition condition, OrderBy order, Pager pager);
    
    int deleteByCondition(FileItemCondition condition);
}
