
## cip-email

### Summary
Proxy/Forwarder server for cip email services

- cip-email-validation
- cip-email-verification
- cip-email-history
- cip-email-insights

### Testing
#### Unit tests
`sbt clean test`

#### Integration tests
`sbt clean it:test`

### Running app

In order to run this microservice cip-email you willl need to run the downstream services first. Then run 
`sbt clean run` and this will run on port 9000

#### Example query
```
curl --request POST \
  --url http://localhost:9000/customer-insight-platform/email/validate-format \
  --header 'content-type: application/json' \
  --data '{"email" : "test@test.com"}'
```
### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

