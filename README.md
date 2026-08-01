# Microservice based application

It consists of 3 main business services, 1 Authentication service and 1 API Gateway. Each microservice has its own DB. Additionally in User Service caching via Redis and external API call in Payment Service should be implemented. There are two ways of communication in the application: synchronous via REST and asynchronous via Kafka.

![schema.png](schema.png)