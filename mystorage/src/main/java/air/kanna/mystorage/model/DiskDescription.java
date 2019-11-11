package air.kanna.mystorage.model;

import air.kanna.mystorage.model.dto.DiskDescriptionDTO;

/**
 * 磁盘或者根目录的描述
 */
public class DiskDescription extends DiskDescriptionDTO{    
    public DiskDescription() {
        
    }
    
    public DiskDescription(DiskDescriptionDTO dto) {
        setVersion(dto.getVersion());
        setId(dto.getId());
        setBasePath(dto.getBasePath());
        setDescription(dto.getDescription());
        setLastUpdate(dto.getLastUpdate());
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj instanceof DiskDescription) {
            DiskDescription disk = (DiskDescription)obj;
            return disk.getBasePath().equals(getBasePath());
        }
        return false;
    }
}
