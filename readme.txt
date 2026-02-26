Perfect 👍
Now we move to actual implementation mode.

You currently have:

✅ Role automation

✅ Menu automation

✅ API automation

🔄 Now adding UI automation (CRUD based, hierarchical, screenSection optional)

Below is complete production-ready UI automation code that fits into your current project (without unified AuthNode refactor yet).

📦 FILES YOU NEED TO ADD

You will add:

model/
    UiNode.java

excel/
    UiExcelReader.java

alfa/
    UiAlfaGenerator.java

util/
    PermissionUtil.java

And update:

Main.java
1️⃣ UiNode.java (Hierarchical Tree Model)

📄 model/UiNode.java

package com.abc.axiomatics.generator.model;

import java.util.*;

public class UiNode {

    private final String name;
    private final String attribute;

    private final Map<String, UiNode> children =
            new LinkedHashMap<>();

    // action -> roles
    private final Map<String, List<String>> actionRoles =
            new LinkedHashMap<>();

    public UiNode(String name, String attribute) {
        this.name = name;
        this.attribute = attribute;
    }

    public UiNode getOrCreateChild(String name, String attribute) {
        return children.computeIfAbsent(
                name,
                k -> new UiNode(name, attribute)
        );
    }

    public void addRole(String action, String role) {
        actionRoles
            .computeIfAbsent(action,
                k -> new ArrayList<>())
            .add(role);
    }

    public Map<String, UiNode> getChildren() {
        return children;
    }

    public Map<String, List<String>> getActionRoles() {
        return actionRoles;
    }

    public String getName() {
        return name;
    }

    public String getAttribute() {
        return attribute;
    }
}
2️⃣ PermissionUtil.java (CRUD Parser)

📄 util/PermissionUtil.java

package com.abc.axiomatics.generator.util;

import java.util.HashSet;
import java.util.Set;

public class PermissionUtil {

    public static Set<String> parseActions(String value) {

        Set<String> actions = new HashSet<>();

        if (value == null)
            return actions;

        value = value.trim().toUpperCase();

        if (value.isEmpty() || "NA".equals(value))
            return actions;

        String[] tokens = value.split("/");

        for (String token : tokens) {

            switch (token.trim()) {

                case "C":
                    actions.add("Create");
                    break;

                case "R":
                    actions.add("Read");
                    break;

                case "U":
                    actions.add("Update");
                    break;

                case "D":
                    actions.add("Delete");
                    break;

                default:
                    throw new RuntimeException(
                        "Invalid permission value: " + value +
                        ". Allowed: C/R/U/D combinations or NA"
                    );
            }
        }

        return actions;
    }
}
3️⃣ UiExcelReader.java

📄 excel/UiExcelReader.java

package com.abc.axiomatics.generator.excel;

import java.io.FileInputStream;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.abc.axiomatics.generator.model.*;
import com.abc.axiomatics.generator.util.PermissionUtil;

public class UiExcelReader {

    private static final DataFormatter FORMATTER =
            new DataFormatter();

    public static UiNode readUiMappings(
            String excelPath,
            List<Role> validRoles) throws Exception {

        UiNode root =
                new UiNode("uiFeature", null);

        Set<String> validRoleNames =
                new HashSet<>();

        for (Role r : validRoles) {
            validRoleNames.add(r.getRoleName());
        }

        try (FileInputStream fis =
                     new FileInputStream(excelPath);
             Workbook workbook =
                     new XSSFWorkbook(fis)) {

            Sheet sheet =
                    workbook.getSheet(
                        "UI & Feature Mapping");

            if (sheet == null) {
                throw new RuntimeException(
                        "UI & Feature Mapping sheet not found.");
            }

            Row subsystemRow = sheet.getRow(1);
            Row componentRow = sheet.getRow(2);
            Row featureRow   = sheet.getRow(3);
            Row sectionRow   = sheet.getRow(4);

            int startCol = 3; // Column D
            int lastCol =
                    subsystemRow.getLastCellNum();

            Map<Integer, String[]> columnMap =
                    new LinkedHashMap<>();

            // Build column definition map
            for (int col = startCol;
                 col < lastCol;
                 col++) {

                String ss =
                    FORMATTER.formatCellValue(
                        subsystemRow.getCell(col)).trim();

                String comp =
                    FORMATTER.formatCellValue(
                        componentRow.getCell(col)).trim();

                String feat =
                    FORMATTER.formatCellValue(
                        featureRow.getCell(col)).trim();

                String sec =
                    FORMATTER.formatCellValue(
                        sectionRow.getCell(col)).trim();

                if (ss.isEmpty()
                        && comp.isEmpty()
                        && feat.isEmpty()) {
                    break;
                }

                columnMap.put(col,
                        new String[]{ss, comp, feat, sec});
            }

            int roleStartRow = 6; // Row 7
            int roleCol = 1;      // Column B

            for (int rowIndex = roleStartRow;
                 rowIndex <= sheet.getLastRowNum();
                 rowIndex++) {

                Row row = sheet.getRow(rowIndex);
                if (row == null) break;

                if (isStrikethrough(
                        row, roleCol, workbook))
                    continue;

                String rawRole =
                    FORMATTER.formatCellValue(
                        row.getCell(roleCol)).trim();

                if (rawRole.isEmpty())
                    break;

                String role =
                        sanitize(rawRole);

                if (!validRoleNames.contains(role)) {
                    throw new RuntimeException(
                            "Unknown role in UI sheet: "
                            + role);
                }

                for (Map.Entry<Integer,String[]> e
                        : columnMap.entrySet()) {

                    String value =
                        FORMATTER.formatCellValue(
                            row.getCell(e.getKey()))
                            .trim();

                    Set<String> actions =
                        PermissionUtil.parseActions(value);

                    if (actions.isEmpty())
                        continue;

                    String[] path = e.getValue();

                    UiNode ssNode =
                        root.getOrCreateChild(
                            path[0],
                            "com.abc.subSystem");

                    UiNode compNode =
                        ssNode.getOrCreateChild(
                            path[1],
                            "com.abc.component");

                    UiNode featNode =
                        compNode.getOrCreateChild(
                            path[2],
                            "com.abc.feature");

                    UiNode finalNode;

                    if (path[3] == null
                            || path[3].isEmpty()) {

                        finalNode = featNode;
                    } else {
                        finalNode =
                            featNode.getOrCreateChild(
                                path[3],
                                "com.abc.screenSection");
                    }

                    for (String action : actions) {
                        finalNode.addRole(action, role);
                    }
                }
            }
        }

        return root;
    }

