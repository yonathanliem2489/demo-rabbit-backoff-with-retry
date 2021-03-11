# Demo Rabbit Retry and Exponential Backoff

## This Article is demo project implementation AMQP Rabbitmq

### Requirement
* Spring Boot 2.4.x
* Rabbit Server 3.8.5

### Why using Retry and Backoff
In some cases of transaction processes, we sometimes face problems if an ongoing process fails, therefore there is a strategy so that the failed process can be reproduced by the system we created.
In this tutorial, I will describe the process mechanism inside the rabbit that we can use so that at least this problem can help make transactions better.

### Planning and Solution
![alt text](https://github.com/yonathanliem2489/demo-rabbit-retry-backoff/blob/master/document/rabbit-retry.jpeg?raw=true)