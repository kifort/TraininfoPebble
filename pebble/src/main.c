#include <pebble.h>
#include "localize.h"
#include "android_listener.h"
#include "android_client.h"
#include "clock_window.h"

//Resolution: 144×168 (144x152 without titlebar)  

int main(void) {
  locale_init();
  init_android_listener();
  send_get_stations();
  show_clock_window("", "");
  app_event_loop();
  
  return 0;
}