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
