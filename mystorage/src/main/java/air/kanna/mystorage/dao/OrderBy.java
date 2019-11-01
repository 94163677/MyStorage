package air.kanna.mystorage.dao;

import java.util.ArrayList;
import java.util.List;

import air.kanna.mystorage.util.StringUtil;

public class OrderBy {
    private static final String ASC = "asc";
    private static final String DESC = "desc";
    
    private List<String[]> orderPairs = new ArrayList<>();
    
    public List<String[]> getOrderPairs(){
        return orderPairs;
    }
    
    public void addOrderAsc(String clomnName) {
        if(StringUtil.isNotNull(clomnName)) {
            String[] pair = new String[] {ASC, clomnName};
            orderPairs.add(pair);
        }
    }
    
    public void addOrderDesc(String clomnName) {
        if(StringUtil.isNotNull(clomnName)) {
            String[] pair = new String[] {DESC, clomnName};
            orderPairs.add(pair);
        }
    }
}
