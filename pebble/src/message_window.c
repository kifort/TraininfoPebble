#include <pebble.h>
#include "message_window.h"
#include "stations_window.h"

static char message[500];

static void select_click_handler(ClickRecognizerRef recognizer, void *context)
{
  //FIXME memory leakage
  //newsearch();
}

static void back_click_handler(ClickRecognizerRef recognizer, void *context)
{
  stopsearch();
  window_stack_pop_all(true);
}

static void click_config_provider(void *context)
{
  window_single_click_subscribe(BUTTON_ID_SELECT, select_click_handler);
  window_single_click_subscribe(BUTTON_ID_BACK, back_click_handler);
}

static Window *s_window;
static TextLayer *message_textlayer;

static void initialise_ui(void) {
  s_window = window_create();
  window_set_fullscreen(s_window, false);
  
  // message_textlayer
  message_textlayer = text_layer_create(GRect(0, 0, 144, 168));
  text_layer_set_text(message_textlayer, "");
  text_layer_set_text_alignment(message_textlayer, GTextAlignmentCenter);
  layer_add_child(window_get_root_layer(s_window), (Layer *)message_textlayer);
  
  window_set_click_config_provider(s_window, click_config_provider);
}

static void destroy_ui(void) {
  window_destroy(s_window);
  text_layer_destroy(message_textlayer);
}

static void handle_window_unload(Window* window) {
  destroy_ui();
}

void show_message_window(char* p_message) {
  initialise_ui();
  strcpy(message, p_message);
  text_layer_set_text(message_textlayer, message);
  window_set_window_handlers(s_window, (WindowHandlers) {
    .unload = handle_window_unload,
  });
  window_stack_push(s_window, true);
}

void hide_message_window(void) {
  window_stack_remove(s_window, true);
}

void update_message(char* message) {
  text_layer_set_text(message_textlayer, message);
}
