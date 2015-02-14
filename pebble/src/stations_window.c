#include <pebble.h>
#include "localize.h"
#include "stations_window.h"
#include "clock_window.h"
#include "android_client.h"

int number_of_selectable_stations = 5;
char* favourite_stations[5] = {"Verőce", "BUDAPEST*", "Vác", "Rákospalota-Újpest", "Vác-Alsóváros"};
int from_station = 5;
int to_station = 5;

static TextLayer *title_textlayer;

static int get_selectable_station_index(int index) {
  return (from_station>index)?index:index+1;
}

void draw_row_callback(GContext *ctx, Layer *cell_layer, MenuIndex *cell_index, void *callback_context)
{
    int favourite_index;
    switch(cell_index->row)
    {
    case 0:
        favourite_index = get_selectable_station_index(0);
        menu_cell_basic_draw(ctx, cell_layer, favourite_stations[favourite_index], NULL, NULL);
        break;
    case 1:
        favourite_index = get_selectable_station_index(1);
        menu_cell_basic_draw(ctx, cell_layer, favourite_stations[favourite_index], NULL, NULL);
        break;
    case 2:
        favourite_index = get_selectable_station_index(2);
        menu_cell_basic_draw(ctx, cell_layer, favourite_stations[favourite_index], NULL, NULL);
        break;
    case 3:
        favourite_index = get_selectable_station_index(3);
        menu_cell_basic_draw(ctx, cell_layer, favourite_stations[favourite_index], NULL, NULL);
        break;
    case 4:
        favourite_index = get_selectable_station_index(4);
        menu_cell_basic_draw(ctx, cell_layer, favourite_stations[favourite_index], NULL, NULL);
        break;
    }
}
 
uint16_t num_rows_callback(MenuLayer *menu_layer, uint16_t section_index, void *callback_context)
{
  return from_station == number_of_selectable_stations ? number_of_selectable_stations : number_of_selectable_stations - 1;
}
 
void select_click_callback(MenuLayer *menu_layer, MenuIndex *cell_index, void *callback_context)
{
  if(from_station == number_of_selectable_stations) {
    from_station = cell_index->row;
    text_layer_set_text(title_textlayer, _("Destination"));
    menu_layer_reload_data ( menu_layer );
  } else {
    to_station = (cell_index->row<from_station)?cell_index->row:cell_index->row+1;
    send_get_timetable(favourite_stations[from_station], favourite_stations[to_station]);
    show_clock_window(favourite_stations[from_station], favourite_stations[to_station]);
  }
}

void research(void) {
  send_get_timetable(favourite_stations[from_station], favourite_stations[to_station]);
}

void newsearch(void) {
  from_station = number_of_selectable_stations;
  to_station = number_of_selectable_stations;
  show_stations_window();
}


static void fix_auto_generated_ui_code(Window *s_window, MenuLayer *stations_menulayer) {
  //Instead of layer_add_child(window_get_root_layer(s_window), (Layer *)stations_menulayer);
  MenuLayerCallbacks callbacks = {
    .draw_row = (MenuLayerDrawRowCallback) draw_row_callback,
    .get_num_rows = (MenuLayerGetNumberOfRowsInSectionsCallback) num_rows_callback,
    .select_click = (MenuLayerSelectCallback) select_click_callback
  };
  menu_layer_set_callbacks(stations_menulayer, NULL, callbacks);
  layer_add_child(window_get_root_layer(s_window), menu_layer_get_layer(stations_menulayer));  
}

// BEGIN AUTO-GENERATED UI CODE; DO NOT MODIFY
static Window *s_window;
static GFont s_res_gothic_28_bold;
static MenuLayer *stations_menulayer;

static void initialise_ui(void) {
  s_window = window_create();
  window_set_fullscreen(s_window, false);
  
  s_res_gothic_28_bold = fonts_get_system_font(FONT_KEY_GOTHIC_28_BOLD);
  // title_textlayer
  title_textlayer = text_layer_create(GRect(0, 0, 144, 40));
  text_layer_set_text(title_textlayer, _("Departure"));
  text_layer_set_text_alignment(title_textlayer, GTextAlignmentCenter);
  text_layer_set_font(title_textlayer, s_res_gothic_28_bold);
  layer_add_child(window_get_root_layer(s_window), (Layer *)title_textlayer);
  
  // stations_menulayer
  stations_menulayer = menu_layer_create(GRect(0, 40, 144, 112));
  menu_layer_set_click_config_onto_window(stations_menulayer, s_window);
  fix_auto_generated_ui_code(s_window, stations_menulayer);  
}

static void destroy_ui(void) {
  window_destroy(s_window);
  text_layer_destroy(title_textlayer);
  menu_layer_destroy(stations_menulayer);
}
// END AUTO-GENERATED UI CODE

static void handle_window_unload(Window* window) {
  destroy_ui();
}

void show_stations_window(void) {
  initialise_ui();
  window_set_window_handlers(s_window, (WindowHandlers) {
    .unload = handle_window_unload,
  });
  window_stack_push(s_window, true);
}

void hide_stations_window(void) {
  window_stack_remove(s_window, true);
}
