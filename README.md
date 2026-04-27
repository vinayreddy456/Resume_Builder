# AI Resume Optimizer

An intelligent resume optimization system that transforms existing resumes into highly optimized, ATS-friendly, and job-specific resumes using multiple AI agents.

## Features

### 🤖 AI-Powered Agents
- **Resume Parser Agent**: Extracts candidate details from PDF, DOC, DOCX, and TXT files
- **Job Description Analyzer Agent**: Analyzes job descriptions to extract requirements and keywords
- **Skill Gap Analyzer Agent**: Compares resume vs job requirements to identify gaps
- **Resume Rewriter Agent**: Rewrites resume content to match job requirements
- **ATS Optimization Agent**: Optimizes resume for Applicant Tracking Systems
- **Formatter Agent**: Formats resume professionally for maximum impact

### 🎯 Key Capabilities
- **Multi-format Support**: Upload resumes in PDF, DOC, DOCX, or TXT format
- **Intelligent Analysis**: Deep analysis of job descriptions and resume content
- **Skill Matching**: Automatic identification of matching and missing skills
- **ATS Optimization**: Keyword density optimization and formatting for ATS success
- **Professional Formatting**: Clean, professional resume structure
- **AI Enhancement**: Integration with Google Gemini API for advanced AI-powered analysis

### 💳 Payment Integration
- **Razorpay Integration**: Secure payment processing
- **Multiple Plans**: Free, Premium, and Enterprise tiers
- **Flexible Pricing**: Affordable pricing for different user needs

## Technology Stack

### Backend
- **Java 17** with Spring Boot 3.2.5
- **Maven** for dependency management
- **H2 Database** for development (configurable for production)
- **Apache POI** for document processing
- **PDFBox** for PDF processing
- **Spring Web** for REST API
- **Gemini API** for AI-powered analysis
- **Razorpay** for payment processing

### Frontend
- **React 18** with modern hooks
- **Tailwind CSS** for styling
- **Framer Motion** for animations
- **React Dropzone** for file uploads
- **Axios** for API communication
- **Lucide React** for icons

## Quick Start

### Prerequisites
- Java 17 or higher
- Node.js 16 or higher
- Maven 3.6 or higher

### Environment Configuration

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd java_Resume_Builder
   ```

2. **Configure Environment Variables**
   
   Create a `.env` file in the root directory:
   ```env
   # Application Configuration
   SPRING_PROFILES_ACTIVE=dev
   SERVER_PORT=8080

   # Database Configuration
   DB_URL=jdbc:h2:mem:testdb
   DB_USERNAME=sa
   DB_PASSWORD=password

   # Gemini API Configuration
   GEMINI_API_KEY=your_gemini_api_key_here
   GEMINI_API_URL=https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent

   # Razorpay Configuration
   RAZORPAY_KEY_ID=your_razorpay_key_id_here
   RAZORPAY_KEY_SECRET=your_razorpay_secret_key_here

   # File Upload Configuration
   MAX_FILE_SIZE=10MB
   UPLOAD_DIR=uploads

   # CORS Configuration
   CORS_ALLOWED_ORIGINS=http://localhost:3000
   ```

3. **Start the Backend**
   ```bash
   mvn spring-boot:run
   ```

4. **Start the Frontend**
   ```bash
   cd frontend
   npm install
   npm start
   ```

5. **Access the Application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console

## API Endpoints

### Resume Optimization
- `POST /api/resume/optimize` - Complete resume optimization
- `POST /api/resume/parse-resume` - Parse resume file
- `POST /api/resume/analyze-job` - Analyze job description
- `GET /api/resume/health` - Health check

### AI Enhancement (Gemini)
- `POST /api/gemini/enhance` - Enhance resume content
- `POST /api/gemini/cover-letter` - Generate cover letter
- `POST /api/gemini/analyze-fit` - Analyze job fit

### Payment (Razorpay)
- `GET /api/payment/plans` - Get payment plans
- `POST /api/payment/create-order` - Create payment order
- `POST /api/payment/verify` - Verify payment
- `GET /api/payment/config` - Get payment configuration

## Usage Guide

### 1. Upload Resume
- Supported formats: PDF, DOC, DOCX, TXT
- Maximum file size: 10MB
- Automatic parsing and extraction of resume content

### 2. Provide Job Description
- Paste the complete job description
- Include requirements, responsibilities, and qualifications
- Minimum 50 characters for proper analysis

### 3. AI Optimization Process
1. **Resume Parsing**: Extract skills, experience, education
2. **Job Analysis**: Identify requirements and keywords
3. **Skill Gap Analysis**: Compare resume vs job requirements
4. **Resume Rewriting**: Optimize content for the target role
5. **ATS Enhancement**: Optimize for Applicant Tracking Systems
6. **Professional Formatting**: Format for maximum impact

### 4. Results
- **ATS Score**: 0-100 score for ATS compatibility
- **Match Percentage**: Overall job fit percentage
- **Skill Analysis**: Detailed matching and missing skills
- **Optimized Resume**: Professional, ATS-friendly resume
- **Improvement Suggestions**: Actionable recommendations

## Architecture

### Multi-Agent System
The system uses a multi-agent architecture where each agent specializes in a specific aspect of resume optimization:

1. **Resume Parser Agent**: Handles document parsing and data extraction
2. **Job Description Analyzer Agent**: Processes job descriptions
3. **Skill Gap Analyzer Agent**: Performs comparative analysis
4. **Resume Rewriter Agent**: Creates optimized content
5. **ATS Optimization Agent**: Ensures ATS compatibility
6. **Formatter Agent**: Provides professional formatting

### Data Flow
```
Resume File → Parser Agent → Job Analysis → Skill Gap → Rewriter → ATS Optimizer → Formatter → Output
```

## Configuration

### Database Configuration
For production, update the database configuration in `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/resume_optimizer
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### File Upload Configuration
Configure file upload limits and storage:
```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## Development

### Project Structure
```
src/
├── main/
│   ├── java/com/resume/airesumoptimizer/
│   │   ├── agent/          # AI Agents
│   │   ├── controller/     # REST Controllers
│   │   ├── model/          # Data Models
│   │   ├── service/        # Business Services
│   │   └── config/         # Configuration
│   └── resources/
│       └── application.properties
└── frontend/
    ├── src/
    │   ├── components/     # React Components
    │   ├── services/       # API Services
    │   └── App.js          # Main App
    └── package.json
```

### Adding New Agents
1. Create agent class in `agent/` package
2. Implement the agent logic
3. Add agent to the optimization pipeline in `ResumeOptimizationController`
4. Update frontend if needed

### Testing
```bash
# Backend tests
mvn test

# Frontend tests
cd frontend
npm test
```

## Deployment

### Docker Deployment
```bash
# Build backend
mvn clean package

# Build frontend
cd frontend
npm run build

# Run with Docker
docker-compose up
```

### Production Configuration
1. Update environment variables for production
2. Configure production database
3. Set up SSL certificates
4. Configure reverse proxy (nginx/Apache)
5. Set up monitoring and logging

## Security

- **File Upload**: Secure file handling with validation
- **API Security**: CORS configuration and input validation
- **Payment Security**: Razorpay integration with signature verification
- **Environment Variables**: Sensitive data in environment variables

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:
- Create an issue in the repository
- Check the documentation
- Review the API endpoints

## Roadmap

- [ ] Multi-language support
- [ ] Advanced analytics dashboard
- [ ] Team collaboration features
- [ ] Integration with job boards
- [ ] Mobile application
- [ ] Advanced AI models integration
