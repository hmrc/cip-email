
## cip-email

### Summary

Proxy/Forwarder server for cip email services

The default port for cip-email-frontend is 6180
The default port for cip-email is port 6181
The default port for cip-email-validation is port 6182
The default port for cip-email-verification is port 6183
The default port for cip-email-stubs is port 6199

### Testing

#### Unit tests
    sbt clean test

#### Integration tests
    sbt clean it:test

### Running app

    sm --start CIP_EMAIL_ALL

Run the services against the current versions in dev, stop the CIP_EMAIL service and start manually

    sm --start CIP_EMAIL_ALL -r
    sm --stop CIP_EMAIL
    cd cip-email
    sbt run

For reference here are the details for running each of the services individually

    cd cip-email-frontend
    sbt run
 
    cd cip-email
    sbt run

    cd cip-email-validation
    sbt run

    cd cip-email-verification
    sbt run

### Curl microservice (for curl microservice build jobs)

#### Verify

    -XPOST -H "Content-type: application/json" -H "Authorization: U8PVkHDh-1JvQdQBAUwxVkCBomn6tq8Ip6J5xZmBxgb1Kb1HmHANuyAquug5ELLb8iSEHBYwQ" -d '{
	    "email": "<email>"
    }' 'https://cip-email.protected.mdtp/customer-insight-platform/email/verify'

#### Check notification status

    -XGET -H "Content-type: application/json" -H "Authorization: U8PVkHDh-1JvQdQBAUwxVkCBomn6tq8Ip6J5xZmBxgb1Kb1HmHANuyAquug5ELLb8iSEHBYwQ"
    'https://cip-email.protected.mdtp/customer-insight-platform/email/notifications/<notificationId>'

#### Verify passcode

    -XPOST -H "Content-type: application/json" -H "Authorization: U8PVkHDh-1JvQdQBAUwxVkCBomn6tq8Ip6J5xZmBxgb1Kb1HmHANuyAquug5ELLb8iSEHBYwQ" -d '{
	    "email": "<email>",
        "passcode": "<passcode>"
    }' 'https://cip-email.protected.mdtp/customer-insight-platform/email/verify/passcode'

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
