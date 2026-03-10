import { NextRequest, NextResponse } from "next/server";
import { COMMON_WORDS } from "@/lib/wordlist";

export async function POST(req: NextRequest) {
  try {
    const { essay } = await req.json();
    if (!essay || typeof essay !== "string" || essay.trim().length < 20) {
      return NextResponse.json(
        { error: "Essay must be at least 20 characters." },
        { status: 400 }
      );
    }
    const result = gradeEssay(essay.trim());
    return NextResponse.json(result);
  } catch (err) {
    console.error("Grading error:", err);
    return NextResponse.json({ error: "Grading failed." }, { status: 500 });
  }
}

// ---------------------------------------------------------------------------
// Quality detection — catches gibberish, random text, and non-English input
// ---------------------------------------------------------------------------

const ENGLISH_FREQ: Record<string, number> = {
  e: 0.127, t: 0.091, a: 0.082, o: 0.075, i: 0.07, n: 0.067, s: 0.063,
  h: 0.061, r: 0.06, d: 0.043, l: 0.04, c: 0.028, u: 0.028, m: 0.024,
  w: 0.024, f: 0.022, g: 0.02, y: 0.02, p: 0.019, b: 0.015, v: 0.0098,
  k: 0.0077, j: 0.0015, x: 0.0015, q: 0.001, z: 0.00074,
};

