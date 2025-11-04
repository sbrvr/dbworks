# Kafka Connect Apache Iceberg Sink Connector - Databricks Unity Catalog Apache Iceberg REST API 

Kafka Connect offers a sink connector called the Apache Iceberg Sink Connector which can be used to write to Iceberg tables. This sinks supports working with Databricks using Unity Catalog Apache Iceberg REST API.

The Apache Iceberg sink connector guarantees that each record from Kafka is written to the Iceberg tables exactly once, even during failures or retries. Besides this exactly-once delivery semantics, the Kafka Iceberg connector has a multi-table fan-out capability. This helps you move data from a single Kafka topic to multiple Iceberg tables.
