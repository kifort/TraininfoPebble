#!/bin/sh

#  getDictionary.sh
#  pebble
#
#  Created by Kifor Tamás on 2015. 02. 14..
#  Copyright (c) 2015. Kifor Tamás. All rights reserved.

rm locale_english_previous.json
rm locale_german_previous.json
rm locale_hungarian_previous.json

mv locale_english.json locale_english_previous.json
mv locale_german.json locale_german_previous.json
mv locale_hungarian.json locale_hungarian_previous.json

python gen_dict.py src/ locale_english.json
cp locale_english.json locale_german.json
cp locale_english.json locale_hungarian.json
