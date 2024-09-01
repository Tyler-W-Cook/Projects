 #include <CapacitiveSensor.h>
 #include <LiquidCrystal.h>

CapacitiveSensor mySensor(2, 4); //creates a measurement across the pins
void setup() { //just sets up the rate of the code
    Serial.begin(9600);
    
}

// the forever loop:
void loop() {
    int thermistorValue = analogRead(A0); //reads the analog input
  float voltage= thermistorValue * (5.0 / 1023.0); // simple conversion of the analog reading back into a voltage (0 - 5V):
 // Serial.println(voltage); // prints the data which then can be plotted/seen
  float temp = voltage*(33.3) - 6.6;
long start = millis();
long cap = mySensor.capacitiveSensor(30);
if(cap < 3) {
  digitalWrite(13, HIGH);
} else {
digitalWrite(13, LOW);
}
//Serial.println(cap);
delay(1000);
int rtd = analogRead(A1);
//Serial.println(rtd); 
float sensfus = (temp + rtd)/2;
float filtered = kahlmanfilter(sensfus); //returns filtered temp
const int rs = 12, en = 11, d4 = 5, d5 = 6, d6 = 3, d7 = 7;
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);
lcd.print(filtered);
if (filtered < 10) {
  digitalWrite(14, LOW);
} else {
  digitalWrite(14, HIGH);
}

}

double kahlmanfilter(float sensfus) {
//Kalhman filter time :)

static const float H = 1.00; 
static const float R = 15; //covariance guess
static float P; //error covar
static float Q = 5;
static float gain;
static float sensfusestimate = 0;

gain  = (P*H)/(H*P*H+R);

sensfusestimate = sensfusestimate + gain*(sensfus-H*sensfusestimate);

P =  (1-gain*H)*P+Q;
return sensfusestimate;
}
