## Run
./gradlew bootRun

## Example request
curl --location 'http://localhost:8080/git/repo/scores?language=java&earliestCreated=2025-10-01'

## Description
Backend service ranking GitHub repositories by stars, forks, and updates recency.
