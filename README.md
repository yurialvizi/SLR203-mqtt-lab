# SLR203-mqtt-lab

sudo update-alternatives --config java

1. cd jorammq-mqtt-ada-1.18.0/jorammq-mqtt-1.18.0/bin
2. ./jorammq-server
3. cd ../..
4. java -jar mqtt-spy-1.0.0.jar

To stop the server: ./jorammq-admin -stop


## Question 4

### Test 4.1

Same behavior as before: subscriber receives only one message from the publisher. If the subscriber is not running when the publisher sends a message, the subscriber misses the message

### Test 4.2

Same as before

### Test 4.3

Now the subscriber does not lose the messages sent when it is off.

### Test 4.4

The subscriber misses the messages sent when it is off.

### Test 4.5

What will happen when:
* clean_session = false for the subscriber and true for the publisher; qos = 1 for both clients ?

The subscriber receives the messages even if it is offline

* same as above, except qos = 0 for the publisher ?

The subscriber doesn't receive the message whe it is offline

* same as above, with qos = 2 for both clients ?

The subscriber receives the messages even if it is offline

#### Using the Retain Flag

With retain = 'true' for the publisher (it is not possible to set it true for the subscriber(?)), clean_session='true' only for the publisher and qos=2 for both, the subscriber can receive messages that were sent when it was offline, but it receives them duplicated.
With clean_session='true' for both and qos=0 for both, the subscriber receives again the last message sent.

### Test 4.6

Explained above

### Test 4.7

It receives just the last one

### Test 4.8

Last will can be set with
connectOptions.setWill(topic, lastWillMessage.getBytes(), qos, retain);

The subscriber received the message

## Question 5

### Ex. 5.1

