# Capstone Project
[Author: Sander van den Oetelaar](https://www.github.com/KoningSanderPander "My Github")

[Think]  
[Make]  
[Design]

## Think
[Think]: #think

In short the app will allow people to automatically log their time at work.
Without having to manually start and stop a timer.

From my personal experience I've noticed that most apps offices have
some kind of time tracking system. Most of the times this is done with a
central station and a card containing an nfc chip. But these systems are
slow and annoying to use. When everyone has break time there is always a
queue at the check-in/check-out station.

The goal of this project is to eliminate the need for those central
systems with an app on your phone. This app will use geo-fencing
techniques to calculate when to start and stop logging your time at work.

## Make
[Make]: #make

###### Features

| Feature          | Description                                                                                                                                    |
| ---------------- | ---------------------------------------------------------------------------------------------------------------------------------------------- |
| GeoFence         | This app will actively track your location to accurately log your time spent at your place of work.                                            |
| Google Maps      | To set your location of work you will need to use google maps to pin a set of gps coordinates.                                                 |
| Firebase         | Firebase will be used to store all of the time logs and to calculate a salary at the and of the month.                                         |
| Random Breaks    | You will need to be able to random breaks you've had throughout the day.                                                                       |
| Scheduled breaks | Every office has their own break schedule. You will need to be able to keep track of this automatically.                                       |
| Wages            | Your employer must be able to set an hourly wage.                                                                                              |
| Companies        | You must be able to join a company.                                                                                                            |
| Time-outs        | The app will have some kind of time-out feature where it will stop tracking your location and logging your time. Based on your configurations. |
| Notifications    | The app will notify you when you've entered/left the time tracking zone and how long you've worked for that day.                               |

Please note that not all of the proposed features are going to be
implemented due to time constraints.

###### Techniques

+ App bars
+ Banners
+ Bottom navigation
+ Buttons
+ Checkboxes
+ Firebase
+ GeoFence
+ Google maps
+ Menus
+ Push Notifications
+ Snackbars
+ Text fields
+ Time Pickers

## Design
[Design]: #design

The app design is not final yet, but the design will be inspired by the
Connecteam app

![alt text](https://techstory.in/wp-content/uploads/2020/08/image2-3.png "Connecteam app design inspiration")