This is Kotlin based TCP service, and it satisfies the following points.

1. Start listening for TCP/UDP connections.
2. Be able to accept connections.
3. Read json payload ({"user_id": 1, "friends": [2, 3, 4]})
3. After establishing successful connection - it "stores" it in memory.
4. When another connection established with the user_id from the list of any other user's "friends" section, they should
   be notified about it with a message {"online": true}
5. When the user goes offline, his "friends" (if it has any and any of them online) should receive a message {"online":
   false}