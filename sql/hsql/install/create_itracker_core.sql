create table idstore (
  id                    VARCHAR(255)         PRIMARY KEY,
  name                  VARCHAR(255),
  last_id               VARCHAR(255)
);

insert into idstore values ('1',  'idstore', '1');

create table componentbean (
  id                    INTEGER                PRIMARY KEY,
  name                  VARCHAR(255),
  description           VARCHAR(255),
  status                INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME,
  project_id            INTEGER
);

create table issuebean (
  id                    INTEGER                PRIMARY KEY,
  severity              INTEGER,
  status                INTEGER,
  resolution            VARCHAR(255),
  description           VARCHAR(255),
  create_date           DATETIME,
  last_modified         DATETIME,
  target_version_id	    INTEGER,
  creator_id            INTEGER,
  owner_id              INTEGER,
  project_id            INTEGER
);

create table issuefieldbean (
  id                    INTEGER                PRIMARY KEY,
  field_id              INTEGER,
  string_value          VARCHAR(255),
  int_value             INTEGER,
  date_value            DATETIME,
  issue_id              INTEGER
);

create table issueactivitybean (
  id                    INTEGER                PRIMARY KEY,
  activity_type         INTEGER,
  description           VARCHAR(255),
  notification_sent     INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME,
  issue_id              INTEGER,
  user_id               INTEGER
);

create table issueattachmentbean (
  id                    INTEGER                PRIMARY KEY,
  orig_file_name        VARCHAR(255),
  attachment_type       VARCHAR(255),
  file_name             VARCHAR(255),
  description           VARCHAR(255),
  file_size             INTEGER,
  file_data             LONGVARBINARY,
  create_date           DATETIME,
  last_modified         DATETIME,
  issue_id              INTEGER,
  user_id               INTEGER
);

create table issuehistorybean (
  id                    INTEGER                PRIMARY KEY,
  description           VARCHAR(2000),
  status                INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME,
  issue_id              INTEGER,
  user_id               INTEGER
);

create table notificationbean (
  id                    INTEGER                PRIMARY KEY,
  user_role             INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME,
  issue_id              INTEGER,
  user_id               INTEGER
);

create table permissionbean (
  id                    INTEGER                PRIMARY KEY,
  permission_type       INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME,
  project_id            INTEGER,
  user_id               INTEGER
);

create table projectbean (
  id                    INTEGER                PRIMARY KEY,
  name                  VARCHAR(255),
  description           VARCHAR(255),
  status                INTEGER,
  options               INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME
);

create table userbean (
  id                    INTEGER                PRIMARY KEY,
  login                 VARCHAR(255),
  user_password         VARCHAR(255),
  first_name            VARCHAR(255),
  last_name             VARCHAR(255),
  email                 VARCHAR(255),
  status                INTEGER,
  registration_type     INTEGER,
  super_user            INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME,
  preferences_id        INTEGER
);

create table userpreferencesbean (
  id                    INTEGER                PRIMARY KEY,
  save_login            BIT,
  user_locale		        VARCHAR(255),
  num_items_index       INTEGER,
  num_items_issue_list  INTEGER,
  show_closed           BIT,
  sort_column           VARCHAR(255),
  hidden_index_sections INTEGER,
  remember_last_search  BIT,
  create_date           DATETIME,
  last_modified         DATETIME,
  user_id               INTEGER
);

create table versionbean (
  id                    INTEGER                PRIMARY KEY,
  major                 INTEGER,
  minor                 INTEGER,
  version_number        VARCHAR(255),
  description           VARCHAR(255),
  status                INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME,
  project_id            INTEGER
);

create table scheduledtaskbean (
  id                    INTEGER,
  hours                 VARCHAR(255),
  minutes               VARCHAR(255),
  days_of_month         VARCHAR(255),
  months                VARCHAR(255),
  weekdays              VARCHAR(255),
  class_name            VARCHAR(255),
  args                  VARCHAR(2000),
  create_date           DATETIME,
  last_modified         DATETIME
);

create table reportbean (
  id                    INTEGER                PRIMARY KEY,
  name                  VARCHAR(255),
  name_key              VARCHAR(255),
  description           VARCHAR(255),
  data_type             INTEGER,
  report_type           INTEGER,
  file_data             LONGVARBINARY,
  class_name            VARCHAR(1000),
  create_date           DATETIME,
  last_modified         DATETIME
);

create table configurationbean (
  id                    INTEGER               PRIMARY KEY,
  item_type             INTEGER,
  item_order            INTEGER,
  item_value            VARCHAR(255),
  item_version          VARCHAR(255),
  create_date           DATETIME,
  last_modified         DATETIME
);

create table customfieldbean (
  id                    INTEGER               PRIMARY KEY,
  field_type            INTEGER,
  date_format           VARCHAR(255),
  is_required           INTEGER,
  sort_options_by_name  INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME
);

create table customfieldvaluebean (
  id                    INTEGER               PRIMARY KEY,
  option_value          VARCHAR(255),
  sort_order            INTEGER,
  custom_field_id       INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME
);

create table languagebean (
  id                    INTEGER               PRIMARY KEY,
  locale                VARCHAR(255),
  resource_key          VARCHAR(255),
  resource_value        VARCHAR(4000),
  create_date           DATETIME,
  last_modified         DATETIME
);

create table issue_component_rel (
  issue_id              INTEGER,
  component_id          INTEGER
);

create table issue_version_rel (
  issue_id              INTEGER,
  version_id            INTEGER
);

create table project_owner_rel (
  project_id            INTEGER,
  user_id               INTEGER
);

create table project_field_rel (
  project_id            INTEGER,
  field_id              INTEGER
);
