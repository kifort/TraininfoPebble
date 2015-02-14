#include <pebble.h>
#include "localize.h"
#include "android_listener.h"
#include "stations_window.h"

//Resolution: 144×168 (144x152 without titlebar)  

int main(void) {
  locale_init();
  init_android_listener();
  show_stations_window();
  app_event_loop();
  return 0;
}