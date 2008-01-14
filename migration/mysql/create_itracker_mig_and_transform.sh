#!
mysqladmin -u <user> -p<password> DROP itracker_mig 
mysqladmin -u <user> -p<password> CREATE itracker_mig 
mysqldump -u <user> -p<password> itracker > itracker_migration_dump.sql
mysql -u <user> -ps30ul3131 itracker_mig < itracker_migration_dump.sql
mysql -u <user> -p<password> itracker_mig < itracker_migration_transform_script.sql

