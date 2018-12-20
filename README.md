## Easy Paxos

This is a simple implementation of Multi-Paxos Replicated Logs system. 

It supports:

- [x] Setup 2f+1 replicas with config files
- [x] Config TCP/UDP connection
- [x] A user interface which can send messages to servers
- [x] Recover from fail-stop failures



In order to run and test easyPaxos, you need to import the project as a Maven project in Java IDE, like Eclipse and Intellij IDEA. After importing, run `ServerApp.java` and `UserApp.java`, then find results in the log file.



The main implementation ideas come from the [user study](https://ramcloud.stanford.edu/~ongaro/userstudy/) of John Ousterhout and Diego Ongaro (Stanford University). Their [slides](https://ramcloud.stanford.edu/~ongaro/userstudy/paxos.pdf) and [paxos summary](https://ramcloud.stanford.edu/~ongaro/userstudy/paxossummary.pdf) are very very helpful.



