# Parking management API

## Requirements

Java 21

## Description

Simple API to manage parking places

## Features

- Park car - parks car in first available place
- Leave parking - charges parking per minute according to pricing per vehicle type + additional charge every 5 mins
- Get parking allocation - returns number of available and occupied spaces

## Assumptions

- Cannot park vehicle with same vehicleReg
- You cannot request a bill for non-parked car

## Questions

- How vehicleReg validation should look like
- What when parking is full - what response is expected

## Building

```
./gradlew build
```

## Testing

To run tests with jacoco report

```
./gradlew test
```

To run tests with coverage

```
./gradlew check
```

## Running

```
./gradlew bootRun
```