function computeQuality(text: string, words: string[]): {
  score: number;
  knownRatio: number;
  letterFreqScore: number;
  vowelOk: boolean;
  repetitionPenalty: number;
  structureScore: number;
  issues: string[];
} {
  const issues: string[] = [];
  const cleanWords = words.map((w) =>
    w.toLowerCase().replace(/[^a-z'-]/g, "")
  ).filter((w) => w.length > 0);

  // 1) Known-word ratio: what % of words are real English?
  let knownCount = 0;
  for (const w of cleanWords) {
    const base = w.replace(/(ing|ed|ly|er|est|tion|ment|ness|ies|es|s)$/, "");
    if (
      COMMON_WORDS.has(w) ||
      COMMON_WORDS.has(base) ||
      COMMON_WORDS.has(w.replace(/ies$/, "y")) ||
      COMMON_WORDS.has(w.replace(/ied$/, "y")) ||
      (w.length <= 2) // allow short words like "I", "a"
    ) {
      knownCount++;
    }
  }
  const knownRatio = cleanWords.length > 0 ? knownCount / cleanWords.length : 0;
  if (knownRatio < 0.35) issues.push("Most words are not recognizable English.");
  else if (knownRatio < 0.55) issues.push("Many words are not recognizable English.");

  // 2) Letter frequency distribution — how "English-like" is the character usage?
  const letters = text.toLowerCase().replace(/[^a-z]/g, "");
  const freq: Record<string, number> = {};
  for (const ch of letters) freq[ch] = (freq[ch] || 0) + 1;
  let chiSq = 0;
  for (const [ch, expected] of Object.entries(ENGLISH_FREQ)) {
    const observed = (freq[ch] || 0) / Math.max(letters.length, 1);
    chiSq += ((observed - expected) ** 2) / expected;
  }
  const letterFreqScore = Math.max(0, 1 - chiSq * 5);
  if (letterFreqScore < 0.3) issues.push("Character distribution is atypical of English.");

  // 3) Vowel ratio
  const vowels = letters.replace(/[^aeiou]/g, "").length;
  const vowelRatio = vowels / Math.max(letters.length, 1);
  const vowelOk = vowelRatio >= 0.25 && vowelRatio <= 0.55;
  if (!vowelOk) issues.push("Unusual vowel/consonant ratio.");

  // 4) Repetition detection — penalize if content is heavily repeated
  const sentences = text.split(/[.!?]+/).map((s) => s.trim().toLowerCase()).filter(Boolean);
  const uniqueSentences = new Set(sentences);
  const sentRepeatRatio = sentences.length > 0 ? uniqueSentences.size / sentences.length : 1;

  const wordFreq: Record<string, number> = {};
  for (const w of cleanWords) wordFreq[w] = (wordFreq[w] || 0) + 1;
  const maxWordFreq = Math.max(...Object.values(wordFreq), 0);
  const topWordRatio = maxWordFreq / Math.max(cleanWords.length, 1);

  let repetitionPenalty = 1.0;
  if (sentRepeatRatio < 0.5) {
    repetitionPenalty *= 0.3;
    issues.push("Excessive sentence repetition detected.");
  } else if (sentRepeatRatio < 0.75) {
    repetitionPenalty *= 0.6;
    issues.push("Significant sentence repetition.");
  }
  if (topWordRatio > 0.15 && cleanWords.length > 20) {
    repetitionPenalty *= 0.7;
    issues.push("Excessive word repetition.");
  }

  // 5) Sentence structure — proper capitalization and punctuation
  const rawSentences = text.split(/[.!?]+/).filter((s) => s.trim().length > 0);
  let properStarts = 0;
  for (const s of rawSentences) {
    const trimmed = s.trim();
    if (trimmed.length > 0 && /^[A-Z]/.test(trimmed)) properStarts++;
  }
  const structureScore = rawSentences.length > 0 ? properStarts / rawSentences.length : 0;
  if (structureScore < 0.4 && rawSentences.length > 2) {
    issues.push("Sentences lack proper capitalization.");
  }

  // Combine into overall quality score (0-1)
  let score =
    knownRatio * 0.45 +
    letterFreqScore * 0.15 +
    (vowelOk ? 0.1 : 0) +
    structureScore * 0.15 +
    repetitionPenalty * 0.15;

  // Hard gates for obvious gibberish
  if (knownRatio < 0.30) score = Math.min(score, 0.1);
  else if (knownRatio < 0.45) score = Math.min(score, 0.25);

  return { score, knownRatio, letterFreqScore, vowelOk, repetitionPenalty, structureScore, issues };
}

// ---------------------------------------------------------------------------
// Content analysis — evaluates actual writing quality
// ---------------------------------------------------------------------------

function gradeEssay(text: string) {
  const words = text.split(/\s+/).filter(Boolean);
  const sentences = text.split(/[.!?]+/).filter((s) => s.trim().length > 0);
  const paragraphs = text.split(/\n\s*\n/).filter((p) => p.trim().length > 0);
  const cleanWords = words.map((w) => w.toLowerCase().replace(/[^a-z']/g, "")).filter(Boolean);
  const uniqueWords = new Set(cleanWords);

  const wordCount = words.length;
  const sentenceCount = Math.max(sentences.length, 1);
  const paragraphCount = Math.max(paragraphs.length, 1);
  const charCount = text.replace(/\s/g, "").length;

  const avgWordLength = charCount / Math.max(wordCount, 1);
  const avgSentenceLength = wordCount / sentenceCount;
  const vocabularyRichness = uniqueWords.size / Math.max(wordCount, 1);

  const sentenceLengths = sentences.map(
    (s) => s.trim().split(/\s+/).filter(Boolean).length
  );
  const sentLenVar = variance(sentenceLengths);

  const longWords = cleanWords.filter((w) => w.length >= 7).length;
  const longWordRatio = longWords / Math.max(wordCount, 1);

  const commaCount = (text.match(/,/g) || []).length;
  const commasPerSentence = commaCount / sentenceCount;

  const TRANSITIONS = [
    "however", "therefore", "furthermore", "moreover", "consequently",
    "nevertheless", "although", "whereas", "likewise", "similarly",
    "additionally", "specifically", "for example", "for instance",
    "in contrast", "on the other hand", "in conclusion", "in summary",
    "first", "second", "third", "finally", "thus", "hence", "accordingly",
    "indeed", "certainly", "undoubtedly", "clearly", "obviously",
  ];
  const lowerText = text.toLowerCase();
  const transitionCount = TRANSITIONS.filter((tw) => lowerText.includes(tw)).length;

  // --- Quality gate ---
  const quality = computeQuality(text, words);

  // --- Dimension scores (same as before) ---
  const rawLength = computeLengthScore(wordCount);
  const rawVocab = computeVocabScore(vocabularyRichness, longWordRatio, avgWordLength);
  const rawStructure = computeStructureScore(paragraphCount, sentenceCount, wordCount, transitionCount);
  const rawFluency = computeFluencyScore(avgSentenceLength, sentLenVar, commasPerSentence);

  // Apply quality multiplier to each dimension
  const qMult = quality.score;
  const lengthScore = roundTo(rawLength * qMult, 1);
  const vocabScore = roundTo(rawVocab * qMult, 1);
  const structureScore = roundTo(rawStructure * qMult, 1);
  const fluencyScore = roundTo(rawFluency * qMult, 1);

  const rawScore =
    lengthScore * 0.25 +
    vocabScore * 0.25 +
    structureScore * 0.25 +
    fluencyScore * 0.25;

  const score = Math.round(Math.min(6.0, Math.max(0.0, rawScore)) * 2) / 2;

  const isGibberish = quality.score < 0.3;

  const breakdown = [
    {
      label: "Development & Length",
      value: lengthScore,
      max: 6,
      feedback: isGibberish ? "Cannot evaluate — text does not appear to be genuine English writing." : lengthFeedback(wordCount),
    },
    {
      label: "Vocabulary & Word Choice",
      value: vocabScore,
      max: 6,
      feedback: isGibberish ? "Cannot evaluate — too many unrecognizable words." : vocabFeedback(vocabularyRichness, longWordRatio),
    },
    {
      label: "Organization & Structure",
      value: structureScore,
      max: 6,
      feedback: isGibberish ? "Cannot evaluate — text lacks coherent structure." : structureFeedback(paragraphCount, transitionCount),
    },
    {
      label: "Sentence Fluency",
      value: fluencyScore,
      max: 6,
      feedback: isGibberish ? "Cannot evaluate — sentences are not coherent." : fluencyFeedback(avgSentenceLength, sentLenVar),
    },
  ];

  return {
    score,
    breakdown,
    overall_feedback: isGibberish
      ? `This response does not appear to be a genuine essay. ${quality.issues.join(" ")} Please write a coherent English essay addressing the prompt to receive a meaningful score.`
      : quality.score < 0.6
      ? `Your response has some quality issues that significantly affect the score: ${quality.issues.join(" ")} ${overallFeedback(score, wordCount)}`
      : overallFeedback(score, wordCount),
  };
}

// ---------------------------------------------------------------------------
// Dimension scorers
// ---------------------------------------------------------------------------

function computeLengthScore(words: number): number {
  if (words >= 500) return 6.0;
  if (words >= 400) return 5.0 + (words - 400) / 100;
  if (words >= 300) return 4.0 + (words - 300) / 100;
  if (words >= 200) return 3.0 + (words - 200) / 100;
  if (words >= 100) return 2.0 + (words - 100) / 100;
  if (words >= 50) return 1.0 + (words - 50) / 50;
  return Math.max(0.5, words / 50);
}

function computeVocabScore(richness: number, longRatio: number, avgLen: number): number {
  let s = 3.0;
  if (richness >= 0.7) s += 1.2;
  else if (richness >= 0.55) s += 0.8;
  else if (richness >= 0.4) s += 0.3;
  else s -= 0.5;

  if (longRatio >= 0.2) s += 1.0;
  else if (longRatio >= 0.12) s += 0.6;
  else if (longRatio >= 0.06) s += 0.2;

  if (avgLen >= 5.5) s += 0.8;
  else if (avgLen >= 4.5) s += 0.4;
  else if (avgLen < 3.8) s -= 0.5;

  return clamp(s, 0.5, 6.0);
}

function computeStructureScore(
  paragraphs: number, sentences: number, words: number, transitions: number
): number {
  let s = 3.0;
  if (paragraphs >= 5) s += 1.0;
  else if (paragraphs >= 4) s += 0.7;
  else if (paragraphs >= 3) s += 0.4;
  else if (paragraphs <= 1) s -= 0.8;

  if (transitions >= 8) s += 1.2;
  else if (transitions >= 5) s += 0.8;
  else if (transitions >= 3) s += 0.4;
  else if (transitions <= 1) s -= 0.3;

  const sentPerPara = sentences / Math.max(paragraphs, 1);
  if (sentPerPara >= 3 && sentPerPara <= 7) s += 0.8;
  else if (sentPerPara >= 2) s += 0.3;

  return clamp(s, 0.5, 6.0);
}

function computeFluencyScore(
  avgSentLen: number, sentVariance: number, commasPerSent: number
): number {
  let s = 3.0;
  if (avgSentLen >= 15 && avgSentLen <= 25) s += 1.2;
  else if (avgSentLen >= 12 && avgSentLen <= 30) s += 0.6;
  else if (avgSentLen < 8 || avgSentLen > 40) s -= 0.5;

  if (sentVariance >= 30 && sentVariance <= 150) s += 1.0;
  else if (sentVariance >= 10) s += 0.5;
  else s -= 0.3;

  if (commasPerSent >= 1.0 && commasPerSent <= 3.0) s += 0.8;
  else if (commasPerSent >= 0.5) s += 0.3;

  return clamp(s, 0.5, 6.0);
}

// ---------------------------------------------------------------------------
// Feedback
// ---------------------------------------------------------------------------

function lengthFeedback(words: number): string {
  if (words >= 500) return "Excellent length with thorough analysis and development.";
  if (words >= 350) return "Good length. Consider expanding with more supporting examples.";
  if (words >= 200) return "Adequate length, but more development would strengthen your response.";
  return "Your essay is quite short. Aim for 350-500 words for a strong response.";
}

function vocabFeedback(richness: number, longRatio: number): string {
  if (richness >= 0.6 && longRatio >= 0.15)
    return "Strong vocabulary with good variety and sophisticated word choices.";
  if (richness >= 0.5)
    return "Decent vocabulary range. Try incorporating more precise, academic language.";
  return "Vocabulary is somewhat repetitive. Vary your word choice and use more specific terms.";
}

function structureFeedback(paragraphs: number, transitions: number): string {
  if (paragraphs >= 4 && transitions >= 5)
    return "Well-organized with clear paragraphing and effective transitions.";
  if (paragraphs >= 3)
    return "Reasonable structure. More transition words would improve logical flow.";
  return "Organize your ideas into distinct paragraphs with clear transitions between them.";
}

function fluencyFeedback(avgLen: number, v: number): string {
  if (avgLen >= 14 && avgLen <= 25 && v >= 20)
    return "Excellent sentence variety — a good mix creates a natural reading rhythm.";
  if (avgLen >= 10)
    return "Sentences are generally well-constructed. Vary lengths more for better flow.";
  return "Sentences tend to be uniform. Mix short and long sentences for better rhythm.";
}

function overallFeedback(score: number, words: number): string {
  if (score >= 5.5)
    return "Your essay demonstrates insightful analysis with well-developed reasoning, strong vocabulary, and excellent organization. Outstanding response.";
  if (score >= 4.5)
    return "A strong response with clear, well-organized ideas and competent language. Adding more nuanced examples and varied sentences would elevate it further.";
  if (score >= 3.5)
    return "An adequate response. To improve, develop arguments with specific examples, use more transition words, and vary sentence structures.";
  if (score >= 2.5)
    return "Shows limited development. Focus on writing multiple paragraphs, each with a clear point, supporting evidence, and transitions.";
  return "The response needs significant development. Write 4-5 paragraphs with an introduction, body paragraphs with examples, and a conclusion.";
}

// ---------------------------------------------------------------------------
// Utilities
// ---------------------------------------------------------------------------

function variance(nums: number[]): number {
  if (nums.length < 2) return 0;
  const mean = nums.reduce((a, b) => a + b, 0) / nums.length;
  return nums.reduce((acc, n) => acc + (n - mean) ** 2, 0) / nums.length;
}

function clamp(val: number, min: number, max: number): number {
  return Math.min(max, Math.max(min, val));
}

function roundTo(val: number, decimals: number): number {
  const f = 10 ** decimals;
  return Math.round(val * f) / f;
}
