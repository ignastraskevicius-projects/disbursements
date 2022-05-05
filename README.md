# Disbursements

REST API for disbursements service in a e-commerce context

## Set up

### Requirements
* Java 17
* Docker Engine 20.10.12
* Docker Compose 1.26.0
* port 8080, 3306 to be free

### 1. Build

./mvnw clean install

### 2. Run

#### Usage

* root resource will be available at http://localhost:8080
* Disbursement resource:
POST /disbursements (async)
```
{
    "timeFrame":{
        "length":"1week",
        "endingBefore":"2022-01-01"
    }
}
```
GET /disbursements?timeFrameEndingBefore=2022-01-01&timeFrameLength=1week