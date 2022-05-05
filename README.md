# Disbursements

REST API for disbursements service in a e-commerce context

#### Problem statement

Potentially due to the cost of making higher amount of lower value pay-outs to merchants SeQura incurs - currently the company chooses to do disbursements to merchants on a weekly single-payment-per-merchant basis as opposed to on a per-each-shopper-payment-received basis. 

This service targets to provide a capability to aggregate a collection of payments over a period of time to a one bigger disbursement to each merchant.


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
* Disbursements resource:

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

#### Deploy (dev-env)

docker-compose -f disbursements-service/docker-compose.yml up

#### Destroy (dev-env)

docker-compose -f disbursements-service/docker-compose.yml down

### Disbursements query optimization 

#### Problem statement
Aggregating huge amount of payments for disbursement amounts is costly:
* The disbursement query caller (a payment system making actual pay-outs to merchants) might be held for excessive periods of time exposing the call to unnecessary risks of call failures.
* Repeating same operation multiple times puts unnecessary pressure on the production database processing live data.

#### Technical implementation

All heavy-lifting of calculating these disbursements for merchants is memoized via asynchronous process initiated via upfront API call for disbursements creation. This process is backed by a stored procedure in the database which aggregates currently completed orders to a requested weekly disbursements for merchants.

Only after this asynchronous process completes, API provides a way to query weekly disbursements for that particular week. Otherwise, the query request is rejected.

### TODO

* Old Data migration is not done.
* API to capture live completed-order flow is not done
* Merchant onboarding API for onboarded merchants after initial migration is not done.