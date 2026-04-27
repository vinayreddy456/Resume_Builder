import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Briefcase, FileText } from 'lucide-react';

const JobDescriptionInput = ({ onJobDescription, value }) => {
  const [description, setDescription] = useState(value || '');
  const [charCount, setCharCount] = useState(0);

  const handleChange = (e) => {
    const text = e.target.value;
    setDescription(text);
    setCharCount(text.length);
  };

  const handleSubmit = () => {
    if (description.trim().length < 50) {
      alert('Please provide a more detailed job description (at least 50 characters)');
      return;
    }
    onJobDescription(description);
  };

  const sampleJobDescription = `We are looking for a Senior Java Developer to join our growing team. The ideal candidate will have strong experience in Java, Spring Boot, and microservices architecture. You will be responsible for designing, developing, and maintaining high-performance, scalable applications.

Requirements:
- 5+ years of experience in Java development
- Strong knowledge of Spring Boot, Spring MVC, and Spring Security
- Experience with microservices and RESTful APIs
- Proficiency in SQL and NoSQL databases
- Experience with cloud platforms (AWS/Azure)
- Knowledge of DevOps practices and CI/CD pipelines
- Strong problem-solving skills and attention to detail`;

  const loadSample = () => {
    setDescription(sampleJobDescription);
    setCharCount(sampleJobDescription.length);
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="bg-white rounded-xl shadow-lg p-6"
    >
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-xl font-semibold text-gray-900 flex items-center gap-2">
          <Briefcase className="w-5 h-5 text-primary-600" />
          Job Description
        </h2>
        <button
          onClick={loadSample}
          className="text-sm text-primary-600 hover:text-primary-700 font-medium"
        >
          Load Sample
        </button>
      </div>
      
      <div className="space-y-4">
        <div>
          <label htmlFor="jobDescription" className="block text-sm font-medium text-gray-700 mb-2">
            Paste the job description you're applying for
          </label>
          <textarea
            id="jobDescription"
            value={description}
            onChange={handleChange}
            placeholder="Paste the complete job description here including requirements, responsibilities, and qualifications..."
            className="w-full h-64 px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500 resize-none"
          />
        </div>
        
        <div className="flex items-center justify-between">
          <div className="text-sm text-gray-500">
            <FileText className="inline w-4 h-4 mr-1" />
            {charCount} characters
          </div>
          <button
            onClick={handleSubmit}
            disabled={description.trim().length < 50}
            className="px-6 py-2 bg-primary-600 text-white rounded-lg font-medium hover:bg-primary-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Analyze Job Description
          </button>
        </div>
        
        {description.trim().length > 0 && description.trim().length < 50 && (
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            className="p-3 bg-yellow-50 border border-yellow-200 rounded-lg text-yellow-700 text-sm"
          >
            Please provide a more detailed job description (at least 50 characters) for better analysis results.
          </motion.div>
        )}
      </div>
    </motion.div>
  );
};

export default JobDescriptionInput;
