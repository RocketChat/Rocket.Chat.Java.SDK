Following methods are provided by LiveChatAPI class

- getInitialData (getting initial configuration data from the server)
- register Guest (registering guest using name, email and department)
- send offline message (sending offline messages to the server)
- set connect listener (register connection listener for the callback)
- create room (using userId and authToken)

**Note (Get rid of callback hell):** Best way to write down callbacks is to let given Class implement the interface and passing it's instance to the room method as a callback parameter. Same should be followed for other methods. 
**No Callback:** Pass null in case callback receive is not important.

**1. getInitialData**

**2. registerGuest**

**3. sendOfflineMessage**

**4. setConnectListener**

**5. createRoom**

