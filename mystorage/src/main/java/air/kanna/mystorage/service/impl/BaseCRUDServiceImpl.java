package air.kanna.mystorage.service.impl;

import java.util.ArrayList;
import java.util.List;

import air.kanna.mystorage.dao.BaseModelDAO;
import air.kanna.mystorage.dao.OrderBy;
import air.kanna.mystorage.dao.Pager;
import air.kanna.mystorage.model.FileItem;
import air.kanna.mystorage.model.dto.FileItemDTO;
import air.kanna.mystorage.service.BaseCRUDService;

public abstract class BaseCRUDServiceImpl<T, K> implements BaseCRUDService<T> {

    protected BaseModelDAO<K> modelDao;
    
    protected abstract T exchangeToPojo(K dto);
    protected abstract K exchangeToDto(T pojo);
    
    @Override
    public T getById(Object id) {
        if(id == null) {
            throw new NullPointerException("Object id is null");
        }
        return exchangeToPojo(modelDao.getById(id));
    }

    @Override
    public List<T> listAll(OrderBy order, Pager pager) {
        return exchangeToPojoList(modelDao.listAll(order, pager));
    }

    @Override
    public int add(T object) {
        if(object == null) {
            throw new NullPointerException("Object to add is null");
        }
        return modelDao.insert(exchangeToDto(object));
    }

    @Override
    public int update(T object) {
        if(object == null) {
            throw new NullPointerException("Object to update is null");
        }
        return modelDao.update(exchangeToDto(object));
    }

    @Override
    public int delete(T object) {
        if(object == null) {
            throw new NullPointerException("Object to delete is null");
        }
        return modelDao.delete(exchangeToDto(object));
    }

    @Override
    public int deleteById(Object id) {
        if(id == null) {
            throw new NullPointerException("Object id is null");
        }
        return modelDao.deleteById(id);
    }
    
    protected List<T> exchangeToPojoList(List<K> list){
        if(list == null || list.size() <= 0) {
            return new ArrayList<>();
        }
        List<T> items = new ArrayList<>(list.size());
        
        for(K k : list) {
            items.add(this.exchangeToPojo(k));
        }
        return items;
    }

    public BaseModelDAO<K> getModelDao() {
        return modelDao;
    }

    public void setModelDao(BaseModelDAO<K> modelDao) {
        this.modelDao = modelDao;
    }
}
