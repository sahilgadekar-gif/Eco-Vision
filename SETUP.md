# Vasundhara v2 — Complete Setup Guide

## Quick Start (Docker)
```bash
cp backend/.env.example backend/.env   # fill values
cp ai-service/.env.example ai-service/.env
docker-compose up --build -d
```

---

## 1. Backend
```bash
cd backend && npm install
cp .env.example .env   # fill MONGODB_URI, JWT_SECRET, GOOGLE_CLIENT_ID
npm run dev            # http://localhost:5000
```

## 2. AI Service
```bash
cd ai-service
python -m venv venv && source venv/bin/activate
pip install -r requirements.txt
python app.py          # http://localhost:8000
```

## 3. Android App
1. Open `Vasundhara/` in Android Studio Hedgehog+
2. Set `BASE_URL` in `app/build.gradle.kts`
3. Add Google Maps API key
4. Sync Gradle → Run on API 26+ device

---

## API Reference

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | — | Register |
| POST | `/api/auth/login` | — | Login |
| POST | `/api/auth/google` | — | Google Sign-In |
| POST | `/api/auth/refresh` | — | Refresh JWT |
| GET | `/api/user/profile` | JWT | Profile |
| GET | `/api/user/dashboard` | JWT | Dashboard stats |
| PUT | `/api/user/preferences` | JWT | Update theme/language |
| POST | `/api/waste/detect` | JWT | AI waste detection |
| POST | `/api/carbon/calculate` | JWT | Log carbon activity |
| GET | `/api/carbon/history` | JWT | Carbon trend |
| POST | `/api/rhi/calculate` | JWT | Calculate RHI |
| GET | `/api/rhi/latest` | JWT | Latest RHI |
| GET | `/api/recommendations` | JWT | AI recommendations |
| POST | `/api/recommendations/chat` | JWT | AI chat |
| GET | `/api/gamification/challenges` | JWT | Daily challenges |
| POST | `/api/gamification/complete/:id` | JWT | Complete challenge |
| GET | `/api/gamification/leaderboard` | JWT | Leaderboard |

---

## Features Implemented

| Feature | Status |
|---------|--------|
| Dark/Light theme toggle + DataStore persist | ✅ |
| English / Hindi / Marathi i18n | ✅ |
| JWT Auth + Google Sign-In + Refresh tokens | ✅ |
| Onboarding flow | ✅ |
| Dashboard with animated CO₂ hero + RHI card | ✅ |
| River Health Index (RHI) gauge + calculator | ✅ |
| AI Waste Detection (ResNet50) | ✅ |
| Carbon Tracker with live calculation | ✅ |
| AI Chat (multilingual, LLM + fallback) | ✅ |
| Gamification: challenges, leaderboard, badges | ✅ |
| Eco Map (Google Maps + markers) | ✅ |
| Profile with level progression | ✅ |
| Settings screen | ✅ |
| Shimmer loading skeletons | ✅ |
| Smooth screen transitions | ✅ |
