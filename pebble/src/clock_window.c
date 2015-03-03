#include <pebble.h>
#include "clock_window.h"
#include "stations_window.h"

static char from_station[100];
static char to_station[100];

static void select_click_handler(ClickRecognizerRef recognizer, void *context)
{
    //FIXME memory leakage
    //newsearch();
}

static void down_click_handler(ClickRecognizerRef recognizer, void *context)
{
    research();
}

static void back_click_handler(ClickRecognizerRef recognizer, void *context)
{
    stopsearch();
    window_stack_pop_all(true);
}

static void click_config_provider(void *context)
{
    window_single_click_subscribe(BUTTON_ID_SELECT, select_click_handler);
    window_single_click_subscribe(BUTTON_ID_DOWN, down_click_handler);
    window_single_click_subscribe(BUTTON_ID_BACK, back_click_handler);
}

// BEGIN AUTO-GENERATED UI CODE; DO NOT MODIFY
static Window *s_window;
static GFont s_res_bitham_42_bold;
static TextLayer *clock_textlayer;
static TextLayer *from_textlayer;
static TextLayer *to_textlayer;

static void initialise_ui(void) {
  s_window = window_create();
  window_set_fullscreen(s_window, false);
  
  s_res_bitham_42_bold = fonts_get_system_font(FONT_KEY_BITHAM_42_BOLD);
  
  // clock_textlayer
  clock_textlayer = text_layer_create(GRect(0, 50, 144, 52));
  text_layer_set_text(clock_textlayer, "??:??");
  text_layer_set_text_alignment(clock_textlayer, GTextAlignmentCenter);
  text_layer_set_font(clock_textlayer, s_res_bitham_42_bold);
  layer_add_child(window_get_root_layer(s_window), (Layer *)clock_textlayer);
  
  // from_textlayer
  from_textlayer = text_layer_create(GRect(0, 0, 144, 20));
  text_layer_set_text_alignment(from_textlayer, GTextAlignmentCenter);
  layer_add_child(window_get_root_layer(s_window), (Layer *)from_textlayer);
  
  // to_textlayer
  to_textlayer = text_layer_create(GRect(0, 132, 144, 20));
  text_layer_set_text_alignment(to_textlayer, GTextAlignmentCenter);
  layer_add_child(window_get_root_layer(s_window), (Layer *)to_textlayer);
    
  window_set_click_config_provider(s_window, click_config_provider);
}

static void destroy_ui(void) {
  window_destroy(s_window);
  text_layer_destroy(clock_textlayer);
  text_layer_destroy(from_textlayer);
  text_layer_destroy(to_textlayer);
}
// END AUTO-GENERATED UI CODE

static void handle_window_unload(Window* window) {
  destroy_ui();
}

void show_clock_window(char* p_from_station, char* p_to_station) {
  initialise_ui();
  //char from_station[strlen(p_from_station)];
  //char to_station[strlen(p_to_station)];
  strcpy(from_station, p_from_station);
  strcpy(to_station, p_to_station);
  text_layer_set_text(from_textlayer, from_station);
  text_layer_set_text(to_textlayer, to_station);
  window_set_window_handlers(s_window, (WindowHandlers) {
      .unload = handle_window_unload,
    });
  window_stack_push(s_window, true);
}

void hide_clock_window(void) {
  window_stack_remove(s_window, true);
}

void update_time(char* message) {
  text_layer_set_text(clock_textlayer, message);
}
