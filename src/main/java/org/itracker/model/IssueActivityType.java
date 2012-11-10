/**
 *
 */
package org.itracker.model;

public enum IssueActivityType implements IntCodeEnum<IssueActivityType> {

    ISSUE_CREATED(1),

    STATUS_CHANGE(2),

    OWNER_CHANGE(3),

    SEVERITY_CHANGE(4),

    COMPONENTS_MODIFIED(5),

    VERSIONS_MODIFIED(6),

    REMOVE_HISTORY(7),

    ISSUE_MOVE(8),

    SYSTEM_UPDATE(9),

    TARGETVERSION_CHANGE(10),

    DESCRIPTION_CHANGE(11),

    RESOLUTION_CHANGE(12),

    RELATION_ADDED(13),

    RELATION_REMOVED(14);

    final Integer code;

    private IssueActivityType(Integer code) {
        this.code = code;
    }

    /**
     * @deprecated
     */
    public static final IssueActivityType forCode(Integer type) {
        switch (type) {
            case 1:
                return ISSUE_CREATED;
            case 2:
                return STATUS_CHANGE;
            case 3:
                return OWNER_CHANGE;
            case 4:
                return SEVERITY_CHANGE;
            case 5:
                return COMPONENTS_MODIFIED;
            case 6:
                return VERSIONS_MODIFIED;
            case 7:
                return REMOVE_HISTORY;
            case 8:
                return ISSUE_MOVE;
            case 9:
                return SYSTEM_UPDATE;
            case 10:
                return TARGETVERSION_CHANGE;
            case 11:
                return DESCRIPTION_CHANGE;
            case 12:
                return RESOLUTION_CHANGE;
            case 13:
                return RELATION_ADDED;
            case 14:
                return RELATION_REMOVED;
        }
        return null;
    }

    public IssueActivityType fromCode(Integer code) {
        switch (code) {
            case 1:
                return ISSUE_CREATED;
            case 2:
                return STATUS_CHANGE;
            case 3:
                return OWNER_CHANGE;
            case 4:
                return SEVERITY_CHANGE;
            case 5:
                return COMPONENTS_MODIFIED;
            case 6:
                return VERSIONS_MODIFIED;
            case 7:
                return REMOVE_HISTORY;
            case 8:
                return ISSUE_MOVE;
            case 9:
                return SYSTEM_UPDATE;
            case 10:
                return TARGETVERSION_CHANGE;
            case 11:
                return DESCRIPTION_CHANGE;
            case 12:
                return RESOLUTION_CHANGE;
            case 13:
                return RELATION_ADDED;
            case 14:
                return RELATION_REMOVED;
        }
        return null;
    }

    public Integer getCode() {
        return this.code;
    }
}