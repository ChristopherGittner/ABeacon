# ABeacon

This repository contains the source code for the Android App ABeacon.

The App generates a new V4 UUID on startup and transmits this UUID in regular intervals (about once every 10 seconds).

This Beacon Frames can be captured for example by an ESP with Tasmota or ESPresence to detect whether or not your phone is at home or in in a certain area.

## Permissions
When the App is first started it will ask for the following Permissions:

* Location - Required to transmit the Beacon
* Nearby Devices - Required to transmit the Beacon
* Notifications - Required to keep the App running in Background
* Disable Battery Optimization - Required to keep the App running in Background
