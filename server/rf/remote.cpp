#include <cstdlib>
#include <iostream>
#include <sstream>
#include <string>
#include <RF24/RF24.h>
#include <unistd.h>


using namespace std;

//initialize radio
// CE Pin, CSN Pin, SPI Speed

// Setup for GPIO 22 CE and CE1 CSN with SPI Speed @ 1Mhz
//RF24 radio(RPI_V2_GPIO_P1_22, RPI_V2_GPIO_P1_26, BCM2835_SPI_SPEED_1MHZ);

// Setup for GPIO 22 CE and CE0 CSN with SPI Speed @ 4Mhz
//RF24 radio(RPI_V2_GPIO_P1_15, BCM2835_SPI_CS0, BCM2835_SPI_SPEED_4MHZ);

// NEW: Setup for RPi B+
//RF24 radio(RPI_BPLUS_GPIO_J8_15,RPI_BPLUS_GPIO_J8_24, BCM2835_SPI_SPEED_8MHZ);

// Setup for GPIO 22 CE and CE0 CSN with SPI Speed @ 8Mhz
RF24 radio(RPI_V2_GPIO_P1_15, RPI_V2_GPIO_P1_24, BCM2835_SPI_SPEED_8MHZ);


//pipes
const uint64_t rf_pipes[2] = { 0xF0F0F0F0E1LL, 0xF0F0F0F0D2LL };
int msg[1];
string readmessage = "";

int main(int argc, char** argv){
  if (argc <= 1)
  {
    cout << "no param received, exiting";
    return 1;
  }
  string message(argv[1]);
  printf("TINA3 Server\n");
  printf("starting up radio\n");
  //startup our radio
  radio.begin();
  //radio.enableDynamicPayloads();
  radio.setRetries(15,15);
  radio.openWritingPipe(rf_pipes[0]);
  radio.openReadingPipe(1,rf_pipes[1]);
  radio.printDetails();
  

  printf("now writing message\n");

  bool timedout = false;
  int length = message.length();
    for (int i = 0; i < length; i++) {
printf("writing char\n");
      int charToSend[1];
      charToSend[0] = message.at(i);
      radio.write(charToSend,1);
printf("char written\n");
    }
    msg[0] = '*';
    radio.write(msg,1);
	printf("message written, waiting for response\n");
    unsigned long now = millis();
    bool waiting = true;
    radio.startListening();
    while(waiting && timedout){
      timedout = (millis() - now > 1000);
      radio.read(msg, 1);
      char theChar = msg[0];
      if (msg[0] != '*'){
        readmessage += theChar;
      }else{
	waiting = false;
	radio.stopListening();
	radio.powerDown();
	printf("Message sent!\n");
	return 0;
	}
    }
    radio.stopListening();
    radio.powerDown();
    cout << timedout ? "timeout" : message;
    return 1;
}
