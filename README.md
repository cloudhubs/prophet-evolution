# Change Impact Microservice Evolution Tool

This project tracks microservice system evolution changes across repositories.


## Prerequisites

* Maven 3.6+
* Java 11+ (11 Recommended)  

## To Compile:
    ``mvn clean install -DskipTests``

## Extracting an Intermediate Representation:
- Run or compile the main method of ``IntermediateExtraction.java`` in the IDE of your choice or via the command line.
- Default input configuration is defined in `./config.json`
- Optional parameters: ``/path/to/config/file``

Sample input config file:

```json
{
  "clonePath": "/repos",
  "outputPath": "/out",
  "repositories": [
    {
      "repoUrl": "https://github.com/cloudhubs/train-ticket-microservices.git",
      "baseCommit": "f34c476",
      "paths": [ "path/to/microservice", "ts-admins-service"]
    },
    {
      "repoUrl": "https://github.com/cloudhubs/tms2020.git"
    }
  ]
}
```

Sample output produced:
```json
{
  "systemName": "train-ticket-system",
  "version": "0.0.1",
  "services": [
    {
      "id": "train-ticket-microservices\\ts-price-service",
      "msName": "ts-price-service",
      "msPath": "C:/Users/.../train-ticket-microservices\\ts-price-service",
      "commitId": "f34c476",
      "restControllers": [
        {
          "id": "GET:ts-price-service.home#0",
          "api": "/api/v1/priceservice/prices/welcome",
          "source-file": "C:\\Users\\...\\train-ticket-microservices\\ts-price-service\\...\\PriceController.java",
          "type": "GetMapping",
          "httpMethod": "GET",
          "parent-method": "com.cloudhubs.trainticket.price.controller.PriceController.home",
          "methodName": "home",
          "arguments": "",
          "return": "String"
        },
        ...,
      ],
      "dependencies": [
        {
          "api": "/api/v1/paymentservice/payment",
          "source-file": "C:\\Users\\...\\train-ticket-microservices\\ts-price-service\\...\\InsidePaymentServiceImpl.java",
          "call-dest": "< TODO >",
          "call-method": "com.cloudhubs.trainticket.price.service.impl.InsidePaymentServiceImpl.pay()",
          "httpMethod": "POST"
        },
        ...
      ]
    },
    ...
  ]
}
```

## Extracting a Delta Change Impact:
- Run or compile the main method of ``DeltaExtraction.java`` in the IDE of your choice or via the command line.
- Command line args list containing ``/path/to/repo(s)``

Sample output produced:
```json
{
    "local-file": "/cimet/.../services/DeltaExtractionService.java",
    "remote-api": "https://api.github.com/repos/cloudhubs/cimet/contents/.../DeltaExtractionService.java",
    "systemChange-type": "MODIFY",
    "changes": [
      {
        "className": "DeltaExtractionService",
        "methodName": "processDifferences",
        "remote-line": "    System.out.println(\"Delta extracted: \" + outputName);",
        "local-line": "      jout.add(\"systemChange-type\", entry.getChangeType().name());",
        "line-number": 107
      },
      {
        "className": "DeltaExtractionService",
        "methodName": "processDifferences",
        "remote-line": "  }",
        "local-line": "      jout.add(\"changes\", deltaChanges);",
        "line-number": 108
      },
      ...
    ]
}
```