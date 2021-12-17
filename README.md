# Spring boot application + AWS  

## Modules
### common
This module contains a config class to load MySQL driver dynamically. It follows the implementation
done [here](https://github.com/danielcasique/aws-secrets-manager-v3). It uses the 
```software.amazon.awssdk```  to connect to MySQL server through AWS Secret Manager.
The following variables is needed to set to properly function:
- aurora_mysql.enabled: Boolean value. True to try to load MySQL Driver
- aurora_mysql.db_name: String value to indicate the Database name on AWS.
- aurora_mysql.secret_name: String value to indicate the ARN generate on AWS Secrete Manager.
- aurora_mysql.default_region: String value to indicate the default region
- aurora_mysql.package_to_load: String to value to indicate the package to load entities. It could one more packages, but 
  each o them separated by ```;```.

### product
Import the common modulo and initialize the variables in order to work with MySQL on AWS properly.
The idea is to separate the connection logic from business logic.

## Important notes:
- [Here](https://aws.amazon.com/secrets-manager/) you can check more info about AWS Secrete Manager. 