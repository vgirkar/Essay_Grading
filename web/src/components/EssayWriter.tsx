"use client";

import { useState, useRef, useEffect, useCallback } from "react";

interface GradeResult {
  score: number;
  breakdown: {
    label: string;
    value: number;
    max: number;
    feedback: string;
  }[];
  overall_feedback: string;
}

const PROMPTS = [
  "As people rely more and more on technology to solve problems, the ability of humans to think for themselves will surely deteriorate. Write a response in which you discuss the extent to which you agree or disagree with the statement and explain your reasoning.",
  "Educational institutions have a responsibility to dissuade students from pursuing fields of study in which they are unlikely to succeed. Write a response in which you discuss the extent to which you agree or disagree with the claim.",
  "The best way to teach is to praise positive actions and ignore negative ones. Write a response in which you discuss the extent to which you agree or disagree with the statement and explain your reasoning.",
  "Governments should offer a free university education to any student who has been admitted to a university but who cannot afford the tuition. Write a response in which you discuss the extent to which you agree or disagree with the recommendation.",
];

export default function EssayWriter() {
  const [essay, setEssay] = useState("");
  const [isGrading, setIsGrading] = useState(false);
  const [result, setResult] = useState<GradeResult | null>(null);
  const [timerSeconds, setTimerSeconds] = useState(30 * 60);
  const [timerRunning, setTimerRunning] = useState(false);
  const [prompt] = useState(() => PROMPTS[Math.floor(Math.random() * PROMPTS.length)]);
  const textareaRef = useRef<HTMLTextAreaElement>(null);
  const [undoStack, setUndoStack] = useState<string[]>([""]);
  const [undoIndex, setUndoIndex] = useState(0);

  useEffect(() => {
    if (!timerRunning || timerSeconds <= 0) return;
    const id = setInterval(() => setTimerSeconds((s) => Math.max(0, s - 1)), 1000);
    return () => clearInterval(id);
  }, [timerRunning, timerSeconds]);

  const formatTime = (s: number) => {
    const m = Math.floor(s / 60);
    const sec = s % 60;
    return `${m.toString().padStart(2, "0")}:${sec.toString().padStart(2, "0")}`;
  };

  const wordCount = essay.trim() ? essay.trim().split(/\s+/).length : 0;
  const charCount = essay.length;
  const sentenceCount = essay.trim()
    ? (essay.match(/[.!?]+/g) || []).length || (essay.trim().length > 0 ? 1 : 0)
    : 0;

  const handleTextChange = (val: string) => {
    setEssay(val);
    if (!timerRunning && val.length > 0) setTimerRunning(true);
    const newStack = [...undoStack.slice(0, undoIndex + 1), val];
    setUndoStack(newStack);
    setUndoIndex(newStack.length - 1);
  };

  const handleUndo = () => {
    if (undoIndex > 0) {
      setUndoIndex(undoIndex - 1);
      setEssay(undoStack[undoIndex - 1]);
    }
  };

  const handleRedo = () => {
    if (undoIndex < undoStack.length - 1) {
      setUndoIndex(undoIndex + 1);
      setEssay(undoStack[undoIndex + 1]);
    }
  };

  const handleCut = () => {
    const ta = textareaRef.current;
    if (!ta) return;
    const sel = ta.value.substring(ta.selectionStart, ta.selectionEnd);
    if (sel) {
      navigator.clipboard.writeText(sel);
      const newVal =
        ta.value.substring(0, ta.selectionStart) +
        ta.value.substring(ta.selectionEnd);
      handleTextChange(newVal);
    }
  };

  const handleCopy = () => {
    const ta = textareaRef.current;
    if (!ta) return;
    const sel = ta.value.substring(ta.selectionStart, ta.selectionEnd);
    if (sel) navigator.clipboard.writeText(sel);
  };

  const handlePaste = async () => {
    const text = await navigator.clipboard.readText();
    const ta = textareaRef.current;
    if (!ta) return;
    const start = ta.selectionStart;
    const end = ta.selectionEnd;
    const newVal =
      ta.value.substring(0, start) + text + ta.value.substring(end);
    handleTextChange(newVal);
  };

  const handleGrade = useCallback(async () => {
    if (wordCount < 10) return;
    setIsGrading(true);
    setTimerRunning(false);
    setResult(null);

    try {
      const res = await fetch("/api/grade", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ essay }),
      });
      const data: GradeResult = await res.json();
      setResult(data);
    } catch {
      alert("Grading failed. Please try again.");
    } finally {
      setIsGrading(false);
    }
  }, [essay, wordCount]);

  const handleReset = () => {
    setEssay("");
    setResult(null);
    setTimerSeconds(30 * 60);
    setTimerRunning(false);
    setUndoStack([""]);
    setUndoIndex(0);
    textareaRef.current?.focus();
  };

  const timerWarning = timerSeconds < 5 * 60 && timerSeconds > 0;
  const timerExpired = timerSeconds === 0;

  return (
    <div className="flex flex-col min-h-screen">
      {/* Header */}
      <header className="bg-ets-navy text-white shadow-lg">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 py-3 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-lg bg-ets-blue flex items-center justify-center">
              <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
              </svg>
            </div>
            <div>
              <h1 className="text-lg font-semibold tracking-tight">Analytical Writing Assessment</h1>
              <p className="text-xs text-slate-400">Automated Essay Grading System</p>
            </div>
          </div>
          <div className="flex items-center gap-4">
            <div className={`font-mono text-lg tracking-wider px-4 py-1.5 rounded-lg ${
              timerExpired
                ? "bg-red-500/20 text-red-300"
                : timerWarning
                ? "bg-amber-500/20 text-amber-300 animate-pulse-slow"
                : "bg-white/10 text-slate-200"
            }`}>
              {formatTime(timerSeconds)}
            </div>
          </div>
        </div>
      </header>

      {result ? (
        <ScoreView result={result} essay={essay} onReset={handleReset} />
      ) : (
        /* Editor View */
        <div className="flex-1 flex flex-col max-w-6xl mx-auto w-full px-4 sm:px-6 py-6">
          {/* Prompt Card */}
          <div className="bg-white border border-slate-200 rounded-t-xl px-6 py-5 shadow-sm">
            <div className="flex items-start gap-3">
              <div className="mt-0.5 w-6 h-6 rounded-full bg-ets-light flex-shrink-0 flex items-center justify-center">
                <svg className="w-3.5 h-3.5 text-ets-teal" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div>
                <h2 className="text-sm font-semibold text-slate-800 uppercase tracking-wide mb-2">Issue Task — Directions</h2>
                <p className="text-sm text-slate-600 leading-relaxed italic">&ldquo;{prompt}&rdquo;</p>
              </div>
            </div>
          </div>

          {/* Toolbar */}
          <div className="bg-slate-100 border-x border-slate-200 px-4 py-2 flex items-center gap-1.5 flex-wrap">
            <button className="toolbar-btn" onClick={handleCut}>Cut</button>
            <button className="toolbar-btn" onClick={handleCopy}>Copy</button>
            <button className="toolbar-btn" onClick={handlePaste}>Paste</button>
            <div className="w-px h-5 bg-slate-300 mx-1" />
            <button className="toolbar-btn" onClick={handleUndo} disabled={undoIndex <= 0}>Undo</button>
            <button className="toolbar-btn" onClick={handleRedo} disabled={undoIndex >= undoStack.length - 1}>Redo</button>
            <div className="w-px h-5 bg-slate-300 mx-1" />
            <button className="toolbar-btn" onClick={handleReset}>Clear All</button>
          </div>

          {/* Text Area */}
          <div className="flex-1 flex flex-col min-h-0">
            <textarea
              ref={textareaRef}
              value={essay}
              onChange={(e) => handleTextChange(e.target.value)}
              placeholder="Begin typing your essay here..."
              className="flex-1 w-full border-x border-slate-200 bg-white px-6 py-5 text-base text-slate-800 
                         leading-relaxed font-serif resize-none focus:ring-0 min-h-[400px]
                         placeholder:text-slate-400 placeholder:italic"
              autoFocus
              spellCheck
            />
          </div>

          {/* Status Bar */}
          <div className="bg-slate-100 border border-slate-200 rounded-b-xl px-5 py-2.5 flex items-center justify-between">
            <div className="flex items-center gap-5 text-xs text-slate-500">
              <span>Words: <strong className="text-slate-700">{wordCount}</strong></span>
              <span>Characters: <strong className="text-slate-700">{charCount}</strong></span>
              <span>Sentences: <strong className="text-slate-700">{sentenceCount}</strong></span>
            </div>
            <button
              onClick={handleGrade}
              disabled={wordCount < 10 || isGrading}
              className={`px-6 py-2 rounded-lg text-sm font-semibold transition-all duration-200 ${
                wordCount < 10
                  ? "bg-slate-300 text-slate-500 cursor-not-allowed"
                  : isGrading
                  ? "bg-ets-blue text-white opacity-70 cursor-wait"
                  : "bg-ets-blue text-white hover:bg-blue-700 active:scale-[0.98] shadow-md hover:shadow-lg"
              }`}
            >
              {isGrading ? (
                <span className="flex items-center gap-2">
                  <svg className="animate-spin h-4 w-4" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                  </svg>
                  Grading...
                </span>
              ) : (
                "Grade Essay"
              )}
            </button>
          </div>

          {wordCount > 0 && wordCount < 10 && (
            <p className="text-xs text-amber-600 mt-2 text-center">Write at least 10 words to enable grading.</p>
          )}
        </div>
      )}
    </div>
  );
}

