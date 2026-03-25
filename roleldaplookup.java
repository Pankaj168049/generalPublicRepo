package com.abc.axiomatics.generator.util;

import java.io.*;
import java.util.*;

public class RoleLdapLookup {

    public static Map<String, String> load(String filePath)
            throws Exception {

        Map<String, String> map = new HashMap<>();

        try (BufferedReader br =
                     new BufferedReader(new FileReader(filePath))) {

            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {

                if (first) { first = false; continue; }

                String[] parts = line.split(",");

                if (parts.length < 2) continue;

                String role = parts[0].trim();
                String ldap = parts[1].trim();

                map.put(role, ldap);
            }
        }

        return map;
    }
}
-------------------------------------------------------------

    package com.abc.axiomatics.generator.util;

import java.io.*;
import java.util.*;

public class RoleLdapLookup {

    private final Map<String, String> roleMap = new HashMap<>();
    private final String filePath;

    public RoleLdapLookup(String filePath) throws Exception {
        this.filePath = filePath;
        load();
    }

    private void load() throws Exception {

        File file = new File(filePath);

        if (!file.exists()) {
            // create file with header
            try (FileWriter fw = new FileWriter(file)) {
                fw.write("RoleName,LDAP_DN\n");
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {

                if (first) {
                    first = false;
                    continue;
                }

                String[] parts = line.split(",");

                if (parts.length < 1) continue;

                String role = parts[0].trim();
                String ldap = parts.length > 1 ? parts[1].trim() : "";

                roleMap.put(role, ldap);
            }
        }
    }

    public String getLdapOrHandle(String roleName) throws Exception {

        // Case 1: Role not present → add to CSV
        if (!roleMap.containsKey(roleName)) {

            addNewRole(roleName);

            throw new RuntimeException(
                "New role detected: " + roleName +
                ". Added to role-ldap-mapping.csv. Please add LDAP DN."
            );
        }

        String ldap = roleMap.get(roleName);

        // Case 2: LDAP missing
        if (ldap == null || ldap.trim().isEmpty()) {

            throw new RuntimeException(
                "LDAP DN missing for role: " + roleName +
                ". Please update role-ldap-mapping.csv"
            );
        }

        // Case 3: Valid
        return ldap;
    }

    private void addNewRole(String roleName) throws Exception {

        try (FileWriter fw = new FileWriter(filePath, true)) {
            fw.write(roleName + ",\n");
        }
    }
}
