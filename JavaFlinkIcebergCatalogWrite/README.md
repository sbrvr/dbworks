# Flink Iceberg Catalog Write - Java Example
The following example shows how to write to a Icebert Table in Databricks using Flink and Unit Catalog Apache Iceberg REST Catalog API.

Prerequisites -
- Access (Roles/Permissions/Network) to Object Store (s3/GCS/ADLS) is configured for Flink cluster to write data
- Databricks External Access to Unity Catalog Enabled - More details [here](https://docs.databricks.com/aws/en/external-access/admin)
- Databricks Credential Vending [Requirements](https://docs.databricks.com/aws/en/external-access/credential-vending#requirements)
- The writing principal must have the following permissions on the table level: SELECT and MODIFY

Set the following in the IcebergRestAPI.java file
- Databricks Workspace URL
- Service Principal PAT Token/ Client ID/ Client Secret
- UC Catalog Name
- UC Schema Name
- UC Table Name

