User
```
curl -s -X POST -H "Content-Type: application/x-www-form-urlencoded" -d 'username=kraken-user&password=kraken&grant_type=password' -d 'client_id=kraken-web' "http://localhost:9080/auth/realms/kraken/protocol/openid-connect/token" | jq -r '.access_token' > build/token
```

Admin
```
curl -s -X POST -H "Content-Type: application/x-www-form-urlencoded" -d 'username=kraken-admin&password=kraken&grant_type=password' -d 'client_id=kraken-web' "http://localhost:9080/auth/realms/kraken/protocol/openid-connect/token" | jq -r '.access_token' > build/token
```

List files
```
curl --verbose -X GET http://localhost:8080/test/user -H "Authorization: Bearer $(cat build/token)"
```

API token exchange
```
curl -s -X POST -H "Content-Type: application/x-www-form-urlencoded" -d 'username=kraken-user&password=kraken&grant_type=password' -d 'client_id=kraken-web' "http://localhost:9080/auth/realms/kraken/protocol/openid-connect/token" | jq -r '.access_token' > build/token

curl -s -X POST \
    -d "client_id=kraken-api" \
    -d "client_secret=c1ab32c0-0ba7-4289-b6c9-0ea1aa5ad1d4" \
    -d "grant_type=urn:ietf:params:oauth:grant-type:token-exchange" \
    -d "subject_token=$(cat build/token)" \
    -d "requested_token_type=urn:ietf:params:oauth:token-type:refresh_token" \
    -d "audience=kraken-api" \
    -d "scope=openid info offline_access" \
    http://localhost:9080/auth/realms/kraken/protocol/openid-connect/token | jq -r '.refresh_token' > build/api-refresh-token

curl -s -X POST \
    -d "client_id=kraken-api" \
    -d "refresh_token=$(cat build/api-refresh-token)" \
    -d "grant_type=refresh_token" \
    http://localhost:9080/auth/realms/kraken/protocol/openid-connect/token | jq -r '.access_token' > build/api-token


curl --verbose -X GET http://localhost:8080/test/user -H "Authorization: Bearer $(cat build/api-token)"
```