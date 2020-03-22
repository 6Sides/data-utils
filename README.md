# data-utils

## A helper library for interfacing with data sources

### Components

- caching
- config
- jwt
- keys
- passwords
- postgres
- queue
- random
- redis
- serialize
- uuid


### Caching

Contains helpers to simplify common caching strategies. Useful for expensive database queries.


### Config

Used to dynamically configure a class based on the runtime environment.


### jwt

Contains helpers to create and verify jwts.


### Keys

Contains helpers to fetch rsa keypairs used to sign jwts


### Passwords

Contains helpers to hash and verify user passwords.


### Postgres

A factory to generate Postgres clients based on the current environment


### Queue

A helper class to simplify processing data streams


### Random

Implementations of cryptographically-secure random generators


### Redis

A factory to generate Redis clients based on the current environment


### Serialize

Helper to serialize/deserialize classes. Used by the caching package to store objects in redis.


### UUID

Implementation of UUID v4 generator
