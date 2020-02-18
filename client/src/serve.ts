import express from 'express';
import path from 'path';

const PORT = Number(process.env.PORT || 8080);

const STATIC_DIR = path.resolve(__dirname, '..', 'build');

const app = express();

app.use(express.static(STATIC_DIR));

app.listen(PORT, () => console.log(`Listening on port ${PORT}`));
