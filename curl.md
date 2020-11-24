# CURL requests


## meals
____
*get meal by id 100002* :arrow_left:    
`curl --location --request GET 'http://localhost:8080/topjava/rest/meals/100002'`
____
*delete meal by id 100002* :heavy_multiplication_x:    
`curl --location --request DELETE 'http://localhost:8080/topjava/rest/meals/100002'`
____
*get all meals* :arrows_counterclockwise:    
`curl --location --request GET 'http://localhost:8080/topjava/rest/meals'`
____
*create meal* :fast_forward:
```
curl --location --request POST 'http://localhost:8080/topjava/rest/meals' \
 --header 'Content-Type: application/json' \
 --data-raw '{
     "dateTime": "2020-02-01T10:00:00",
     "description": "Завтрак",
     "calories": 500
 }'
```
____
*update meal by id 100002* :repeat:
```
curl --location --request PUT 'http://localhost:8080/topjava/rest/meals/100002' \
 --header 'Content-Type: application/json' \
 --data-raw '{
     "id": 100002,
     "dateTime": "2020-01-30T10:00:00",
     "description": "Завтрак",
     "calories": 600
 }'
```
____
*get meals between date and time* :calendar:    
`curl --location --request GET 'http://localhost:8080/topjava/rest/meals/filter?start_date=2020-01-31&start_time=13:00:00'`
____
*get meals between date and time* :calendar:    
`curl --location --request GET 'http://localhost:8080/topjava/rest/meals/filter?start_date=2020-01-31&start_time=13:00:00&end_date=2020-01-31&end_time=20:00:00'`
____
