#include <pebble.h>
#include "android_listener.h"
#include "android_client.h"
#include "clock_window.h"
#include "message_window.h"
#include "stations_window.h"

const int MAX_NUMBER_OF_FAVOURITE_STATIONS = 5;

static void in_received_handler(DictionaryIterator *iter, void *context)
{
    Tuple *firstElement = dict_read_first(iter);
    if(firstElement)
    {
      if(firstElement->key == TIME_UPDATE) {
        char* timeTillTrain = firstElement->value->cstring;
        if(strstr(timeTillTrain, ":") != NULL) {
          update_time(timeTillTrain);
        } else {
          //update_time("00:00");
        }

        send_int(ACK, 0);
      } else if(firstElement->key == BYE) {
        update_time("00:00");
      } else if(firstElement->key > GET_STATIONS) {
        char* favourite_stations[MAX_NUMBER_OF_FAVOURITE_STATIONS];
        int number_of_stations = 1;

        int station_index = ((firstElement->key) - GET_STATIONS) - 1;
        favourite_stations[station_index] = firstElement->value->cstring;
        for (; number_of_stations<MAX_NUMBER_OF_FAVOURITE_STATIONS; number_of_stations++) {
          Tuple *nextElement = dict_read_next(iter);
          if (nextElement == NULL)
          {
            break;
          }
          
          station_index = ((nextElement->key) - GET_STATIONS) - 1;
          favourite_stations[station_index] = nextElement->value->cstring;
        }

        app_log(APP_LOG_LEVEL_DEBUG, "android_listener", 36, "number_of_stations %d", number_of_stations);
        
        set_favourite_stations(favourite_stations, number_of_stations);
        //TODO pop
        newsearch();
      } else {
        char log_message [100];
        snprintf ( log_message, 100, "Unknown Message: %lu", firstElement->key );
        send_log(log_message);
      }
    }
}

void init_android_listener(void) {
  //Register AppMessage events
  app_message_register_inbox_received(in_received_handler);
  app_message_open(512, 512);    //Large input and output buffer sizes
}
