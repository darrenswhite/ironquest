#!/bin/bash

set -e

TMP_DIR="/tmp/ironquest"
APP_DIR="${TMP_DIR}/app"
STORE_DIR="${TMP_DIR}/store"
ZIP_FILE="ironquest.zip"

TARGET=overwolf API=external npm run compile

rm -rf "${TMP_DIR}"
rm -f "${ZIP_FILE}"
mkdir -p "${APP_DIR}" "${STORE_DIR}"

convert ../assets/logo-258x198.png ../assets/Tile.jpg
convert ../assets/icon-55x55.png ../assets/Icon.jpg
convert ../assets/screenshot-username.png ../assets/Screenshot1.jpg
convert ../assets/screenshot-actions-horizontal.png ../assets/Screenshot2.jpg
convert ../assets/screenshot-actions-vertical.png ../assets/Screenshot3.jpg
convert ../assets/screenshot-settings.png ../assets/Screenshot4.jpg

cp -r manifest.json build/ "${APP_DIR}"
mv ../assets/*.jpg "${STORE_DIR}"
cp -r description.html store.json "${STORE_DIR}"

pushd "${TMP_DIR}"
zip -r "${ZIP_FILE}" .
popd
mv "${TMP_DIR}/${ZIP_FILE}" .
rm -rf "${TMP_DIR}"
