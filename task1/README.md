There are multiple thing that could be added that would improve this project
- Add more tests
- Add additional validations to make app more bulletproof (e.g odd range, amount range, etc.)
- Insted of getting client from post request, we could get it from JWT token (if it would be used)
    - DB is set as such that trader_id is not a primary key, so it could be used to "identify" to what trader client belongs
- We could create admin resource to manage traders
- We could secure endpoints to be accessed only by some kind of  role that user has (admin, trader, user,...)
- Maybe we could add swagger to document API if this service would be used by other developers/clients
- Some namings could be improved
- Responses could be changed/improved that is calculation is not possible e.g. negative return, we return what excactly is wrong
- maybe use snake_case instead of camelCase for api response/requests
- errors returned to client could be localized. e.g. we could extract this from JWT, or maybe header (if provided)
- etc..

# Dev mode run

Running in dev mode, test data is already loaded from assigment text.

Test with curl for calculation for trader 2:
```
curl --location 'localhost:8080/v1/taxation/calculate' \
--header 'Content-Type: application/json' \
--data '{
"traderId": 2,
"playedAmount": 5,
"odd": 1.5
}'
```
Get trader 2 information:
```
curl --location 'localhost:8080/v1/trader/2'
```

# Run tests

There are multiple tests that are testing different parts of the application. 
Because of this, if we need to "refactor" some part of the application, we can be sure that we didn't break anything.

Tests should cover all the cases provided in the assignment text and some additional ones for trader api.

```
mvn clean test
```