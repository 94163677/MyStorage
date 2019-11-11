CREATE TABLE IF NOT EXISTS db_version (
  version TEXT NOT NULL);

CREATE TABLE IF NOT EXISTS disk_description (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  version TEXT NOT NULL,
  base_path TEXT NOT NULL,
  description TEXT,
  last_update TEXT NOT NULL);
  
CREATE TABLE IF NOT EXISTS file_list (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  disk_id INTEGER NOT NULL,
  file_name TEXT NOT NULL,
  file_type INTEGER NOT NULL,
  file_size INTEGER NOT NULL,
  file_path TEXT NOT NULL,
  
  file_hash_01 TEXT,
  file_hash_02 TEXT,
  file_hash_03 TEXT,
  
  create_date INTEGER NOT NULL,
  update_date INTEGER NOT NULL,
  remark TEXT);
  
CREATE INDEX file_list_idx01
  ON file_list (disk_id);
  
CREATE INDEX file_list_idx02
  ON file_list (file_name);
  
CREATE INDEX file_list_idx03
  ON file_list (update_date);