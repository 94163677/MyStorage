package air.kanna.mystorage.service.hash;

import java.util.Map;

import air.kanna.mystorage.model.FileItem;

public interface HashService {
    Map<String, String> getFileItemHashString(FileItem item) throws Exception;
}
