#!/usr/bin/env python3
"""
Trains a Ridge regression model on the training essays and exports
coefficients to public/model.json for use in the API route.

Usage:
    cd web/
    python scripts/train_model.py

Reads: ../Train100.txt
Writes: public/model.json
"""

import json
import re
import os
import sys

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(SCRIPT_DIR)
TRAIN_FILE = os.path.join(PROJECT_ROOT, "..", "Train100.txt")
OUTPUT_FILE = os.path.join(PROJECT_ROOT, "public", "model.json")

TRANSITION_WORDS = [
    "however", "therefore", "furthermore", "moreover", "consequently",
    "nevertheless", "although", "whereas", "likewise", "similarly",
    "additionally", "specifically", "for example", "for instance",
    "in contrast", "on the other hand", "in conclusion", "in summary",
    "first", "second", "third", "finally", "thus", "hence", "accordingly",
]


def extract_features(text):
    words = text.split()
    sentences = [s.strip() for s in re.split(r'[.!?]+', text) if s.strip()]
    paragraphs = [p.strip() for p in text.split('\n\n') if p.strip()]
    unique_words = set(w.lower() for w in words)

    word_count = len(words)
    sent_count = max(len(sentences), 1)
    para_count = max(len(paragraphs), 1)
    char_count = len(text.replace(' ', ''))

    avg_word_len = char_count / max(word_count, 1)
    avg_sent_len = word_count / sent_count
    vocab_richness = len(unique_words) / max(word_count, 1)

    long_words = sum(1 for w in words if len(re.sub(r'[^a-zA-Z]', '', w)) >= 7)
    long_word_ratio = long_words / max(word_count, 1)

    comma_count = text.count(',')
    commas_per_sent = comma_count / sent_count

    lower_text = text.lower()
    transition_count = sum(1 for tw in TRANSITION_WORDS if tw in lower_text)

    sent_lengths = [len(s.split()) for s in sentences]
    if len(sent_lengths) >= 2:
        mean_sl = sum(sent_lengths) / len(sent_lengths)
        sent_var = sum((x - mean_sl) ** 2 for x in sent_lengths) / len(sent_lengths)
    else:
        sent_var = 0

    return [
        word_count, sent_count, para_count, avg_word_len,
        avg_sent_len, vocab_richness, long_word_ratio,
        commas_per_sent, transition_count, sent_var
    ]


def main():
    if not os.path.exists(TRAIN_FILE):
        print(f"Training file not found: {TRAIN_FILE}")
        sys.exit(1)

    with open(TRAIN_FILE, 'r', encoding='utf-8', errors='replace') as f:
        lines = f.readlines()

    X, y = [], []
    for line in lines[1:]:
        parts = line.strip().split('\t')
        if len(parts) >= 4:
            essay_text = parts[2]
            score = float(parts[3])
            essay_set = int(parts[1])

            if essay_set == 1:
                normalized_score = score * 6.0 / 12.0
            elif essay_set == 3:
                normalized_score = score * 6.0 / 3.0
            else:
                normalized_score = score

            features = extract_features(essay_text)
            X.append(features)
            y.append(normalized_score)

    n = len(X)
    if n == 0:
        print("No training data found.")
        sys.exit(1)

    print(f"Loaded {n} essays for training.")

    # Feature names for reference
    feature_names = [
        "word_count", "sent_count", "para_count", "avg_word_len",
        "avg_sent_len", "vocab_richness", "long_word_ratio",
        "commas_per_sent", "transition_count", "sent_variance"
    ]

    # Normalize features (z-score)
    means = [0.0] * len(feature_names)
    stds = [1.0] * len(feature_names)

    for j in range(len(feature_names)):
        col = [X[i][j] for i in range(n)]
        means[j] = sum(col) / n
        var = sum((x - means[j]) ** 2 for x in col) / n
        stds[j] = var ** 0.5 if var > 0 else 1.0

    X_norm = []
    for i in range(n):
        row = [(X[i][j] - means[j]) / stds[j] for j in range(len(feature_names))]
        X_norm.append(row)

    # Ridge regression (closed-form: w = (X^T X + lambda I)^-1 X^T y)
    p = len(feature_names)
    lam = 1.0

    # X^T X + lambda I
    XtX = [[0.0] * p for _ in range(p)]
    for i in range(n):
        for j in range(p):
            for k in range(p):
                XtX[j][k] += X_norm[i][j] * X_norm[i][k]
    for j in range(p):
        XtX[j][j] += lam

    # X^T y
    Xty = [0.0] * p
    for i in range(n):
        for j in range(p):
            Xty[j] += X_norm[i][j] * y[i]

    # Solve via Gaussian elimination
    aug = [XtX[i][:] + [Xty[i]] for i in range(p)]
    for col in range(p):
        max_row = max(range(col, p), key=lambda r: abs(aug[r][col]))
        aug[col], aug[max_row] = aug[max_row], aug[col]
        pivot = aug[col][col]
        if abs(pivot) < 1e-12:
            continue
        for j in range(col, p + 1):
            aug[col][j] /= pivot
        for row in range(p):
            if row != col:
                factor = aug[row][col]
                for j in range(col, p + 1):
                    aug[row][j] -= factor * aug[col][j]

    weights = [aug[i][p] for i in range(p)]

    # Compute intercept
    y_mean = sum(y) / n
    intercept = y_mean

    # Compute training R^2
    ss_res, ss_tot = 0.0, 0.0
    for i in range(n):
        pred = intercept + sum(weights[j] * X_norm[i][j] for j in range(p))
        ss_res += (y[i] - pred) ** 2
        ss_tot += (y[i] - y_mean) ** 2
    r2 = 1 - ss_res / ss_tot if ss_tot > 0 else 0

    print(f"Training R^2: {r2:.4f}")
    print(f"Intercept: {intercept:.4f}")
    for name, w in zip(feature_names, weights):
        print(f"  {name}: {w:.4f}")

    model = {
        "feature_names": feature_names,
        "weights": weights,
        "intercept": intercept,
        "means": means,
        "stds": stds,
        "r2": r2,
    }

    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
    with open(OUTPUT_FILE, 'w') as f:
        json.dump(model, f, indent=2)

    print(f"\nModel saved to {OUTPUT_FILE}")


if __name__ == "__main__":
    main()
