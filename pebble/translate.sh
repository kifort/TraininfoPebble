#!/bin/sh

#  translate.sh
#  pebble
#
#  Created by Kifor Tamás on 2015. 02. 14..
#  Copyright (c) 2015. Kifor Tamás. All rights reserved.

rm resources/locale_english.bin
rm resources/locale_german.bin
rm resources/locale_hungarian.bin

python dict2bin.py locale_english.json
python dict2bin.py locale_german.json
python dict2bin.py locale_hungarian.json

mv locale_english.bin resources/
mv locale_german.bin resources/
mv locale_hungarian.bin resources/