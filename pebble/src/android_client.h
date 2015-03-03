#pragma once
void send_log(char* message);
void send_get_timetable(char* from_station, char* to_station);
void send_stop_timetable_update();
void send_get_stations();
void send_int(uint8_t key, uint8_t cmd);

enum {
  LOG_MESSAGE = 0,
  FROM_STATION = 1,
  TO_STATION = 2,
  TIME_UPDATE = 3,
  ACK = 4,
  BYE = 5,
  STOP_TIMETABLE_UPDATE = 6,
  GET_STATIONS = 7
};
