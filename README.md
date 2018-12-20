## Easy Paxos

This is a simple implementation of Multi-Paxos Replicated Logs system. 

It supports:

- [x] Setup 2f+1 replicas with config files

- [x] Config TCP/UDP connection

- [x] A user interface which can send messages to servers

- [x] Recover from fail-stop failures


The main implementation ideas come from the [user study](https://ramcloud.stanford.edu/~ongaro/userstudy/) of John Ousterhout and Diego Ongaro (Stanford University). Their [slides](https://ramcloud.stanford.edu/~ongaro/userstudy/paxos.pdf) and [implementation summary](are very helpful).



