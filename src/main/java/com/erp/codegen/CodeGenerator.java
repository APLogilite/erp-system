package com.erp.codegen;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple code generator to create basic DTO and Service classes for entities.
 * Run this as a main method to generate code for all entities.
 */
public class CodeGenerator {

    public static void main(String[] args) throws Exception {
        // List of entity classes to generate for
        List<Class<? extends BaseEntity>> entities = Arrays.asList(
            com.erp.modules.product.entity.Product.class,
            com.erp.modules.inventory.entity.Warehouse.class,
            com.erp.modules.order.entity.Order.class,
            com.erp.modules.order.entity.OrderLine.class,
            com.erp.modules.inventory.entity.StockMovement.class
        );

        for (Class<? extends BaseEntity> entityClass : entities) {
            generateDto(entityClass);
            generateService(entityClass);
        }

        System.out.println("Code generation completed.");
    }

    private static void generateDto(Class<? extends BaseEntity> entityClass) throws IOException {
        String entityName = entityClass.getSimpleName();
        String dtoName = entityName + "Dto";
        String packageName = entityClass.getPackageName().replace(".entity", ".dto");

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("import java.time.LocalDateTime;\n");
        sb.append("import java.util.UUID;\n\n");
        sb.append("public class ").append(dtoName).append(" {\n\n");

        // Add fields
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (!field.getName().equals("id") && !field.getName().startsWith("$")) {
                sb.append("    private ").append(field.getType().getSimpleName()).append(" ").append(field.getName()).append(";\n");
            }
        }

        // Add getters and setters
        for (Field field : fields) {
            if (!field.getName().equals("id") && !field.getName().startsWith("$")) {
                String fieldName = field.getName();
                String type = field.getType().getSimpleName();
                String capitalized = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                sb.append("\n    public ").append(type).append(" get").append(capitalized).append("() {\n");
                sb.append("        return ").append(fieldName).append(";\n");
                sb.append("    }\n\n");

                sb.append("    public void set").append(capitalized).append("(").append(type).append(" ").append(fieldName).append(") {\n");
                sb.append("        this.").append(fieldName).append(" = ").append(fieldName).append(";\n");
                sb.append("    }\n");
            }
        }

        sb.append("}\n");

        // Write to file
        String path = "src/main/java/" + packageName.replace(".", "/") + "/" + dtoName + ".java";
        writeToFile(path, sb.toString());
    }

    private static void generateService(Class<? extends BaseEntity> entityClass) throws IOException {
        String entityName = entityClass.getSimpleName();
        String serviceName = entityName + "Service";
        String packageName = entityClass.getPackageName().replace(".entity", ".service");
        String repoName = entityName + "Repository";
        String repoPackage = entityClass.getPackageName().replace(".entity", ".repository");

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("import ").append(entityClass.getName()).append(";\n");
        sb.append("import ").append(repoPackage).append(".").append(repoName).append(";\n");
        sb.append("import com.erp.common.base.BaseService;\n");
        sb.append("import org.springframework.data.jpa.repository.JpaRepository;\n");
        sb.append("import org.springframework.stereotype.Service;\n");
        sb.append("import java.util.UUID;\n\n");
        sb.append("@Service\n");
        sb.append("public class ").append(serviceName).append(" extends BaseService<").append(entityName).append("> {\n\n");
        sb.append("    private final ").append(repoName).append(" ").append(repoName.substring(0, 1).toLowerCase() + repoName.substring(1)).append(";\n\n");
        sb.append("    public ").append(serviceName).append("(").append(repoName).append(" ").append(repoName.substring(0, 1).toLowerCase() + repoName.substring(1)).append(") {\n");
        sb.append("        this.").append(repoName.substring(0, 1).toLowerCase() + repoName.substring(1)).append(" = ").append(repoName.substring(0, 1).toLowerCase() + repoName.substring(1)).append(";\n");
        sb.append("    }\n\n");
        sb.append("    @Override\n");
        sb.append("    protected JpaRepository<").append(entityName).append(", UUID> getRepository() {\n");
        sb.append("        return ").append(repoName.substring(0, 1).toLowerCase() + repoName.substring(1)).append(";\n");
        sb.append("    }\n\n");
        sb.append("}\n");

        // Write to file
        String path = "src/main/java/" + packageName.replace(".", "/") + "/" + serviceName + ".java";
        writeToFile(path, sb.toString());
    }

    private static void writeToFile(String path, String content) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        System.out.println("Generated: " + path);
    }
}