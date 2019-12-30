# orleans

a REST engine to play the game orleans

1) init Game:
`GET /game/init?playerNames=Bob,Tony,Jane`

2) Iterate through phases:
`GET /game/test/nextPhase`

3) For Planning Phase, Submit Plans:
`GET /game/test/Bob/plan?action=FarmHouse&followerTypes=StarterBoatman,StarterCraftsman`

4) For Action Phase, Sumit Actions
4a) Simple actions: `GET /game/{gameId}/{playerId}/action?action=FarmHouse`
4b) Actions with params:


`GET /game/{gameId}/{playerId}/action?action=Ship&from=Orleans%to=Vierzon`
`GET /game/{gameId}/{playerId}/action?action=Village&followerType=Boatman`
`GET /game/{gameId}/{playerId}/action?action=Village&followerType=Craftsman%techAction=University&position=0`
`GET /game/{gameId}/{playerId}/action?action=Village&followerType=Trader%placeTile=Pharmacy`
`GET /game/{gameId}/{playerId}/action?action=Pharmacy&times=3`
`GET /game/{gameId}/{playerId}/action?action=TownHall&benefit1=DefeatPlague&benefit2=PapalConclave`


Notes
=====
User facing indexes are 0 based