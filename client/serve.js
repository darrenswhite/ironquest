const path = require('path');

const compression = require('compression');
const express = require('express');

const PORT = Number(process.env.PORT || 8081);

const STATIC_DIR = path.resolve(__dirname, 'build');

const app = express();

app.use(compression());

app.use(express.static(STATIC_DIR));

app.listen(PORT, () => console.log(`Listening on port ${PORT}`));
