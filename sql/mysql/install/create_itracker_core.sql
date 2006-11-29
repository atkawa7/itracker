create table IDSTORE (
  id                    VARCHAR(80)         PRIMARY KEY,
  name                  VARCHAR(80)         UNIQUE,
  last_id               VARCHAR(255)
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

insert into IDSTORE values ('1',  'idstore', '1');

create table componentbean (
  id                    INT                PRIMARY KEY,
  name                  VARCHAR(255),
  description           VARCHAR(255),
  status                INT,
  create_date           DATETIME,
  last_modified         DATETIME,
  project_id            INT
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table issuebean (
  id                    INT                PRIMARY KEY,
  severity              INT,
  status                INT,
  resolution            VARCHAR(255),
  description           VARCHAR(255),
  create_date           DATETIME,
  last_modified         DATETIME,
  target_version_id     INT,
  creator_id            INT,
  owner_id              INT,
  project_id            INT
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table issuefieldbean (
  id                    INT                PRIMARY KEY,
  field_id              INT,
  string_value          VARCHAR(255),
  int_value             INT,
  date_value            DATETIME,
  issue_id              INT
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table issueactivitybean (
  id                    INT                PRIMARY KEY,
  activity_type         INT,
  description           VARCHAR(255),
  notification_sent     INT,
  create_date           DATETIME,
  last_modified         DATETIME,
  issue_id              INT,
  user_id               INT
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table issueattachmentbean (
  id                    INT                PRIMARY KEY,
  orig_file_name        VARCHAR(255),
  attachment_type       VARCHAR(255),
  file_name             VARCHAR(255),
  description           VARCHAR(255),
  file_size             INT,
  file_data             LONGBLOB,
  create_date           DATETIME,
  last_modified         DATETIME,
  issue_id              INT,
  user_id               INT
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table issuehistorybean (
  id                    INT                PRIMARY KEY,
  description           TEXT,
  status                INT,
  create_date           DATETIME,
  last_modified         DATETIME,
  issue_id              INT,
  user_id               INT
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table issuerelationbean (
  id                    INT                PRIMARY KEY,
  issue_id              INT,
  rel_issue_id          INT,
  relation_type         INT,
  matching_relation_id  INT,
  create_date           DATETIME,
  last_modified         DATETIME
) DEFAULT CHARACTER SET utf8;

create table notificationbean (
  id                    INT                PRIMARY KEY,
  user_role             INT,
  create_date           DATETIME,
  last_modified         DATETIME,
  issue_id              INT,
  user_id               INT
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table permissionbean (
  id                    INT                PRIMARY KEY,
  permission_type       INT,
  create_date           DATETIME,
  last_modified         DATETIME,
  project_id            INT,
  user_id               INT
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table projectbean (
  id                    INT                PRIMARY KEY,
  name                  VARCHAR(255),
  description           VARCHAR(255),
  status                INT,
  options               INT,
  create_date           DATETIME,
  last_modified         DATETIME
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table userbean (
  id                    INT                PRIMARY KEY,
  login                 VARCHAR(255),
  user_password         VARCHAR(255),
  first_name            VARCHAR(255),
  last_name             VARCHAR(255),
  email                 VARCHAR(255),
  status                INT,
  registration_type     INT,
  super_user            INT,
  create_date           DATETIME,
  last_modified         DATETIME,
  preferences_id        INT
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table userpreferencesbean (
  id                    INT                PRIMARY KEY,
  save_login            INT,
  user_locale           VARCHAR(255),
  num_items_index       INT,
  num_items_issue_list  INT,
  show_closed           INT,
  sort_column           VARCHAR(255),
  hidden_index_sections INT,
  remember_last_search  INT,
  use_text_actions      INT,
  create_date           DATETIME,
  last_modified         DATETIME,
  user_id               INT
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table versionbean (
  id                    INT                PRIMARY KEY,
  major                 INT,
  minor                 INT,
  version_number        VARCHAR(255),
  description           VARCHAR(255),
  status                INT,
  create_date           DATETIME,
  last_modified         DATETIME,
  project_id            INT
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table scheduledtaskbean (
  id                    INT               PRIMARY KEY,
  hours                 VARCHAR(255),
  minutes               VARCHAR(255),
  days_of_month         VARCHAR(255),
  months                VARCHAR(255),
  weekdays              VARCHAR(255),
  class_name            VARCHAR(255),
  args                  TEXT,
  create_date           DATETIME,
  last_modified         DATETIME
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table reportbean (
  id                    INT                PRIMARY KEY,
  name                  VARCHAR(255),
  name_key              VARCHAR(255),
  description           VARCHAR(255),
  data_type             INT,
  report_type           INT,
  file_data             LONGBLOB,
  class_name            TEXT,
  create_date           DATETIME,
  last_modified         DATETIME
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table configurationbean (
  id                    INT               PRIMARY KEY,
  item_type             INT,
  item_order            INT,
  item_value            VARCHAR(255),
  item_version          VARCHAR(255),
  create_date           DATETIME,
  last_modified         DATETIME
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table customfieldbean (
  id                    INT               PRIMARY KEY,
  field_type            INT,
  date_format           VARCHAR(255),
  is_required           INT,
  sort_options_by_name  INT,
  create_date           DATETIME,
  last_modified         DATETIME
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table customfieldvaluebean (
  id                    INT               PRIMARY KEY,
  option_value          VARCHAR(255),
  sort_order            INT,
  custom_field_id       INT,
  create_date           DATETIME,
  last_modified         DATETIME
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table workflowscriptbean (
  id                    INT               PRIMARY KEY,
  script_name           VARCHAR(255),
  event_type            INT,
  script_data           TEXT,
  create_date           DATETIME,
  last_modified         DATETIME
) DEFAULT CHARACTER SET utf8;

create table projectscriptbean (
  id                    INT               PRIMARY KEY,
  project_id            INT,
  field_id              INT,
  script_id             INT,
  script_priority       INT,
  create_date           DATETIME,
  last_modified         DATETIME
) DEFAULT CHARACTER SET utf8;


create table languagebean (
  id                    INT               PRIMARY KEY,
  locale                VARCHAR(255),
  resource_key          VARCHAR(255),
  resource_value        TEXT,
  create_date           DATETIME,
  last_modified         DATETIME
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table issue_component_rel (
  issue_id              INT,
  component_id          INT
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table issue_version_rel (
  issue_id              INT,
  version_id            INT
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table project_owner_rel (
  project_id            INT,
  user_id               INT
) DEFAULT CHARACTER SET utf8 TYPE=innodb;

create table project_field_rel (
  project_id            INT,
  field_id              INT
) DEFAULT CHARACTER SET utf8 TYPE=innodb;
