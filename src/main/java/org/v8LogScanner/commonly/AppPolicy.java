package org.v8LogScanner.commonly;

import java.security.*;

public class AppPolicy extends Policy {
    @Override
    public PermissionCollection getPermissions(CodeSource codesource) {
        Permissions p = new Permissions();
        p.add(new AllPermission());
        return p;
    }

    public static void addAdministratorRights() {
        Policy.setPolicy(new AppPolicy());
    }
}
