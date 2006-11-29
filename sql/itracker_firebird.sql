
    alter table componentbean
        drop constraint FK65BC6A2D87217ED0;

    alter table customfieldvaluebean
        drop constraint FK68618E989794044D;

    alter table issue_component_rel
        drop constraint FKA6FDC971BEEA70D0;

    alter table issue_component_rel
        drop constraint FKA6FDC9716B4AA850;

    alter table issue_version_rel
        drop constraint FK1FD6624C35ADBAF0;

    alter table issue_version_rel
        drop constraint FK1FD6624C6B4AA850;

    alter table issueactivitybean
        drop constraint FK7CEE1298F9981184;

    alter table issueactivitybean
        drop constraint FK7CEE12986B4AA850;

    alter table issueattachmentbean
        drop constraint FK8FB2DC6CF9981184;

    alter table issueattachmentbean
        drop constraint FK8FB2DC6C6B4AA850;

    alter table issuebean
        drop constraint FKB09A809657EC19C;

    alter table issuebean
        drop constraint FKB09A80954941583;

    alter table issuebean
        drop constraint FKB09A80987217ED0;

    alter table issuebean
        drop constraint FKB09A809F5EBAA5E;

    alter table issuefieldbean
        drop constraint FKF68FE2F1642E0F3F;

    alter table issuefieldbean
        drop constraint FKF68FE2F16B4AA850;

    alter table issuehistorybean
        drop constraint FK790013CBF9981184;

    alter table issuehistorybean
        drop constraint FK790013CB6B4AA850;

    alter table issuerelationbean
        drop constraint FKC2EDB05AB01BD36;

    alter table issuerelationbean
        drop constraint FKC2EDB056B4AA850;

    alter table notificationbean
        drop constraint FK60142F1BF9981184;

    alter table notificationbean
        drop constraint FK60142F1B6B4AA850;

    alter table permissionbean
        drop constraint FKAA4E59FFF9981184;

    alter table permissionbean
        drop constraint FKAA4E59FF87217ED0;

    alter table project_field_rel
        drop constraint FKB6F3762E642E0F3F;

    alter table project_field_rel
        drop constraint FKB6F3762E87217ED0;

    alter table project_owner_rel
        drop constraint FK6C974367F9981184;

    alter table project_owner_rel
        drop constraint FK6C97436787217ED0;

    alter table projectscriptbean
        drop constraint FK921C86D487217ED0;

    alter table userbean
        drop constraint FKF02421FB7F1A8DA5;

    alter table userpreferencesbean
        drop constraint FK72315B7DF9981184;

    alter table versionbean
        drop constraint FK2919048887217ED0;

    drop table componentbean;

    drop table configurationbean;

    drop table customfieldbean;

    drop table customfieldvaluebean;

    drop table issue_component_rel;

    drop table issue_version_rel;

    drop table issueactivitybean;

    drop table issueattachmentbean;

    drop table issuebean;

    drop table issuefieldbean;

    drop table issuehistorybean;

    drop table issuerelationbean;

    drop table languagebean;

    drop table notificationbean;

    drop table permissionbean;

    drop table project_field_rel;

    drop table project_owner_rel;

    drop table projectbean;

    drop table projectscriptbean;

    drop table reportbean;

    drop table scheduledtaskbean;

    drop table userbean;

    drop table userpreferencesbean;

    drop table versionbean;

    drop table workflowscriptbean;

    drop generator hibernate_sequence;

    create table componentbean (
        id integer not null,
        create_date timestamp not null,
        last_modified timestamp not null,
        name varchar(255) not null,
        description varchar(255) not null,
        project_id integer,
        primary key (id)
    );

    create table configurationbean (
        id integer not null,
        item_type integer not null,
        item_order integer not null,
        item_value varchar(255) not null,
        item_version varchar(255) not null,
        create_date timestamp not null,
        last_modified timestamp not null,
        primary key (id)
    );

    create table customfieldbean (
        id integer not null,
        create_date timestamp not null,
        last_modified timestamp not null,
        field_type integer not null,
        date_format varchar(255) not null,
        is_required integer not null,
        sort_options_by_name integer not null,
        primary key (id)
    );

    create table customfieldvaluebean (
        id integer not null,
        create_date timestamp not null,
        last_modified timestamp not null,
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
        id integer not null,
        activity_type integer not null,
        description varchar(255) not null,
        notification_sent integer not null,
        create_date timestamp not null,
        last_modified timestamp not null,
        issue_id integer,
        user_id integer,
        primary key (id)
    );

    create table issueattachmentbean (
        id integer not null,
        create_date timestamp not null,
        last_modified timestamp not null,
        description varchar(255) not null,
        orig_file_name varchar(255) not null,
        attachment_type varchar(255) not null,
        file_data blob not null,
        file_name varchar(255) not null,
        file_size numeric(18,0) not null,
        issue_id integer,
        user_id integer,
        primary key (id)
    );

    create table issuebean (
        id integer not null,
        create_date timestamp not null,
        last_modified timestamp not null,
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
        id integer not null,
        string_value varchar(255) not null,
        int_value integer not null,
        date_value timestamp not null,
        issue_id integer,
        field_id integer,
        primary key (id)
    );

    create table issuehistorybean (
        id integer not null,
        create_date timestamp not null,
        last_modified timestamp not null,
        description varchar(255) not null,
        status integer not null,
        issue_id integer,
        user_id integer,
        primary key (id)
    );

    create table issuerelationbean (
        id integer not null,
        create_date timestamp not null,
        last_modified timestamp not null,
        matching_relation_id integer not null,
        relation_type integer not null,
        issue_id integer,
        rel_issue_id integer,
        primary key (id)
    );

    create table languagebean (
        id integer not null,
        create_date timestamp not null,
        last_modified timestamp not null,
        locale varchar(255) not null,
        resource_key varchar(255) not null,
        resource_value varchar(512) not null,
        primary key (id)
    );

    create table notificationbean (
        id integer not null,
        create_date timestamp not null,
        last_modified timestamp not null,
        user_role integer not null,
        user_id integer,
        issue_id integer,
        primary key (id)
    );

    create table permissionbean (
        id integer not null,
        create_date timestamp not null,
        last_modified timestamp not null,
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
        id integer not null,
        create_date timestamp not null,
        last_modified timestamp not null,
        name varchar(255) not null,
        description varchar(255) not null,
        status integer not null,
        options integer not null,
        primary key (id)
    );

    create table projectscriptbean (
        id integer not null,
        create_date timestamp not null,
        last_modified timestamp not null,
        field_id integer not null,
        script_priority integer not null,
        project_id integer,
        primary key (id)
    );

    create table reportbean (
        id integer not null,
        name varchar(255) not null,
        name_key varchar(255) not null,
        description varchar(255) not null,
        data_type integer not null,
        report_type integer not null,
        file_data blob,
        class_name varchar(255),
        create_date timestamp not null,
        last_modified timestamp not null,
        primary key (id)
    );

    create table scheduledtaskbean (
        id integer not null,
        hours varchar(255) not null,
        minutes varchar(255) not null,
        days_of_month varchar(255) not null,
        months varchar(255) not null,
        weekdays varchar(255) not null,
        class_name varchar(255) not null,
        args varchar(255) not null,
        create_date timestamp not null,
        last_modified timestamp not null,
        primary key (id)
    );

    create table userbean (
        id integer not null,
        create_date timestamp not null,
        last_modified timestamp not null,
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
        id integer not null,
        create_date timestamp not null,
        last_modified timestamp not null,
        save_login smallint not null,
        user_locale varchar(255) not null,
        num_items_index integer not null,
        num_items_issue_list integer not null,
        show_closed smallint not null,
        sort_column varchar(255) not null,
        hidden_index_sections integer not null,
        remember_last_search smallint not null,
        use_text_actions smallint not null,
        user_id integer,
        primary key (id)
    );

    create table versionbean (
        id integer not null,
        create_date timestamp not null,
        last_modified timestamp not null,
        version_number varchar(255) not null,
        major integer not null,
        minor integer not null,
        description varchar(255) not null,
        project_id integer,
        primary key (id)
    );

    create table workflowscriptbean (
        id integer not null,
        create_date timestamp not null,
        last_modified timestamp not null,
        script_name varchar(255) not null,
        script_data varchar(255) not null,
        event_type integer not null,
        primary key (id)
    );

    alter table componentbean
        add constraint FK65BC6A2D87217ED0
        foreign key (project_id)
        references projectbean;

    alter table customfieldvaluebean
        add constraint FK68618E989794044D
        foreign key (custom_field_id)
        references customfieldbean;

    alter table issue_component_rel
        add constraint FKA6FDC971BEEA70D0
        foreign key (component_id)
        references componentbean;

    alter table issue_component_rel
        add constraint FKA6FDC9716B4AA850
        foreign key (issue_id)
        references issuebean;

    alter table issue_version_rel
        add constraint FK1FD6624C35ADBAF0
        foreign key (version_id)
        references versionbean;

    alter table issue_version_rel
        add constraint FK1FD6624C6B4AA850
        foreign key (issue_id)
        references issuebean;

    alter table issueactivitybean
        add constraint FK7CEE1298F9981184
        foreign key (user_id)
        references userbean;

    alter table issueactivitybean
        add constraint FK7CEE12986B4AA850
        foreign key (issue_id)
        references issuebean;

    alter table issueattachmentbean
        add constraint FK8FB2DC6CF9981184
        foreign key (user_id)
        references userbean;

    alter table issueattachmentbean
        add constraint FK8FB2DC6C6B4AA850
        foreign key (issue_id)
        references issuebean;

    alter table issuebean
        add constraint FKB09A809657EC19C
        foreign key (owner_id)
        references userbean;

    alter table issuebean
        add constraint FKB09A80954941583
        foreign key (creator_id)
        references userbean;

    alter table issuebean
        add constraint FKB09A80987217ED0
        foreign key (project_id)
        references projectbean;

    alter table issuebean
        add constraint FKB09A809F5EBAA5E
        foreign key (target_version_id)
        references versionbean;

    alter table issuefieldbean
        add constraint FKF68FE2F1642E0F3F
        foreign key (field_id)
        references customfieldbean;

    alter table issuefieldbean
        add constraint FKF68FE2F16B4AA850
        foreign key (issue_id)
        references issuebean;

    alter table issuehistorybean
        add constraint FK790013CBF9981184
        foreign key (user_id)
        references userbean;

    alter table issuehistorybean
        add constraint FK790013CB6B4AA850
        foreign key (issue_id)
        references issuebean;

    alter table issuerelationbean
        add constraint FKC2EDB05AB01BD36
        foreign key (rel_issue_id)
        references issuebean;

    alter table issuerelationbean
        add constraint FKC2EDB056B4AA850
        foreign key (issue_id)
        references issuebean;

    alter table notificationbean
        add constraint FK60142F1BF9981184
        foreign key (user_id)
        references userbean;

    alter table notificationbean
        add constraint FK60142F1B6B4AA850
        foreign key (issue_id)
        references issuebean;

    alter table permissionbean
        add constraint FKAA4E59FFF9981184
        foreign key (user_id)
        references userbean;

    alter table permissionbean
        add constraint FKAA4E59FF87217ED0
        foreign key (project_id)
        references projectbean;

    alter table project_field_rel
        add constraint FKB6F3762E642E0F3F
        foreign key (field_id)
        references customfieldbean;

    alter table project_field_rel
        add constraint FKB6F3762E87217ED0
        foreign key (project_id)
        references projectbean;

    alter table project_owner_rel
        add constraint FK6C974367F9981184
        foreign key (user_id)
        references userbean;

    alter table project_owner_rel
        add constraint FK6C97436787217ED0
        foreign key (project_id)
        references projectbean;

    alter table projectscriptbean
        add constraint FK921C86D487217ED0
        foreign key (project_id)
        references projectbean;

    alter table userbean
        add constraint FKF02421FB7F1A8DA5
        foreign key (preferences_id)
        references userpreferencesbean;

    alter table userpreferencesbean
        add constraint FK72315B7DF9981184
        foreign key (user_id)
        references userbean;

    alter table versionbean
        add constraint FK2919048887217ED0
        foreign key (project_id)
        references projectbean;

    create generator hibernate_sequence;
