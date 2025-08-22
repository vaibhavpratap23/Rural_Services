# GigFinder - Enterprise Home Services Platform

A comprehensive full-stack web application connecting clients with verified skilled workers for various service-based jobs, including construction and household services.

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.9+
- PostgreSQL 14+
- Node.js 18+

### Environment Setup
1. Create PostgreSQL database: `gigfinder`
2. Update `application.properties` with your database credentials
3. Run the application: `mvn spring-boot:run`
4. Frontend will be available at: `http://localhost:5173`

## 🎯 Enterprise Features

### ✅ Phase 1-3: Core MVP (Completed)
- **User Authentication**: JWT-based login/registration for Clients & Workers
- **Job Lifecycle**: OPEN → ASSIGNED → IN_PROGRESS → COMPLETED
- **Worker Verification**: Document upload (Aadhaar, address proof)
- **Real-time Notifications**: Job updates, worker assignments
- **Payment History**: Track all transactions

### ✅ Phase 4: Smart Matching & UX
- **Radius-based Search**: Find workers within specified distance
- **Worker Availability Toggle**: Online/Offline status
- **Instant Hire**: Quick worker matching for urgent jobs
- **Enhanced Job Cards**: Urban Company-style UI with status tracking

### ✅ Phase 5: Payment & Wallet System
- **In-app Wallet**: Load money, track balance
- **UPI Integration**: Add money via UPI
- **Worker Payouts**: Direct bank transfers
- **Transaction History**: Complete payment logs

### ✅ Phase 6: Admin Dashboard & Analytics
- **Platform Analytics**: Jobs, users, revenue metrics
- **Geo Heatmaps**: High-demand area visualization
- **Worker Leaderboard**: Top-rated professionals
- **Fraud Detection**: Report management system
- **User Management**: Ban/unban functionality

### ✅ Phase 7: Multi-Language Support
- **Language Selector**: English, Hindi, Marathi, Bengali, Tamil
- **Regional Support**: Localized UI elements
- **Cultural Adaptation**: India-specific features

## 🛠 Technical Stack

### Backend
- **Spring Boot 3.3.2**: RESTful APIs
- **Spring Security**: JWT authentication
- **PostgreSQL**: Primary database
- **Flyway**: Database migrations
- **Hibernate**: ORM framework

### Frontend
- **React 18**: Component-based UI
- **TypeScript**: Type safety
- **Tailwind CSS**: Modern styling
- **React Router**: Client-side routing
- **Axios**: HTTP client

## 📊 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### Jobs
- `GET /api/jobs` - List all jobs
- `POST /api/jobs` - Create new job
- `PUT /api/jobs/{id}/accept` - Accept job (worker)
- `PUT /api/jobs/{id}/start` - Start job
- `PUT /api/jobs/{id}/complete` - Complete job
- `GET /api/jobs/nearby` - Radius-based job search

### Workers
- `GET /api/workers/available` - Find available workers
- `PUT /api/workers/availability` - Toggle availability
- `PUT /api/workers/verification` - Upload documents

### Wallet
- `GET /api/wallet/balance` - Get wallet balance
- `POST /api/wallet/add-money` - Add money to wallet
- `POST /api/wallet/withdraw` - Withdraw money

### Admin
- `GET /api/admin/dashboard` - Platform statistics
- `GET /api/admin/jobs/heatmap` - Job location heatmap
- `GET /api/admin/workers/leaderboard` - Top workers
- `POST /api/admin/users/{id}/ban` - Ban user

## 🎨 UI Features

### Modern Design
- **Urban Company-inspired**: Clean, professional interface
- **Responsive Layout**: Mobile-first design
- **Status Badges**: Visual job progress indicators
- **Notification Bell**: Real-time updates with unread count

### Enhanced UX
- **Tabbed Interfaces**: Organized content sections
- **Loading States**: Smooth user feedback
- **Error Handling**: Graceful error messages
- **Form Validation**: Client-side validation

## 🔒 Security Features

### Authentication & Authorization
- **JWT Tokens**: Secure session management
- **Role-based Access**: CLIENT, WORKER, ADMIN roles
- **Password Hashing**: BCrypt encryption
- **CORS Configuration**: Cross-origin security

### Data Protection
- **Input Validation**: Server-side validation
- **SQL Injection Prevention**: Parameterized queries
- **XSS Protection**: Content Security Policy

## 📈 Scalability Features

### Performance
- **Database Indexing**: Optimized queries
- **Connection Pooling**: HikariCP configuration
- **Caching Strategy**: Redis-ready architecture

### Monitoring
- **Health Checks**: Application monitoring
- **Logging**: Comprehensive error tracking
- **Metrics**: Performance analytics

## 🚀 Deployment

### Production Setup
1. Configure production database
2. Set environment variables
3. Build frontend: `npm run build`
4. Deploy Spring Boot JAR
5. Configure reverse proxy (Nginx)

### Docker Support
```bash
# Build and run with Docker Compose
docker-compose up -d
```

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

---

**GigFinder** - Connecting skilled workers with clients across India 🇮🇳
