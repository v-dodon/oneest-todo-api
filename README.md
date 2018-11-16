# Todo api

## Requirements
Java - 1.8.x

Maven - 3.x.x

PostgreSql - 9.x.x


## Running the App
Type the following command in your terminal to run the app -

`mvn spring-boot:run`

The app will start running at http://localhost:8080.

## Available resources

The app defines the following crud resources:
```
POST /todo with body 
{
	title: {title},
	text: {text},
	tag: {tag}
}

GET /todo

GET /todo?title={title}

GET /todo?text={text}

GET/todo?tag={tag}

GET /todo?title={title}&text={text}&tag={tag}

DELETE /todo/{id}
```
To access these resources you will need to define the following headers: <br/>
```
Content-type: application/json
Authorization: Bearer {token}
```
If the token is expired or you didn't define it then you will get the response with status **Unauthorized** and body <br/>
`{errorMessage: You are not logged in or the session has been expired, to use this resource you need to be logged in}`
If you will try to remove an unexistent todo then you will get the response with status **Not found** and body <br/>
`{errorMessage: There are no todo with the id: {id} }`

Before testing this resources you need to be registered using the following resource: <br/>
`POST /auth/register` with the header `Content-type: application/json` and body: <br/>
```
{
username: {username},
password: {password}
}
```
After registering you need to be logged in using the resource: <br/>
`POST /auth/login` with the header `Content-type: application/json` and body:
```
{
username: {username},
password: {password}
}
```
If the user is registered then in the Response header, you will find `Authorization: Bearer {token}`, use this token to access the crud resources defined above<br/>
**Note** `The token will expire in one day`