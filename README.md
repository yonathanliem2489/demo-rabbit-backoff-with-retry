# Demo Rabbit Exponential Backoff with Retry

## This Article is demo project implementation AMQP Rabbitmq

### Requirement
* Spring Boot 2.4.x
* Rabbit Server

### Why using Retry and Backoff
In some cases of transaction processes, we sometimes face problems if an ongoing process fails, therefore there is a strategy so that the failed process can be reproduced by the system we created.
In this tutorial, I will describe the process mechanism inside the rabbit that we can use so that at least this problem can help make transactions better.

### Planning and Solution
![alt text](https://github.com/yonathanliem2489/demo-rabbit-retry-backoff/blob/master/document/rabbit-retry.jpeg?raw=true)

**Process flow**
1. Message will enter via Exchange with the routing key (primary) and listener (primary) will process the message
2. If the transaction is successful, the process will be completed
3. If the transaction fails (a problem occurs), the queue interceptor will detect it
4. The interceptor will check the retry whether the retry has reached the maximum or not, otherwise the process will continue by publishing and breaking the message to the Wait Queue
5. If the retry is exhaused, the interceptor will publish a message to the parking lot queue with information on the cause of the problem