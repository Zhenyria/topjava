#curl requests
____

##meals

*get meal by id 100002*
`curl --location --request GET 'http://localhost:8080/topjava/rest/meals/100002'`

*delete meal by id 100002*
`curl --location --request DELETE 'http://localhost:8080/topjava/rest/meals/100002'`

*get all meals*
`curl --location --request GET 'http://localhost:8080/topjava/rest/meals'`

*create meal*
`curl --location --request POST 'http://localhost:8080/topjava/rest/meals' \
 --header 'Content-Type: application/json' \
 --data-raw '{
     "dateTime": "2020-02-01T10:00:00",
     "description": "Завтрак",
     "calories": 500
 }'`

*update meal by id 100002*
`curl --location --request POST 'http://localhost:8080/topjava/rest/meals/100002' \
 --header 'Content-Type: application/json' \
 --data-raw '{
     "id": 100002,
     "dateTime": "2020-01-30T10:00:00",
     "description": "Завтрак",
     "calories": 600
 }'`

*get meals between date and time*
`curl --location --request GET 'http://localhost:8080/topjava/rest/meals/filter?start_date=2020-01-31&start_time=13:00:00'`

