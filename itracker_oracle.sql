
    drop table componentbean cascade constraints;

    drop table configurationbean cascade constraints;

    drop table customfieldbean cascade constraints;

    drop table customfieldvaluebean cascade constraints;

    drop table issue_component_rel cascade constraints;

    drop table issue_version_rel cascade constraints;

    drop table issueactivitybean cascade constraints;

    drop table issueattachmentbean cascade constraints;

    drop table issuebean cascade constraints;

    drop table issuefieldbean cascade constraints;

    drop table issuehistorybean cascade constraints;

    drop table issuerelationbean cascade constraints;

    drop table languagebean cascade constraints;

    drop table notificationbean cascade constraints;

    drop table permissionbean cascade constraints;

    drop table project_field_rel cascade constraints;

    drop table project_owner_rel cascade constraints;

    drop table projectbean cascade constraints;

    drop table projectscriptbean cascade constraints;

    drop table reportbean cascade constraints;

    drop table scheduledtaskbean cascade constraints;

    drop table userbean cascade constraints;

    drop table userpreferencesbean cascade constraints;

    drop table versionbean cascade constraints;

    drop table workflowscriptbean cascade constraints;

    drop sequence hibernate_sequence;

    create table componentbean (
        id number(10,0) not null,
        create_date date not null,
        last_modified date not null,
        name varchar2(255) not null,
        description varchar2(255) not null,
        project_id number(10,0),
        primary key (id)
    );

    create table configurationbean (
        id number(10,0) not null,
        item_type number(10,0) not null,
        item_order number(10,0) not null,
        item_value varchar2(255) not null,
        item_version varchar2(255) not null,
        create_date date not null,
        last_modified date not null,
        primary key (id)
    );

    create table customfieldbean (
        id number(10,0) not null,
        create_date date not null,
        last_modified date not null,
        field_type number(10,0) not null,
        date_format varchar2(255) not null,
        is_required number(10,0) not null,
        sort_options_by_name number(10,0) not null,
        primary key (id)
    );

    create table customfieldvaluebean (
        id number(10,0) not null,
        create_date date not null,
        last_modified date not null,
        custom_field_id number(10,0),
        option_value varchar2(255) not null,
        sort_order number(10,0) not null,
        primary key (id)
    );

    create table issue_component_rel (
        component_id number(10,0) not null,
        issue_id number(10,0) not null
    );

    create table issue_version_rel (
        issue_id number(10,0) not null,
        version_id number(10,0) not null
    );

    create table issueactivitybean (
        id number(10,0) not null,
        activity_type number(10,0) not null,
        description varchar2(255) not null,
        notification_sent number(10,0) not null,
        create_date date not null,
        last_modified date not null,
        issue_id number(10,0),
        user_id number(10,0),
        primary key (id)
    );

    create table issueattachmentbean (
        id number(10,0) not null,
        create_date date not null,
        last_modified date not null,
        description varchar2(255) not null,
        orig_file_name varchar2(255) not null,
        attachment_type varchar2(255) not null,
        file_data long raw not null,
        file_name varchar2(255) not null,
        file_size number(19,0) not null,
        issue_id number(10,0),
        user_id number(10,0),
        primary key (id)
    );

    create table issuebean (
        id number(10,0) not null,
        create_date date not null,
        last_modified date not null,
        description varchar2(255) not null,
        severity number(10,0) not null,
        status number(10,0) not null,
        resolution varchar2(255) not null,
        project_id number(10,0),
        creator_id number(10,0),
        owner_id number(10,0),
        target_version_id number(10,0),
        primary key (id)
    );

    create table issuefieldbean (
        id number(10,0) not null,
        string_value varchar2(255) not null,
        int_value number(10,0) not null,
        date_value date not null,
        issue_id number(10,0),
        field_id number(10,0),
        primary key (id)
    );

    create table issuehistorybean (
        id number(10,0) not null,
        create_date date not null,
        last_modified date not null,
        description varchar2(255) not null,
        status number(10,0) not null,
        issue_id number(10,0),
        user_id number(10,0),
        primary key (id)
    );

    create table issuerelationbean (
        id number(10,0) not null,
        create_date date not null,
        last_modified date not null,
        matching_relation_id number(10,0) not null,
        relation_type number(10,0) not null,
        issue_id number(10,0),
        rel_issue_id number(10,0),
        primary key (id)
    );

    create table languagebean (
        id number(10,0) not null,
        create_date date not null,
        last_modified date not null,
        locale varchar2(255) not null,
        resource_key varchar2(255) not null,
        resource_value varchar2(512) not null,
        primary key (id)
    );

    create table notificationbean (
        id number(10,0) not null,
        create_date date not null,
        last_modified date not null,
        user_role number(10,0) not null,
        user_id number(10,0),
        issue_id number(10,0),
        primary key (id)
    );

    create table permissionbean (
        id number(10,0) not null,
        create_date date not null,
        last_modified date not null,
        permission_type number(10,0) not null,
        user_id number(10,0),
        project_id number(10,0),
        primary key (id)
    );

    create table project_field_rel (
        field_id number(10,0) not null,
        project_id number(10,0) not null
    );

    create table project_owner_rel (
        project_id number(10,0) not null,
        user_id number(10,0) not null
    );

    create table projectbean (
        id number(10,0) not null,
        create_date date not null,
        last_modified date not null,
        name varchar2(255) not null,
        description varchar2(255) not null,
        status number(10,0) not null,
        options number(10,0) not null,
        primary key (id)
    );

    create table projectscriptbean (
        id number(10,0) not null,
        create_date date not null,
        last_modified date not null,
        field_id number(10,0) not null,
        script_priority number(10,0) not null,
        project_id number(10,0),
        primary key (id)
    );

    create table reportbean (
        id number(10,0) not null,
        name varchar2(255) not null,
        name_key varchar2(255) not null,
        description varchar2(255) not null,
        data_type number(10,0) not null,
        report_type number(10,0) not null,
        file_data raw(1014),
        class_name varchar2(255),
        create_date date not null,
        last_modified date not null,
        primary key (id)
    );

    create table scheduledtaskbean (
        id number(10,0) not null,
        hours varchar2(255) not null,
        minutes varchar2(255) not null,
        days_of_month varchar2(255) not null,
        months varchar2(255) not null,
        weekdays varchar2(255) not null,
        class_name varchar2(255) not null,
        args varchar2(255) not null,
        create_date date not null,
        last_modified date not null,
        primary key (id)
    );

    create table userbean (
        id number(10,0) not null,
        create_date date not null,
        last_modified date not null,
        login varchar2(255) not null,
        user_password varchar2(255) not null,
        first_name varchar2(255) not null,
        last_name varchar2(255) not null,
        email varchar2(255) not null,
        status number(10,0) not null,
        super_user number(10,0) not null,
        registration_type number(10,0) not null,
        preferences_id number(10,0),
        primary key (id)
    );

    create table userpreferencesbean (
        id number(10,0) not null,
        create_date date not null,
        last_modified date not null,
        save_login number(1,0) not null,
        user_locale varchar2(255) not null,
        num_items_index number(10,0) not null,
        num_items_issue_list number(10,0) not null,
        show_closed number(1,0) not null,
        sort_column varchar2(255) not null,
        hidden_index_sections number(10,0) not null,
        remember_last_search number(1,0) not null,
        use_text_actions number(1,0) not null,
        user_id number(10,0),
        primary key (id)
    );

    create table versionbean (
        id number(10,0) not null,
        create_date date not null,
        last_modified date not null,
        version_number varchar2(255) not null,
        major number(10,0) not null,
        minor number(10,0) not null,
        description varchar2(255) not null,
        project_id number(10,0),
        primary key (id)
    );

    create table workflowscriptbean (
        id number(10,0) not null,
        create_date date not null,
        last_modified date not null,
        script_name varchar2(255) not null,
        script_data varchar2(255) not null,
        event_type number(10,0) not null,
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

    create sequence hibernate_sequence;
