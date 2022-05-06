# BasicChatServerClient  
Made with Java using JavaFX and mySQLConnector  
A basic server/chat program I made for fun whilst taking my Computer Networking class. All registered accounts are stored on a MySQL database.  
Currently only connects over LAN as my current wifi does not support portfowarding and also password isn't encrypted but that is on the to-do list.

Currently, the server can be set to start automatically on launch or be started/stopped via buttons.  
From the admin panel, a table of data from the SQL is displayed where the server admin can modify certain user attributes such as banning/unbanning, deleting account, or making the user an admin.

The client registration form actually checks for duplicate username/email and will prohibit the user from creating an account.  
It also checks the specified username/password to see if it matches in the SQL database before letting the user enter.  
The client can be set to remember username entered by storing it in a file locally to load next the the client is opened.  
Administrators will have the [ADMIN] prefix before their name in chat and are able to access more commands than the normal user.  

# Server 
![image](https://user-images.githubusercontent.com/101494059/167072170-f2e3fa20-8b47-4a5a-afff-dacca1e228bb.png) ![image](https://user-images.githubusercontent.com/101494059/167072123-c182af98-51e9-47c5-b4d7-aeaa21867c3b.png)  

# Client  
![image](https://user-images.githubusercontent.com/101494059/167072319-41639bd3-8454-4677-ae99-d48324f97de8.png) ![image](https://user-images.githubusercontent.com/101494059/167072331-437fda20-b071-47af-ba40-9c16f590cc85.png)
![image](https://user-images.githubusercontent.com/101494059/167072381-7b00eca0-41fd-4f9b-8d32-6910421e6c03.png)
![image](https://user-images.githubusercontent.com/101494059/167073344-fcdcfe2c-5dbd-4db8-bebf-9094facda627.png)
![image](https://user-images.githubusercontent.com/101494059/167073376-96bc3e79-d958-45d6-9526-c2373fcc539c.png)

# MySQL Database 
![image](https://user-images.githubusercontent.com/101494059/167073116-623ca6f6-872f-43b6-9858-a6da2e080b13.png)


