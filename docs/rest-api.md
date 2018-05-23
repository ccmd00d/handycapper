# REST API (Beta)

The API is available when the application is running. There is no authentication required for any API request.

## Get Race Results

Gets the Races saved in the database

**Query Parameters**

* `track`: *required*; string; a single track short code e.g. "ARP", "SA", "BEL" or "CD"
* `date`: *required*; date; ISO Date format i.e. "yyyy-MM-dd" e.g. "2016-07-24"
* `num`: number; the race number (should you wish to return a single race)
* `canc`: boolean; set to "true" if you wish to include cancelled races in your results; default "false"

**URL** : `/races`

**Method** : `GET`

**Example** : [http://localhost:8080/races?track=ARP&date=2016-07-24]()

### Success Response

**Code** : `200 OK`

**Content examples**

Always returns a JSON array of `RaceResult` objects. An empty array will be return if no matching races were found.

See [the JSON API design](json-design.md) document for a full breakdown.

```json
[
  {
    "links": [
      {
        "rel": "web",
        "href": "https://www.equibase.com/premium/chartEmb.cfm?track=SA&raceDate=1/11/2014&cy=USA&rn=1"
      },
      {
        "rel": "pdf",
        "href": "https://www.equibase.com/premium/eqbPDFChartPlus.cfm?RACE=1&BorP=P&TID=SA&CTRY=USA&DT=1/11/2014&DAY=D&STYLE=EQB"
      },
      {
        "rel": "allWeb",
        "href": "https://www.equibase.com/premium/chartEmb.cfm?track=SA&raceDate=1/11/2014&cy=USA"
      },
      {
        "rel": "allPdf",
        "href": "https://www.equibase.com/premium/eqbPDFChartPlus.cfm?RACE=A&BorP=P&TID=SA&CTRY=USA&DT=1/11/2014&DAY=D&STYLE=EQB"
      }
    ],
    "cancelled": false,
    "raceDate": {
      "text": "2014-01-11",
      "year": 2014,
      "month": 1,
      "day": 11,
      "dayOfWeek": "Saturday",
      "dayOfYear": 11
    },
    "track": {
      "code": "SA",
      ...
	},
	{
		...
	}
]
```

## Search Race Results

Searches for Races saved in the database that match the search criteria

**Query Parameters**

* `track`: *required*; string; a single track short code e.g. "ARP", "SA", "BEL" or "CD"
* `date`: *required*; date; ISO Date format i.e. "yyyy-MM-dd" e.g. "2016-07-24"
* `lowDist`: number; the minimum number of furlongs (inclusive) e.g. `5.5`, `12`
* `highDist`: number; the maximum number of furlongs (inclusive); set to the same as `lowDist` for that distance only
 e.g. `7.5`, `16`
* `lowAge`: number; the minimum age permitted (inclusive) e.g. `2`
* `highAge`: number; the maximum age permitted (inclusive); set to the same as `lowAge` for that age only e.g. `3` 
* `lowRnrs`: number; the minimum number of runners in the field (inclusive)
* `highRnrs`: number; the maximum number of runners in the field (inclusive); set to the same as `lowRnrs` for that age only
* `female`: boolean; set to "true" if you wish to only consider female-only horse races (fillies, mares)
* `surface`: string; one of "Dirt", "Turf", or "Synthetic"
* `types`: list of strings; full type names; comma-separated e.g. "MAIDEN SPECIAL WEIGHT,MAIDEN CLAIMING"

**URL** : `/search/races`

**Method** : `GET`

**Example** : [http://localhost:8080/search/races?lowDist=6&lowAge=3&female=true&surface=Dirt]()

### Success Response

**Code** : `200 OK`

**Content examples**

Always returns a JSON array of `RaceResult` objects. An empty array will be return if no matching races were found.

See [the JSON API design](json-design.md) document for a full breakdown.

```json
[
  {
    "links": [
      {
        "rel": "web",
        "href": "https://www.equibase.com/premium/chartEmb.cfm?track=SA&raceDate=1/11/2014&cy=USA&rn=1"
      },
      {
        "rel": "pdf",
        "href": "https://www.equibase.com/premium/eqbPDFChartPlus.cfm?RACE=1&BorP=P&TID=SA&CTRY=USA&DT=1/11/2014&DAY=D&STYLE=EQB"
      },
      {
        "rel": "allWeb",
        "href": "https://www.equibase.com/premium/chartEmb.cfm?track=SA&raceDate=1/11/2014&cy=USA"
      },
      {
        "rel": "allPdf",
        "href": "https://www.equibase.com/premium/eqbPDFChartPlus.cfm?RACE=A&BorP=P&TID=SA&CTRY=USA&DT=1/11/2014&DAY=D&STYLE=EQB"
      }
    ],
    "cancelled": false,
    "raceDate": {
      "text": "2014-01-11",
      "year": 2014,
      "month": 1,
      "day": 11,
      "dayOfWeek": "Saturday",
      "dayOfYear": 11
    },
    "track": {
      "code": "SA",
      ...
	},
	{
		...
	}
]
```
