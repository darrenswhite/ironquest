#!/bin/bash

set -e

TMP_DIR="/tmp/ironquest"
APP_DIR="${TMP_DIR}/app"
STORE_DIR="${TMP_DIR}/store"
ZIP_FILE="ironquest.zip"

COMPILE='false'
UPDATE_STORE='false'

function usage() {
  cat << EOF
  Usage: $0 [-hs]

  -h --help            Display help
  -c --compile         Compile using npm
  -s --update-store    Update the store directory
EOF
}

function parseArgs() {
  options=$(getopt -l compile,help,update-store -o chs -- "$@")

  if [ $? -ne 0 ]; then
    usage
    exit 1
  fi

  eval set -- "$options"

  while true; do
    case "$1" in
      -c | --compile)
        COMPILE='true'
        ;;
      -h | --help)
        usage
        exit 0
        ;;
      -s)
        UPDATE_STORE='true'
        ;;
      --)
        shift;
        break
        ;;
    esac

    shift
  done
}

function cleanup() {
  echo "Cleaning up old files..."

  rm -rf "${TMP_DIR}"
  rm -f "${ZIP_FILE}"
}

function compile() {
  echo "Compiling..."

  TARGET=overwolf API=external npm run compile
}

function copyFiles() {
  echo "Copying files..."

  mkdir -p "${APP_DIR}"

  cp -r manifest.json build/ "${APP_DIR}"
}

function convertAndCopyStoreFiles() {
  echo "Converting store files..."

  convert ../assets/logo-258x198.png ../assets/Tile.jpg
  convert ../assets/icon-55x55.png ../assets/Icon.jpg
  convert ../assets/screenshot-username.png ../assets/Screenshot1.jpg
  convert ../assets/screenshot-actions-horizontal.png ../assets/Screenshot2.jpg
  convert ../assets/screenshot-actions-vertical.png ../assets/Screenshot3.jpg
  convert ../assets/screenshot-settings.png ../assets/Screenshot4.jpg

  echo "Copying store files..."

  mkdir -p "${STORE_DIR}"
  mv ../assets/*.jpg "${STORE_DIR}"
  cp -r description.html store.json "${STORE_DIR}"
}

function zipFiles() {
  echo "Zipping files..."

  pushd "${TMP_DIR}" >/dev/null
  zip -r "${ZIP_FILE}" .
  popd >/dev/null

  mv "${TMP_DIR}/${ZIP_FILE}" .
  rm -rf "${TMP_DIR}"
}

parseArgs "$@"

cleanup

if ${COMPILE}; then
  compile
fi

copyFiles

if ${UPDATE_STORE}; then
  convertAndCopyStoreFiles
fi

zipFiles
