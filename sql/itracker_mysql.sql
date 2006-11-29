
    alter table componentbean
        drop
        foreign key FK65BC6A2D87217ED0;

    alter table customfieldvaluebean
        drop
        foreign key FK68618E989794044D;

    alter table issue_component_rel
        drop
        foreign key FKA6FDC971BEEA70D0;

    alter table issue_component_rel
        drop
        foreign key FKA6FDC9716B4AA850;

    alter table issue_version_rel
        drop
        foreign key FK1FD6624C35ADBAF0;

    alter table issue_version_rel
        drop
        foreign key FK1FD6624C6B4AA850;

    alter table issueactivitybean
        drop
        foreign key FK7CEE1298F9981184;

    alter table issueactivitybean
        drop
        foreign key FK7CEE12986B4AA850;

    alter table issueattachmentbean
        drop
        foreign key FK8FB2DC6CF9981184;

    alter table issueattachmentbean
        drop
        foreign key FK8FB2DC6C6B4AA850;

    alter table issuebean
        drop
        foreign key FKB09A809657EC19C;

    alter table issuebean
        drop
        foreign key FKB09A80954941583;

    alter table issuebean
        drop
        foreign key FKB09A80987217ED0;

    alter table issuebean
        drop
        foreign key FKB09A809F5EBAA5E;

    alter table issuefieldbean
        drop
        foreign key FKF68FE2F1642E0F3F;

    alter table issuefieldbean
        drop
        foreign key FKF68FE2F16B4AA850;

    alter table issuehistorybean
        drop
        foreign key FK790013CBF9981184;

    alter table issuehistorybean
        drop
        foreign key FK790013CB6B4AA850;

    alter table issuerelationbean
        drop
        foreign key FKC2EDB05AB01BD36;

    alter table issuerelationbean
        drop
        foreign key FKC2EDB056B4AA850;

    alter table notificationbean
        drop
        foreign key FK60142F1BF9981184;

    alter table notificationbean
        drop
        foreign key FK60142F1B6B4AA850;

    alter table permissionbean
        drop
        foreign key FKAA4E59FFF9981184;

    alter table permissionbean
        drop
        foreign key FKAA4E59FF87217ED0;

    alter table project_field_rel
        drop
        foreign key FKB6F3762E642E0F3F;

    alter table project_field_rel
        drop
        foreign key FKB6F3762E87217ED0;

    alter table project_owner_rel
        drop
        foreign key FK6C974367F9981184;

    alter table project_owner_rel
        drop
        foreign key FK6C97436787217ED0;

    alter table projectscriptbean
        drop
        foreign key FK921C86D487217ED0;

    alter table userbean
        drop
        foreign key FKF02421FB7F1A8DA5;

    alter table userpreferencesbean
        drop
        foreign key FK72315B7DF9981184;

    alter table versionbean
        drop
        foreign key FK2919048887217ED0;

    drop table if exists componentbean;

    drop table if exists configurationbean;

    drop table if exists customfieldbean;

    drop table if exists customfieldvaluebean;

    drop table if exists issue_component_rel;

    drop table if exists issue_version_rel;

    drop table if exists issueactivitybean;

    drop table if exists issueattachmentbean;

    drop table if exists issuebean;

    drop table if exists issuefieldbean;

    drop table if exists issuehistorybean;

    drop table if exists issuerelationbean;

    drop table if exists languagebean;

    drop table if exists notificationbean;

    drop table if exists permissionbean;

    drop table if exists project_field_rel;

    drop table if exists project_owner_rel;

    drop table if exists projectbean;

    drop table if exists projectscriptbean;

    drop table if exists reportbean;

    drop table if exists scheduledtaskbean;

    drop table if exists userbean;

    drop table if exists userpreferencesbean;

    drop table if exists versionbean;

    drop table if exists workflowscriptbean;

    create table componentbean (
        id integer not null auto_increment,
        create_date datetime not null,
        last_modified datetime not null,
        name varchar(255) not null,
        description varchar(255) not null,
        project_id integer,
        primary key (id)
    );

    create table configurationbean (
        id integer not null auto_increment,
        item_type integer not null,
        item_order integer not null,
        item_value varchar(255) not null,
        item_version varchar(255) not null,
        create_date datetime not null,
        last_modified datetime not null,
        primary key (id)
    );

    create table customfieldbean (
        id integer not null auto_increment,
        create_date datetime not null,
        last_modified datetime not null,
        field_type integer not null,
        date_format varchar(255) not null,
        is_required integer not null,
        sort_options_by_name integer not null,
        primary key (id)
    );

    create table customfieldvaluebean (
        id integer not null auto_increment,
        create_date datetime not null,
        last_modified datetime not null,
        custom_field_id integer,
        option_value varchar(255) not null,
        sort_order integer not null,
        primary key (id)
    );

    create table issue_component_rel (
        component_id integer not null,
        issue_id integer not null
    );

    create table issue_version_rel (
        issue_id integer not null,
        version_id integer not null
    );

    create table issueactivitybean (
        id integer not null auto_increment,
        activity_type integer not null,
        description varchar(255) not null,
        notification_sent integer not null,
        create_date datetime not null,
        last_modified datetime not null,
        issue_id integer,
        user_id integer,
        primary key (id)
    );

    create table issueattachmentbean (
        id integer not null auto_increment,
        create_date datetime not null,
        last_modified datetime not null,
        description varchar(255) not null,
        orig_file_name varchar(255) not null,
        attachment_type varchar(255) not null,
        file_data mediumblob not null,
        file_name varchar(255) not null,
        file_size bigint not null,
        issue_id integer,
        user_id integer,
        primary key (id)
    );

    create table issuebean (
        id integer not null auto_increment,
        create_date datetime not null,
        last_modified datetime not null,
        description varchar(255) not null,
        severity integer not null,
        status integer not null,
        resolution varchar(255) not null,
        project_id integer,
        creator_id integer,
        owner_id integer,
        target_version_id integer,
        primary key (id)
    );

    create table issuefieldbean (
        id integer not null auto_increment,
        string_value varchar(255) not null,
        int_value integer not null,
        date_value datetime not null,
        issue_id integer,
        field_id integer,
        primary key (id)
    );

    create table issuehistorybean (
        id integer not null auto_increment,
        create_date datetime not null,
        last_modified datetime not null,
        description varchar(255) not null,
        status integer not null,
        issue_id integer,
        user_id integer,
        primary key (id)
    );

    create table issuerelationbean (
        id integer not null auto_increment,
        create_date datetime not null,
        last_modified datetime not null,
        matching_relation_id integer not null,
        relation_type integer not null,
        issue_id integer,
        rel_issue_id integer,
        primary key (id)
    );

    create table languagebean (
        id integer not null auto_increment,
        create_date datetime not null,
        last_modified datetime not null,
        locale varchar(255) not null,
        resource_key varchar(255) not null,
        resource_value text not null,
        primary key (id)
    );

    create table notificationbean (
        id integer not null auto_increment,
        create_date datetime not null,
        last_modified datetime not null,
        user_role integer not null,
        user_id integer,
        issue_id integer,
        primary key (id)
    );

    create table permissionbean (
        id integer not null auto_increment,
        create_date datetime not null,
        last_modified datetime not null,
        permission_type integer not null,
        user_id integer,
        project_id integer,
        primary key (id)
    );

    create table project_field_rel (
        field_id integer not null,
        project_id integer not null
    );

    create table project_owner_rel (
        project_id integer not null,
        user_id integer not null
    );

    create table projectbean (
        id integer not null auto_increment,
        create_date datetime not null,
        last_modified datetime not null,
        name varchar(255) not null,
        description varchar(255) not null,
        status integer not null,
        options integer not null,
        primary key (id)
    );

    create table projectscriptbean (
        id integer not null auto_increment,
        create_date datetime not null,
        last_modified datetime not null,
        field_id integer not null,
        script_priority integer not null,
        project_id integer,
        primary key (id)
    );

    create table reportbean (
        id integer not null auto_increment,
        name varchar(255) not null,
        name_key varchar(255) not null,
        description varchar(255) not null,
        data_type integer not null,
        report_type integer not null,
        file_data blob,
        class_name varchar(255),
        create_date datetime not null,
        last_modified datetime not null,
        primary key (id)
    );

    create table scheduledtaskbean (
        id integer not null auto_increment,
        hours varchar(255) not null,
        minutes varchar(255) not null,
        days_of_month varchar(255) not null,
        months varchar(255) not null,
        weekdays varchar(255) not null,
        class_name varchar(255) not null,
        args varchar(255) not null,
        create_date datetime not null,
        last_modified datetime not null,
        primary key (id)
    );

    create table userbean (
        id integer not null auto_increment,
        create_date datetime not null,
        last_modified datetime not null,
        login varchar(255) not null,
        user_password varchar(255) not null,
        first_name varchar(255) not null,
        last_name varchar(255) not null,
        email varchar(255) not null,
        status integer not null,
        super_user integer not null,
        registration_type integer not null,
        preferences_id integer,
        primary key (id)
    );

    create table userpreferencesbean (
        id integer not null auto_increment,
        create_date datetime not null,
        last_modified datetime not null,
        save_login bit not null,
        user_locale varchar(255) not null,
        num_items_index integer not null,
        num_items_issue_list integer not null,
        show_closed bit not null,
        sort_column varchar(255) not null,
        hidden_index_sections integer not null,
        remember_last_search bit not null,
        use_text_actions bit not null,
        user_id integer,
        primary key (id)
    );

    create table versionbean (
        id integer not null auto_increment,
        create_date datetime not null,
        last_modified datetime not null,
        version_number varchar(255) not null,
        major integer not null,
        minor integer not null,
        description varchar(255) not null,
        project_id integer,
        primary key (id)
    );

    create table workflowscriptbean (
        id integer not null auto_increment,
        create_date datetime not null,
        last_modified datetime not null,
        script_name varchar(255) not null,
        script_data varchar(255) not null,
        event_type integer not null,
        primary key (id)
    );

    alter table componentbean
        add index FK65BC6A2D87217ED0 (project_id),
        add constraint FK65BC6A2D87217ED0
        foreign key (project_id)
        references projectbean (id);

    alter table customfieldvaluebean
        add index FK68618E989794044D (custom_field_id),
        add constraint FK68618E989794044D
        foreign key (custom_field_id)
        references customfieldbean (id);

    alter table issue_component_rel
        add index FKA6FDC971BEEA70D0 (component_id),
        add constraint FKA6FDC971BEEA70D0
        foreign key (component_id)
        references componentbean (id);

    alter table issue_component_rel
        add index FKA6FDC9716B4AA850 (issue_id),
        add constraint FKA6FDC9716B4AA850
        foreign key (issue_id)
        references issuebean (id);

    alter table issue_version_rel
        add index FK1FD6624C35ADBAF0 (version_id),
        add constraint FK1FD6624C35ADBAF0
        foreign key (version_id)
        references versionbean (id);

    alter table issue_version_rel
        add index FK1FD6624C6B4AA850 (issue_id),
        add constraint FK1FD6624C6B4AA850
        foreign key (issue_id)
        references issuebean (id);

    alter table issueactivitybean
        add index FK7CEE1298F9981184 (user_id),
        add constraint FK7CEE1298F9981184
        foreign key (user_id)
        references userbean (id);

    alter table issueactivitybean
        add index FK7CEE12986B4AA850 (issue_id),
        add constraint FK7CEE12986B4AA850
        foreign key (issue_id)
        references issuebean (id);

    alter table issueattachmentbean
        add index FK8FB2DC6CF9981184 (user_id),
        add constraint FK8FB2DC6CF9981184
        foreign key (user_id)
        references userbean (id);

    alter table issueattachmentbean
        add index FK8FB2DC6C6B4AA850 (issue_id),
        add constraint FK8FB2DC6C6B4AA850
        foreign key (issue_id)
        references issuebean (id);

    alter table issuebean
        add index FKB09A809657EC19C (owner_id),
        add constraint FKB09A809657EC19C
        foreign key (owner_id)
        references userbean (id);

    alter table issuebean
        add index FKB09A80954941583 (creator_id),
        add constraint FKB09A80954941583
        foreign key (creator_id)
        references userbean (id);

    alter table issuebean
        add index FKB09A80987217ED0 (project_id),
        add constraint FKB09A80987217ED0
        foreign key (project_id)
        references projectbean (id);

    alter table issuebean
        add index FKB09A809F5EBAA5E (target_version_id),
        add constraint FKB09A809F5EBAA5E
        foreign key (target_version_id)
        references versionbean (id);

    alter table issuefieldbean
        add index FKF68FE2F1642E0F3F (field_id),
        add constraint FKF68FE2F1642E0F3F
        foreign key (field_id)
        references customfieldbean (id);

    alter table issuefieldbean
        add index FKF68FE2F16B4AA850 (issue_id),
        add constraint FKF68FE2F16B4AA850
        foreign key (issue_id)
        references issuebean (id);

    alter table issuehistorybean
        add index FK790013CBF9981184 (user_id),
        add constraint FK790013CBF9981184
        foreign key (user_id)
        references userbean (id);

    alter table issuehistorybean
        add index FK790013CB6B4AA850 (issue_id),
        add constraint FK790013CB6B4AA850
        foreign key (issue_id)
        references issuebean (id);

    alter table issuerelationbean
        add index FKC2EDB05AB01BD36 (rel_issue_id),
        add constraint FKC2EDB05AB01BD36
        foreign key (rel_issue_id)
        references issuebean (id);

    alter table issuerelationbean
        add index FKC2EDB056B4AA850 (issue_id),
        add constraint FKC2EDB056B4AA850
        foreign key (issue_id)
        references issuebean (id);

    alter table notificationbean
        add index FK60142F1BF9981184 (user_id),
        add constraint FK60142F1BF9981184
        foreign key (user_id)
        references userbean (id);

    alter table notificationbean
        add index FK60142F1B6B4AA850 (issue_id),
        add constraint FK60142F1B6B4AA850
        foreign key (issue_id)
        references issuebean (id);

    alter table permissionbean
        add index FKAA4E59FFF9981184 (user_id),
        add constraint FKAA4E59FFF9981184
        foreign key (user_id)
        references userbean (id);

    alter table permissionbean
        add index FKAA4E59FF87217ED0 (project_id),
        add constraint FKAA4E59FF87217ED0
        foreign key (project_id)
        references projectbean (id);

    alter table project_field_rel
        add index FKB6F3762E642E0F3F (field_id),
        add constraint FKB6F3762E642E0F3F
        foreign key (field_id)
        references customfieldbean (id);

    alter table project_field_rel
        add index FKB6F3762E87217ED0 (project_id),
        add constraint FKB6F3762E87217ED0
        foreign key (project_id)
        references projectbean (id);

    alter table project_owner_rel
        add index FK6C974367F9981184 (user_id),
        add constraint FK6C974367F9981184
        foreign key (user_id)
        references userbean (id);

    alter table project_owner_rel
        add index FK6C97436787217ED0 (project_id),
        add constraint FK6C97436787217ED0
        foreign key (project_id)
        references projectbean (id);

    alter table projectscriptbean
        add index FK921C86D487217ED0 (project_id),
        add constraint FK921C86D487217ED0
        foreign key (project_id)
        references projectbean (id);

    alter table userbean
        add index FKF02421FB7F1A8DA5 (preferences_id),
        add constraint FKF02421FB7F1A8DA5
        foreign key (preferences_id)
        references userpreferencesbean (id);

    alter table userpreferencesbean
        add index FK72315B7DF9981184 (user_id),
        add constraint FK72315B7DF9981184
        foreign key (user_id)
        references userbean (id);

    alter table versionbean
        add index FK2919048887217ED0 (project_id),
        add constraint FK2919048887217ED0
        foreign key (project_id)
        references projectbean (id);
