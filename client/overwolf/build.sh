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

cp -r manifest.json ../assets/ build/ "${APP_DIR}"
cp -r ../assets/logo-258-198.png ../assets/icon-55x55.png ../assets/screenshot*.png description.txt store.json "${STORE_DIR}"

pushd "${TMP_DIR}"
zip -r "${ZIP_FILE}" .
popd
mv "${TMP_DIR}/${ZIP_FILE}" .
rm -rf "${TMP_DIR}"
