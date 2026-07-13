# Vasundhara: AI-Powered Environmental Monitoring & Carbon Tracking Platform



## Overview



Eco Vision is an integrated, AI-driven environmental monitoring platform that combines personal carbon footprint tracking with large-scale satellite imagery analysis for water pollution detection. It bridges the gap between individual daily awareness and macro-level environmental management.

---

## Core Features



* **Carbon Footprint Tracking:** Log daily activities (travel, energy consumption, food intake, and waste generation) to calculate and monitor personal $CO_{2}$ emissions.


* **Satellite-Based Pollution Detection:** Fetches Sentinel-2 satellite imagery via automated cron jobs and APIs to detect and segment water contamination and plastic waste.


* **AI-Driven Data Pipeline:** Processes multi-source data through message queuing (Apache Kafka/RabbitMQ) and ETL workflows (Apache Airflow), utilizing CNN, UNET++, and LSTM for predictive analysis.


* **Interactive Dashboards & Alerts:** Provides visual analytics, heatmaps, charts (MPAndroidChart), and real-time push notifications (Firebase FCM) for threshold alerts.



---

## Technology Stack



* **Frontend:** Kotlin (Android Studio), XML, MPAndroidChart


* **Backend:** Node.js, Express.js, REST APIs, JWT/Firebase Authentication


* **AI/ML:** Python, TensorFlow, PyTorch, Scikit-learn, OpenCV, CNN, UNET++, LSTM


* **Database & Storage:** MongoDB, PostgreSQL, AWS S3, Redis


* **Cloud & DevOps:** AWS/GCP, Docker, Apache Airflow, Apache Kafka/RabbitMQ



---

