// import React from 'react';
// import { motion } from 'framer-motion';
// import { Download, TrendingUp, AlertCircle } from 'lucide-react';

// const ResultsDisplay = ({ results }) => {
//   if (!results) return null;

//   const {
//     atsScore,
//     matchedKeywords,
//     missingKeywords,
//     improvements,
//     optimizedResume
//   } = results;

//   const downloadResume = () => {
//     const element = document.createElement('a');
//     const file = new Blob([optimizedResume], { type: 'text/plain' });
//     element.href = URL.createObjectURL(file);
//     element.download = 'optimized_resume.txt';
//     document.body.appendChild(element);
//     element.click();
//   };

//   return (
//     <motion.div
//       initial={{ opacity: 0 }}
//       animate={{ opacity: 1 }}
//       className="bg-white rounded-xl shadow-lg p-6"
//     >
//       <h2 className="text-2xl font-bold mb-4">Optimization Results</h2>

//       {/* ATS Score */}
//       <div className="mb-6">
//         <h3 className="text-lg font-semibold">ATS Score</h3>
//         <div className="text-3xl font-bold text-green-600">{atsScore}</div>
//       </div>

//       {/* Matched Keywords */}
//       <div className="mb-6">
//         <h3 className="font-semibold">Matched Keywords</h3>
//         <div className="flex flex-wrap gap-2 mt-2">
//           {matchedKeywords?.map((k, i) => (
//             <span key={i} className="bg-green-100 px-2 py-1 rounded text-sm">
//               {k}
//             </span>
//           ))}
//         </div>
//       </div>

//       {/* Missing Keywords */}
//       <div className="mb-6">
//         <h3 className="font-semibold text-red-600">Missing Keywords</h3>
//         <div className="flex flex-wrap gap-2 mt-2">
//           {missingKeywords?.map((k, i) => (
//             <span key={i} className="bg-red-100 px-2 py-1 rounded text-sm">
//               {k}
//             </span>
//           ))}
//         </div>
//       </div>

//       {/* Improvements */}
//       <div className="mb-6">
//         <h3 className="font-semibold flex items-center gap-2">
//           <TrendingUp className="w-4 h-4" />
//           Improvements
//         </h3>
//         <ul className="mt-2 space-y-2">
//           {improvements?.map((imp, i) => (
//             <li key={i} className="flex items-start gap-2">
//               <AlertCircle className="w-4 h-4 text-yellow-500 mt-1" />
//               {imp}
//             </li>
//           ))}
//         </ul>
//       </div>

//       {/* Resume */}
//       <div className="mb-6">
//         <h3 className="font-semibold">Optimized Resume</h3>
//         <pre className="bg-gray-100 p-4 rounded text-sm whitespace-pre-wrap">
//           {optimizedResume}
//         </pre>
//       </div>

//       {/* Download */}
//       <button
//         onClick={downloadResume}
//         className="bg-primary-600 text-white px-4 py-2 rounded-lg flex items-center gap-2"
//       >
//         <Download className="w-4 h-4" />
//         Download Resume
//       </button>
//     </motion.div>
//   );
// };

// export default ResultsDisplay;


import React from 'react';
import { motion } from 'framer-motion';
import { Download, TrendingUp, AlertCircle, FileText, FileCode } from 'lucide-react';

const ResultsDisplay = ({ results }) => {
  if (!results) return null;

  const {
    atsScore,
    matchedKeywords,
    missingKeywords,
    improvements,
    optimizedResume,
    optimizedResumeLatex // 🔥 NEW
  } = results;

  // 📄 TXT Download
  const downloadResume = () => {
    const element = document.createElement('a');
    const file = new Blob([optimizedResume], { type: 'text/plain' });
    element.href = URL.createObjectURL(file);
    element.download = 'optimized_resume.txt';
    element.click();
  };

  // 📄 LATEX Download
  const downloadLatex = () => {
    const element = document.createElement('a');
    const file = new Blob([optimizedResumeLatex || ""], { type: 'text/plain' });
    element.href = URL.createObjectURL(file);
    element.download = 'resume.tex';
    element.click();
  };

  // 📄 PDF Download (Backend API)
  const downloadPDF = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/resume/download-pdf', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ latex: optimizedResumeLatex })
      });

      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);

      const a = document.createElement('a');
      a.href = url;
      a.download = 'resume.pdf';
      a.click();
    } catch (err) {
      alert("PDF generation failed");
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="bg-white rounded-xl shadow-lg p-6"
    >
      <h2 className="text-2xl font-bold mb-6">Optimization Results</h2>

      {/* ATS Score */}
      <div className="mb-6 text-center">
        <h3 className="text-lg font-semibold">ATS Score</h3>
        <div className="text-5xl font-bold text-green-600">{atsScore}</div>
      </div>

      {/* Matched Keywords */}
      <div className="mb-6">
        <h3 className="font-semibold text-green-700">Matched Keywords</h3>
        <div className="flex flex-wrap gap-2 mt-2">
          {matchedKeywords?.map((k, i) => (
            <span key={i} className="bg-green-100 px-2 py-1 rounded text-sm">
              {k}
            </span>
          ))}
        </div>
      </div>

      {/* Missing Keywords */}
      <div className="mb-6">
        <h3 className="font-semibold text-red-600">Missing Keywords</h3>
        <div className="flex flex-wrap gap-2 mt-2">
          {missingKeywords?.map((k, i) => (
            <span key={i} className="bg-red-100 px-2 py-1 rounded text-sm">
              {k}
            </span>
          ))}
        </div>
      </div>

      {/* Improvements */}
      <div className="mb-6">
        <h3 className="font-semibold flex items-center gap-2">
          <TrendingUp className="w-4 h-4" />
          Improvements
        </h3>
        <ul className="mt-2 space-y-2">
          {improvements?.map((imp, i) => (
            <li key={i} className="flex items-start gap-2">
              <AlertCircle className="w-4 h-4 text-yellow-500 mt-1" />
              {imp}
            </li>
          ))}
        </ul>
      </div>

      {/* Resume Preview */}
      <div className="mb-6">
        <h3 className="font-semibold">Optimized Resume</h3>
        <pre className="bg-gray-100 p-4 rounded text-sm whitespace-pre-wrap max-h-60 overflow-y-auto">
          {optimizedResume}
        </pre>
      </div>

      {/* 🔥 DOWNLOAD SECTION */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-3">

        {/* TXT */}
        <button
          onClick={downloadResume}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center justify-center gap-2 hover:bg-blue-700"
        >
          <FileText className="w-4 h-4" />
          TXT
        </button>

        {/* LATEX */}
        <button
          onClick={downloadLatex}
          className="bg-purple-600 text-white px-4 py-2 rounded-lg flex items-center justify-center gap-2 hover:bg-purple-700"
        >
          <FileCode className="w-4 h-4" />
          Overleaf (.tex)
        </button>

        {/* PDF */}
        <button
          onClick={downloadPDF}
          className="bg-green-600 text-white px-4 py-2 rounded-lg flex items-center justify-center gap-2 hover:bg-green-700"
        >
          <Download className="w-4 h-4" />
          PDF
        </button>

      </div>
    </motion.div>
  );
};

export default ResultsDisplay;