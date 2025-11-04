package com.databricks.sample.app;

import java.util.*;
import org.apache.iceberg.Table;
import org.apache.iceberg.catalog.TableIdentifier;
import org.apache.iceberg.rest.RESTCatalog;
import org.apache.iceberg.*;
import org.apache.iceberg.catalog.Namespace;
import org.apache.iceberg.types.Types;
import org.apache.iceberg.exceptions.NoSuchTableException;

public class IcebergRestAPI {

    public static void main(String[] args) {
        // Replace with your Databricks workspace URL and PAT token for the service principal
        String workspaceUrl = "<workspaceurl>";
        String patToken = "<pat_token>";

        // Unity Catalog specific configuration
        String ucCatalogName = "<catalog_name>";
        String ucSchemaName = "<schema_name>";
        String tableName = "<table_name>";

        // Configure the REST catalog properties
        Map<String, String> properties = new HashMap<>();

        // Configure properties for the Iceberg RESTCatalog
        properties.put("uri", String.format("https://%s/api/2.1/unity-catalog/iceberg-rest/", workspaceUrl));
        properties.put("oauth2-server-uri", String.format("https://%s/oidc/v1/token", workspaceUrl));
        properties.put("type", "iceberg");
        properties.put("catalog-type", "rest");
        properties.put("token", patToken);
        properties.put("warehouse", ucCatalogName); // warehouse is often optional with UC REST catalog
        properties.put("scope", "all-apis"); // Scope should be defined for the service principal.

        // Initialize the REST catalog
        RESTCatalog catalog = new RESTCatalog();
        catalog.initialize("databricks_uc_catalog", properties);

        TableIdentifier tableIdentifier = TableIdentifier.of(Namespace.of(ucSchemaName), tableName);

        Schema schema = new Schema(
                Types.NestedField.required(1, "id", Types.LongType.get()),
                Types.NestedField.required(2, "name", Types.StringType.get())
        );
        PartitionSpec spec = PartitionSpec.unpartitioned();

        // Perform table operations
        try {
            catalog.dropTable(tableIdentifier, true); // Drop if exists
            System.out.println("Existing table dropped.");
        } catch (NoSuchTableException e) {
            System.out.println("No existing table to drop. Proceeding with create.");
        }

        System.out.println("\nCreating table: " + tableIdentifier);
        Table table = catalog.createTable(tableIdentifier, schema, spec);
        System.out.println("Table created: " + table.location());

        // List tables in the namespace
        System.out.println("\nListing tables in schema " + ucSchemaName + ":");
        List<TableIdentifier> tables = catalog.listTables(Namespace.of(ucSchemaName));
        // *** Replaced lambda with an enhanced for loop ***
        for (TableIdentifier tbl : tables) {
            System.out.println(" - " + tbl);
        }
    }
}
