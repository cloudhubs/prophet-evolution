# Prophet Microservice System Evolution Tool

This project tracks microservice system evolution changes across repositories.

## Prerequisites

* Maven 3.6+
* Java 11+ (11 Recommended)

## Reproducing results

To compile:

``mvn clean install -DskipTests``

To generate the files, provide in your first argument a comma-separated list of remote microservice repositories to clone (ending in .git), followed by your output folder:

``java -jar target/semantics-1.0-SNAPSHOT-runner.jar 'https://github.com/<owner>/<repo1>.git,https://github.com/<owner>/<repo2>.git,...,https://github.com/<owner>/<repoN>.git' 'your-results-folder/'``

To run the server:

``java -jar target/semantics-1.0-SNAPSHOT-runner.jar``