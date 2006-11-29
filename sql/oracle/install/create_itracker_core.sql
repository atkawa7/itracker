create table idstore (
  id                    VARCHAR2(255)         PRIMARY KEY,
  name                  VARCHAR2(255)         UNIQUE,
  last_id               VARCHAR2(255)
);

insert into idstore values ('1',  'idstore', '1');

create table componentbean (
  id                    NUMBER                PRIMARY KEY,
  name                  VARCHAR2(255),
  description           VARCHAR2(255),
  status                NUMBER,
  create_date           DATE,
  last_modified         DATE,
  project_id            NUMBER
);

create table issuebean (
  id                    NUMBER                PRIMARY KEY,
  severity              NUMBER,
  status                NUMBER,
  resolution            VARCHAR2(255),
  description           VARCHAR2(255),
  create_date           DATE,
  last_modified         DATE,
  target_version_id     NUMBER,
  creator_id            NUMBER,
  owner_id              NUMBER,
  project_id            NUMBER
);

create table issuefieldbean (
  id                    NUMBER                PRIMARY KEY,
  field_id              NUMBER,
  string_value          VARCHAR2(255),
  int_value             NUMBER,
  date_value            DATE,
  issue_id              NUMBER
);

create table issueactivitybean (
  id                    NUMBER                PRIMARY KEY,
  activity_type         NUMBER,
  description           VARCHAR2(255),
  notification_sent     NUMBER,
  create_date           DATE,
  last_modified         DATE,
  issue_id              NUMBER,
  user_id               NUMBER
);

create table issueattachmentbean (
  id                    NUMBER                PRIMARY KEY,
  orig_file_name        VARCHAR2(255),
  attachment_type       VARCHAR2(255),
  file_name             VARCHAR2(255),
  description           VARCHAR2(255),
  file_size             NUMBER,
  file_data             LONG RAW,
  create_date           DATE,
  last_modified         DATE,
  issue_id              NUMBER,
  user_id               NUMBER
);

create table issuehistorybean (
  id                    NUMBER                PRIMARY KEY,
  description           VARCHAR2(2000),
  status                NUMBER,
  create_date           DATE,
  last_modified         DATE,
  issue_id              NUMBER,
  user_id               NUMBER
);

create table issuerelationbean (
  id                    NUMBER                PRIMARY KEY,
  issue_id              NUMBER,
  rel_issue_id          NUMBER,
  relation_type         NUMBER,
  matching_relation_id  NUMBER,
  create_date           DATE,
  last_modified         DATE
);

create table notificationbean (
  id                    NUMBER                PRIMARY KEY,
  user_role             NUMBER,
  create_date           DATE,
  last_modified         DATE,
  issue_id              NUMBER,
  user_id               NUMBER
);

create table permissionbean (
  id                    NUMBER                PRIMARY KEY,
  permission_type       NUMBER,
  create_date           DATE,
  last_modified         DATE,
  project_id            NUMBER,
  user_id               NUMBER
);

create table projectbean (
  id                    NUMBER                PRIMARY KEY,
  name                  VARCHAR2(255),
  description           VARCHAR2(255),
  status                NUMBER,
  options               NUMBER,
  create_date           DATE,
  last_modified         DATE
);

create table userbean (
  id                    NUMBER                PRIMARY KEY,
  login                 VARCHAR2(255),
  user_password         VARCHAR2(255),
  first_name            VARCHAR2(255),
  last_name             VARCHAR2(255),
  email                 VARCHAR2(255),
  status                NUMBER,
  registration_type     NUMBER,
  super_user            NUMBER,
  create_date           DATE,
  last_modified         DATE,
  preferences_id        NUMBER
);

create table userpreferencesbean (
  id                    NUMBER                PRIMARY KEY,
  save_login            NUMBER,
  user_locale           VARCHAR2(255),
  num_items_index       NUMBER,
  num_items_issue_list  NUMBER,
  show_closed           NUMBER,
  sort_column           VARCHAR2(255),
  hidden_index_sections NUMBER,
  remember_last_search  NUMBER,
  create_date           DATE,
  last_modified         DATE,
  user_id               NUMBER
);

create table versionbean (
  id                    NUMBER                PRIMARY KEY,
  major                 NUMBER,
  minor                 NUMBER,
  version_number        VARCHAR2(255),
  description           VARCHAR2(255),
  status                NUMBER,
  create_date           DATE,
  last_modified         DATE,
  project_id            NUMBER
);

create table scheduledtaskbean (
  id                    NUMBER,
  hours                 VARCHAR2(255),
  minutes               VARCHAR2(255),
  days_of_month         VARCHAR2(255),
  months                VARCHAR2(255),
  weekdays              VARCHAR2(255),
  class_name            VARCHAR2(255),
  args                  VARCHAR2(2000),
  create_date           DATE,
  last_modified         DATE
);

create table reportbean (
  id                    NUMBER                PRIMARY KEY,
  name                  VARCHAR2(255),
  name_key              VARCHAR2(255),
  description           VARCHAR2(255),
  data_type             NUMBER,
  report_type           NUMBER,
  file_data             LONG RAW,
  class_name            VARCHAR2(1000),
  create_date           DATE,
  last_modified         DATE
);

create table configurationbean (
  id                    NUMBER               PRIMARY KEY,
  item_type             NUMBER,
  item_order            NUMBER,
  item_value            VARCHAR2(255),
  item_version          VARCHAR2(255),
  create_date           DATE,
  last_modified         DATE
);

create table customfieldbean (
  id                    NUMBER               PRIMARY KEY,
  field_type            NUMBER,
  date_format           VARCHAR2(255),
  is_required           NUMBER,
  sort_options_by_name  NUMBER,
  create_date           DATE,
  last_modified         DATE
);

create table customfieldvaluebean (
  id                    NUMBER               PRIMARY KEY,
  option_value          VARCHAR2(255),
  sort_order            NUMBER,
  custom_field_id       NUMBER,
  create_date           DATE,
  last_modified         DATE
);

create table languagebean (
  id                    NUMBER               PRIMARY KEY,
  locale                VARCHAR2(255),
  resource_key          VARCHAR2(255),
  resource_value        CLOB,
  create_date           DATE,
  last_modified         DATE
);

create table workflowscriptbean (
  id                    NUMBER               PRIMARY KEY,
  script_name           VARCHAR(255),
  event_type            NUMBER,
  script_data           CLOB,
  create_date           DATE,
  last_modified         DATE
);

create table projectscriptbean (
  id                    NUMBER               PRIMARY KEY,
  project_id            NUMBER,
  field_id              NUMBER,
  script_id             NUMBER,
  script_priority       NUMBER,
  create_date           DATE,
  last_modified         DATE
);

create table issue_component_rel (
  issue_id              NUMBER,
  component_id          NUMBER
);

create table issue_version_rel (
  issue_id              NUMBER,
  version_id            NUMBER
);

create table project_owner_rel (
  project_id            NUMBER,
  user_id               NUMBER
);

create table project_field_rel (
  project_id            NUMBER,
  field_id               NUMBER
);



