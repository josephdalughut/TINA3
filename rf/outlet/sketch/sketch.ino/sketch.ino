//libraries

#include <SPI.h>
#include <printf.h>
#include <nRF24L01.h>
#include <RF24_config.h>
#include <RF24.h>

//uuid for this outlet
const String UUID = "sample";
const String STATE_ON = "ON", STATE_OFF = "OFF";
String message = "";
int msg[1];


//rf radio (pins 9 & 10)
RF24 radio(9,10);

// Radio pipe addresses for the 2 nodes to communicate.
const uint64_t rf_pipes[2] = { 0xF0F0F0F0E1LL, 0xF0F0F0F0D2LL };

String appendUUIDHead(String message){
  String head = String(UUID);
  head.concat(String("_"));
  head.concat(message);
  return head;
}

String getState(){
  return digitalRead(8) == true ? STATE_ON : STATE_OFF;
}

String setState(String state){
  String currentState = getState();
  if(currentState != state){
    if(state == STATE_ON){
      digitalWrite(8, HIGH);
    }
    if(state == STATE_OFF){
      digitalWrite(8, LOW);
    }
  }
  return state;
}

void ping(String message){
  Serial.println("Pinging back message:");
  Serial.println(message);
  radio.stopListening();
  String callback = appendUUIDHead(message);
  radio.write(&callback, callback.length());
  radio.startListening();
}

void pong(String message){
  Serial.println("Ponging message:");
  Serial.println(message);
  int uuid_idx = message.indexOf("_");
  int cmd_idx = message.indexOf("_", uuid_idx + 1);
  int val_idx = message.indexOf("_", cmd_idx + 1);
  String uuid = message.substring(0, uuid_idx);
  String cmd = message.substring(uuid_idx + 1, cmd_idx);
  String value = message.substring(cmd_idx + 1, val_idx);
  uuid.toUpperCase();
  cmd.toUpperCase();
  value.toUpperCase();
  Serial.println("UUID, CMD, VALUE are");
  Serial.println(uuid);
  Serial.println(cmd);
  Serial.println(value);
  if(message != UUID){
    Serial.println("UUID mismatch, returning.");
    return;
  }
  if(cmd == "TO"){
    String callback = String("IS_");
    callback.concat(setState(value));
    ping(callback);
    return;
  }
  if(cmd == "SAY"){
    String callback = String("IS_");
    callback.concat(getState());
    ping(callback);
    return;
  }
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(57600);
  printf_begin();
  UUID.toUpperCase();
  pinMode(8, OUTPUT);
  digitalWrite(8, HIGH);
  Serial.println("TINA3 Wireless Outlet");
  Serial.println("Outlet number:");
  Serial.println(UUID);
  Serial.println("starting up radio");
  radio.begin();
  radio.setRetries(15,15);

  radio.openWritingPipe(rf_pipes[1]);
  radio.openReadingPipe(1,rf_pipes[0]);
  radio.startListening();
  radio.printDetails();
  Serial.println("Now listening");
}

void loop() {
  if (radio.available()){
    //bool done = false;  
      //done = radio.read(msg, 1); 
      radio.read(msg, 1); 
      char theChar = msg[0];
      if (msg[0] != '*'){
        message.concat(theChar);
        }
      else {
        pong(message);
        message = "";
      }
  }
}
