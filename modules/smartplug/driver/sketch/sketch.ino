//libraries

#include <SPI.h>
#include <printf.h>
#include <nRF24L01.h>
#include <RF24_config.h>
#include <RF24.h>

//uuid for this outlet
const String UUID = "12345";
const String STATE_ON = "ON", STATE_OFF = "OFF";
String message = "";
int msg[1];


//rf radio (pins 9 & 10)
RF24 radio(9,10);

// Radio pipe addresses for the 2 nodes to communicate.
const uint64_t rf_pipes[2] = { 0xE8E8F0F0E1LL, 0xF0F0F0F0E1LL };

String appendUUIDHead(String message){
  String head = String("");
  head.concat(UUID);
  head.concat(String("_"));
  head.concat(message);
  return head;
}

String getState(){
  return digitalRead(8) == true ? STATE_OFF : STATE_ON;
}

String setState(String state){
  String currentState = getState();
  if(!currentState.equals(state)){
    if(state.equals(STATE_ON)){
      digitalWrite(8, LOW);
    }
    if(state.equals(STATE_OFF)){
      digitalWrite(8, HIGH);
    }
  }
  return state;
}

void ping(String message){
  Serial.println("Return message:");
  Serial.println(message);
  radio.stopListening();
  char chars[message.length()+1];
  message.toCharArray(chars, message.length()+1);
  radio.write(chars, sizeof(chars));
  radio.startListening();
}

void pong(String message){
  //Serial.println("Ponging message:");
  //Serial.println(message);
  int uuid_idx = message.indexOf("_");
  int cmd_idx = message.indexOf("_", uuid_idx + 1);
  int val_idx = message.indexOf("_", cmd_idx + 1);
  String uuid = message.substring(0, uuid_idx);
  String cmd = message.substring(uuid_idx + 1, cmd_idx);
  String value = message.substring(cmd_idx + 1, val_idx);
  uuid.toUpperCase();
  cmd.toUpperCase();
  value.toUpperCase();
  //Serial.println("UUID, CMD, VALUE are");
  //Serial.println(uuid);
  //Serial.println(cmd);
  //Serial.println(value);
  if(!uuid.equals(UUID)){
    Serial.println("UUID mismatch, returning.");
    return;
  }
  if(cmd.equals("TO")){
    String callback = String("");
    callback.concat(uuid);
    callback.concat("_IS_");
    callback.concat(setState(value));
    ping(callback);
    return;
  }
  if(cmd.equals("SAY")){
    String callback = String("");
    callback.concat(uuid);
    callback.concat("_IS_");
    callback.concat(getState());
    ping(callback);
    return;
  }
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(57600);
  //Serial.println("TINA3 Wireless Outlet");
  //Serial.println("Outlet number:");
  //Serial.println(UUID);
  //Serial.println("starting up radio");

  pinMode(8, OUTPUT);
  digitalWrite(8, HIGH);

  UUID.toUpperCase();
  radio.begin();
  radio.setPALevel(RF24_PA_MAX);
  radio.setChannel(0x76);
  
  radio.openWritingPipe(rf_pipes[1]);
  radio.openReadingPipe(1,rf_pipes[0]);
  radio.enableDynamicPayloads();
  radio.powerUp();
  radio.printDetails();
  Serial.println("listening");
}

void loop(void){
  radio.startListening();
  char receivedMessage[32] = {0};
  if(radio.available()){
    radio.read(receivedMessage, sizeof(receivedMessage));
    Serial.println("Received Message:");
    Serial.println(receivedMessage);
    String stringMessage(receivedMessage);
    pong(stringMessage);
  }
  delay(100);
 
}
