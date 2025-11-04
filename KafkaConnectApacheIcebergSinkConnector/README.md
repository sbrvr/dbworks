# Kafka Connect Apache Iceberg Sink Connector - Databricks Unity Catalog Apache Iceberg REST API 

Kafka Connect offers a sink connector called the Apache Iceberg Sink Connector which can be used to write to Iceberg tables. This sinks supports working with Databricks using Unity Catalog Apache Iceberg REST Catalog API.

The Apache Iceberg sink connector guarantees that each record from Kafka is written to the Iceberg tables exactly once, even during failures or retries. Besides this exactly-once delivery semantics, the Kafka Iceberg connector has a multi-table fan-out capability. This helps you move data from a single Kafka topic to multiple Iceberg tables.

Once the Connector is configured and created, it starts pushing data from Topic(s) to Iceberg Tables in Unity Catalog.

## Prerequisities:
- Kafka Cluster 2.5 or higher
- Apache Iceberg Sink Connector is installed on Kafka Cluster under Plugins directory and then plugin.path property is set in Kafka Connect worker configuration properties file to point to the plugins directory. Make sure to use HMS supported Iceberg Sink Connector.
- Access (Roles/Permissions/Network) to Object Store (s3/GCS/ADLS) is configured for Kafka cluster to write data
- Databricks External Access to Unity Catalog Enabled - More details [here](https://docs.databricks.com/aws/en/external-access/admin)
- Databricks Credential Vending [Requirements](https://docs.databricks.com/aws/en/external-access/credential-vending#requirements)

## Step 1: Create Iceberg Sink Connector Configuration JSON File
This JSON file will store the Databricks UC information like Catalog,Schema,Table and Kafka like Topics, Regex rules to map topics to UC Iceberg tables.
More Info on Kafka Connect Configuration Properties can be found [here](https://iceberg.apache.org/docs/nightly/kafka-connect/#configuration).

JSON_FILE -

AWS Databricks:
```
{
"name": "events-sink",
"config": {
    "connector.class": "org.apache.iceberg.connect.IcebergSinkConnector",
    "tasks.max": "2",
    "topics": "<topic_name>",
    "iceberg.tables": "<uc_schema.tablename>",
    "iceberg.catalog.uri": "<databricks_workspace_url>/api/2.1/unity-catalog/iceberg-rest",
    "iceberg.catalog.token": "<service_principal_pat_token>",
    "iceberg.catalog.warehouse": "<uc_catalog_name>",
    "iceberg.catalog.scope": "all_apis",
    "iceberg.catalog.type": "rest",
    "iceberg.catalog.io-impl": "org.apache.iceberg.aws.s3.S3FileIO"
    }
}
```

Azure Databricks:
```
{
"name": "events-sink",
"config": {
    "connector.class": "org.apache.iceberg.connect.IcebergSinkConnector",
    "tasks.max": "2",
    "topics": "<topic_name>",
    "iceberg.tables": "<uc_schema.tablename>",
    "iceberg.catalog.uri": "<databricks_workspace_url>/api/2.1/unity-catalog/iceberg-rest",
    "iceberg.catalog.token": "<service_principal_pat_token>",
    "iceberg.catalog.warehouse": "<uc_catalog_name>",
    "iceberg.catalog.scope": "all_apis",
    "iceberg.catalog.type": "rest",
    "iceberg.catalog.io-impl": "org.apache.iceberg.azure.adlsv2.ADLSFileIO",
    "iceberg.catalog.include-credentials": "true"
    }
}
```

Note: Make sure to set enviornment variables for AZURE_CLIENT_ID,AZURE_TENANT_ID,AZURE_CLIENT_SECRET.
More Details for [ADLS](https://iceberg.apache.org/docs/1.7.0/kafka-connect/#azure-adls-configuration-example)

GCP Databricks:
```
{
"name": "events-sink",
"config": {
    "connector.class": "org.apache.iceberg.connect.IcebergSinkConnector",
    "tasks.max": "2",
    "topics": "<topic_name>",
    "iceberg.tables": "<uc_schema.tablename>",
    "iceberg.catalog.uri": "<databricks_workspace_url>/api/2.1/unity-catalog/iceberg-rest",
    "iceberg.catalog.token": "<service_principal_pat_token>",
    "iceberg.catalog.warehouse": "<uc_catalog_name>",
    "iceberg.catalog.scope": "all_apis",
    "iceberg.catalog.type": "rest",
    "iceberg.catalog.io-impl": "org.apache.iceberg.google.gcs.GCSFileIO"
    }
}
```
More Details for [GCS](https://iceberg.apache.org/docs/1.7.0/kafka-connect/#google-gcs-configuration-example)

## Step 3: Start the Connector
Kafka Connect REST API is used to start the connector using the JSON file created in Step 2.

```
curl -X PUT http://<Kafka_cluster_url>/connectors/<new_connector_name>/config \
     -i -H "Content-Type: application/json" -d @<JSON_File_Created_In_Step2>.json
```

## Step 3: Verfity the connector is Running
The following command can be used to check if the connector is running.

```
curl -s http://<Kafka_cluster_url>/connectors/<new_connector_name>/status 
```

This should start pushing data from Kafka Topic(s) to Unity Catalog Table(s).

## Limitations of Kafka Connect Apache Iceberg Sink Connector:

Schema Evolution: 
Iceberg supports schema evolution whereas the Iceberg Sink Connector does not manages complex schema changes. If a column type is changed in Kafka, the connector might fail to map the changes properly, leading to data issues or write failures.
