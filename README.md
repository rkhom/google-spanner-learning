# Demo GCP Spanner project

### Prerequisites
1. Java 11
2. Maven 3+
3. Google Cloud Platform Account
4. An existing instance of Spanner database on GCP

### Setup
1. Open GCP SDK console and make sure you are authenticated.
2. Switch to the project with existing Spanner instance: 
`gcloud config set project <PROJECT_ID>`.
3. Create new Spanner database on existing instance: 
`gcloud spanner databases create <DB_NAME> --instance=<INSTANCE_NAME>`.
Replace also corresponding DB properties in `application.properties` file.
4. Go to project root folder.
5. Execute DDL script for table creation: 
`gcloud spanner databases ddl update <DB_NAME> --instance=<INSTANCE_NAME> --ddl-file="src/main/resources/schema.sql"`.
5. Run the application executing the command `mvn spring-boot:run`. Application will start on `loclalhost:8099` 
(you may change app port in `application.properties` if needed).

### Usage
Perform HTTP requests via your favourite HTTP client. For convenience, app also provides Swagger UI interface via `localhost:8099/swagger-ui/`.

Application has 2 controllers: Dashboard controller for CRUD operations with dashboards and auxiliary DDL controller for programmatic schema creation and cleanup.
Below are some request and response examples for the Dashboard controller:

#### 1. Create new dashboard

##### Example URL and method
```
POST http://localhost:8099/api/dashboards
```
##### Request body
```
{
    "title": "Dashboard 1",
    "dateRange": {
        "startDate": "2020-01-01T00:00:00Z",
        "endDate": "2020-12-31T23:59:59Z"
    },
    "widgets": [
        {
            "title": "Widget1",
            "type": "PROGRESS_BAR"
        },
        {
            "title": "Widget2",
            "type": "CHART"
        }
    ]
}
```

##### Response body (ID of the newly created dashboard)
```
1611857587973
```

#
#### 2. Update existing dashboard

##### Example URL and method
```
PUT http://localhost:8099/api/dashboards
```
##### Request body (changed type of existing Widget2 and added new Widget3)
```
{
    "id": 1611857587973,
    "title": "Dashboard 1",
    "dateRange": {
        "startDate": "2020-01-01T00:00:00Z",
        "endDate": "2020-12-31T23:59:59Z"
    },
    "widgets": [
        {
            "dashboardId": 1611857587973,
            "id": 1910857766451680526,
            "title": "Widget1",
            "type": "PROGRESS_BAR"
        },
        {
            "dashboardId": 1611857587973,
            "id": 528734490897115576,
            "title": "Widget2",
            "type": "TEXT"
        },
        {
            "title": "Widget3",
            "type": "CHART"
        }
    ]
}
```

##### Response body (ID of the updated dashboard)
```
1611933236227
```

#
#### 3. Delete existing dashboard

##### Example URL and method
```
DELETE http://localhost:8099/api/dashboards/1611857587973
```

#
#### 4. Get all existing dashboards

##### Example URL and method
```
GET http://localhost:8099/api/dashboards
```

##### Response body
```
[
  {
    "id": 1611857587973,
    "title": "Dashboard 1",
    "dateRange": {
      "startDate": "2020-01-01T00:00:00",
      "endDate": "2020-12-31T23:59:59"
    },
    "widgets": [
      {
        "id": 1910857766451680526,
        "dashboardId": 1611857587973,
        "title": "Widget1",
        "type": "PROGRESS_BAR"
      },
      {
        "id": 528734490897115576,
        "dashboardId": 1611857587973,
        "title": "Widget2",
        "type": "CHART"
      }
    ]
  }
]
```

#
#### 5. Get dashboard by ID

##### Example URL and method
```
GET http://localhost:8099/api/dashboards/1611857587973
```

##### Response body
```
{
    "id": 1611857587973,
    "title": "Dashboard 1",
    "dateRange": {
        "startDate": "2020-01-01T00:00:00",
        "endDate": "2020-12-31T23:59:59"
    },
    "widgets": [
        {
            "id": 528734490897115576,
            "dashboardId": 1611857587973,
            "title": "Widget2",
            "type": "CHART"
        },
        {
            "id": 1910857766451680526,
            "dashboardId": 1611857587973,
            "title": "Widget1",
            "type": "PROGRESS_BAR"
        }
    ]
}
```

### Implementation details
For education purposes, DML operations with dashboards implemented via Spring `SpannerRepository` feature, whereas DQL operations via `SpannerTemplate`. 
For simplicity, DQL operations could be also implemented using `SpannerRepository`.