#!/usr/bin/env python

import RPi.GPIO as GPIO
from lib_nrf24 import NRF24
import time
import spidev
import sys
import cgitb

cgitb.enable()

GPIO.setmode(GPIO.BCM)

pipes = [[0xE8, 0xE8, 0xF0, 0xF0, 0xE1], [0xF0, 0xF0, 0xF0, 0xF0, 0xE1]]

radio = NRF24(GPIO, spidev.SpiDev())
radio.begin(0, 17)

radio.setPayloadSize(32)
radio.setChannel(0x76)
radio.setDataRate(NRF24.BR_1MBPS)
radio.setPALevel(NRF24.PA_MIN)

radio.setAutoAck(True)
radio.enableDynamicPayloads()
radio.enableAckPayload()

radio.openWritingPipe(pipes[0])
radio.openReadingPipe(1, pipes[1])
#radio.printDetails()
# radio.startListening()
if len(sys.argv) != 2:
    print ("ERROR: missing args")
else:
    message = list(str(sys.argv[1]))
    returned=1
    while len(message) < 32:
        message.append(0)
    retry=0
    while(retry < 5 && returned=1):
        start = time.time()
        radio.write(message)
        #print("Sent the message: {}".format(message))
        radio.startListening()
        while not radio.available(0):
            time.sleep(1 / 100)
            if time.time() - start > 2:
                #print("Timed out.")
                break

        receivedMessage = []
        radio.read(receivedMessage, radio.getDynamicPayloadSize())
        #print("Received: {}".format(receivedMessage))
        string = ""
        for n in receivedMessage:
            # Decode into standard unicode set
            if (n >= 32 and n <= 126):
                string += chr(n)
        #print("Out received message decodes to: {}".format(string))
        if len(string) > 1:
            #print ("Callback received:")
            print string
            returned=0
            radio.write("")
            #sys.stdout.write(string);
    	#print ("ERROR: No Response")
    	#sys.exit(0)
        radio.stopListening()
        retry = retry + 1
        time.sleep(1/100)
    if returned != 0:
        print ("ERROR: timed out")
    radio.stopListening()
    radio.powerDown()
#sys.exit(0)
