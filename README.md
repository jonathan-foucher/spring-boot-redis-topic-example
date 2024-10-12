## Introduction
This project is an example of Redis pub/sub implementation with Spring Boot.

An endpoint allows to post a job in the queue and the subscriber will automatically consume it.
The publisher and subscriber are both set on the same project on this example.

Note that all the jobs will run in parallel when the message is received by the subscriber.
If you need to wait for the current job to end before launching the next one, Redis Stream might be a better solution and you can check [this project](https://github.com/jonathan-foucher/spring-boot-redis-stream-example).


## Run the project
You will need to launch a Redis instance on your computer before running the project.

You can either install Redis directly on your machine or run it through Docker :
`docker run -p 6379:6379 redis`

Once Redis is launched, you can start the Spring Boot project and start posting HTTP requests on the endpoint:
```
curl --request POST \
  --url http://localhost:8080/redis-topic-example/v1/jobs/start \
  --header 'Content-Type: application/json' \
  --data '{"id": 1, "name": "some job name"}'
```
