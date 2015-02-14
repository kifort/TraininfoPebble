#include <pebble.h>
#include "android_listener.h"
#include "android_client.h"
#include "clock_window.h"
  
static void in_received_handler(DictionaryIterator *iter, void *context)
{
    Tuple *t = dict_read_first(iter);
    if(t)
    {
      //if(!strcmp(t->value->cstring, "A vonat elment")) {
      if(t->key == BYE) {
          //window_pop_all();
          //window_stack_pop(current_window)
          //send_log("BYE? " + t->key->cstring);
          //window_stack_pop_all(true);
          update_time("00:00");
        } else {
          update_time(t->value->cstring);
          send_int(ACK, 0);
        }
    }
}

void init_android_listener(void) {
  //Register AppMessage events
  app_message_register_inbox_received(in_received_handler);
  app_message_open(512, 512);    //Large input and output buffer sizes
}
