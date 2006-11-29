-- MySQL version 4.0.1
insert into user (Host, User, password, Select_priv, Insert_priv, Update_priv, Delete_priv,
                  Create_priv, Drop_priv, Reload_priv, Shutdown_priv, Process_priv, File_priv, 
                  Grant_priv, References_priv, Index_priv, Alter_priv, Show_db_priv, Super_priv, 
                  Create_tmp_table_priv, Lock_tables_priv, Execute_priv) 
          values ('%', 'itracker', password('itracker'), 'Y', 'Y', 'Y', 'Y',
                  'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y');
                         
insert into user (Host, User, password, Select_priv, Insert_priv, Update_priv, Delete_priv,
                  Create_priv, Drop_priv, Reload_priv, Shutdown_priv, Process_priv, File_priv, 
                  Grant_priv, References_priv, Index_priv, Alter_priv, Show_db_priv, Super_priv, 
                  Create_tmp_table_priv, Lock_tables_priv, Execute_priv) 
          values ('localhost', 'itracker', password('itracker'), 'Y', 'Y', 'Y', 'Y',
                  'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y');
                         