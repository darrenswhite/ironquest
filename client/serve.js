const express = require('express');
const expressStaticGzip = require('express-static-gzip');
const path = require('path');

const PORT = Number(process.env.PORT || 8081);

const STATIC_DIR = path.resolve(__dirname, 'build');

const app = express();

app.use(
  expressStaticGzip(STATIC_DIR, {
    enableBrotli: true,
  })
);

app.listen(PORT, () => console.log(`Listening on port ${PORT}`));
