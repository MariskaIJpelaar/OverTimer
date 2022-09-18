# OverTimer
OverTimer is an Android application to manage your overtime.
This project is still a work in progress. 
While its main features are implemented, they are mostly untested and unstable. 

## Features
Currently, Overtimer supports the following features:
1. Set your weekday schema, so the app automatically determines what is overtime and what not. 
Currently, flexible weekdays are not supported.
2. Add hours worked for a specific date and time. 
The app automatically determines your overtime and saves the information.
3. Display information in the home-sceen about the number of overtime you have, 
and the percentage of work you have done this week.
4. Export your overtime in plain text sorted by date, such that you have an overview of your worked hours.

> **Note**: above features are implemented, but not tested extensively. If you use this app, make sure you always verify the outcomes. 

### Future Features
Once the main features are properly implemented and tested, we will start working on the following features:
1. Overtime format should also be displayed in minutes
2. Setup flexible work-weeks
3. Export in smaller formats
4. Imports
5. Graphs visualization

## Building
To compile source code:
```bash
./gradlew assemble
```

To test source code:
```bash
./gradlew test
```

To create deployable (debug) application:
```bash
./gradlew assembleDebug 
./gradlew assembleRelease
```
This produces `OverTimer-<VERSION>-debug.apk` in 
`companion/app/build/outputs/apk/`, which can be installed on Android smartphones.
