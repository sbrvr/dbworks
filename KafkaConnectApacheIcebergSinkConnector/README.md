# Kafka Connect Apache Iceberg Sink Connector - Databricks Unity Catalog Apache Iceberg REST API 

Kafka Connect offers a sink connector called the Apache Iceberg Sink Connector which can be used to write to Iceberg tables. This sinks supports working with Databricks using Unity Catalog Apache Iceberg REST Catalog API.

The Apache Iceberg sink connector guarantees that each record from Kafka is written to the Iceberg tables exactly once, even during failures or retries. Besides this exactly-once delivery semantics, the Kafka Iceberg connector has a multi-table fan-out capability. This helps you move data from a single Kafka topic to multiple Iceberg tables.

Prerequisities:
- Kafka Cluster 2.5 or higher
- Apache Iceberg Sink Connector is installed on Kafka Cluster under Plugins directory and then plugin.path property is set in Kafka Connect worker configuration properties file to point to the plugins directory. Make sure to use HMS supported Iceberg Sink Connector.
- Access (Roles/Permissions) to Object Store (s3/GCS/ADLS) is configured for Kafka cluster to write data

Step 1: Create Iceberg Sink Connector Configuration JSON File
This JSON file will store the Databricks UC information and Kafka Topics 

Databricks AWS -

{
"name": "events-sink",
"config": {
    "connector.class": "org.apache.iceberg.connect.IcebergSinkConnector",
    "tasks.max": "2",
    "topics": "<topic_name>",
    "iceberg.tables": "<uc_schema.tablename>",
    "iceberg.catalog.token": "<service>",
    "iceberg.catalog.type": "rest",
    "iceberg.catalog.uri": "<databricks_workspace_url>/api/2.1/unity-catalog/iceberg-rest",
    "iceberg.catalog.oauth2-server-uri": "<workspace-url>/oidc/v1/token",
    "iceberg.catalog.token": "<service_principal_pat_token>",
    "iceberg.catalog.warehouse": "<uc_catalog_name>"
    }
}

"spark.sql.catalog.<spark-catalog-name>": "org.apache.iceberg.spark.SparkCatalog",
"spark.sql.catalog.<spark-catalog-name>.type": "rest",
"spark.sql.catalog.<spark-catalog-name>.rest.auth.type": "oauth2",
"spark.sql.catalog.<spark-catalog-name>.uri": "<workspace-url>/api/2.1/unity-catalog/iceberg-rest",
"spark.sql.catalog.<spark-catalog-name>.oauth2-server-uri": "<workspace-url>/oidc/v1/token",
"spark.sql.catalog.<spark-catalog-name>.credential":"<oauth_client_id>:<oauth_client_secret>",
"spark.sql.catalog.<spark-catalog-name>.warehouse":"<uc-catalog-name>"
"spark.sql.catalog.<spark-catalog-name>.scope":"all-apis"

Limitations of Kafka Connect Apache Iceberg Sink Connector:
Schema Evolution: Iceberg supports schema evolution whereas the Iceberg Sink Connector does not manages complex schema changes. If a column type is changed in Kafka, the connector might fail to map the changes properly, leading to data issues or write failures.
