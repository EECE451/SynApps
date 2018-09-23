# SynApps
An android application that detects neighboring devices (using wifi P2P). The app keeps listening to changes in the neighborhood networks for any new devices that just connected or disconnected.  
Using their MAC addresses as primary identifier, these devices are then stored on a server with relevant information such as _lastSeen Time stamp_, _Connection Frequency_, _Name of Master Device (star graph)_ and _GPS lat & long_.  
## Technical Implementation
### Data Management and storage
The data was stored locally on the phone, using SQLite before being shipped to an online server that updates every second. A robust ER design helped with shaving off delays and boosting performance in case of a large network (in the case of a large assembly, for example in a football stadium). The interface between the local device and the server was written using Php.  
### Wifi P2P
In order to keep on looking for different devices, a thread is maintained in the background that keeps on looking for any updates in the network. The receiver class (WiFiP2PReceiver) will then manage our data collection for the attribute we usually need, including MAC address, detection length and battery level.  
### Graphing a star network: 
The star network always depended on a Master device that detected all the remaining parts of the network. Hence, it was essential to assign roles to the devices when the connected, which helps to plot the network. 

![Demo 1](/static/1.png)  
![Demo 2](/static/2.png)   
![Demo 3](/static/3.png)  
![Demo 5](/static/5.png)  
![Demo 4](/static/4.png)  
![Demo 6](/static/6.png)  
