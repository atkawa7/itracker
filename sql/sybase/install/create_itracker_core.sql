create table IDSTORE (
  id                    VARCHAR(255)         NOT NULL PRIMARY KEY,
  name                  VARCHAR(255)         UNIQUE,
  last_id               VARCHAR(255)
)
go
insert into IDSTORE values ('1',  'idstore', '1')
go
create table COMPONENTBEAN(
  id                    INTEGER                NOT NULL PRIMARY KEY,
  name                  VARCHAR(255),
  description           VARCHAR(255),
  status                INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME,
  project_id            INTEGER
)

create table ISSUEBEAN (
  id                    INTEGER                NOT NULL PRIMARY KEY,
  severity              INTEGER,
  status                INTEGER,
  resolution            VARCHAR(255),
  description           VARCHAR(255),
  create_date           DATETIME,
  last_modified         DATETIME,
  target_version_id     INTEGER,
  creator_id            INTEGER,
  owner_id              INTEGER,
  project_id            INTEGER
)

create table ISSUEACTIVITYBEAN(
  id                    INTEGER                NOT NULL PRIMARY KEY,
  activity_type         INTEGER,
  description           VARCHAR(255),
  notification_sent     INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME,
  issue_id              INTEGER,
  user_id               INTEGER
)

create table ISSUEATTACHMENTBEAN(
  id                    INTEGER                NOT NULL PRIMARY KEY,
  orig_file_name        VARCHAR(255),
  attachment_type       VARCHAR(255),
  file_name             VARCHAR(255),
  description           VARCHAR(255),
  file_size             INTEGER,
  file_data             BLOB,
  create_date           DATETIME,
  last_modified         DATETIME,
  issue_id              INTEGER,
  user_id               INTEGER
)

create table ISSUEFIELDBEAN(
  id                    INTEGER                NOT NULL PRIMARY KEY,
  field_id              INTEGER,
  string_value          VARCHAR(255),
  int_value             INTEGER,
  date_value            DATETIME,
  issue_id              INTEGER
)

create table ISSUEHISTORYBEAN(
  id                    INTEGER                NOT NULL PRIMARY KEY,
  description           TEXT,
  status                INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME,
  issue_id              INTEGER,
  user_id               INTEGER
)

create table NOTIFICATIONBEAN(
  id                    INTEGER                NOT NULL PRIMARY KEY,
  user_role             INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME,
  issue_id              INTEGER,
  user_id               INTEGER
)

create table PERMISSIONBEAN(
  id                    INTEGER                NOT NULL PRIMARY KEY,
  permission_type       INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME,
  project_id            INTEGER,
  user_id               INTEGER
)

create table PROJECTBEAN(
  id                    INTEGER                NOT NULL PRIMARY KEY,
  name                  VARCHAR(255),
  description           VARCHAR(255),
  status                INTEGER,
  options               INTEGER,
  custom_fields         INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME
)

create table USERBEAN(
  id                    INTEGER                NOT NULL PRIMARY KEY,
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
)

create table USERPREFERENCESBEAN(
  id                    INTEGER                NOT NULL PRIMARY KEY,
  save_login            INTEGER,
  user_locale           VARCHAR(255),
  num_items_index       INTEGER,
  num_items_issue_list  INTEGER,
  show_closed           INTEGER,
  sort_column           VARCHAR(255),
  hidden_index_sections INTEGER,
  remember_last_search  INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME,
  user_id               INTEGER
)

create table VERSIONBEAN(
  id                    INTEGER                NOT NULL PRIMARY KEY,
  major                 INTEGER,
  minor                 INTEGER,
  version_number        VARCHAR(255),
  description           VARCHAR(255),
  status                INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME,
  project_id            INTEGER
)

create table REPORTBEAN (
  id                    INTEGER                NOT NULL PRIMARY KEY,
  name                  VARCHAR(255),
  name_key              VARCHAR(255),
  description           VARCHAR(255),
  data_type             INTEGER,
  report_type           INTEGER,
  file_data             BLOB,
  class_name            TEXT,
  create_date           DATETIME,
  last_modified         DATETIME
)

create table CONFIGURATIONBEAN (
  id                    INTEGER               NOT NULL PRIMARY KEY,
  item_type             INTEGER,
  item_order            INTEGER,
  item_value            VARCHAR(255),
  item_version          VARCHAR(255),
  create_date           DATETIME,
  last_modified         DATETIME
)

create table CUSTOMFIELDBEAN (
  id                    INTEGER               NOT NULL PRIMARY KEY,
  field_type            INTEGER,
  date_format           VARCHAR(255),
  is_required           INTEGER,
  sort_options_by_name  INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME
)

create table CUSTOMFIELDVALUEBEAN (
  id                    INTEGER               NOT NULL PRIMARY KEY,
  option_value          VARCHAR(255),
  sort_order            INTEGER,
  custom_field_id       INTEGER,
  create_date           DATETIME,
  last_modified         DATETIME
)

create table LANGUAGEBEAN (
  id                    INTEGER               NOT NULL PRIMARY KEY,
  locale                VARCHAR(255),
  resource_key          VARCHAR(255),
  resource_value        TEXT,
  create_date           DATETIME,
  last_modified         DATETIME
)

create table SCHEDULEDTASKBEAN (
  id                    INTEGER,
  hours                 VARCHAR(255),
  minutes               VARCHAR(255),
  days_of_month         VARCHAR(255),
  months                VARCHAR(255),
  weekdays              VARCHAR(255),
  class_name            VARCHAR(255),
  args                  TEXT,
  create_date           DATETIME,
  last_modified         DATETIME
)

create table ISSUE_COMPONENT_REL(
  issue_id              INTEGER,
  component_id          INTEGER
)

create table ISSUE_VERSION_REL(
  issue_id              INTEGER,
  version_id            INTEGER
)

create table PROJECT_OWNER_REL(
  project_id            INTEGER,
  user_id               INTEGER
)

create table PROJECT_FIELD_REL(
  project_id            INTEGER,
  field_id              INTEGER
)
go