function ScoreView({ result, essay, onReset }: { result: GradeResult; essay: string; onReset: () => void }) {
  const circumference = 2 * Math.PI * 52;
  const progress = (result.score / 6) * circumference;
  const offset = circumference - progress;
  const wordCount = essay.trim().split(/\s+/).length;

  const scoreColor =
    result.score >= 5
      ? "text-emerald-500"
      : result.score >= 4
      ? "text-blue-500"
      : result.score >= 3
      ? "text-amber-500"
      : "text-red-500";

  const ringColor =
    result.score >= 5
      ? "#10b981"
      : result.score >= 4
      ? "#3b82f6"
      : result.score >= 3
      ? "#f59e0b"
      : "#ef4444";

  const scoreLabel =
    result.score >= 5.5
      ? "Outstanding"
      : result.score >= 4.5
      ? "Strong"
      : result.score >= 3.5
      ? "Adequate"
      : result.score >= 2.5
      ? "Limited"
      : result.score >= 1.5
      ? "Seriously Flawed"
      : "Fundamentally Deficient";

  return (
    <div className="flex-1 max-w-5xl mx-auto w-full px-4 sm:px-6 py-8">
      <div className="animate-fade-in">
        {/* Score Hero */}
        <div className="bg-white rounded-2xl shadow-lg border border-slate-200 p-8 mb-6">
          <div className="flex flex-col md:flex-row items-center gap-8">
            {/* Score Ring */}
            <div className="relative animate-score-reveal">
              <svg width="140" height="140" className="score-ring">
                <circle className="score-ring-bg" cx="70" cy="70" r="52" fill="none" strokeWidth="10" />
                <circle
                  className="score-ring-fill"
                  cx="70" cy="70" r="52"
                  fill="none"
                  stroke={ringColor}
                  strokeWidth="10"
                  strokeDasharray={circumference}
                  strokeDashoffset={offset}
                />
              </svg>
              <div className="absolute inset-0 flex flex-col items-center justify-center">
                <span className={`text-3xl font-bold ${scoreColor}`}>{result.score.toFixed(1)}</span>
                <span className="text-xs text-slate-400 font-medium">/ 6.0</span>
              </div>
            </div>

            <div className="flex-1 text-center md:text-left">
              <h2 className="text-2xl font-bold text-slate-800 mb-1">{scoreLabel}</h2>
              <p className="text-sm text-slate-500 mb-4">{wordCount} words analyzed</p>
              <p className="text-sm text-slate-600 leading-relaxed">{result.overall_feedback}</p>
            </div>
          </div>
        </div>

        {/* Breakdown */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-6">
          {result.breakdown.map((item, i) => (
            <div
              key={item.label}
              className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm animate-fade-in"
              style={{ animationDelay: `${i * 100}ms` }}
            >
              <div className="flex items-center justify-between mb-3">
                <span className="text-sm font-semibold text-slate-700">{item.label}</span>
                <span className="text-sm font-bold text-slate-800">
                  {item.value.toFixed(1)}<span className="text-slate-400 font-normal">/{item.max}</span>
                </span>
              </div>
              <div className="w-full bg-slate-100 rounded-full h-2 mb-2">
                <div
                  className="h-2 rounded-full transition-all duration-1000 ease-out"
                  style={{
                    width: `${(item.value / item.max) * 100}%`,
                    backgroundColor: ringColor,
                    transitionDelay: `${i * 100 + 300}ms`,
                  }}
                />
              </div>
              <p className="text-xs text-slate-500 leading-relaxed">{item.feedback}</p>
            </div>
          ))}
        </div>

        {/* Actions */}
        <div className="flex items-center justify-center gap-4">
          <button
            onClick={onReset}
            className="px-6 py-2.5 rounded-lg text-sm font-semibold bg-ets-blue text-white
                       hover:bg-blue-700 transition-all shadow-md hover:shadow-lg active:scale-[0.98]"
          >
            Write Another Essay
          </button>
        </div>
      </div>
    </div>
  );
}
