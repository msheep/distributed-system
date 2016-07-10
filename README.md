# distributed-system
Distributed system project ------ Multi-server Activity Stream using java

## Setup servers
* Run 'server.jar' with following attributes 
```shell
java -jar server.jar -lp <local port number> (-lh <local host name>)
```
to start a root server. A secret will be returned can be found in console. Same secret will be used to start other servers.

**Attributes:**

Attribute | Meaning 
----:|:--------
-lp  | local port number
-lh  | local host name (optional, 'localhost' by default)
-s   | secret

* Run 'server.jar' with following attributes to start child servers:

**Attributes:**

Attribute | Meaning 
----:|:--------
-lp  | local port number
-lh  | local host name (optional, 'localhost' by default)
-rp  | remote port number
-rh  | remote host name
-s   | secret

## Client register & login

* Start client: user can either start a client with attributes below, or type the same attributes in the window popped up.
**Attributes:**

Attribute | Meaning 
----:|:--------
-u  | user name
-rp  | remote port number
-rh  | remote host name
