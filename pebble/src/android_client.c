#include <pebble.h>
#include "android_client.h"

void send_log(char* message)
{
    DictionaryIterator *iter;
    app_message_outbox_begin(&iter);
      
    Tuplet value = TupletCString(LOG_MESSAGE, message);
    dict_write_tuplet(iter, &value);
      
    app_message_outbox_send();
}

void send_get_timetable(char* from_station, char* to_station)
{
    //send_log("send_get_timetable");
  
    DictionaryIterator *iter;
    app_message_outbox_begin(&iter);
      
    //Tuplet input_from_station = TupletCString(FROM_STATION, favourite_stations[from_station]);
    Tuplet input_from_station = TupletCString(FROM_STATION, from_station);
    dict_write_tuplet(iter, &input_from_station);
  
    //Tuplet input_to_station = TupletCString(TO_STATION, favourite_stations[to_station]);
    Tuplet input_to_station = TupletCString(TO_STATION, to_station);
    dict_write_tuplet(iter, &input_to_station);
      
    app_message_outbox_send();
}

void send_int(uint8_t key, uint8_t cmd)
{
    DictionaryIterator *iter;
    app_message_outbox_begin(&iter);
      
    Tuplet value = TupletInteger(key, cmd);
    dict_write_tuplet(iter, &value);
      
    app_message_outbox_send();
}