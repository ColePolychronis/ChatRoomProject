# Dealio Delivery (Optimal) Service - DDOS v1.0.0.1
Server and Client side applications for a Chat Room. Final project for CMPT 352

## Authors:
Matt Gerber & Cole Polychronis

Steps to Running DDOS server:
1. Add json-simple-1.1.1.jar file to build path for the ChatRoomServer and ChatRoomClient project folders
(See http://www.oxfordmathcenter.com/drupal7/node/44 for more information).
2. If you wish to change the number of clients that can be accommodated by the server, edit line 22 of ChatRoomServer.java (the maxUsers variable).
3. Run ChatRoomServer.java
4. Run ChatRoomClient.java or the Chat App.jar executable provided in the submission

### To use the DDOS client application
Sending a message is simple, type the message and hit the send button or press enter on your keyboard.
To direct-message a user click on their name, any highlighted name will be sent the private message.
The app can also send a direct message to multiple users, use shift+click to select multiple users.
To deselect a user from the client list to message everyone again ctrl+click highlighted names.

### Bonus Features
DDOS includes a client list so users can easily see who is in the chatroom.
Direct-DDOSing (Direct Message) support is included between any number of clients so the user can direct message as many/few people as they want.
Speaking of seeing who's in the chatroom, we have optimized the color palette to make it as (difficult) easy as possible to distinguish between private and public messages.
