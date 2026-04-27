import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Toaster } from 'react-hot-toast';
import FileUpload from './components/FileUpload';
import JobDescriptionInput from './components/JobDescriptionInput';
import ResultsDisplay from './components/ResultsDisplay';
import { FileText, Briefcase, Sparkles, Download } from 'lucide-react';
import { optimizeResume } from './services/api';
import toast from 'react-hot-toast';

function App() {
  const [resumeFile, setResumeFile] = useState(null);
  const [jobDescription, setJobDescription] = useState('');
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);
  const [currentStep, setCurrentStep] = useState(1);

  const handleFileUpload = (file) => {
    setResumeFile(file);
    setCurrentStep(2);
    toast.success('Resume uploaded successfully!');
  };

  const handleJobDescription = (description) => {
    setJobDescription(description);
    setCurrentStep(3);
  };

 const handleOptimize = async () => {
  if (!resumeFile || !jobDescription) {
    toast.error('Please upload resume and provide job description');
    return;
  }

  setLoading(true);
  try {
    const formData = new FormData();
    formData.append('resumeFile', resumeFile);
    formData.append('jobDescription', jobDescription);

    const response = await optimizeResume(formData);

    console.log("API RESPONSE:", response); // 🔥 debug

    if (response.success) {
      const data = response.data;

      // 🔥 MAP BACKEND → FRONTEND (IMPORTANT)
      // const formattedResult = {
      //   atsScore: data.ats_score,
      //   matchedKeywords: data.matched_keywords || [],
      //   missingKeywords: data.missing_keywords || [],
      //   improvements: data.improvements || [],
      //   optimizedResume: data.optimized_resume || ""
      // };
const formattedResult = {
  atsScore: data.ats_score,
  matchedKeywords: data.matched_keywords || [],
  missingKeywords: data.missing_keywords || [],
  improvements: data.improvements || [],
  optimizedResume: data.optimized_resume || "",
  optimizedResumeLatex: data.optimized_resume_latex || "" // 🔥 IMPORTANT
};
      setResults(formattedResult);
      setCurrentStep(4);
      toast.success('Resume optimized successfully!');
    } else {
      toast.error(response.error || 'Optimization failed');
    }
  } catch (error) {
    toast.error('Error optimizing resume: ' + error.message);
  } finally {
    setLoading(false);
  }
};

  const steps = [
    { id: 1, title: 'Upload Resume', icon: FileText, completed: resumeFile !== null },
    { id: 2, title: 'Job Description', icon: Briefcase, completed: jobDescription !== '' },
    { id: 3, title: 'Optimize', icon: Sparkles, completed: results !== null },
    { id: 4, title: 'Download', icon: Download, completed: false }
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50">
      <Toaster position="top-right" />
      
      {/* Header */}
      <header className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 flex items-center gap-3">
                <Sparkles className="w-8 h-8 text-primary-600" />
                AI Resume Optimizer
              </h1>
              <p className="text-gray-600 mt-2">
                Transform your resume with AI-powered optimization for ATS success
              </p>
            </div>
          </div>
        </div>
      </header>

      {/* Progress Steps */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex items-center justify-between mb-12">
          {steps.map((step, index) => {
            const Icon = step.icon;
            const isActive = currentStep === step.id;
            const isCompleted = step.completed;
            
            return (
              <div key={step.id} className="flex items-center">
                <motion.div
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.1 }}
                  className={`flex flex-col items-center ${isActive ? 'scale-110' : ''}`}
                >
                  <div className={`w-16 h-16 rounded-full flex items-center justify-center transition-all duration-300 ${
                    isCompleted 
                      ? 'bg-green-500 text-white' 
                      : isActive 
                        ? 'bg-primary-600 text-white ring-4 ring-primary-100'
                        : 'bg-gray-200 text-gray-500'
                  }`}>
                    <Icon className="w-8 h-8" />
                  </div>
                  <span className={`mt-2 text-sm font-medium ${
                    isActive ? 'text-primary-600' : isCompleted ? 'text-green-600' : 'text-gray-500'
                  }`}>
                    {step.title}
                  </span>
                </motion.div>
                
                {index < steps.length - 1 && (
                  <div className={`flex-1 h-1 mx-4 rounded-full transition-all duration-300 ${
                    steps[index + 1].completed ? 'bg-green-500' : 'bg-gray-200'
                  }`} />
                )}
              </div>
            );
          })}
        </div>

        {/* Main Content */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Left Column - Input */}
          <motion.div
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            className="space-y-6"
          >
            <FileUpload onFileUpload={handleFileUpload} />
            <JobDescriptionInput 
              onJobDescription={handleJobDescription}
              value={jobDescription}
            />
            
            {currentStep >= 3 && (
              <motion.button
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                onClick={handleOptimize}
                disabled={loading}
                className="w-full bg-primary-600 text-white py-4 px-6 rounded-lg font-semibold 
                         hover:bg-primary-700 transition-all duration-200 disabled:opacity-50 
                         disabled:cursor-not-allowed flex items-center justify-center gap-2"
              >
                {loading ? (
                  <>
                    <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
                    Optimizing Resume...
                  </>
                ) : (
                  <>
                    <Sparkles className="w-5 h-5" />
                    Optimize Resume
                  </>
                )}
              </motion.button>
            )}
          </motion.div>

          {/* Right Column - Results */}
          <motion.div
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
          >
            {results ? (
              <ResultsDisplay results={results} />
            ) : (
              <div className="bg-white rounded-xl shadow-lg p-8 text-center">
                <div className="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-6">
                  <Sparkles className="w-12 h-12 text-gray-400" />
                </div>
                <h3 className="text-xl font-semibold text-gray-900 mb-2">
                  Ready to optimize your resume?
                </h3>
                <p className="text-gray-600">
                  Upload your resume and provide a job description to get started with AI-powered optimization.
                </p>
              </div>
            )}
          </motion.div>
        </div>
      </div>

      {/* Features Section */}
      <div className="bg-white border-t border-gray-200 mt-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
          <h2 className="text-2xl font-bold text-center text-gray-900 mb-12">
            AI-Powered Resume Optimization Features
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center">
              <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <FileText className="w-8 h-8 text-blue-600" />
              </div>
              <h3 className="font-semibold text-gray-900 mb-2">Smart Resume Parsing</h3>
              <p className="text-gray-600 text-sm">
                Extract and analyze your resume content with advanced AI parsing technology
              </p>
            </div>
            <div className="text-center">
              <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <Briefcase className="w-8 h-8 text-green-600" />
              </div>
              <h3 className="font-semibold text-gray-900 mb-2">Job Description Analysis</h3>
              <p className="text-gray-600 text-sm">
                Analyze job requirements and match them with your skills and experience
              </p>
            </div>
            <div className="text-center">
              <div className="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <Sparkles className="w-8 h-8 text-purple-600" />
              </div>
              <h3 className="font-semibold text-gray-900 mb-2">ATS Optimization</h3>
              <p className="text-gray-600 text-sm">
                Optimize your resume to pass through Applicant Tracking Systems successfully
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
