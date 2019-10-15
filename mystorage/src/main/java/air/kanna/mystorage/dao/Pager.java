package air.kanna.mystorage.dao;

public class Pager {

    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_SIZE = 10;
    
    private int page = DEFAULT_PAGE;
    private int size = DEFAULT_SIZE;
    
    public Pager() {}
    
    public Pager(int page, int size) {
        setPage(page);
        setSize(size);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        if(page <= 0) {
            page = DEFAULT_PAGE;
        }
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if(size <= 0) {
            size = DEFAULT_SIZE;
        }
        this.size = size;
    }
}
