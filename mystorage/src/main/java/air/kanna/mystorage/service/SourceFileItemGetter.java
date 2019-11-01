package air.kanna.mystorage.service;

import java.util.List;

import air.kanna.mystorage.model.DiskDescription;
import air.kanna.mystorage.model.FileItem;

public interface SourceFileItemGetter {
    List<FileItem> createNewDiskFileItem(DiskDescription disk);
}