    private static boolean isStrikethrough(
            Row row,
            int colIndex,
            Workbook workbook) {

        Cell cell = row.getCell(colIndex);
        if (cell == null) return false;

        Font font =
            workbook.getFontAt(
                cell.getCellStyle()
                    .getFontIndex());

        return font != null
                && font.getStrikeout();
    }

    private static String sanitize(String value) {
        return value
                .replaceAll("\\s+", "")
                .replaceAll(
                    "[^a-zA-Z0-9]", "");
    }
}
4️⃣ UiAlfaGenerator.java

📄 alfa/UiAlfaGenerator.java

package com.abc.axiomatics.generator.alfa;

import com.abc.axiomatics.generator.model.UiNode;

import java.util.List;
import java.util.Map;

public class UiAlfaGenerator {

    public static String generate(UiNode root) {

        StringBuilder sb = new StringBuilder();

        sb.append("// AUTO-GENERATED — DO NOT EDIT\n");
        sb.append("namespace com.abc.A {\n\n");

        sb.append("policyset uiFeature {\n");
        sb.append(" apply firstApplicable\n\n");

        for (UiNode child :
                root.getChildren().values()) {
            build(child, sb, 1);
        }

        sb.append("}\n\n");
        sb.append("}\n");

        return sb.toString();
    }

    private static void build(
            UiNode node,
            StringBuilder sb,
            int indent) {

        String tab = repeat("  ", indent);

        sb.append(tab)
          .append("policyset ")
          .append(node.getName())
          .append(" {\n");

        if (node.getAttribute() != null) {
            sb.append(tab)
              .append(" target clause ")
              .append(node.getAttribute())
              .append(" == \"")
              .append(node.getName())
              .append("\"\n");
        }

        if (node.getChildren().isEmpty()) {

            sb.append(tab)
              .append(" apply firstApplicable\n\n");

            for (Map.Entry<String,
                    List<String>> entry :
                    node.getActionRoles()
                        .entrySet()) {

                sb.append(tab)
                  .append(" policy ")
                  .append(entry.getKey())
                  .append(" {\n");

                sb.append(tab)
                  .append("  target clause ")
                  .append("com.abc.action == \"")
                  .append(entry.getKey())
                  .append("\"\n");

                sb.append(tab)
                  .append("  apply denyUnlessPermit\n");

                for (String role :
                        entry.getValue()) {

                    sb.append(tab)
                      .append("  role.")
                      .append(role)
                      .append("Invoke\n");
                }

                sb.append(tab).append(" }\n\n");
            }

        } else {

            sb.append(tab)
              .append(" apply firstApplicable\n\n");

            for (UiNode child :
                    node.getChildren().values()) {
                build(child, sb, indent + 1);
            }
        }

        sb.append(tab).append("}\n\n");
    }

    private static String repeat(
            String s,
            int count) {

        StringBuilder sb =
                new StringBuilder();

        for (int i = 0;
             i < count;
             i++) {
            sb.append(s);
        }

        return sb.toString();
    }
}
5️⃣ Update Main.java

Add UI execution:

UiNode uiRoot =
    UiExcelReader.readUiMappings(
        excelPath, roles);

String uiAlfa =
    UiAlfaGenerator.generate(uiRoot);

FileWriterUtil.write(
    outputDir,
    "uiFeature.alfa",
    uiAlfa);
✅ After Adding This

Your project will generate:

role.alfa
menu.alfa
api.alfa
uiFeature.alfa
🚀 What You Should Test

CRUD combinations (C/R/U)

NA cell

Blank screenSection

Strike-through role

Unknown role error

If anything throws unexpected behavior tonight, send:

UI sheet layout

Error stack trace

Generated ALFA snippet

We’ll debug precisely.
