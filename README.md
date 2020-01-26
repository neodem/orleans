# orleans

a REST engine to play the game orleans

Swagger UI
==========
http://localhost:8081/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/

Examples
========

1) init Game:
`GET /game/init?playerNames=Bob,Tony,Jane`

2) Start game:
`GET /game/test/startGame`

3) For Planning Phase, Submit Plans:
`GET /game/test/Bob/plan?action=FarmHouse&followerTypes=StarterBoatman,StarterCraftsman`
`GET /game/test/Bob/pass`

4) For Action Phase, Sumit Actions
4a) Simple actions: `GET /game/{gameId}/{playerId}/action?action=FarmHouse`
4b) Actions with params:


`GET /game/{gameId}/{playerId}/action?action=Ship&from=Orleans%to=Vierzon`
`GET /game/{gameId}/{playerId}/action?action=Village&followerType=Boatman`
`GET /game/{gameId}/{playerId}/action?action=Village&followerType=Craftsman%techAction=University&position=0`
`GET /game/{gameId}/{playerId}/action?action=Village&followerType=Trader%placeTile=Pharmacy`
`GET /game/{gameId}/{playerId}/action?action=Pharmacy&times=3`
`GET /game/{gameId}/{playerId}/action?action=TownHall&benefit1=DefeatPlague&benefit2=PapalConclave`

`GET /game/test/Bob/action/pass`




Notes
=====
User facing indexes are 0 based

see GameITest for a good idea of how the API works.. 