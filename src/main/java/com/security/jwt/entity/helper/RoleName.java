package com.security.jwt.entity.helper;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public enum RoleName {

    /**
     * admin rights
     * */
    ADMIN("ADMIN"),

    /**
     * admin for the company
     */
    COMPANY_ADMIN("COMPANY_ADMIN");

    private final String humanReadable;

    private RoleName(
            String humanReadable) {
        this.humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return humanReadable;
    }

    public static boolean roleExists(String value) {
        for(RoleName roleName : values()) {
            if (value.equals(roleName.getHumanReadable())) {
                return true;
            }
        }
        return false;
    }

    public String getRoleName() {
        // Spring security expects all roles to have the ROLE_ prefix
        return "ROLE_" + this.name();
    }

    /**
     * Converts a set of privileges to an integer value, which contains one bit for each enabled
     * privilege
     *
     * @param authorities to be converted
     * @return an integer with one 1 flag for each unique authority granted
     */
    public static int convertToInt(Collection<RoleName> authorities) {
        int result = 0;
        for (RoleName authority : authorities) {
            result |= (1 << authority.ordinal());
        }
        return result;
    }

    public static Set<RoleName> convertToAuthorities(int rights) {
        final Set<RoleName> result = EnumSet.noneOf(RoleName.class);
        for (RoleName authority : RoleName.values()) {
            if (((rights >> authority.ordinal()) & 0x01) > 0) {
                result.add(authority);
            }
        }
        return result;
    }
}