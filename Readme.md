# ABeacon

This repository contains the source code for the Android app ABeacon.

When the app is first started, it generates a new V4 UUID and transmits this UUID as iBeacons at regular intervals (about once every 10 seconds).

The iBeacon frames can be captured, for example, by an ESP with Tasmota or ESPresence to detect whether your phone is at home or in a certain area.

## Permissions
When the app is first started, it will ask for the following permissions:

* Location (Background) - Required to transmit the beacon
* Nearby Devices - Required to transmit the beacon
* Notifications - Required to keep the app running in the background
* Disable Battery Optimization - Required to keep the app running in the background