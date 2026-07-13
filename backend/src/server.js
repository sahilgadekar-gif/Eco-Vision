require('dotenv').config();
const express = require('express');
const helmet = require('helmet');
const cors = require('cors');
const compression = require('compression');
const morgan = require('morgan');
const rateLimit = require('express-rate-limit');
const connectDB = require('./config/database');
const logger = require('./utils/logger');
const errorHandler = require('./middleware/errorHandler');

const app = express();
app.use(helmet());
app.use(compression());
app.use(cors({ origin: (process.env.ALLOWED_ORIGINS || '').split(','), credentials: true }));
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true }));
app.use(morgan('combined', { stream: { write: m => logger.info(m.trim()) } }));
app.use('/api/', rateLimit({ windowMs: parseInt(process.env.RATE_LIMIT_WINDOW_MS) || 900000, max: parseInt(process.env.RATE_LIMIT_MAX) || 100, standardHeaders: true, legacyHeaders: false }));

app.use('/api/auth',            require('./routes/auth'));
app.use('/api/user',            require('./routes/user'));
app.use('/api/waste',           require('./routes/waste'));
app.use('/api/carbon',          require('./routes/carbon'));
app.use('/api/rhi',             require('./routes/rhi'));
app.use('/api/recommendations', require('./routes/recommendations'));
app.use('/api/gamification',    require('./routes/gamification'));

app.get('/health', (_, res) => res.json({ status: 'ok', timestamp: new Date() }));
app.use((req, res) => res.status(404).json({ success: false, message: `${req.originalUrl} not found` }));
app.use(errorHandler);

const start = async () => {
  await connectDB();
  const PORT = process.env.PORT || 5000;
  app.listen(PORT, () => logger.info(`🌿 Vasundhara backend on port ${PORT} [${process.env.NODE_ENV}]`));
};
start();
process.on('SIGTERM', () => { logger.info('Shutting down...'); process.exit(0); });
module.exports = app;
