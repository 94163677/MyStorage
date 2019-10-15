package air.kanna.mystorage.dao;

import java.util.List;

public interface BaseModelDAO<T> {
    
    T getById(Object id);
    
    List<T> listAll(Pager pager);
    
    int insert(T object);
    
    int update(T object);
    
    int delete(T object);
    
    int deleteById(Object id);
}