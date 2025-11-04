package com.databricks.sample.app;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.types.Row;
import org.apache.hadoop.conf.Configuration; // Note: Use the Hadoop Configuration
import org.apache.iceberg.catalog.TableIdentifier;
import org.apache.iceberg.flink.CatalogLoader;
import org.apache.iceberg.flink.TableLoader;
import org.apache.iceberg.flink.sink.FlinkSink;
import org.apache.iceberg.catalog.Catalog; // Import the Iceberg Catalog interface

import java.util.HashMap;
import java.util.Map;
import java.util.Properties; // java.util.Properties is not used directly, use Map<String, String> instead

public class FlinkIcebergRestCatalogWriter {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(10000); // Checkpointing is required

   
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
        //properties.put("oauth2-server-uri", String.format("https://%s/oidc/v1/token", workspaceUrl));  // to be used when OAuth is used
        properties.put("type", "iceberg");
        properties.put("catalog-type", "rest");
        properties.put("token", patToken); // to be used when OAuth is NOT used
        //properties.put("credential", "<service_principal_client_id>:<service_principal_client_secret>"); // to be used when OAuth is used
        properties.put("warehouse", ucCatalogName); // warehouse is often optional with UC REST catalog
        properties.put("scope", "all-apis"); // Scope should be defined for the service principal.


    
        // The hadoopConf can be empty if the catalog doesn't rely on local Hadoop configs
        org.apache.hadoop.conf.Configuration hadoopConf = new org.apache.hadoop.conf.Configuration();
        CatalogLoader catalogLoader = CatalogLoader.rest(ucCatalogName, hadoopConf, properties);
        
        // Optional: Load the catalog instance to interact with it programmatically (e.g., create a table)
        Catalog catalog = catalogLoader.loadCatalog();

        TableIdentifier tableIdentifier = TableIdentifier.of(ucSchemaName, tableName);
        // Ensure the table exists or create it using 'catalog.createTable(...)'

 
        DataStream<Row> input = env.addSource(new SampleSourceFunction())
                .returns(Types.ROW_INFO);

  
        // Use TableLoader.fromCatalog to allow the sink tasks to load the table using the serialized CatalogLoader
        TableLoader tableLoader = TableLoader.fromCatalog(catalogLoader, tableIdentifier);

        // Map Flink Row to the Iceberg table schema. (Requires a custom mapper or using RowData)
        // For this example, assuming the Row structure matches the Iceberg schema (id: int, data: string)
        
        // Note: Writing DataStream<Row> natively to Iceberg often requires specific utility classes 
        // provided in Iceberg's tests (like SimpleDataUtil.FLINK_SCHEMA) or writing your own mapping logic 
        // to convert Row to RowData, which is the preferred type for performance.

        FlinkSink.forRow(input, null) // 'null' should be replaced with appropriate TypeInfo or Schema 
            .tableLoader(tableLoader)
            .append();

     
        env.execute("Flink Iceberg REST Catalog Write Job");
    }

    // A simple source function to generate some sample data (as in the previous example)
    public static class SampleSourceFunction implements SourceFunction<Row> {
        private volatile boolean isRunning = true;
        private int count = 0;

        @Override
        public void run(SourceContext<Row> ctx) throws Exception {
            while (isRunning && count < 10) {
                Row row = new Row(2);
                row.setField(0, count);
                row.setField(1, "data_" + count);
                ctx.collect(row);
                count++;
                Thread.sleep(1000);
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }
}
