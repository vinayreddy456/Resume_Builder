import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 60000, // 60 seconds timeout for file uploads
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to handle FormData
api.interceptors.request.use((config) => {
  if (config.data instanceof FormData) {
    config.headers['Content-Type'] = 'multipart/form-data';
  }
  return config;
});

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message = error.response?.data?.error || error.message || 'An error occurred';
    throw new Error(message);
  }
);

export const optimizeResume = async (formData) => {
  return await api.post('/resume/optimize', formData);
};

export const parseResume = async (file) => {
  const formData = new FormData();
  formData.append('file', file);
  return await api.post('/resume/parse-resume', formData);
};

export const analyzeJobDescription = async (jobDescription) => {
  return await api.post('/resume/analyze-job', jobDescription, {
    headers: {
      'Content-Type': 'text/plain',
    },
  });
};

export const checkHealth = async () => {
  return await api.get('/resume/health');
};

export default api;
